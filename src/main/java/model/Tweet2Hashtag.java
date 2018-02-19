package model;

import java.util.ArrayList;

public class Tweet2Hashtag {
    private String tweet;
    private ArrayList<String> hashtags;
    // TODO insert the perplexity and resave in db

    public Tweet2Hashtag(){
        this.hashtags = new ArrayList<>();
    }

    public String getTweet() {
        return tweet;
    }

    public void setTweet(String tweet) {
        this.tweet = tweet;
    }

    public Tweet2Hashtag(String tweet) {
        this.tweet = tweet;
        this.hashtags = new ArrayList<>();
    }

    public ArrayList<String> getHashtags() {
        return hashtags;
    }

    public void setHashtags(ArrayList<String> hashtags) {
        this.hashtags = hashtags;
    }

    public void addHashtags(String hashtags) {
        this.hashtags.add(hashtags);
    }
}
