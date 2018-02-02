import api.news.NLPExtractor;
import api.news.NewsExtractor;
import api.tagme4j.TagMeClient;
import api.tagme4j.TagMeException;
import api.tagme4j.model.Annotation;
import api.tagme4j.model.Relatedness;
import api.tagme4j.response.RelResponse;
import api.tagme4j.response.TagResponse;
import controller.*;
import db.MongoCRUD;
import model.*;
import org.jongo.MongoCursor;

import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class Main {

    // ritrova circa 15 annotazioni per news
    private static double sogliaMinimaRho = 0.2;

    // settare a false per fare test su piccole quantità di dati
    private static boolean realDB = false;

    // settare a 0 se si vogliono salvare tutte le features
    private static int sogliaCluster1 = 0;
    private static int sogliaCluster2 = 2;
    private static int sogliaCluster3 = 1;

    // settare a true per eseguire i passi relativi
    private static boolean[] step = {
            false,
            false, false, false, false, false,
            false, true , false, true , false,
            false, true , true , false, false
    };

    // settare a true per eseguire la distanza di Manhattan per i relativi cluster 0 1 2 3
    private static boolean[] manhattanDistance = { false, false, true, false };

    // settare il num di cluster
    private static int[] numCluster = {14,12,6,0};

    public static void main(String[] args) throws Exception {
        //System.out.print((char)27 + "[30m");
        //System.out.print((char)27 + "[35m");
        //System.out.print((char)27 + "[32m");
        System.out.println("\n------------------------\tSTART\t------------------------\n");

        NewsController newsController = new NewsController(realDB);
        ClusterOneController clusterOneController = new ClusterOneController(realDB, sogliaCluster1);
        ClusterTwoController clusterTwoController = new ClusterTwoController(realDB, sogliaCluster2);
        ClusterThreeController clusterThreeController = new ClusterThreeController(realDB, sogliaCluster3);

        FileController fileController = new FileController(realDB);

        SimpleKMeans cluster0 = null;
        SimpleKMeans cluster1 = null;
        SimpleKMeans cluster2 = null;
        SimpleKMeans cluster3 = null;

        Cluster cluster2_matrix = null;

        if (step[0])
            newsController.newsExtractionAndSave();
        if (step[1])
            newsController.newsCleaning();
        if (step[2])
            newsController.fontsExtractionAndSave();
        if (step[3])
            clusterOneController.createMatrix();
        if (step[4])
            fileController.saveCluster(1);

        // TODO execute
        if (step[5])
            cluster1 = clusterOneController.executeCluster(manhattanDistance[1]);
        if (step[6])
            newsController.annotationsExtractionAndSave(sogliaMinimaRho);

        // TODO execute
        if (step[7])
            cluster2_matrix = clusterTwoController.createMatrix(false);
        if (step[8])
            fileController.saveCluster(2);

        // TODO execute
        if (step[9])
            cluster2 = clusterTwoController.executeCluster(manhattanDistance[2]);
        if (step[10])
            clusterThreeController.createMatrix0();
        if (step[11])
            fileController.saveCluster(0);

        // TODO execute
        if (step[12])
            cluster0 = clusterThreeController.executeCluster0(realDB);

        if (step[13])
            clusterThreeController.createMatrix(cluster0, cluster2_matrix);




        if (step[14])
            fileController.saveCluster(3);
        if (step[15])
            clusterThreeController.executeCluster();


        System.out.println("\n------------------------\tEND\t\t------------------------\n");



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
