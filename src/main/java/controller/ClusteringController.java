package controller;

import weka.clusterers.SimpleKMeans;

public interface ClusteringController {

    SimpleKMeans executeCluster();
}
