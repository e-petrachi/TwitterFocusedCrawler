package controller;

import api.tagme4j.TagMeClient;
import api.tagme4j.TagMeException;
import api.tagme4j.model.Relatedness;
import api.tagme4j.response.RelResponse;
import db.MongoCRUD;
import model.Cluster;
import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.util.ArrayList;
import java.util.List;

public class ClusterThreeController implements ClusterController {

    private boolean realDB = true;
    private int sogliaCluster = 0;

    public ClusterThreeController(boolean realDB, int sogliaCluster) {
        this.realDB = realDB;
        this.sogliaCluster = sogliaCluster;
    }


    @Override
    public SimpleKMeans executeCluster(boolean manhattanDistance) {
        return null;
    }

    public void createMatrix(SimpleKMeans cluster0, Cluster cluster2) {

        System.out.println("\n\tCREAZIONE MATRICE per CLUSTER3\n");

        int num_cluster0 = cluster0.getNumClusters();
        Instances centroidi = cluster0.getClusterCentroids();
        Instance centroide = null;


        for (ArrayList<Double> row : cluster2.getEntries()) {

            for (int i = 0; i < num_cluster0; i++) {
                centroide = centroidi.instance(i);
                int value = centroide.numValues();

                ArrayList<String> labels = new ArrayList<>();
                ArrayList<Double> values = new ArrayList<>();
                double result = 0;
                for (int j = 0; j < value; j++) {
                    double val = Math.floor(centroide.value(j) * 1000) / 1000;
                    values.add(val);
                    if (val > 0.5) {
                        labels.add(centroide.attribute(j).name());
                    }
                    double vval = row.get(j);
                    result = result + Math.floor((val*vval) * 1000) / 1000;

                }
                double rr = Math.floor((result/value) * 1000) / 1000;
                System.out.print(rr + " ");
            }
            System.out.println();
        }

    }
    public void executeCluster() {}

    public void createMatrix0() {

        MongoCRUD mongoCRUD = new MongoCRUD(realDB);
        ArrayList<Long> labels = mongoCRUD.findAllLabelId(2);

        TagMeClient tagMeClient = new TagMeClient();

        double[][] matrix3 = new double[labels.size()][labels.size()];

        System.out.println("\tCREAZIONE MATRIX per CLUSTER di SUPPORTO\n");

        int i = 0;
        for (Long label_out : labels) {
            int j = 0;
            for (Long label_in : labels) {
                if (i == j) {
                    matrix3[i][j] = 1;
                } else {
                    RelResponse relResponse = null;
                    try {
                        relResponse = tagMeClient.rel().id(label_in, label_out).execute();
                    } catch (TagMeException e) {
                        System.out.print(".");
                    }
                    List<Relatedness> lr = relResponse.getResult();
                    matrix3[i][j] = (Math.floor(lr.get(0).getRel() * 1000) / 1000);
                }
                j++;
            }
            System.out.print(".");
            i++;
        }
        System.out.println("\n\tSALVATAGGIO CLUSTER di SUPPORTO");
        mongoCRUD.setCollection("cluster0");
        mongoCRUD.saveCluster(matrix3);
        System.out.println("\tSALVATAGGIO COMPLETATO\n");
    }

    public SimpleKMeans executeCluster0(boolean manhattanDistance) {
        System.out.println("\n\tCLUSTERING0\n");

        ConverterUtils.DataSource source = null;
        try {
            source = new ConverterUtils.DataSource("cluster0.arff");
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
        double error_pre = 0;
        double error = 0;
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

            if (num > 1){
                error_pre = error - model.getSquaredError();
                error = model.getSquaredError();
            } else {
                error = model.getSquaredError();
                error_pre = model.getSquaredError();
            }

            num = num+1;

        } while (error_pre > 2);

        return model;
    }
}
