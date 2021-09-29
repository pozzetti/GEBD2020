package fin;

import java.util.ArrayList;
import org.apache.spark.api.java.function.FlatMapFunction;
import java.util.HashMap;
import java.util.List;

import org.neo4j.driver.AuthToken;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

import scala.Tuple2;

//import gebd.Studente;

import java.util.Scanner;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import java.io.Serializable;

public class GestioneDnaDistribuito implements Serializable {
public static void main(String[] args) {

   //Connessione Spark
	Logger.getLogger("org").setLevel(Level.ERROR);
	Logger.getLogger("akka").setLevel(Level.ERROR);
	       
	SparkConf sc = new SparkConf();
	sc.setAppName("Test");
	sc.setMaster("local[*]");
	JavaSparkContext jsc = new JavaSparkContext(sc);
	

	
	
	JavaRDD<String> dReads = jsc.textFile("Chimpanzee.txt");
	JavaRDD<String> dRead = dReads.flatMap(new EstrapolaParole());
	JavaPairRDD <Integer,String> Pair = dRead.flatMapToPair(new DeBruijnGraph());
	Pair.foreach(x->System.out.println("SEQUENZA:"+x));
	
	
	//FASE REDUCE CHE NON STAMPA NULLA 
	
	//JavaPairRDD <Integer,String> Classification = Pair.reduceByKey((x,y) -> x+ y);
	//JavaPairRDD <Integer,String> Classification=Pair.reduceByKey(new Jaccard());
	//Classification.foreach(x->System.out.println("Classification"+x));
	
	
	

	
	
	
}

}
