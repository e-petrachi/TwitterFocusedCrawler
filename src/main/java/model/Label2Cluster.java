package model;

import api.tagme4j.model.Annotation;
import java.util.ArrayList;
import java.util.TreeMap;

public class Label2Cluster {

    private Cluster cluster;
    private TreeMap<Annotation, Integer> ann2occ;
    private TreeMap<String, Integer> label2occ;
    private ArrayList<String> labelsList;
    private ArrayList<Long> idsList;
    private int soglia;

    public Label2Cluster(){
        this.ann2occ = new TreeMap<>();
        this.label2occ = new TreeMap<>();
        this.soglia = 0;
    }

    public Label2Cluster(int soglia){
        this.ann2occ = new TreeMap<>();
        this.label2occ = new TreeMap<>();
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

    public ArrayList<String> getLabelsList() {
        return labelsList;
    }

    public ArrayList<Long> getIdsList() {
        return idsList;
    }

    public void setLabelsList() {
        if (ann2occ.isEmpty()){
            this.labelsList = new ArrayList<>();
            for (String k : this.label2occ.keySet()) {
                if (this.label2occ.get(k) > soglia) {
                    this.labelsList.add(k);
                }
            }
        } else {
            this.labelsList = new ArrayList<>();
            for (Annotation k : this.ann2occ.keySet()) {
                if (this.ann2occ.get(k) > soglia) {
                    this.labelsList.add(k.getTitle());
                }
            }
            this.idsList = new ArrayList<>();
            for (Annotation k : this.ann2occ.keySet()) {
                if (this.ann2occ.get(k) > soglia) {
                    this.idsList.add(k.getId());
                }
            }
        }
    }

    public void addLabel(Annotation label) {
        if (label.getTitle().equals(" ") || label.getTitle().equals("  ") || label.getTitle().isEmpty())
            return;
        if (this.ann2occ.containsKey(label)){
            Integer occorrenze = new Integer(this.ann2occ.get(label));

            this.ann2occ.remove(label);
            this.ann2occ.put(label,occorrenze + 1);
        }
        else {
            this.ann2occ.put(label,1);
        }
    }
    public void addLabel(String label) {
        if (label.equals(" ") || label.equals("  ") || label.isEmpty())
            return;
        if (this.label2occ.containsKey(label)){
            Integer occorrenze = new Integer(this.label2occ.get(label));

            this.label2occ.remove(label);
            this.label2occ.put(label,occorrenze + 1);
        }
        else {
            this.label2occ.put(label,1);
        }
    }


    @Override
    public String toString() {
        return "Label2Cluster{ " + ann2occ + " }";
    }
}
