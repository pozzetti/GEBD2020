package fin;
 
import java.util.ArrayList;
import org.neo4j.driver.AuthToken;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;
import java.util.Iterator;
import java.util.List;

import org.apache.spark.api.java.function.FlatMapFunction;
import org.neo4j.driver.AuthToken;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import scala.Tuple2;


public class DeBrujineGraph implements PairFlatMapFunction<String,Integer,String>  {
	
	
	public java.util.Iterator<Tuple2<Integer,String>> call (String line){
		List<Tuple2<Integer, String>> output = new ArrayList<Tuple2<Integer, String>>();
		List<String> kmers = new ArrayList<String>();
		//SCELTA DI K ARBITRARIA
	int k=5;
	//CONNESIONE
	String uri = "bolt://localhost:11003";
	AuthToken token = AuthTokens.basic("neo4j", "gedb2021");
	Driver driver = GraphDatabase.driver(uri, token);
	Session s = driver.session();
	//CREAZIONE GRAFO DI DEBRUJINE
		for( int j=0; j<line.length() ;j++)
		{

			String  km= line.substring(j, k);
			String kL=km.substring(0,km.length()-1);
			String kR=km.substring(1,km.length());
			
			String create = "merge (k:kmers {valore:'"+km+"',kLeft:'"+kL+"',kRight:'"+kR+"'})";
			   s.run(create);
			   kmers.add(km);
					if(k<line.length()) {
					k++;
		}
					else {
						break;
					}
					
		}
		String arc="match (k1:kmers),(k2:kmers)  where k1.kRight=k2.kLeft merge (k1)-[:arc]->(k2)";
		s.run(arc);
		
		//adesso eliminiamo le tips
		//nodi che hanno solo archi entranti
		int t=1;
		while(t>0) {
			String tips="match (k2:kmers) where (:kmers)-[:arc]->(k2:kmers) and not (k2:kmers)-[:arc]->(:kmers) detach delete k2 return count(k2) as conteggio";
			Result result=s.run(tips);
			Record r=result.next();
			t=r.get("conteggio").asInt();
		}
		//nodi che hanno solo archi uscenti
		 t=1;
		while(t>0) {
			String tips="match (k2:kmers) where not (:kmers)-[:arc]->(k2:kmers) and  (k2:kmers)-[:arc]->(:kmers) detach delete k2 return count(k2) as conteggio";
			Result result=s.run(tips);
			Record r=result.next();
			t=r.get("conteggio").asInt();
		}
		//nodi isolati
		 t=1;
		while(t>0) {
			String tips="match (k2:kmers) where not (:kmers)-[:arc]->(k2:kmers) and not (k2:kmers)-[:arc]->(:kmers) detach delete k2 return count(k2) as conteggio";
			Result result=s.run(tips);
			Record r=result.next();
			t=r.get("conteggio").asInt();
		}
		
		
		/*IL PROCEDIMENTO E' IL SEGUENTE:
		 * 1.TROVIAMO UN SEQUENZA TRAMITE UN PERCORSO CASUALE SUL GRAFO
		 * 2. QUESTA SEQUENZA DIVENTA QUELLA DI RIFERIMENTO PER UNA DELLE CLASSI IN CUI RACCOGLIEREMO LE SUCCESSIVE
		 * 3. TROVO NumPath ALTRE SEQUENZE DA CONFRONTARE CON QUELLA DI RIFERIMENTO CHE VERRANO
		 * INSERITE IN QUELLA CLASSE SE IL SIMILARITY RATIO E' DI ALMENO 0.6
		*/
		for(int i=0;i<5;i++) {
		int NumPath=10;
		String P1=FindPath(s);
		output.add(new Tuple2<Integer,String>(i,P1));
		while(NumPath>0) {
		String P2=FindPath(s);
		int [][] M=MatriceSiml(P1, P2);
	    float SR = SimilarityRatio (M);
		if(SR>0.6)
		output.add(new Tuple2<Integer,String>(i,P2));
		NumPath--;
		}
		}
		s.close();
		return output.iterator();
		
		
	}
	
	public int[][] MatriceSiml(String k1,String k2)
	{   int m = k1.length();
	     int n = k2.length();
	     int max=m;
	     if (n>max)
	    	 {max=n;
	    	 while(k1.length()<k2.length())
	    	 { k1=k1+"N";}
	    	 }
	     else {
	    	 
	    	 { while(k2.length()<k1.length())
	    		 k2=k2+"N";}
	    	 }
	     
	    int[][] M = new int[max][max];
	    
		for(int i=0;i<max;i++) {
			for(int j=0;j<max;j++) {
			 
				if(k1.charAt(i)  == k2.charAt(j))
					M[i][j]=1;
				else M[i][j]=0;
			}
		}
		return (M);
		
	}
	
	public float SimilarityRatio (int [][] M) {
		float s=0;
		for (int i=0; i< M.length;i++)
		{
			
				s+= M[i][i];
		}
		return (s/M.length);
	}
	
	

	public String FindPath(Session s)
	{ String sequenza = "";
		String origin = "";
		String random = "match (n:kmers) return n.valore as origine, rand() as r order by r limit 1";
		Result r1 = s.run(random);
		
		while(r1.hasNext()) {
			Record r = r1.next();
			 origin = r.get("origine").asString();
			
		}
		
		/*
		 * Si estrae un cammino da un nodo casuale
		 */
		
		String K = "";
		boolean cont = true;
		while(cont==true) {
			
			
			String query = "match (n1:kmers)-[a:arc]->(n2:kmers) where n1.valore='"+origin+"' return n2.valore as Kmer limit 1";
			
			Result result=s.run(query);
			
			while(result.hasNext()) {
				Record r = result.next();
				K = r.get("Kmer").asString();
			}
			
			
			if (K!="null") 
			{
				 sequenza+=K;
				origin = K;
				}
			else cont = false;
		
			
		if(sequenza.length()>=300) 
				{ 
					cont=false;
				}
		}
		return sequenza;
		
	
	}
}

	

