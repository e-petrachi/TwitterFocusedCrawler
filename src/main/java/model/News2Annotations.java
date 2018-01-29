package model;

import api.tagme4j.model.Annotation;
import org.jongo.marshall.jackson.oid.MongoId;

import java.util.ArrayList;
import java.util.List;

public class News2Annotations {
    private News news;
    private List<Annotation> annotations;

    public News2Annotations(){
        this.news = new News();
        this.annotations = new ArrayList<>();
    }

    public News2Annotations(News news, List<Annotation> annotations) {
        this.news = news;
        this.annotations = annotations;
    }

    public News getNews() {
        return news;
    }

    public void setNews(News news) {
        this.news = news;
    }

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    public List<String> getAnnotationsTitleAsString() {
        List<Annotation> annotations = this.getAnnotations();
        ArrayList<String> strings = new ArrayList<>();
        for (Annotation annotation : annotations){
            if (!annotation.getTitle().isEmpty() && annotation.getTitle().length() > 1)
                strings.add(annotation.getTitle());
        }
        return strings;
    }
    public List<String> getAnnotationsSpotAsString() {
        List<Annotation> annotations = this.getAnnotations();
        ArrayList<String> strings = new ArrayList<>();
        for (Annotation annotation : annotations){
            if (!annotation.getSpot().isEmpty() && annotation.getSpot().length() > 1 )
                strings.add(annotation.getSpot());
        }
        return strings;
    }

    public void setAnnotations(List<Annotation> annotations) {
        this.annotations = annotations;
    }

    public void addAnnotations(Annotation annotation) {
        this.annotations.add(annotation);
    }

}
