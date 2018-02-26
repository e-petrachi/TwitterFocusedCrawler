# TWITTER FOCUSED CRAWLER

Ricerca autonoma di informazioni (**focused** e  **intelligent crawler**): 
valutare tecniche di _crawling intelligente_ sul social network **TWITTER** per trovare informazioni rilevanti impiegando non solo le search API.

· Un tool per cercare **TWEET** su un determinato **TOPIC** 
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


### ALGORITMI IMPLEMENTATI

1) CLUSTERING ONE - 
ottenuto dal testo delle news filtrando le stopwords ed eseguendo stemming del testo per poi utilizzare il TF-IDF per clusterizzare

##### ESECUZIONE CLUSTERING ONE:

a) [newsController.newsExtractionAndSave()](src/main/java/Main.java:47) : crea e popola il database di news, estraendo il contenuto delle news ed effettuando stopwords e stemming; 
b) [newsController.newsCleaning()]()Calcolare i tf-idf delle parole estratte
Creare il file arff per il training (x completezza viene creato anche il file csv)
Eseguire il clustering 

2_Clustering basato sulla probabilità dei link delle annotazioni di TagMe
*Creare e popolare il db (solo se non è stato già fatto sopra)
*Estrarre il contenuto delle news( stopwords ) (solo se non è stato già fatto sopra)
Estrarre le annotazioni dal testo con TagMe
Creare il file arff per il training (x completezza viene creato anche il file csv)
Eseguire il clustering 

3_Clustering del clustering di supporto con il grado di relatività tra annotazioni di TagMe

#### ESECUZIONE del COSDICE

#### CONSIDERAZIONI FINALI e SVILUPPI

