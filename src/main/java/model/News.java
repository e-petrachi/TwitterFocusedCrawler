package model;

import org.jongo.marshall.jackson.oid.Id;
import org.jongo.marshall.jackson.oid.MongoId;
import org.json.JSONObject;
import java.util.Date;

public class News {
    private String title;

    private String description;
    private String url;
    private String text;
    private String textWithoutStopword;
    private String textWithStemmer;
    private String urlToImage;
    private Source source;
    private String author;
    private Date publishedAt;

    public News() {
        this.source = new Source();
    }

    public News(Source source, String author, String title, String description, String url, String urlToImage, Date publishedAt) {
        this.source = source;
        this.author = author;
        this.title = title;
        this.description = description;
        this.url = url;
        this.text = "";
        this.urlToImage = urlToImage;
        this.publishedAt = publishedAt;
    }

    public static News convertFromJSONToNews(JSONObject json) {
        if (json == null) {
            return null;
        }
        News news = new News();

        try {
            String s = (String) json.get("title");
            news.setTitle(s.replace("\n", " "));
        } catch (Exception e){
            news.setTitle("");
        }
        try {
            news.setAuthor((String) json.get("author"));
        } catch (Exception e){
            news.setAuthor("");
        }
        try {
            String s = (String) json.get("description");
            news.setDescription(s.replace("\n", " "));
        } catch (Exception e){
            news.setDescription("");
        }
        try {
            news.setUrl((String) json.get("url"));
        } catch (Exception e){
            news.setUrl("");
        }
        try {
            news.setUrlToImage((String) json.get("urlToImage"));
        } catch (Exception e){
            news.setUrlToImage("");
        }
        try {
            news.setPublishedAt((Date) json.get("publishedAt"));
        } catch (Exception e){
            news.setPublishedAt(new Date());
        }

        Source source = new Source();
        JSONObject jo = json.getJSONObject("source");

        try {
            source.setId((String) jo.get("id"));
        } catch (Exception e){
            source.setId("");
        }
        try {
            source.setName((String) jo.get("name"));
        } catch (Exception e){
            source.setName("");
        }

        news.setSource(source);

        return news;
    }


    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    public Date getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Date publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTextWithoutStopword() { return textWithoutStopword; }

    public void setTextWithoutStopword(String textWithoutStopword) {
        this.textWithoutStopword = textWithoutStopword;
    }

    public String getTextWithStemmer() {
        return textWithStemmer;
    }

    public void setTextWithStemmer(String textWithStemmer) {
        this.textWithStemmer = textWithStemmer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof News)) return false;

        News news = (News) o;

        if (!getTitle().equals(news.getTitle())) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = 31 * getTitle().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "\n\tNews {" +
                " source= " + source +
                " , title= '" + title + "' }";
    }
}
