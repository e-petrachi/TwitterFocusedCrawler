import controller.*;
import model.*;

import weka.clusterers.SimpleKMeans;

import java.util.ArrayList;

public class Main {

    // ritrova circa 15 annotazioni per news
    private static double sogliaMinimaRho = 0.2;

    // settare a false per fare test su piccole quantità di dati
    private static boolean realDB = true;

    // settare a 0 se si vogliono salvare tutte le features
    private static int sogliaCluster1 = 6;
    private static int sogliaCluster2 = 2;
    private static int sogliaCluster3 = 1;

    // settare a true per eseguire i passi relativi
    private static boolean[] learn_step = {
            false ,
            false , false , false , false , false ,
            false , false , false , true , false ,
            false , false , false , false , false
    };

    // settare a true per eseguire i passi relativi
    private static boolean[] class_step = {
            false ,
            false , false , false , false , false ,
            false , false , false , false , false ,
            false , false , false , false , false
    };

    // settare a true per eseguire la distanza di Manhattan per i relativi cluster 0 1 2 3
    private static boolean[] manhattanDistance = { false, false, true, false };

    // settare il num di cluster
    private static int[] numCluster = {1006,590,50,80};

    public static void main(String[] args) throws Exception {
        System.out.println("\n------------------------\tSTART LEARNING\t------------------------\n");

        NewsController newsController = new NewsController(realDB);
        ClusteringOneController clusteringOneController = new ClusteringOneController(realDB, sogliaCluster1);
        ClusteringTwoController clusteringTwoController = new ClusteringTwoController(realDB, sogliaCluster2);
        ClusteringThreeController clusteringThreeController = new ClusteringThreeController(realDB, sogliaCluster3);

        FileController fileController = new FileController(realDB);

        SimpleKMeans cluster0 = null;
        SimpleKMeans cluster1 = null;
        SimpleKMeans cluster2 = null;
        SimpleKMeans cluster3 = null;

        Cluster cluster2_matrix = null;

        if (learn_step[0])
            newsController.newsExtractionAndSave();
        if (learn_step[1])
            newsController.newsCleaning();
        if (learn_step[2])
            // to do remove method because increase too large the dataset
            newsController.fontsExtractionAndSave();
        if (learn_step[3])
            clusteringOneController.createMatrix();
        if (learn_step[4])
            fileController.saveCluster(1);

        // TODO execute
        if (learn_step[5])
            cluster1 = clusteringOneController.executeCluster(manhattanDistance[1]);
        if (learn_step[6])
            newsController.annotationsExtractionAndSave(sogliaMinimaRho);
        if (learn_step[7])
            cluster2_matrix = clusteringTwoController.createMatrix(false);
        if (learn_step[8])
            fileController.saveCluster(2);

        // TODO execute
        if (learn_step[9])
            cluster2 = clusteringTwoController.executeCluster(manhattanDistance[2]);
        if (learn_step[10])
            clusteringThreeController.createMatrix0();
        if (learn_step[11])
            fileController.saveCluster(0);
        if (learn_step[12])
            cluster0 = clusteringThreeController.executeCluster0(manhattanDistance[0]);

        // for this is necessary steps 7 and 12
        if (learn_step[13])
            clusteringThreeController.createMatrix(cluster0, cluster2_matrix);
        if (learn_step[14])
            fileController.saveCluster(3);

        // TODO execute
        if (learn_step[15])
            cluster3 = clusteringThreeController.executeCluster(manhattanDistance[3]);


        System.out.println("\n------------------------\tEND LEARNING\t------------------------\n");
        System.out.println("\n------------------------\tSTART CLASSIFICATION\t------------------------\n");

        if (class_step[0]) {
            ClassifierController classifier = new ClassifierController(cluster3);

            ArrayList<String> topics = classifier.getTopics();

            int i = 1;
            for (String topic : topics) {
                System.out.print("" + i + "|" + topic + " ");
                if (i % 5 == 0)
                    System.out.println();
                i++;
            }
        }

        System.out.println("\n------------------------\tEND CLASSIFICATION\t------------------------\n");
        /*

        TweetExtractor tweetExtractor = new TweetExtractor();
        tweetExtractor.auth();
        List<String> tweets1 = tweetExtractor.searchTweet("usa");
        System.out.println("\n\nTWEET WITH URL: ");
        for (String t : tweets1){
            System.out.println(t);
        }

        List<String> tweets2 = tweetExtractor.searchTweet("trump");
        System.out.println("\n\nTWEET WITH URL: ");
        for (String t : tweets2){
            System.out.println(t);
        }
        */
    }
}
