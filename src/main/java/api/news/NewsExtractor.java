package api.news;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import model.Articles;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;

public class NewsExtractor {

    // TODO cambia le apikey
    private static String newsAPI = "d24aaf7ff6304c408443b2aaae218c9e";

    private String language = "en";
    private int pageSize = 100;

    // TODO settare la data in questo formato x 7gg fa
    private String dateFrom = "2018-01-20";

    // Settare a TRUE x estrarre + di 100 news per ogni fonte
    private boolean depth = false;

    public NewsExtractor() { }

    public NewsExtractor(boolean depth) {
        this.depth = depth;
    }

    public Articles getTopNews(){

        HttpResponse<JsonNode> response = null;
        try {
            response =  Unirest.get("https://newsapi.org/v2/top-headlines")
                    .queryString("language", this.language)
                    .queryString("pageSize", this.pageSize)
                    .queryString("page", 1)
                    .queryString("apiKey", newsAPI)
                    .asJson();
        } catch (UnirestException e) {
            System.out.println("------\tIl server non risponde!\t------");
            e.printStackTrace();
        }
        if (response != null){
            Articles articles = this.getNews(response);
            if (articles.getPages() > 1){
                for (int i=2;i<=articles.getPages();i++ ) {
                    try {
                        response =  Unirest.get("https://newsapi.org/v2/top-headlines")
                                .queryString("language", this.language)
                                .queryString("pageSize", this.pageSize)
                                .queryString("page", i)
                                .queryString("apiKey", newsAPI)
                                .asJson();
                    } catch (UnirestException e) {
                        System.out.println("------\tIl server non risponde!\t------");
                        e.printStackTrace();
                    }
                    if (response != null) {
                        Articles articles_otherPage = this.getNews(response);
                        articles.addArticles(articles_otherPage);
                    }
                }
            }
            return articles;
        }
        return null;
    }

    public Articles getEverything(String title){
        HttpResponse<JsonNode> response = null;
        try {
            response =  Unirest.get("https://newsapi.org/v2/everything")
                    .queryString("sources", title)
                    .queryString("from", this.dateFrom)
                    .queryString("language", this.language)
                    .queryString("pageSize", this.pageSize)
                    .queryString("page", 1)
                    .queryString("sortBy", "popularity")
                    .queryString("apiKey", newsAPI)
                    .asJson();
        } catch (UnirestException e) {
            System.out.println("------\tIl server non risponde!\t------");
            e.printStackTrace();
        }

        if (response != null){
            Articles articles = this.getNews(response);
            if (articles == null)
                return null;
            if (articles.getPages() > 1 && this.depth == true){
                for (int i=2;i<=articles.getPages();i++ ) {
                    try {
                        response =  Unirest.get("https://newsapi.org/v2/everything")
                                .queryString("sources", title)
                                .queryString("from", this.dateFrom)
                                .queryString("language", this.language)
                                .queryString("pageSize", this.pageSize)
                                .queryString("page", i)
                                .queryString("sortBy", "popularity")
                                .queryString("apiKey", newsAPI)
                                .asJson();
                    } catch (UnirestException e) {
                        System.out.println("------\tIl server non risponde!\t------");
                        e.printStackTrace();
                    }
                    if (response != null) {
                        Articles articles_otherPage = this.getNews(response);
                        articles.addArticles(articles_otherPage);
                    }
                }
            }
            return articles;
        }
        return null;
    }

    public Articles getNews(HttpResponse<JsonNode> response){

        JsonNode jsonNode = response.getBody();
        JSONArray jsonArray = jsonNode.getArray();
        JSONObject jsonObject = jsonArray.getJSONObject(0);

        Integer length = 0;

        if (jsonObject.getString("status").equals("ok")) {
            try {
                length = jsonObject.getInt("totalResults");
            } catch (Exception e) {
                e.printStackTrace();
            }
            JSONArray jsonArrayArticles = jsonObject.getJSONArray("articles");
            Articles articles = Articles.convertFromJSONToArticles(jsonArrayArticles, length);
            return articles;
        } else
            return null;
    }
}
