package controller;

import api.kvalid.SilhouetteIndex;
import weka.clusterers.AbstractClusterer;
import weka.clusterers.SimpleKMeans;
import weka.core.Attribute;
import weka.core.DistanceFunction;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.HashMap;

public class ClassifierController {
    private SimpleKMeans model;
    private int num_clusters;

    private Instances centroidi;
    private ArrayList<Instance> instances_centroidi;

    private Instances stdDevs;
    private ArrayList<Instance> instances_stdDevs;

    private HashMap<Integer, ArrayList<Integer>> cluster2elements;

    private int num_values;

    public ClassifierController(SimpleKMeans model){
        this.model = model;
        this.num_clusters = model.getNumClusters();
        this.centroidi = model.getClusterCentroids();
        this.instances_centroidi = this.getInstances(this.centroidi);

        this.stdDevs = model.getClusterStandardDevs();
        this.instances_stdDevs = this.getInstances(this.stdDevs);

        this.num_values = this.centroidi.numAttributes();

        this.cluster2elements = new HashMap<>();

        try {
            int[] assignments = model.getAssignments();
            int i = 0;
            for (int cluster: assignments){
                if (this.cluster2elements.containsKey(cluster)) {
                    ArrayList<Integer> elements = this.cluster2elements.get(cluster);
                    elements.add(i);
                } else{
                    ArrayList<Integer> elements = new ArrayList<>();
                    elements.add(i);
                    this.cluster2elements.put(cluster, elements);
                }
                i++;
            }
        } catch (Exception e) { }

    }

    private ArrayList<Instance> getInstances(Instances inst){
        ArrayList<Instance> instances = new ArrayList<>();
        for (int i=0;i<this.num_clusters;i++){
            instances.add(inst.instance(i));
        }
        return instances;
    }

    public ArrayList<String> getTopics(){
        ArrayList<String> topics = new ArrayList<>();

        for (int i=0;i<this.num_clusters;i++){
            Instance cluster = this.centroidi.instance(i);
            double value = 0;
            int index = 0;
            for (int j=0; j<this.num_values; j++) {
                if(cluster.value(j) > value){
                    value = cluster.value(j);
                    index = j;
                }
            }
            topics.add(cluster.attribute(index).name());
        }
        return topics;
    }
    public int[] getMinMaxElementsOfClusters(){
        int min = Integer.MAX_VALUE;
        int max = 0;

        for (Integer cluster: this.cluster2elements.keySet()){

            if (min > this.cluster2elements.get(cluster).size())
                min = this.cluster2elements.get(cluster).size();
            if (max < this.cluster2elements.get(cluster).size())
                max = this.cluster2elements.get(cluster).size();
        }
        return new int[]{min, max};
    }

    public int[] getNumElementsForCluster(){
        int[] elements = new int[this.cluster2elements.keySet().size()];
        int index = 0;
        for (Integer cluster: this.cluster2elements.keySet()){
            elements[index] = this.cluster2elements.get(cluster).size();
            /*
            if (elements[index] == 1){
                System.out.println("\nIndice" + this.cluster2elements.get(cluster).get(0) + "\n");
            }
            */
            index++;
        }
        return elements;
    }

    public double[] getSumInternalVariance(){

        double max = 0;
        double min = Double.MAX_VALUE;
        for (Instance internal :this.instances_stdDevs) {

            double sum_internal = 0;
            for (int i = 0; i <this.num_values; i++) {
                sum_internal += (internal.value(i)*internal.value(i));
            }

            if (min > sum_internal)
                min = sum_internal;
            if (max < sum_internal)
                max = sum_internal;

        }

        double result[] = new double[2];

        result[0] = (Math.floor(min * 100000) / 100000);
        result[1] = (Math.floor(max * 100000) / 100000);

        return result;
    }

    public void evaluate(Instances dataset) throws Exception {
        SilhouetteIndex si = new SilhouetteIndex();
        si.evaluate(this.model,this.centroidi,dataset, model.getDistanceFunction());
        System.out.println(si.toString() + "\n");
    }
}
