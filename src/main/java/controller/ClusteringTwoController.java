package controller;

import api.kvalid.SilhouetteIndex;
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

    private int sogliaCluster = 0;

    public ClusteringTwoController(int sogliaCluster) {
        this.sogliaCluster = sogliaCluster;
    }

    public Cluster createMatrix(boolean save){

        MongoCRUD mongoCRUD = new MongoCRUD();
        mongoCRUD.setDbName("tfc");
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
        mongoCRUD = new MongoCRUD();
        mongoCRUD.setDbName("tfc");

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
            double sum = 0;
            for (String annotation : labels) {
                boolean exist = false;
                for (Annotation annotationExist : news.getAnnotations()) {
                    if (annotation.equalsIgnoreCase(annotationExist.getTitle()) && !exist){
                        c2.addCurrentValue( (Math.floor(annotationExist.getLinkProbability() * 1000) / 1000) );
                        sum += (Math.floor(annotationExist.getLinkProbability() * 1000) / 1000);
                        exist = true;
                    }
                }
                if (!exist){
                    c2.addCurrentValue(0.0);
                }
            }
            if (sum > 0.0) {
                c2.addCurrentToCluster();
                System.out.print(".");
            } else {
                c2.emptyCurrentValues();
                System.out.print("e");
            }
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

    public SimpleKMeans executeCluster() {
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

        int num = 5;
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
            try {
                model.buildClusterer(data);
            } catch (Exception e) {
                System.out.println("### CLUSTERING non valido!");
            }
            System.out.println("\n\n\t\t" + num + " cluster -> RSS: " + model.getSquaredError());

            this.getStatsClusters(model, data);

            num = num+1;
        } while (model.getSquaredError() > 10);

        return model;
    }

    public void getStatsClusters(SimpleKMeans model, Instances data){
        SilhouetteIndex si = new SilhouetteIndex();
        try {
            si.evaluate(model,model.getClusterCentroids(),data, model.getDistanceFunction());
        } catch (Exception e) {
            System.out.println("e");
        }
        System.out.println(si.toString() + "\n");
    }
}
