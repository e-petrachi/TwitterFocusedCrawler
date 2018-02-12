package controller;

import api.tagme4j.model.Annotation;
import db.MongoCRUD;
import model.Cluster;
import model.Label2Cluster;
import model.News2Annotations;
import org.jongo.MongoCursor;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClusteringTwoController implements ClusteringController {

    private boolean realDB = true;
    private int sogliaCluster = 0;

    public ClusteringTwoController(boolean realDB, int sogliaCluster) {
        this.realDB = realDB;
        this.sogliaCluster = sogliaCluster;
    }

    public Cluster createMatrix(boolean save){

        MongoCRUD mongoCRUD = new MongoCRUD(realDB);
        mongoCRUD.setCollection("news2annotations");

        MongoCursor<News2Annotations> allNews2Annotation = mongoCRUD.findAllNews2Annotations("");

        Label2Cluster l2c2 = new Label2Cluster(sogliaCluster);
        System.out.println("\tCREAZIONE LABEL per CLUSTERING2");
        for (News2Annotations news: allNews2Annotation){
            List<Annotation> args = news.getAnnotations();

            for (Annotation key: args) {
                l2c2.addLabel(key);
            }
            System.out.print(".");
        }

        l2c2.setLabelsList();

        try {
            allNews2Annotation.close();
        } catch (IOException e) { }

        if (save) {
            System.out.println("\n\tSALVATAGGIO LABEL per CLUSTERING2");
            mongoCRUD.setCollection("label2");
            mongoCRUD.saveLabel(l2c2);
            System.out.println("\tSALVATAGGIO LABEL COMPLETATO\n");
        }

        Cluster c2 = new Cluster();
        ArrayList<String> labels = l2c2.getLabelsList();

        System.out.println("\tCREAZIONE MATRICE per CLUSTERING2\n");
        mongoCRUD = new MongoCRUD(realDB);

        mongoCRUD.setCollection("news2annotations");
        allNews2Annotation = mongoCRUD.findAllNews2Annotations("");

        ArrayList<News2Annotations> allNewsList = new ArrayList<>();
        while (allNews2Annotation.hasNext()){
            allNewsList.add(allNews2Annotation.next());
        }

        try {
            allNews2Annotation.close();
        } catch (IOException e) { }

        System.out.println("\tCALCOLO valori per la MATRICE\n");
        for (News2Annotations news: allNewsList) {
            for (String annotation : labels) {
                for (Annotation annotationExist : news.getAnnotations()) {
                    if (annotation.equalsIgnoreCase(annotationExist.getTitle())){
                        c2.addCurrentValue( (Math.floor(annotationExist.getLinkProbability() * 1000) / 1000) );
                    } else {
                        c2.addCurrentValue(0.0);
                    }
                }
            }
            c2.addCurrentToCluster();
            System.out.print(".");
        }

        l2c2.setCluster(c2);

        if (save) {
            System.out.println("\n\tSALVATAGGIO CLUSTERING2");
            mongoCRUD.setCollection("cluster2");
            mongoCRUD.saveCluster(l2c2);
            System.out.println("\tSALVATAGGIO COMPLETATO\n");
        }
        return c2;
    }

    public SimpleKMeans executeCluster(boolean manhattanDistance) {
        System.out.println("\n\tCLUSTERING2\n");

        ConverterUtils.DataSource source = null;
        try {
            source = new ConverterUtils.DataSource("cluster2.arff");
        } catch (Exception e) {
            System.out.println("### FILE ARFF non trovato!");
        }
        Instances data = null;
        try {
            data = source.getDataSet();
        } catch (Exception e) {
            System.out.println("### FILE ARFF non valido!");
        }

        int num = 90;
        SimpleKMeans model;
        ArrayList<Double> stats;

        do {
            model = new SimpleKMeans();
            model.setPreserveInstancesOrder(true);
            model.setDisplayStdDevs(true);

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
            System.out.println("\n\n\t\t" + num + " cluster -> RSS: " + model.getSquaredError());

            stats = this.getStatsIntraClusters(model);

            System.out.print("\t\tvarianza_interna_min: " + stats.get(0) + " varianza_interna_max: " + stats.get(1) + " dimensione_cluster_min: " + stats.get(2) + " dimensione_cluster_max: " + stats.get(3) + "\n");

            int index = 0;
            for (Double stat : stats){
                if(index > 3){
                    System.out.print("cl" + (index-3) + ": " + stat.intValue() + " elem.\t");
                }
                if (index != 0 && index % 10 == 0)
                    System.out.println();
                index++;
            }

            num = num+2;
        } while (model.getSquaredError() > 10);

        return model;
    }

    public ArrayList<Double> getStatsIntraClusters(SimpleKMeans model){
        ClassifierController cc = new ClassifierController(model);
        double[] var = cc.getSumInternalVariance();
        int min[] = cc.getMinMaxElementsOfClusters();
        int elements[] = cc.getNumElementsForCluster();

        ArrayList<Double> stats = new ArrayList<>();
        stats.add(var[0]);
        stats.add(var[1]);
        stats.add((double) min[0]);
        stats.add((double) min[1]);
        for (int i :elements){
            stats.add((double) i);
        }

        return stats;
    }
}
