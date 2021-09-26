package fin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.spark.api.java.function.FlatMapFunction;

import scala.Tuple2;

/*
 * Estrapola da stringhe di testo la lista delle parole ivi contenute.
 * Richiamata all'interno di una trasformazione flatMapToPair
 */
public class EstrapolaParole implements FlatMapFunction<String, String> {

	/*
	 * Data una stringa in input, restituisce
	 * in output una lista delle parole presenti al suo interno,
	 * sotto forma di Iterator
	 */
	public java.util.Iterator<String>	call(String line){
		/*
		 * Suddivido la stringa in input in sottostringhe
		 * equivalenti circa alle singole parole che formano il testo
		 * tramite il metodo split ed ottenendo un array di stringhe
		 */
		String[] words = line.split(" ");
		/*
		 * Creo una lista di stringhe in cui ricopio, una ad una,
		 * tutte le parole precedentemente individuate
		 */
		List<String> result = new ArrayList<String>();
		for (String word : words) {
			result.add(word);
		}
		
		/*
		 * Restituisco il contenuto della lista sotto forma
		 * di iteratore
		 */
		
		return result.iterator();
		
	} 
	
	
}
