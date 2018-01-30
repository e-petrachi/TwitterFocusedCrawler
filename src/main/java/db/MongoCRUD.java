package db;

import api.news.NLPExtractor;
import api.news.UrlExtractor;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import model.Label2Cluster;
import model.News;
import model.News2Annotations;
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


public class MongoCRUD {
    private String dbName = "twitterFC";
    private MongoClient mongo;
    private MongoDatabase database;
    private DB db;
    private Jongo jongo;

    private MongoCollection collection;

    static Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    static { root.setLevel(Level.ERROR); }

    public MongoCRUD(boolean realDB){

        this.mongo = new MongoClient( "localhost" , 27017 );
        if (realDB == false)
            this.database = mongo.getDatabase("tfc");
        else
            this.database = mongo.getDatabase(this.dbName);

        if (realDB == false)
            this.db = mongo.getDB("tfc");
        else
            this.db = mongo.getDB(this.dbName);
        this.jongo = new Jongo(db);

    }

    public void setCollection(String name){
        this.collection = jongo.getCollection(name);
    }
    public void setCollection(){
        this.collection = jongo.getCollection("news");
    }

    public MongoCollection getCollection() { return this.collection; }

    public void saveNews(News news){
        String url = news.getUrl();
        String content = "";
        try {
            content = new UrlExtractor().getUrlOneLinerContent(url);
        } catch (Exception e ) {
            System.out.println("### URL non consistente...ELIMINO NEWS!");
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
        collection.remove("{textWithStemmer: ' '}");
    }

    public News findFirstNews(String query){
        News one = collection.findOne(query).as(News.class);
        return one;
    }
    public void saveCluster(Label2Cluster l2c){
        for (ArrayList<Double> cluster : l2c.getCluster().getEntries()){
            collection.save(cluster);
        }
    }

    public void saveLabel(Label2Cluster l2c){
        collection.save(l2c.getLabelsList());
    }

    public void retrieveCluster(int num) throws IOException {

        this.saveCSV(num);
        this.saveARFF(num);

    }
    private void saveCSV(int num) throws IOException{
        DBCollection collection0 = db.getCollection("label" + num);
        DBCursor cursor0 = collection0.find();

        BufferedWriter writer = new BufferedWriter(new FileWriter("cluster" + num + ".csv"));
        String head = "";

        int lunghezza = 0;

        cursor0.next();
        lunghezza = cursor0.curr().keySet().size()-1;
        for (int i=0;i<lunghezza;i++) {
            head = head + ((String) cursor0.curr().get("" + i)).toLowerCase().replaceAll(" |,|;|:", "_").replaceAll("\\.|!|\\?|\\'","") + ";";
        }
        head = head + "\n";

        writer.write(head);

        DBCollection collection = db.getCollection("cluster" + num);
        DBCursor cursor = collection.find();

        while (cursor.hasNext()) {
            cursor.next();
            if (cursor.curr().get("" + 0) != null ) {
                String line = "";
                for (int i = 0; i < lunghezza; i++) {
                    line = line + cursor.curr().get("" + i) + ";";
                }
                line = line + "\n";
                writer.write(line);
            }
        }

        writer.close();
    }
    private void saveARFF(int num) throws IOException{
        DBCollection collection0 = db.getCollection("label" + num);
        DBCursor cursor0 = collection0.find();

        BufferedWriter writer = new BufferedWriter(new FileWriter("cluster" + num + ".arff"));
        String head = "@RELATION cluster" +  num + "\n\n";

        int lunghezza = 0;


        cursor0.next();
        lunghezza = cursor0.curr().keySet().size()-1;

        System.out.println("\tATTRIBUTI # " + lunghezza);

        for (int i=0;i<lunghezza;i++) {
            head = head + "@ATTRIBUTE " + ((String) cursor0.curr().get("" + i)).toLowerCase().replaceAll(" |,|;|:", "_").replaceAll("\\.|!|\\?|\\'","") + " NUMERIC\n";
        }
        head = head + "\n";

        head = head + "\n\n@DATA\n";
        writer.write(head);

        DBCollection collection = db.getCollection("cluster" + num);
        DBCursor cursor = collection.find();

        int rows = 0;
        while (cursor.hasNext()) {
            cursor.next();
            if (cursor.curr().get("" + 0) != null ) {
                String line = "";
                for (int i = 0; i < lunghezza; i++) {
                    line = line + cursor.curr().get("" + i) + ",";
                }
                line = line + "\n";
                writer.write(line);
                rows++;
            }
        }

        System.out.println("\tDATI # " + rows);

        writer.close();
    }
    public void saveNews2Annotations(News2Annotations news2Annotations) {
        collection.save(news2Annotations);
    }

}
