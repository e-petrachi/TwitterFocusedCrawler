package model;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

public class Articles {
    private ArrayList<News> article;
    private int lenght;
    private int pages;


    public Articles(ArrayList<News> article) {
        this.article = article;
    }

    public Articles() {
        this.article = new ArrayList<News>();
    }

    public ArrayList<News> getArticle() {
        return article;
    }

    public void setArticle(ArrayList<News> article) {
        this.article = article;
    }

    public void addArticles(Articles articles_otherPage) {
        this.getArticle().addAll(articles_otherPage.getArticle());
    }

    public void addArticle(News news) {
        this.article.add(news);
    }

    public int getLenght() {
        return lenght;
    }

    public void setLenght(int lenght) {
        this.lenght = lenght;
        this.pages = lenght/100;
        if (lenght%100 > 0)
            this.pages++;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public static Articles convertFromJSONToArticles(JSONArray jsonArray, Integer lenght) {
        if (jsonArray == null) {
            return null;
        }
        Articles articles = new Articles();

        articles.setLenght(lenght.intValue());

        for (int i=0;i<100;i++){
            JSONObject jo = null;
            try {
                jo = jsonArray.getJSONObject(i);
            } catch (Exception e){ }

            if (jo != null ) {
                News news = News.convertFromJSONToNews(jo);
                articles.addArticle(news);
            }
        }
        return articles;
    }

    public static Articles convertFromJSONToArticles(JSONArray jsonArray) {
        if (jsonArray == null) {
            return null;
        }
        Articles articles = new Articles();

        boolean terminate = false;
        int i = 0;

        while(terminate == false){
            JSONObject jo = null;
            try {
                jo = jsonArray.getJSONObject(i);
            } catch (Exception e){ }

            if (jo != null ) {
                News news = News.convertFromJSONToNews(jo);
                articles.addArticle(news);
            }else{
                terminate = true;
            }
            i++;
        }


        return articles;
    }

    @Override
    public String toString() {
        return "Articles {" + this.lenght +
                " article=" + article +
                "\n}";
    }
}
