package db;

import api.nlp.NLPExtractor;
import api.news.UrlExtractor;
import com.mongodb.*;
import com.mongodb.client.MongoDatabase;
import model.*;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;


public class MongoCRUD {
    private String dbName;
    private MongoClient mongo;
    private MongoDatabase database;
    private DB db;
    private Jongo jongo;

    private MongoCollection collection;

    static Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    static { root.setLevel(Level.ERROR); }

    public MongoCRUD(){
        this.mongo = new MongoClient( "localhost" , 27017 );
    }

    public void setDbName(String dbName){
        this.dbName = dbName;
        this.database = mongo.getDatabase(this.dbName);
        this.db = mongo.getDB(this.dbName);
        this.jongo = new Jongo(db);
    }

    public DB getDb() { return this.db; }
    public void setCollection(String name){
        this.collection = jongo.getCollection(name);
    }
    public String getCollectionName(){ return this.collection.getName(); }
    public MongoCollection getCollection() { return this.collection; }

    public void saveNews(News news){
        if (!this.getCollectionName().equalsIgnoreCase("news")){
            System.out.println("\n\nCollections sbagliata!");
            return;
        }

        String url = news.getUrl();
        String content = "";
        try {
            content = new UrlExtractor().getUrlOneLinerContent(url);
        } catch (Exception e ) {
            System.out.println("*url non accedibile*");
        }
        if (content != "") {
            news.setText(content);
            NLPExtractor nlpExtractor = new NLPExtractor();
            String stopped = nlpExtractor.removeStopwords(content);
            news.setTextWithoutStopword(stopped);
            news.setTextWithStemmer(nlpExtractor.stemming(stopped));
            collection.save(news);
        }
    }
    public MongoCursor<News> findAllNews(String query){
        MongoCursor<News> all;
        if (query.isEmpty()){
            all = collection.find().as(News.class);
        } else {
            all = collection.find(query).as(News.class);
        }
        return all;
    }
    public ArrayList<Long> findAllLabelId(int num){
        DBCollection collection0 = db.getCollection("label" + num);
        DBCursor cursor0 = collection0.find();
        cursor0.next();
        cursor0.next();

        ArrayList<Long> labelsId = new ArrayList<>();

        int lunghezza = cursor0.curr().keySet().size()-1;
        for (int i=0;i<lunghezza;i++) {
            labelsId.add( (Long) cursor0.curr().get("" + i));
        }
        return labelsId;
    }
    public MongoCursor<News2Annotations> findAllNews2Annotations(String query){
        MongoCursor<News2Annotations> all;
        if (query.isEmpty()){
            all = collection.find().as(News2Annotations.class);
        } else {
            all = collection.find(query).as(News2Annotations.class);
        }
        return all;
    }

    public void cleanNews(){
        if (!this.getCollectionName().equalsIgnoreCase("news")){
            System.out.println("\n\nCollections sbagliata!");
            return;
        }

        collection.remove("{textWithStemmer: ' '}");
    }

    public void cleanNews2Annotations(){
        if (!this.getCollectionName().equalsIgnoreCase("news2annotations")){
            System.out.println("\n\nCollections sbagliata!");
            return;
        }

        collection.remove("{ annotations.0 : { $exists: false } }");
    }
    public void cleanTweet2Hashtag(){
        if (!this.getCollectionName().equalsIgnoreCase("tweet2hashtag")){
            System.out.println("\n\nCollections sbagliata!");
            return;
        }
        collection.remove("{ tweet: ''}");
        collection.remove("{ hashtags.0 : { $exists: false } }");
    }

    public void saveCluster(Label2Cluster l2c){
        if (!this.getCollectionName().matches("cluster[0-9]")){
            System.out.println("\n\nCollections sbagliata!");
            return;
        }
        for (ArrayList<Double> cluster : l2c.getCluster().getEntries()){
            collection.save(cluster);
        }
    }
    public void saveCluster(double[][] matrix){
        if (!this.getCollectionName().matches("cluster[0-9]")){
            System.out.println("\n\nCollections sbagliata!");
            return;
        }

        for (double[] row : matrix){
            collection.save(row);
        }
    }

    public void saveLabel(Label2Cluster l2c){
        if (!this.getCollectionName().matches("label[0-9]")){
            System.out.println("\n\nCollections sbagliata!");
            return;
        }

        this.clearCollection();
        collection.save(l2c.getLabelsList());
        if (l2c.getIdsList() == null)
            return;
        collection.save(l2c.getIdsList());
    }

    public void saveTweet2Hashtag(Tweet2Hashtag t2h){
        if (!this.getCollectionName().equalsIgnoreCase("tweet2hashtag") && !this.getCollectionName().equalsIgnoreCase("tweetONtopic")){
            System.out.println("\n\nCollections sbagliata!");
            return;
        }

        collection.save(t2h);
    }
    public ArrayList<String> getTopHashtags(){
        if (!this.getCollectionName().equalsIgnoreCase("hashtag2vec")){
            System.out.println("\n\nCollections sbagliata!");
            return null;
        }

        ArrayList<String> hashtags = new ArrayList<>();
        MongoCursor<Word2Vec> h2v = collection.find().as(Word2Vec.class);

        for (Word2Vec row: h2v){
            hashtags.addAll(row.getWord2vec().keySet());
        }
        return hashtags;
    }
    public void saveTweet(String user, String text){
        if (!this.getCollectionName().equalsIgnoreCase("en") && !this.getCollectionName().equalsIgnoreCase("smooth")){
            System.out.println("\n\nCollections sbagliata!");
            return;
        }

        Tweet tweet = new Tweet(user,text);
        collection.save(tweet);
    }

    public void clearCollection(){
        collection.drop();
    }

    public void saveNews2Annotations(News2Annotations news2Annotations) {
        collection.save(news2Annotations);
    }

    public void convertTweets(ArrayList<Tweet> tweets){
        if (!this.getCollectionName().equalsIgnoreCase("tweet2hashtag")) {
            System.out.println("\n\nCollections sbagliata!");
            return;
        }

        NLPExtractor nlpExtractor = new NLPExtractor();
        for (Tweet t: tweets) {
            Tweet2Hashtag t2h = new Tweet2Hashtag();
            ArrayList al = nlpExtractor.extractHashtag(t.getTweet());
            t2h.setHashtags(al);
            String tw = nlpExtractor.removeHashtag(t.getTweet());
            t2h.setTweet(tw);
            this.saveTweet2Hashtag(t2h);
        }

    }
    public void convertTweetsInWords(ArrayList<Tweet2Hashtag> tweets, int sogliaMinima){
        if (!this.getCollectionName().equalsIgnoreCase("wordsONtopic")) {
            System.out.println("\n\nCollections sbagliata!");
            return;
        }

        NLPExtractor nlpExtractor = new NLPExtractor();

        TreeMap<String, Double> word2occ = new TreeMap<>();

        for (Tweet2Hashtag t: tweets){
            for (String s : t.getTweet().split("[^A-Za-z]")) {
                if (nlpExtractor.isWord(s) && nlpExtractor.isNotExplicit(s)) {
                    if (!word2occ.containsKey(s))
                        word2occ.put(s, 1d);
                    else
                        word2occ.put(s, word2occ.get(s) + 1d);
                }
            }
            for (String h : t.getHashtags()){
                if (nlpExtractor.isWord(h) && nlpExtractor.isNotExplicit(h)) {
                    if (!word2occ.containsKey(h))
                        word2occ.put(h, 1d);
                    else
                        word2occ.put(h, word2occ.get(h) + 1d);
                }
            }
        }

        Word2Vec w2v = new Word2Vec(word2occ, sogliaMinima);

        ArrayList<String> vocabulary = new ArrayList<>();
        for (Tweet2Hashtag t: tweets) {
            String tweet = t.getTweet();

            for (String s: tweet.split("[^A-Za-z]")) {
                String clean = s.replaceAll(" ","");
                if (nlpExtractor.isWord(clean) && w2v.getWord2vec().containsKey(clean))
                    vocabulary.add(clean);
            }

            ArrayList<String> hashtag = t.getHashtags();

            for (String h: hashtag) {
                String clean = h.replaceAll(" ","");
                if (nlpExtractor.isWord(clean) && w2v.getWord2vec().containsKey(clean))
                    vocabulary.add(clean);
            }

        }

        TreeMap<String, Double> word2w = new TreeMap<>();
        for (String s : w2v.getWord2vec().keySet()){
            double pbw = nlpExtractor.calculateBackgroundProbability(s, vocabulary);
            word2w.put(s,pbw);
        }

        Word2Vec w2w = new Word2Vec(word2w,0);
        collection.save(w2w);
    }
    public void convertTweet2HashtagInHashtag(ArrayList<Tweet2Hashtag> tweets, int soglia){
        if (!this.getCollectionName().equalsIgnoreCase("hashtag2vec")) {
            System.out.println("\n\nCollections sbagliata!");
            return;
        }
        NLPExtractor nlpExtractor = new NLPExtractor();
        TreeMap<String, Double> hashtag2vec = new TreeMap<>();

        for (Tweet2Hashtag t: tweets){
            for (String hashtag: t.getHashtags()) {
                String hasht = hashtag.replaceAll(" ","");
                if (nlpExtractor.isWord(hasht)) {
                    if (hashtag2vec.get(hasht) == null)
                        hashtag2vec.put(hasht, 1d);
                    else
                        hashtag2vec.put(hasht, hashtag2vec.get(hasht) + 1);
                }
            }
        }

        Word2Vec w2v = new Word2Vec(hashtag2vec, soglia);
        collection.save(w2v);
    }

    public ArrayList<Tweet> findAllTweets(){
        if (!this.getCollectionName().equalsIgnoreCase("en")){
            System.out.println("\n\nCollections sbagliata!");
            return null;
        }
        MongoCursor<Tweet> all = collection.find().as(Tweet.class);
        ArrayList<Tweet> tweetArrayList = new ArrayList<>();
        for (Tweet t: all) {
            tweetArrayList.add(t);
        }
        return  tweetArrayList;
    }

    public ArrayList<Tweet2Hashtag> findAllTweets2Hashtag(){
        if (!this.getCollectionName().equalsIgnoreCase("tweet2hashtag") && !this.getCollectionName().equalsIgnoreCase("tweetONtopic")){
            System.out.println("\n\nCollections sbagliata!");
            return null;
        }
        MongoCursor<Tweet2Hashtag> all = collection.find().as(Tweet2Hashtag.class);
        ArrayList<Tweet2Hashtag> tweetArrayList = new ArrayList<>();
        for (Tweet2Hashtag t: all) {
            tweetArrayList.add(t);
        }
        return  tweetArrayList;
    }

    public HashMap<String, Double> getBackgroundModel(){
        if (!this.getCollectionName().equalsIgnoreCase("wordsONtopic")){
            System.out.println("\n\nCollections sbagliata!");
            return null;
        }

        MongoCursor<Word2Vec> h2v = collection.find().as(Word2Vec.class);

        Word2Vec w2v = null;
        for (Word2Vec row: h2v){
            w2v = row;
        }

        HashMap<String,Double> word2prob = new HashMap<>(w2v.getWord2vec());
        return word2prob;
    }
}
