# De Bruijn Graph


## Abstract

L'obiettivo del progetto e' quello di implementare un algoritmo distribuito per la classificazione di catene di DNA con strumenti quali il grafo di De bruijn per la ricostruzione delle sequenze come cammini, la similairty matrix e la Jaccard similarity per la suddivisione in classi.
Lo svolgimento del progetto fa riferimento all'articolo *"De-Bruijn graph with MapReduce framework towards metagenomic data classification"* scritto da Md. Sarwar Kamal, S. Parvin, A. S. Ashour, F. Shi, N. Dey.
Il linguaggio di programmazione utilizzato e' Java. Il DBMS NoSQL di supporto, per la memorizzazione e visualizzazione del grafo, e' Neo4j.


## Il dataset

Fonte: Network Repository Kaggle \
Link: https://www.kaggle.com/nageshsingh/demystify-dna-sequencing-with-machine-learning/data
I dati sono costituiti da circa 50 sequenze di DNA di chimpazee divise in 5 classi.


## I file .java

| Classe        | Descrizione           |
|:---------- |:------------- |
| `GestioneDnaDistribuito.java` | classe main |
| `DeBruijnGraph.java` | classe utilizzata per la creazione del grafo e per l'individuazione di cammini |
| `EstrapolaParole.java` | classe per isolare la singola read |
| `Jaccard.java` | classe che implementa la Jaccard similarity |


## Descrizione dell'algoritmo

L'algoritmo prevede 4 Stages. 
- Il grafo di De Bruijn: Per ogni read vengono creati i rispettivi k-mers che costituiranno i nodi del grafo di Bruijn. Gli archi collegano i nodi N1 e N2 se gli ultimi k-1 nucleotidi di N1 sono uguali ai k-1 primi nucleotidi di N2.
- Pulizia del grafo: Dal grafo vengono rimosse le tips.
- Individuazione dei percorsi: si trovano C percorsi partendo da nodi casuali del grafo e si usano come referenti per C classi.
- Similarity ratio: Si confronta un numero arbitrario di ulteriori percorsi con i referenti di ogni classe tramite la similarity matrix e nel caso in cui il similarity ratio con il referente sia superiore di 0.6 si ritiene che il percorso in analisi faccia parte di quella classe.


### Importazione dei dati 

La nostra applicazione legge il file `.txt` contenente le read e tramite la classe `Estrapola parole` si crea una `JavaRDD<String> dread`. Tramite una operazione di `.mapToPair` su dread, crea una `JavaPairRDD<Integer, String> Pair` con in chiave la classe e in valore la sequenza, dopo aver creato il grafo di De Bruijn.


### Stage 1

La prima fase consiste nel suddividere i reads in k-mer di lunghezza fissata e nel creare il grafo di De Bruijn in cui i nodi sono i k-mer e due nodi sono collegati da un arco se i rispettivi (k-1)-mer che hanno la funzione di prefisso e suffisso sono uguali.


### Stage 2

Per migliorare il grafo di De Bruijn e per rendere possibile l'individuazione di un cammino, si eliminano gli errori, come la presenza di tips, ovvero diramazioni cieche del percorso. In particolare, si eliminano tutti quei nodi che hanno solo archi entranti, solo archi uscenti oppure sono totalemte isolati.


### Stage 3

Viene individuato un cammino partendo da un nodo selezionato casualmente e si etichetta questo cammino come "referente".
L'algoritmo trova altri cammini e li confronta con il referente mediante matrice di similarita', andando a comparare le singole basi azotate delle catene.


### Stage 4

Si calcola il similarity ratio per ogni coppia di catene; se esso supera un valore soglia posto uguale a 0.6, le due catene vengono allocate all'interno della medesima classe. Il similarity ratio Ã¨ costruito nel seguente modo:
1. Si crea una matrice che ha la prima riga e la prima colonna rispettivamente le due sequenze in analisi.
2. Si confrontano a due a due le basi azotate, ponendo uguale a 1 l'elemento ij se la base in posizione i della prima sequenza Ã¨ uguale alla base j della seconda.
3. Si calcola il rapporto tra la somma degli '1' presenti sulla diagonale e la lunghezza della stessa.
4. Il numero cosÃ¬ ottenuto Ã¨ il similarity ratio.
Si ripetono lo Stage 3 e lo Stage 4 per un numero pari al numero di classi in cui suddividere i reads di DNA.


## Neo4j

Si e' ritenuto opportuno far uso di Neo4j in quanto DBMS Graph-based, per riuscire a sfruttare tutte le proprieta' del grafo di De Bruijn e per interagire con esso mediante query del linguaggio CypherQl.
La gestione del grafo di De Bruijn avviene all'interno della classe `DeBruijnGraph.java`.


## Analisi dei risultati
Sebbene non si sia riusciti a ricostruire le sequenze originali come risultano nel file dei dati originali, si ottiene una suddivisione in classi abbastanza soddisfacente.
