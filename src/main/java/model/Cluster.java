package model;

import java.util.ArrayList;
import java.util.List;

public class Cluster {

    private ArrayList<Double> currentValues;
    private List<ArrayList<Double>> entries;

    public Cluster(){
        this.currentValues = new ArrayList<>();
        this.entries = new ArrayList<>();
    }

    public List<Double> getCurrentValues() {
        return currentValues;
    }

    public void setCurrentValues(ArrayList<Double> currentValues) {
        this.currentValues = currentValues;
    }

    public void addCurrentValue(double value) {
        this.currentValues.add(new Double(value));
    }

    public List<ArrayList<Double>> getEntries() { return entries; }

    public void setEntries(List<ArrayList<Double>> entries) { this.entries = entries; }

    public void addCurrentToCluster() {
        this.entries.add(this.currentValues);
        this.currentValues = new ArrayList<>();
    }
    public void emptyCurrentValues(){
        this.currentValues = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Cluster{" +
                "entries=" + entries +
                '}';
    }
}
