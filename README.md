# TWITTER FOCUSED CRAWLER 

#### <a href="https://github.com/e-petrachi">Enrico Petrachi</a> + Bernardo Marino


Ricerca autonoma di informazioni (**focused** e  **intelligent crawler**): 
valutare tecniche di _crawling intelligente_ sul social network **TWITTER** per trovare informazioni rilevanti impiegando non solo le search API.

Un tool per cercare **TWEET** su un determinato **TOPIC** 
utilizzando tecniche di **clustering** e **crawling intelligente**.


#### APIs e LIBRERIE utilizzate:

* <a href="https://newsapi.org/docs">NewsAPI</a>:
libreria REST per cercare e recuperare articoli (news) da tutto il web e popolare il database delle news;
* <a href="http://unirest.io/java">Unirest4j</a>: 
comoda libreria per fare richieste HTTP;
* <a href="https://github.com/kohlschutter/boilerpipe">BoilerPIPE</a>: 
libreria Google per estrarre testo dalle pagine HTML;
* <a href="https://github.com/enrichman/tagme4j">TAGme4j</a>: 
TagMe API client per applicazioni Java;
* <a href="https://mongodb.github.io/mongo-java-driver/">MONGOdb</a>: 
database noSQL per la memorizzazione semplice dei dati persistenti;
* <a href="https://github.com/bguerout/jongo">Jongo</a>: 
libreria per fare il cast al volo dei dati estratti da mongoDB nelle relative classi;
* <a href="http://weka.sourceforge.net/doc.stable/">WEKA</a>:
 libreria per eseguire gli algoritmi di apprendimento automatico e di data mining;
* <a href="http://twitter4j.org/en/index.html">TWITTER4j</a>:
libreria per interfacciarsi con le API e lo stream di twitter.

#### APIKEYs da INSERIRE

* [NewsAPI: newsAPI](src/main/java/api/news/NewsExtractor.java) 
previa registrazione gratuita sul <a href="https://newsapi.org/docs">sito</a>;
* [TagMe: apikey](src/main/java/api/tagme4j/TagMeClient.java) 
previa registrazione gratuita sul <a href="https://tagme.d4science.org">sito</a>;
* [Twitter: apikey + apiSecret](src/main/java/api/twitter/TweetExtractor.java) 
previa registrazione gratuita sul <a href="https://developer.twitter.com">sito</a>;
* [Twitter4j: properties](twitter4j.properties) 
previa autenticazione e via OAUTH dalla classe sopra (vedi file .env generato).

#### ESECUZIONE del CODICE

N.B. Ogni passo della classe Main è autocontenuto ed apparte alcune dipendenze dei parametri dei metodi, 
ogni passo può essere eseguito in maniera totalmente indipendente dagli altri in quanto memorizza/preleva dati direttamente dal database.

E' necessario creare preliminarmente i seguenti database Mongo con le seguenti collezioni:

* **tfc** -> news, news2annotations, label1, label2, label3, cluster0, cluster1, cluster2, cluster3;
* **twitterDB** -> en, smooth, tweet2hashtag, hashtag2vec, tweetONtopic, wordsONtopic.

### ALGORITMI IMPLEMENTATI

1) CLUSTERING ONE - 
ottenuto dal testo delle news filtrando le stopwords ed eseguendo stemming del testo per poi utilizzare il TF-IDF per clusterizzare

> ##### ESECUZIONE CLUSTERING ONE:
> * [`newsController.newsExtractionAndSave()`](src/main/java/Main.java) : crea e popola il database di news, estraendo il contenuto delle news ed effettuando stopwords e stemming;
> * [`newsController.newsCleaning()`](src/main/java/Main.java) : ripulisce il db da eventuali news senza testo;
> * [`clusteringOneController.createMatrix()`](src/main/java/Main.java): calcola i tf-idf delle parole estratte e crea la matrice per eseguire il clustering;
> * [`fileController.saveCluster(1)`](src/main/java/Main.java): crea il file arff per il training;
> * [`cluster1 = clusteringOneController.executeCluster()`](src/main/java/Main.java): esegue il clustering.



2) CLUSTERING TWO - 
ottenuto dal testo delle news e basato sulla probabilità dei link delle annotazioni di TagMe

> ##### ESECUZIONE CLUSTERING TWO:
> * [`newsController.annotationsExtractionAndSave(sogliaMinimaLink)`](src/main/java/Main.java) : estrae le annotazioni dal testo delle news con TAGME4j
> * [`newsController.news2AnnCleaning()`](src/main/java/Main.java) : ripulisce il db da eventuali news senza annotazioni;
> * [`cluster2_matrix = clusteringTwoController.createMatrix()`](src/main/java/Main.java): salva le probabilità dei link di TAGME sopra una certa soglia e crea la matrice per eseguire il clustering;
> * [`fileController.saveCluster(2)`](src/main/java/Main.java): crea il file arff per il training;
> * [`cluster2 = clusteringTwoController.executeCluster()`](src/main/java/Main.java): esegue il clustering.


3) CLUSTERING THREE - 
clustering del clustering di supporto (clustering 0) creato con il grado di relatività tra annotazioni di TagMe e basato sulla media delle probabilità dei link delle annotazioni di TagMe all'interno dei cluster

> ##### ESECUZIONE CLUSTERING THREE:
> * [`clusteringThreeController.createMatrix0()`](src/main/java/Main.java) : crea la matrice per addestrare il clustering di supporto con il grado di relatività tra annotazioni TAGME;
> * [`fileController.saveCluster(0)`](src/main/java/Main.java): crea il file arff per il training del clustering di supporto;
> * [`cluster0 = clusteringThreeController.executeCluster0()`](src/main/java/Main.java): esegue il clustering di supporto;
> * [`clusteringThreeController.createMatrix(cluster0, cluster2_matrix)`](src/main/java/Main.java) : crea la matrice per addestrare il clustering finale;
> * [`fileController.saveCluster(3)`](src/main/java/Main.java): crea il file arff per il training del clustering finale;
> * [`cluster3 = clusteringThreeController.executeCluster()`](src/main/java/Main.java): esegue il clustering finale.

4) <a href="http://citeseerx.ist.psu.edu/viewdoc/download?doi=10.1.1.221.9092&rep=rep1&type=pdf">SMOOTHING STREAMS con Normalized Stupid Backoff</a> - 
focused crawler ottimizzato su un determinato lessico attuale (background) e addestrato su un determinato topic in continuo aggiornamento (coda runtime) che verrà selezionato in automatico tra i top topics dell'ultimo clustering e i topic ottenuti facendo uno stream di tutti i tweets di twitter per qualche giorno/settimana 

> ##### ESECUZIONE SMOOTHING STREAMS:
> * [`TweetPopulate`](src/main/java/TweetPopulate.java) : popola il background di twitter (lasciarlo in esecuzione per qualche giorno..);

> * [`topics = classifier.getTopics()`](src/main/java/Main.java) : estrae i top topic dal clustering three;
> * [`tweetElaborator.elaborateBackground()`](src/main/java/Main.java) : elabora i tweet memeorizzati nel db in una forma tweet -> hashtag;
> * [`tweetElaborator.createHashtag2vec(sogliaMinimaHashtag)`](src/main/java/Main.java) : estrae e memorizza solo gli hashtag che superano una certa soglia di tweet;
> * [`common = tweetElaborator.findCommonTopic(topics)`](src/main/java/Main.java) : trova un topic in comune tra gli hashtag scelti ed i top topic del clustering three;
> * [`tweetElaborator.createBackgroundForTopic(common)`](src/main/java/Main.java) : crea il background sul topic scelto memorizzando i tweet nel db;
> * [`tweetElaborator.createWord2weight(sogliaMinimaWords)`](src/main/java/Main.java) : salva le probabilità rispetto alle parole contenute nel background model;

> * [`TweetSmoothingStreams`](src/main/java/TweetSmoothingStreams.java) : apre lo stream di twitter ed esegue il focused crawler in real time memorizzando solo i tweet pertinenti.

#### SVILUPPI

Per quanto riguarda i clustering è evidente che sarebbe necessaria una maggiore ricchezza di news su cui basare i diversi clustering,
che magari potrebbero scaturire da altre APIs (da testare) che magari renderebbero più ricca la base da cui far partire i clustering.

Per quanto riguarda lo smoothing stream invece sarebbe interessante creare un background di tweet molto più corposo, 
magari accumulando tweet per un intero mese e andarlo a integrare tenendo conto anche del CLUSTERING ONE come corpus linguistico; 
si vede infatti una notevole presenza di parole rumorose come FOLLOW o CLICK e altre, che dovrebbero essere rimosse in quanto non costituiscono
contenuto informativo riguardante un topic in particolare. 

Risulta poi difficile il settaggio della soglia di perplessità dello Smoothing Stream 
in quanto dipende fortemente dal numero di parole del tweet, quindi magari sarebbe interessante trovare un modo per settarlo in automatico,
oppure modificare tale misura per renderla indipendente dal numero di parole del tweet.
