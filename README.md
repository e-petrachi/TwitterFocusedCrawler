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


#### ALGORITMI IMPLEMENTATI



#### CONSIDERAZIONI FINALI

