package controller;

import api.news.NewsExtractor;
import api.tagme4j.TagMeClient;
import api.tagme4j.TagMeException;
import api.tagme4j.model.Annotation;
import api.tagme4j.response.TagResponse;
import db.MongoCRUD;
import model.Articles;
import model.News;
import model.News2Annotations;
import org.jongo.MongoCursor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

public class NewsController {

    private boolean realDB = true;

    public NewsController(boolean realDB){
        this.realDB = realDB;
    }

    public void newsExtractionAndSave() {
        System.out.println("\n\tESTRAZIONE TOP NEWS");
        NewsExtractor extractor = new NewsExtractor();
        Articles top = extractor.getTopNews();

        MongoCRUD mongoCRUD = this.connect2dbNews();

        System.out.println("\n\tSALVATAGGIO TOP NEWS");
        for (News nn : top.getArticle()) {
            mongoCRUD.saveNews(nn);
            System.out.print(".");
        }
        System.out.println("\tSALVATAGGIO TOP NEWS COMPLETATO\n");


    }

    public void newsCleaning() {
        System.out.println("\n\tPULIZIA NEWS");

        MongoCRUD mongoCRUD = this.connect2dbNews();
        mongoCRUD.cleanNews();

        System.out.println("\tPULIZIA NEWS COMPLETATA\n");
    }

    public void news2AnnCleaning() {
        System.out.println("\n\tPULIZIA NEWS2ANNOTATIONS");

        MongoCRUD mongoCRUD = this.connect2dbNews();
        mongoCRUD.setCollection("news2annotations");
        mongoCRUD.cleanNews2Annotations();

        System.out.println("\tPULIZIA NEWS2ANNOTATIONS COMPLETATA\n");
    }

    public void annotationsExtractionAndSave(double sogliaMinimaLink) {
        MongoCRUD mongoCRUD = this.connect2dbNews();

        TagMeClient tagMeClient = new TagMeClient();
        TagResponse tagResponse = null;

        System.out.println("\n\tESTRAZIONE ANNOTATIONS");

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

        System.out.println("\tESTRAZIONE e SALVATAGGIO ANNOTAZIONI\t_lento_\n");

        boolean prevision = false;
        Date now = new Date();
        Date then = null;

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
                    if (a.getLinkProbability() > sogliaMinimaLink)
                        annotations_good.add(a);
                }

                News2Annotations n2a = new News2Annotations(news, annotations_good);
                mongoCRUD.saveNews2Annotations(n2a);
                tot++;

                if (!prevision){
                    then = new Date();

                    long millisDiff = (then.getTime() - now.getTime())*all.size();
                    int minutes = (int) (millisDiff / 60000 % 60);
                    int hours = (int) (millisDiff / 3600000 % 24);

                    System.out.println("\tTermine stimato fra circa " + hours + " ore e " + minutes + " minuti");
                    prevision = true;
                }

                System.out.print("*");
            }
        }

        System.out.println("\tSALVATAGGIO NEWS2ANNOTATIONS COMPLETATO: salvate " + tot + " news su " + all.size() + ".\n");

    }

    private MongoCRUD connect2dbNews(){
        MongoCRUD mongoCRUD = new MongoCRUD(this.realDB);
        mongoCRUD.setCollection("news");
        return mongoCRUD;
    }
}
