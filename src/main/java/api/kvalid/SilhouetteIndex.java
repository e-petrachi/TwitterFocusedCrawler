package api.kvalid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import weka.clusterers.SimpleKMeans;
import weka.core.DistanceFunction;
import weka.core.Instance;
import weka.core.Instances;
import weka.clusterers.AbstractClusterer;

public class SilhouetteIndex {

	private HashMap<Integer, ArrayList<Integer>> cluster2elements;

	/** Clusters SI. */
	protected ArrayList<Double> m_clustersSilhouette;

	/** Global SI. */
	protected double m_globalSilhouette;

	public SilhouetteIndex() {
        this.cluster2elements = new HashMap<>();
		m_clustersSilhouette = new ArrayList<>();
		m_globalSilhouette = 0.0;
	}

	public void evaluate(AbstractClusterer clusterer, Instances centroids,
		Instances instances, DistanceFunction distanceFunction) throws Exception {

		if (clusterer == null || instances == null)
			throw new Exception("SilhouetteIndex: the cluster or instances are null!");

		if (clusterer instanceof SimpleKMeans)
		    this.setDimension((SimpleKMeans) clusterer);

		/*
		 * Attributes each instance to your centroid.
		 * 
		 * Note that this is not the right way to do, because there's
		 * one way to get the instances already classified instead
		 * of classify again. As long as I do not know how to accomplish
		 * that, I'll classify again.
		 */
		ArrayList<Instance>[] clusteredInstances =
			(ArrayList<Instance>[]) new ArrayList<?>[centroids.numInstances()];

		/* Initialize. */
		for (int i = 0; i < centroids.numInstances(); i++)
			clusteredInstances[i] = new ArrayList<Instance>();

		/* Fills. */
		for (int i = 0; i < instances.numInstances(); i++)
			clusteredInstances[ clusterer.clusterInstance( instances.instance(i) ) ]
				.add( instances.instance(i) );

		/* For each centroid. */
		for (int i = 0; i < clusteredInstances.length; i++) {
			double centroidSilhouetteIndex = 0.0;

			/* 
			 * Calculate the distance between a given point to the others
			 * within the same centroid.
			 */
			for (int j = 0; j < clusteredInstances[i].size(); j++) {
				double pointSilhouetteIndex = 0.0;
				double meanDistSameC  = 0.0;
				double meanDistOtherC = 0.0;

				/* My reference point. */
				Instance i1 = clusteredInstances[i].get(j);

				/* For each other point, in the same centroid.. */
				for (int k = 0; k < clusteredInstances[i].size(); k++) {
					/* Different point. */
					if (k == j)
						continue;

					/* Gets the distance between p1 and p2. */
					Instance i2 = clusteredInstances[i].get(k);
					meanDistSameC += distanceFunction.distance(i1, i2);
				}

				/* Mean. */
				meanDistSameC /= (clusteredInstances[i].size() - 1);

				/* Get the nearest cluster to the point j. */
				double minDistance = Double.MAX_VALUE;
				int minCentroid = 0;

				for (int k = 0; k < centroids.numInstances(); k++) {
					/* Other clusters, ;-). */
					if (k == i)
						continue;

					/* Distance. */
					Instance i2 = centroids.instance(k);
					double distance = distanceFunction.distance(i1, i2);

					/* Checks if is lower. */
					if (distance < minDistance) {
						minDistance = distance;
						minCentroid = k;
					}
				}

				/*
				 * We already know which cluster is closest, so now we have to go
				 * through this cluster and get the average distance from all points
				 * to point p1.
				 */
				for (int k = 0; k < clusteredInstances[minCentroid].size(); k++) {
					/* Gets the distance between p1 and p2. */
					Instance i2 = clusteredInstances[minCentroid].get(k);

					/* Distance. */
					meanDistOtherC += distanceFunction.distance(i1, i2);
				}

				/* Mean. */
				meanDistOtherC /= (clusteredInstances[minCentroid].size() - 1);

				/* Now, we calculate the silhouette index, \o/. */
				pointSilhouetteIndex = (meanDistOtherC - meanDistSameC) / 
					Math.max( meanDistSameC, meanDistOtherC );

				/* Sum to the centroid silhouette. */
				centroidSilhouetteIndex += pointSilhouetteIndex;
			}


			centroidSilhouetteIndex = centroidSilhouetteIndex / (clusteredInstances[i].size() -1 );
			if (Double.compare(centroidSilhouetteIndex, Double.NaN) == 0)
			    centroidSilhouetteIndex = 0.0;
			m_globalSilhouette += centroidSilhouetteIndex;

			m_clustersSilhouette.add( centroidSilhouetteIndex );
		}

		m_globalSilhouette /= m_clustersSilhouette.size();
	}

    private void setDimension(SimpleKMeans clusterer) {
        try {
            int[] assignments = clusterer.getAssignments();
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
    public int[] getNumElementsForCluster(){

        int[] elements = new int[this.cluster2elements.keySet().size()];
        int index = 0;
        for (Integer cluster: this.cluster2elements.keySet()){
            elements[index] = this.cluster2elements.get(cluster).size();
            index++;
        }
        return elements;
    }

    public ArrayList<Double> getClustersSilhouette() {
		return m_clustersSilhouette;
	}

	public double getGlobalSilhouette() {
		return m_globalSilhouette;
	}

	public String evalSilhouette(double si) {
		String eval = "";

		if (si > 0.70)
			eval = "strong structure!";
		else if (si >  0.50 && si <= 0.70)
			eval = "reasonably structure!";
		else if (si >  0.25 && si <= 0.50)
			eval = "weak structure!";
		else if (si <= 0.25)
			eval = "not substantial structure!";

		return eval;
	}

	 @Override
	 public String toString() {

        int[] elements = this.getNumElementsForCluster();

	 	StringBuffer description = new StringBuffer("");

		/* Clusters. */
		for (int i = 0; i < m_clustersSilhouette.size(); i++) {
			double si = m_clustersSilhouette.get(i);
			description.append("   Cluster " + (i+1) + ": " + String.format(Locale.US, "%.4f", si)
				+ ", size: "+ elements[i] + ", verdict: " + evalSilhouette(si) + "\n");
		}

		description.append("   Mean: " + String.format(Locale.US, "%.4f", m_globalSilhouette)
			+ ", verdict: " + evalSilhouette(m_globalSilhouette));

		return description.toString();
	 }
}
