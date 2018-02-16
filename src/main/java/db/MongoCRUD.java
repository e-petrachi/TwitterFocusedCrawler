package db;

import api.news.NLPExtractor;
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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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
    public MongoCRUD(boolean realDB){

        this.mongo = new MongoClient( "localhost" , 27017 );
        if (realDB == false)
            this.database = mongo.getDatabase("tfc");
        else
            this.database = mongo.getDatabase("twitterFC");

        if (realDB == false)
            this.db = mongo.getDB("tfc");
        else
            this.db = mongo.getDB("twitterFC");
        this.jongo = new Jongo(db);

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
    public ArrayList<String> findAllLabel(int num){
        DBCollection collection0 = db.getCollection("label" + num);
        DBCursor cursor0 = collection0.find();
        cursor0.next();

        ArrayList<String> labels = new ArrayList<>();

        int lunghezza = cursor0.curr().keySet().size()-1;
        for (int i=0;i<lunghezza;i++) {
            labels.add( (String) cursor0.curr().get("" + i));
        }
        return labels;
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

    public void saveTweet(String user, String text){
        if (!this.getCollectionName().equalsIgnoreCase("en")){
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

    public void convertTweetsInWords(ArrayList<Tweet> tweets){
        if (!this.getCollectionName().equalsIgnoreCase("word")) {
            System.out.println("\n\nCollections sbagliata!");
            return;
        }
        NLPExtractor nlpExtractor = new NLPExtractor();
        TreeMap<String, Integer> word2vec = new TreeMap<>();

        for (Tweet t: tweets){
            String cleaned =  nlpExtractor.removeStopwords(t.getTweet());
            for (String s :cleaned.split("[^A-Za-z]")) {
                if (nlpExtractor.isWord(s)) {
                    if (word2vec.get(s) == null)
                        word2vec.put(s, 1);
                    else
                        word2vec.put(s, word2vec.get(s) + 1);
                }
            }
        }

        Word2Vec w2v = new Word2Vec(word2vec);
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
}
