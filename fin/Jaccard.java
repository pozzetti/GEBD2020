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
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.ReduceFunction;
import scala.Tuple2;



public class Jaccard  implements Function2 <String,String,String>{
	/*L'IDEA E' QUELLA DI APPLICARE LA JACCARD SIMILARITY TRA LE SEQUENZE
	 * DI UNA STESSA CLASSE E SE QUESTO VALORE SUPERA LA SOGLIA DI 0.8 LE TENGO NELLA 
	 * STESSA CLASSE
	 */
	public String call (String S1,String S2){
		String result = "";
		
		int intersectionSize=0;
		for(int i=0; i<S1.length();i++) 
		{
			for(int j=0;j<S2.length();j++)
			{
				if(S1.charAt(i)==S2.charAt(j))
					{intersectionSize++;}
			}
		}
	
		float unionSize= S1.length() + S2.length() - intersectionSize;
		
		if(( intersectionSize/unionSize)>=0.8)
			result=result+S1+" "+S2;
			
		else 
			result+=" ";
			
		
	
 return result;}
}
