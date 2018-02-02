package controller;

import weka.clusterers.SimpleKMeans;

public interface ClusterController {
    
    public SimpleKMeans executeCluster(boolean manhattanDistance);
}
