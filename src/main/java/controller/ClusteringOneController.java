package controller;

import api.news.NLPExtractor;
import db.MongoCRUD;
import model.Cluster;
import model.Label2Cluster;
import model.News;
import org.jongo.MongoCursor;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class ClusteringOneController implements ClusteringController {

    private boolean realDB = true;
    private int sogliaCluster = 0;

    public ClusteringOneController(boolean realDB, int sogliaCluster) {
        this.realDB = realDB;
        this.sogliaCluster = sogliaCluster;
    }


    public void createMatrix() {
        MongoCRUD mongoCRUD = new MongoCRUD(this.realDB);
        mongoCRUD.setCollection("news");

        MongoCursor<News> allNews = mongoCRUD.findAllNews("");

        Label2Cluster l2c1 = new Label2Cluster(sogliaCluster);
        System.out.println("\tCREAZIONE LABEL per CLUSTERING1");
        for (News news: allNews){
            String[] args = news.getTextWithStemmer().split(" ");
            for (String key: args) {
                if (key.length() > 2)
                    l2c1.addLabel(key);
            }
            System.out.print(".");
        }

        l2c1.setLabelsList();

        try {
            allNews.close();
        } catch (IOException e) { }

        System.out.println("\n\tSALVATAGGIO LABEL per CLUSTERING1");
        mongoCRUD.setCollection("label1");
        mongoCRUD.saveLabel(l2c1);
        System.out.println("\tSALVATAGGIO LABEL COMPLETATO\n");


        NLPExtractor nlp = new NLPExtractor();
        Cluster c1 = new Cluster();
        ArrayList<String> labels = l2c1.getLabelsList();

        System.out.println("\tCREAZIONE MATRICE per CLUSTERING1\n");
        mongoCRUD = new MongoCRUD(realDB);

        mongoCRUD.setCollection("news");
        allNews = mongoCRUD.findAllNews("");

        ArrayList<News> allNewsList = new ArrayList<>();
        while (allNews.hasNext()){
            allNewsList.add(allNews.next());
        }

        try {
            allNews.close();
        } catch (IOException e) { }

        System.out.println("\tCALCOLO TF-IDF per la MATRICE\t_molto lento_\n");

        boolean prevision = false;
        Date now = new Date();
        Date then = null;

        for (News news: allNewsList) {
            for (String label : labels) {
                double tfidf = nlp.tfIdf( news, allNewsList, label);
                c1.addCurrentValue(tfidf);
                // calcolo pesantuccio
            }
            c1.addCurrentToCluster();
            if (!prevision){
                then = new Date();

                long millisDiff = (then.getTime() - now.getTime())*allNewsList.size();
                int minutes = (int) (millisDiff / 60000 % 60);
                int hours = (int) (millisDiff / 3600000 % 24);

                System.out.println("\tTermine stimato fra circa " + hours + " ore e " + minutes + " minuti");
                prevision = true;
            }
            System.out.print(".");
        }

        l2c1.setCluster(c1);

        System.out.println("\n\tSALVATAGGIO CLUSTERING1");
        mongoCRUD.setCollection("cluster1");
        mongoCRUD.saveCluster(l2c1);
        System.out.println("\tSALVATAGGIO COMPLETATO\n");
    }
    public SimpleKMeans executeCluster(boolean manhattanDistance) {

        System.out.println("\n\tCLUSTERING1\n");

        ConverterUtils.DataSource source = null;
        try {
            source = new ConverterUtils.DataSource("cluster1.arff");
        } catch (Exception e) {
            System.out.println("### FILE ARFF non trovato!");
        }
        Instances data = null;
        try {
            data = source.getDataSet();
        } catch (Exception e) {
            System.out.println("### FILE ARFF non valido!");
        }

        int num = 550;
        SimpleKMeans model;

        do {
            model = new SimpleKMeans();
            try {
                model.setNumClusters(num);
            } catch (Exception e) {
                System.out.println("### NUM CLUSTER non valido!");
            }
            if(manhattanDistance) {
                try {
                    model.setDistanceFunction(new weka.core.ManhattanDistance());
                } catch (Exception e) {
                    System.out.println("### DISTANZA di CLUSTERING non valida!");
                }
            }

            try {
                model.buildClusterer(data);
            } catch (Exception e) {
                System.out.println("### CLUSTERING non valido!");
            }
            System.out.println("# cluster: " + num + "-> RSS: " + model.getSquaredError());
            num = num+5;
        } while (num<600);

        return model;

    }
}
