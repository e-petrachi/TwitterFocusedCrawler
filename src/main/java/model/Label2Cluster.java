package model;

import java.util.ArrayList;
import java.util.TreeMap;

public class Label2Cluster {

    private Cluster cluster;
    private TreeMap<String, Integer> labels;
    private ArrayList<String> labelsList;
    private int soglia;

    public Label2Cluster(){
        this.labels = new TreeMap<>();
        this.soglia = 0;
    }

    public Label2Cluster(int soglia){
        this.labels = new TreeMap<>();
        this.soglia = soglia;
    }

    public int getSoglia() { return soglia; }

    public void setSoglia(int soglia) { this.soglia = soglia; }

    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    public TreeMap<String, Integer> getLabels() {
        return labels;
    }

    public void setLabels(TreeMap<String, Integer> labels) {
        this.labels = labels;
    }

    public ArrayList<String> getLabelsList() {
        return labelsList;
    }

    public void setLabelsList() {
        if (this.soglia == 0)
            this.labelsList = new ArrayList<>(this.labels.keySet());
        else{
            this.labelsList = new ArrayList<>();
            for (String k: this.labels.keySet()) {
                if (this.labels.get(k) > soglia){
                    this.labelsList.add(k);
                }
            }
        }
    }

    public void addLabel(String label) {
        if (label.equals(" ") || label.equals("") || label.equals("  ") || label.isEmpty())
            return;
        if (this.labels.containsKey(label)){
            Integer occorrenze = new Integer(this.labels.get(label));

            this.labels.remove(label);

            this.labels.put(label,occorrenze + 1);
        }
        else {
            this.labels.put(label,1);
        }
    }

    @Override
    public String toString() {
        return "Label2Cluster{ " + labels + " }";
    }
}
