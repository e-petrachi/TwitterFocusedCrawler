package model;

public class Tweet {
    private String user;
    private String tweet;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTweet() {
        return tweet;
    }

    public void setTweet(String tweet) {
        this.tweet = tweet;
    }

    public Tweet(String user, String tweet) {

        this.user = user;
        this.tweet = tweet;
    }
}
