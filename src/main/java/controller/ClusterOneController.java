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

public class ClusterOneController implements ClusterController {

    private boolean realDB = true;
    private int sogliaCluster = 0;

    public ClusterOneController(boolean realDB, int sogliaCluster) {
        this.realDB = realDB;
        this.sogliaCluster = sogliaCluster;
    }


    public void createMatrix() {
        MongoCRUD mongoCRUD = new MongoCRUD(this.realDB);
        mongoCRUD.setCollection("news");

        MongoCursor<News> allNews = mongoCRUD.findAllNews("");

        Label2Cluster l2c1 = new Label2Cluster(sogliaCluster);
        System.out.println("\tCREAZIONE LABEL per CLUSTER1");
        for (News news: allNews){
            String[] args = news.getTextWithStemmer().split(" ");
            for (String key: args) {
                l2c1.addLabel(key);
            }
            System.out.print(".");
        }

        l2c1.setLabelsList();

        try {
            allNews.close();
        } catch (IOException e) { }

        System.out.println("\n\tSALVATAGGIO LABEL per CLUSTER1");
        mongoCRUD.setCollection("label1");
        mongoCRUD.saveLabel(l2c1);
        System.out.println("\tSALVATAGGIO LABEL COMPLETATO\n");


        NLPExtractor nlp = new NLPExtractor();
        Cluster c1 = new Cluster();
        ArrayList<String> labels = l2c1.getLabelsList();

        System.out.println("\tCREAZIONE MATRIX per CLUSTER1\n");
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

        System.out.println("\tCALCOLO TF-IDF per MATRIX\n");
        for (News news: allNewsList) {
            for (String label : labels) {
                double tfidf = nlp.tfIdf( news, allNewsList, label);
                c1.addCurrentValue(tfidf);
            }
            c1.addCurrentToCluster();
            System.out.print(".");
        }

        l2c1.setCluster(c1);
        System.out.println("\n\n" + l2c1.getCluster().toString());

        System.out.println("\n\tSALVATAGGIO CLUSTER1");
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

        int num = 1;
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
            System.out.println("#Cluster " + num + "-> sum of squared errors : " + model.getSquaredError());
            num = num+1;
        } while (model.getSquaredError() > 0);

        return model;

    }
}
