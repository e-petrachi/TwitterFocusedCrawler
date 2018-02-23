package controller;

import api.kvalid.SilhouetteIndex;
import api.tagme4j.TagMeClient;
import api.tagme4j.TagMeException;
import api.tagme4j.model.Relatedness;
import api.tagme4j.response.RelResponse;
import db.MongoCRUD;
import model.Cluster;
import model.Label2Cluster;
import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClusteringThreeController implements ClusteringController {

    private int sogliaCluster = 0;
    private int numClusters = 10;

    public ClusteringThreeController(int sogliaCluster) {
        this.sogliaCluster = sogliaCluster;
    }

    public SimpleKMeans executeCluster0() {
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

        int num = 38;
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

        } while (num==39);

        return model;
    }

    @Override
    public SimpleKMeans executeCluster() {
        System.out.println("\n\tCLUSTERING3\n");

        ConverterUtils.DataSource source = null;
        try {
            source = new ConverterUtils.DataSource("cluster3.arff");
        } catch (Exception e) {
            System.out.println("### FILE ARFF non trovato!");
        }
        Instances data = null;
        try {
            data = source.getDataSet();
        } catch (Exception e) {
            System.out.println("### FILE ARFF non valido!");
        }

        //int num = this.numClusters;
        int num = 1;
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

            num = num + 1;

        //} while (num <= this.numClusters);
        } while (num <= 100);

        return model;
    }

    public void createMatrix(SimpleKMeans cluster0, Cluster cluster2) {

        System.out.println("\n\tCREAZIONE MATRICE per CLUSTERING3\n");

        int num_cluster0 = cluster0.getNumClusters();
        Instances centroidi = cluster0.getClusterCentroids();
        Instance centroide = null;

        Label2Cluster l2c1 = new Label2Cluster(sogliaCluster);

        System.out.println("\tMATRICE " + cluster2.getEntries().size() + "x" + num_cluster0);

        double[][] matrix3 = new double[cluster2.getEntries().size()][num_cluster0];

        int k = 0;

        for (ArrayList<Double> row : cluster2.getEntries()) {

            if (row.size() > 0) {
                for (int i = 0; i < num_cluster0; i++) {
                    centroide = centroidi.instance(i);
                    int num_values = centroide.numValues();
                    int values_positive = 0;
                    String label = "";
                    ArrayList<Double> values = new ArrayList<>();
                    double result = 0;
                    double maxval = 0;
                    for (int j = 0; j < num_values; j++) {
                        double cluster_val = centroide.value(j);
                        values.add(cluster_val);
                        if (cluster_val > maxval) {
                            label = centroide.attribute(j).name();
                            maxval = cluster_val;
                        }
                        if (cluster_val > 0) {
                            values_positive++;
                        }

                        double news_val = row.get(j);
                        result = result + cluster_val * news_val;

                    }
                    matrix3[k][i] = (Math.floor((result / values_positive) * 1000) / 1000);

                    l2c1.addLabel(label);
                }
                k++;

            }
        }

        l2c1.setLabelsList();

        System.out.println("\tSALVATAGGIO LABEL per CLUSTERING3");
        MongoCRUD mongoCRUD = new MongoCRUD();
        mongoCRUD.setDbName("tfc");
        mongoCRUD.setCollection("label3");
        mongoCRUD.saveLabel(l2c1);
        System.out.println("\tSALVATAGGIO LABEL COMPLETATO\n");

        System.out.println("\n\tSALVATAGGIO MATRICE per CLUSTERING3");
        mongoCRUD.setCollection("cluster3");
        mongoCRUD.saveCluster(matrix3);
        System.out.println("\tSALVATAGGIO COMPLETATO\n");


    }

    public void createMatrix0() {

        MongoCRUD mongoCRUD = new MongoCRUD();
        mongoCRUD.setDbName("tfc");

        ArrayList<Long> labels = mongoCRUD.findAllLabelId(2);

        TagMeClient tagMeClient = new TagMeClient();

        double[][] matrix0 = new double[labels.size()][labels.size()];

        System.out.println("\tCREAZIONE MATRIX per CLUSTERING di SUPPORTO\n");

        boolean prevision = false;
        Date now = new Date();
        Date then = null;

        int i = 0;
        for (Long label_out : labels) {
            int j = 0;
            for (Long label_in : labels) {
                if (i == j) {
                    matrix0[i][j] = 1;
                } if (i > j){
                    matrix0[i][j] = matrix0[j][i];
                } else {
                    RelResponse relResponse = null;
                    try {
                        relResponse = tagMeClient.rel().id(label_in, label_out).execute();
                    } catch (TagMeException e) { System.out.print("."); }

                    List<Relatedness> lr = relResponse.getResult();
                    matrix0[i][j] = (Math.floor(lr.get(0).getRel() * 1000) / 1000);
                }
                j++;
            }
            if (!prevision){
                then = new Date();

                long millisDiff = (then.getTime() - now.getTime())*labels.size()/4;
                int minutes = (int) (millisDiff / 60000 % 60);
                int hours = (int) (millisDiff / 3600000 % 24);

                System.out.println("\tTermine stimato fra circa " + hours + " ore e " + minutes + " minuti");
                prevision = true;
            }
            System.out.print("*");
            i++;
        }
        System.out.println("\n\tSALVATAGGIO CLUSTERING di SUPPORTO");
        mongoCRUD.setCollection("cluster0");
        mongoCRUD.saveCluster(matrix0);
        System.out.println("\tSALVATAGGIO COMPLETATO\n");
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
