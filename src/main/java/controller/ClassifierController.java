package controller;

import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import weka.core.Instances;
import java.util.ArrayList;

public class ClassifierController {
    private SimpleKMeans model;
    private int num_clusters;

    private Instances centroidi;
    private ArrayList<Instance> instances_centroidi;
    private int num_values;

    public ClassifierController(SimpleKMeans model){
        this.model = model;
        this.num_clusters = model.getNumClusters();
        this.centroidi = model.getClusterCentroids();
        this.instances_centroidi = this.getInstances(this.centroidi);
        this.num_values = this.centroidi.numAttributes();
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
}
