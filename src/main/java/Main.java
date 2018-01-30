import api.news.NLPExtractor;
import api.news.NewsExtractor;
import api.tagme4j.TagMeClient;
import api.tagme4j.TagMeException;
import api.tagme4j.model.Annotation;
import api.tagme4j.model.Relatedness;
import api.tagme4j.request.RelRequest;
import api.tagme4j.request.TagRequest;
import api.tagme4j.response.RelResponse;
import api.tagme4j.response.TagResponse;
import db.MongoCRUD;
import model.*;
import org.jongo.MongoCursor;

import weka.clusterers.SimpleKMeans;
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
    private static boolean[] passoAlgoritmo = {
            false,
            false,false,false,false,false,
            true,false,false,false,false,
            false,false,false,false,false
    };

    private static boolean[] manhattanDistance = { false, true, false };


    public static void main(String[] args) throws Exception {
        //System.out.print((char)27 + "[30m");
        //System.out.print((char)27 + "[35m");
        //System.out.print((char)27 + "[32m");
        System.out.println("\n------------------------\tSTART\t------------------------\n");

        if (passoAlgoritmo[0])
            newsExtractionAndSave();
        if (passoAlgoritmo[1])
            newsCleaning();
        if (passoAlgoritmo[2])
            fontsExtractionAndSave();
        if (passoAlgoritmo[3])
            createMatrixForClustering1();
        if (passoAlgoritmo[4])
            annotationsExtractionAndSave();
        if (passoAlgoritmo[5])
            createMatrixForClustering2();
        if (passoAlgoritmo[6])
            calculatingRelativityForEntitiesAndSave();

        if (passoAlgoritmo[7])
            createMatrixForClustering3();
        if (passoAlgoritmo[8])
            exportAllClusterCsvArff();
        if (passoAlgoritmo[9])
            executeCluster1();
        if (passoAlgoritmo[10])
            executeCluster2();
        if (passoAlgoritmo[11])
            executeCluster3();

        System.out.println("\n------------------------\tEND\t\t------------------------\n");
    }


    private static void newsExtractionAndSave() {
        System.out.println("\n\tESTRAZIONE TOP NEWS");
        NewsExtractor extractor = new NewsExtractor();
        Articles top = extractor.getTopNews();

        MongoCRUD mongoCRUD = new MongoCRUD(realDB);
        mongoCRUD.setCollection("news");

        System.out.println("\n\tSALVATAGGIO TOP NEWS");
        for (News nn : top.getArticle()) {
            mongoCRUD.saveNews(nn);
            System.out.print(".");
        }
        System.out.println("\tSALVATAGGIO TOP NEWS COMPLETATO\n");
    }

    private static void fontsExtractionAndSave() {
        MongoCRUD mongoCRUD = new MongoCRUD(realDB);
        mongoCRUD.setCollection("news");

        MongoCursor<News> top = mongoCRUD.findAllNews("");

        System.out.println("\n\tESTRAZIONE TOP SOURCES");
        TreeSet<String> sources = new TreeSet<>();
        for (News news : top) {
            String s = news.getSource().getId();
            sources.add(s);
        }

        System.out.println("\n\tESTRAZIONE NEWS extra");
        NewsExtractor extractor = new NewsExtractor();
        for (String source : sources){
            Articles all = extractor.getEverything(source);

            if (all != null) {
                System.out.println("\n\tSALVATAGGIO NEWS extra");
                for (News nn: all.getArticle()) {
                    mongoCRUD.saveNews(nn);
                    System.out.print(".");
                }
            }
        }
        System.out.println("\tSALVATAGGIO NEWS extra COMPLETATO\n");

    }

    private static void newsCleaning() {
        System.out.println("\n\tPULIZIA NEWS");

        MongoCRUD mongoCRUD = new MongoCRUD(realDB);
        mongoCRUD.setCollection("news");
        mongoCRUD.cleanNews();

        System.out.println("\tPULIZIA NEWS COMPLETATA\n");
    }

    private static void annotationsExtractionAndSave() {
        MongoCRUD mongoCRUD = new MongoCRUD(realDB);
        mongoCRUD.setCollection("news");
        TagMeClient tagMeClient = new TagMeClient();
        TagResponse tagResponse = null;

        System.out.println("\n\tESTRAZIONE ANNOTATIONS e SALVATAGGIO");

        ArrayList<News> all = new ArrayList<>();
        MongoCursor<News> allNews = mongoCRUD.findAllNews("");
        while (allNews.hasNext()){
            all.add(allNews.next());
        }
        System.out.println("\tESTRATTE #NEWS: " + all.size());
        try {
            allNews.close();
        } catch (IOException e) { }


        int tot = 0;

        System.out.println("\tESTRAZIONE e SALVATAGGIO ANNOTAZIONI");

        for (News news : all){

            mongoCRUD.setCollection("news2annotations");

            boolean found = true;
            try {
                tagResponse = tagMeClient.tag().includeCategories(true).text(news.getText()).execute();
            } catch (TagMeException e) {
                System.out.print(".");
                found = false;
            }
            if (found) {
                List<Annotation> annotations = tagResponse.getAnnotations();
                List<Annotation> annotations_good = new ArrayList<>();

                for (Annotation a : annotations) {
                    if (a.getRho() > sogliaMinimaRho)
                        annotations_good.add(a);
                }

                News2Annotations n2a = new News2Annotations(news, annotations_good);
                System.out.print("*");
                mongoCRUD.saveNews2Annotations(n2a);
                tot++;
            }
        }

        System.out.println("\tSALVATAGGIO NEWS2ANNOTATIONS COMPLETATO: salvate " + tot + " news su " + all.size() + ".\n");

    }

    private static void createMatrixForClustering1() {
        MongoCRUD mongoCRUD = new MongoCRUD(realDB);
        mongoCRUD.setCollection("news");

        MongoCursor<News> allNews = mongoCRUD.findAllNews("");

        Label2Cluster l2c1 = new Label2Cluster(sogliaCluster1);
        System.out.println("\tCREAZIONE LABEL per CLUSTER1");
        for (News news: allNews){
            String[] args = news.getTextWithStemmer().split(" ");
            for (String key: args) {
                l2c1.addLabel(key);
            }
            System.out.print(".");
        }

        l2c1.setLabelsList();

        try {
            allNews.close();
        } catch (IOException e) { }

        System.out.println("\n\tSALVATAGGIO LABEL per CLUSTER1");
        mongoCRUD.setCollection("label1");
        mongoCRUD.saveLabel(l2c1);
        System.out.println("\tSALVATAGGIO LABEL COMPLETATO\n");


        NLPExtractor nlp = new NLPExtractor();
        Cluster c1 = new Cluster();
        ArrayList<String> labels = l2c1.getLabelsList();

        System.out.println("\tCREAZIONE MATRIX per CLUSTER1\n");
        mongoCRUD = new MongoCRUD(realDB);

        mongoCRUD.setCollection("news");
        allNews = mongoCRUD.findAllNews("");

        ArrayList<News> allNewsList = new ArrayList<>();
        while (allNews.hasNext()){
            allNewsList.add(allNews.next());
        }

        try {
            allNews.close();
        } catch (IOException e) { }

        System.out.println("\tCALCOLO TF-IDF per MATRIX\n");
        for (News news: allNewsList) {
            for (String label : labels) {
                double tfidf = nlp.tfIdf( news, allNewsList, label);
                c1.addCurrentValue(tfidf);
            }
            c1.addCurrentToCluster();
            System.out.print(".");
        }

        l2c1.setCluster(c1);
        System.out.println("\n\n" + l2c1.getCluster().toString());

        System.out.println("\n\tSALVATAGGIO CLUSTER1");
        mongoCRUD.setCollection("cluster1");
        mongoCRUD.saveCluster(l2c1);
        System.out.println("\tSALVATAGGIO COMPLETATO\n");
    }

    private static void executeCluster1() {

        System.out.println("\n\tCLUSTERING1\n");

        DataSource source = null;
        try {
            source = new DataSource("cluster1.arff");
        } catch (Exception e) {
            System.out.println("### FILE ARFF non trovato!");
        }
        Instances data = null;
        try {
            data = source.getDataSet();
        } catch (Exception e) {
            System.out.println("### FILE ARFF non valido!");
        }

        int num = 1;
        SimpleKMeans model;

        do {
            model = new SimpleKMeans();
            try {
                model.setNumClusters(num);
            } catch (Exception e) {
                System.out.println("### NUM CLUSTER non valido!");
            }
            if(manhattanDistance[0]) {
                try {
                    model.setDistanceFunction(new weka.core.ManhattanDistance());
                } catch (Exception e) {
                    System.out.println("### DISTANZA di CLUSTERING non valida!");
                }
            }

            try {
                model.buildClusterer(data);
            } catch (Exception e) {
                System.out.println("### CLUSTERING non valido!");
            }
            System.out.println("#Cluster " + num + "-> sum of squared errors : " + model.getSquaredError());
            num = num+1;
        } while (model.getSquaredError() > 0);

    }

    private static void createMatrixForClustering2(){

        MongoCRUD mongoCRUD = new MongoCRUD(realDB);
        mongoCRUD.setCollection("news2annotations");

        MongoCursor<News2Annotations> allNews2Annotation = mongoCRUD.findAllNews2Annotations("");

        Label2Cluster l2c2 = new Label2Cluster(sogliaCluster2);
        System.out.println("\tCREAZIONE LABEL per CLUSTER2");
        for (News2Annotations news: allNews2Annotation){
            List<String> args = news.getAnnotationsTitleAsString();
            //List<String> args = news.getAnnotationsSpotAsString();
            for (String key: args) {
                l2c2.addLabel(key);
            }
            System.out.print(".");
        }

        l2c2.setLabelsList();

        try {
            allNews2Annotation.close();
        } catch (IOException e) { }

        System.out.println("\n\tSALVATAGGIO LABEL per CLUSTER2");
        mongoCRUD.setCollection("label2");
        mongoCRUD.saveLabel(l2c2);
        System.out.println("\tSALVATAGGIO LABEL COMPLETATO\n");


        Cluster c2 = new Cluster();
        ArrayList<String> labels = l2c2.getLabelsList();

        System.out.println("\tCREAZIONE MATRIX per CLUSTER2\n");
        mongoCRUD = new MongoCRUD(realDB);

        mongoCRUD.setCollection("news2annotations");
        allNews2Annotation = mongoCRUD.findAllNews2Annotations("");

        ArrayList<News2Annotations> allNewsList = new ArrayList<>();
        while (allNews2Annotation.hasNext()){
            allNewsList.add(allNews2Annotation.next());
        }

        try {
            allNews2Annotation.close();
        } catch (IOException e) { }

        System.out.println("\tCALCOLO valori per MATRIX\n");
        for (News2Annotations news: allNewsList) {
            for (String annotation : labels) {
                for (Annotation annotationExist : news.getAnnotations()) {
                    if (annotation.equalsIgnoreCase(annotationExist.getTitle())){
                        c2.addCurrentValue( (Math.floor(annotationExist.getLinkProbability() * 1000) / 1000) );
                    } else {
                        c2.addCurrentValue(0.0);
                    }
                }
            }
            c2.addCurrentToCluster();
            System.out.print(".");
        }

        l2c2.setCluster(c2);

        System.out.println("\n\tSALVATAGGIO CLUSTER2");
        mongoCRUD.setCollection("cluster2");
        mongoCRUD.saveCluster(l2c2);
        System.out.println("\tSALVATAGGIO COMPLETATO\n");
    }
    private static void executeCluster2() {
        System.out.println("\n\tCLUSTERING2\n");

        DataSource source = null;
        try {
            source = new DataSource("cluster2.arff");
        } catch (Exception e) {
            System.out.println("### FILE ARFF non trovato!");
        }
        Instances data = null;
        try {
            data = source.getDataSet();
        } catch (Exception e) {
            System.out.println("### FILE ARFF non valido!");
        }

        int num = 1;
        SimpleKMeans model;

        do {
            model = new SimpleKMeans();
            try {
                model.setNumClusters(num);
            } catch (Exception e) {
                System.out.println("### NUM CLUSTER non valido!");
            }
            if(manhattanDistance[1]) {
                try {
                    model.setDistanceFunction(new weka.core.ManhattanDistance());
                } catch (Exception e) {
                    System.out.println("### DISTANZA di CLUSTERING non valida!");
                }
            }

            try {
                model.buildClusterer(data);
            } catch (Exception e) {
                System.out.println("### CLUSTERING non valido!");
            }
            System.out.println("#Cluster " + num + "-> sum of squared errors : " + model.getSquaredError());
            num = num+1;
        } while (model.getSquaredError() > 0);
    }
    private static void createMatrixForClustering3(){

    }
    private static void executeCluster3() {}

    private static void exportAllClusterCsvArff() {

        System.out.println("\n\tSALVATAGGIO file CSV e ARFF\n");

        MongoCRUD mongoCRUD = new MongoCRUD(realDB);
        /*
        try {
            mongoCRUD.retrieveCluster(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        try {
            mongoCRUD.retrieveCluster(2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*
        try {
            mongoCRUD.retrieveCluster(3);
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

        System.out.println("\n\tSALVATAGGIO FILE CSV ARFF COMPLETATO\n");

    }

    private static void calculatingRelativityForEntitiesAndSave(){

        MongoCRUD mongoCRUD = new MongoCRUD(realDB);
        // TODO change to 2
        ArrayList<String> labels = mongoCRUD.findAllLabel(3);

        TagMeClient tagMeClient = new TagMeClient();

        double [][] matrix3 = new double[labels.size()][labels.size()];

        int i = 0;
        for (String label_out :labels) {
            int j = 0;
            for (String label_in : labels){
                RelRequest relRequest = tagMeClient.rel().tt(label_in, label_out);
                System.out.println(label_in + " ," + label_out);
                RelResponse relResponse = null;
                try {
                    relResponse = relRequest.execute();
                } catch (TagMeException e) {
                    System.out.print(".");
                }
                List<Relatedness> lr = relResponse.getResult();
                System.out.println(lr.get(0).getErr());
                matrix3[i][j] = lr.get(0).getRel();
                System.out.println(matrix3[i][j]);
                j++;
            }
            i++;
        }

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
