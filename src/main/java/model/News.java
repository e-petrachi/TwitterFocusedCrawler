package model;

import org.json.JSONObject;

public class News {
    private String title;
    private String description;
    private String url;
    private String text;
    private String textWithoutStopword;
    private String textWithStemmer;
    private String author;

    public News() { }

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


        return news;
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
                " , title= '" + title + "' }";
    }
}
