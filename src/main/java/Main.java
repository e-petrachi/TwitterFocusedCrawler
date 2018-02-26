import api.twitter.TweetElaborator;
import controller.*;
import model.*;

import weka.clusterers.SimpleKMeans;

import java.util.ArrayList;

public class Main {

    // ritrova circa 15 annotazioni per news
    private static double sogliaMinimaLink = 0.2;

    // settare a 0 se si vogliono salvare tutte le features
    private static int sogliaCluster1 = 10;
    private static int sogliaCluster2 = 6;
    private static int sogliaCluster3 = 1;

    // settare a 0 se si vogliono salvare tutte le parole
    private static int sogliaMinimaWords = 10;

    // settare a 0 se si vogliono salvare tutti gli hashtag
    private static int sogliaMinimaHashtag = 100;

    public static void main(String[] args) throws Exception {
        System.out.println("\n------------------------\tSTART LEARNING TOPIC\t------------------------\n");

        NewsController newsController = new NewsController();
        ClusteringOneController clusteringOneController = new ClusteringOneController(sogliaCluster1);
        ClusteringTwoController clusteringTwoController = new ClusteringTwoController(sogliaCluster2);
        ClusteringThreeController clusteringThreeController = new ClusteringThreeController(sogliaCluster3);

        FileController fileController = new FileController();

        SimpleKMeans cluster0 = null;
        SimpleKMeans cluster1 = null;
        SimpleKMeans cluster2 = null;
        SimpleKMeans cluster3 = null;

        Cluster cluster2_matrix = null;

        if (false)
            newsController.newsExtractionAndSave();
        if (false)
            newsController.newsCleaning();
        if (false)
            clusteringOneController.createMatrix();
        if (false)
            fileController.saveCluster(1);

        // TODO execute
        if (false)
            cluster1 = clusteringOneController.executeCluster();

        /*
        * CLUSTERING 2
        */

        if (false)
            newsController.annotationsExtractionAndSave(sogliaMinimaLink);
        if (false)
            newsController.news2AnnCleaning();
        if (false)
            cluster2_matrix = clusteringTwoController.createMatrix(false);
        if (false)
            fileController.saveCluster(2);

        // TODO execute
        if (false)
            cluster2 = clusteringTwoController.executeCluster();

        /*
        * CLUSTERING 3
        */

        if (false)
            clusteringThreeController.createMatrix0();
        if (false)
            fileController.saveCluster(0);
        if (false)
            cluster0 = clusteringThreeController.executeCluster0();

        // for this is necessary steps 7 and 12 and to remove cluster3 on db
        if (false)
            clusteringThreeController.createMatrix(cluster0, cluster2_matrix);
        if (false)
            fileController.saveCluster(3);

        // TODO execute
        if (false)
            cluster3 = clusteringThreeController.executeCluster();


        System.out.println("\n------------------------\tEND LEARNING TOPIC\t------------------------\n");
        System.out.println("\n------------------------\tSTART LEARNING TWEET\t------------------------\n");

        TweetElaborator tweetElaborator = new TweetElaborator();
        ArrayList<String> topics = null;

        if (false) {
            ClassifierController classifier = new ClassifierController(cluster3);

            topics = classifier.getTopics();

            int i = 1;
            for (String topic : topics) {
                System.out.print("" + i + "|" + topic + " ");
                if (i % 5 == 0)
                    System.out.println();
                i++;
            }
        }
        if (false){
            tweetElaborator.elaborateBackground();
            tweetElaborator.createHashtag2vec(sogliaMinimaHashtag);
        }
        if (false){
            String common = tweetElaborator.findCommonTopic(topics);

            System.out.println("\n\tTOPIC TROVATO: " + common);
            tweetElaborator.createBackgroundForTopic(common);
            System.out.println("\n\tBACKGROUND CREATO su " + common);
        }
        if (false){
            tweetElaborator.createWord2weight(sogliaMinimaWords);
        }


        System.out.println("\n------------------------\tEND LEARNING TWEET\t------------------------\n");
    }
}
