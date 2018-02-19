package api.twitter;

import db.MongoCRUD;
import model.Tweet;
import model.Tweet2Hashtag;

import java.util.ArrayList;

public class TweetElaborator {

    public TweetElaborator(){ }

    public void elaborate(){

        MongoCRUD mongoCRUD = new MongoCRUD();
        mongoCRUD.setDbName("twitterDB");
        mongoCRUD.setCollection("en");

        ArrayList<Tweet> tweets = mongoCRUD.findAllTweets();

        mongoCRUD.setCollection("tweet2hashtag");
        mongoCRUD.convertTweets(tweets);
    }
    public void word2vec(int soglia){
        MongoCRUD mongoCRUD = new MongoCRUD();
        mongoCRUD.setDbName("twitterDB");
        mongoCRUD.setCollection("tweet2hashtag");

        ArrayList<Tweet2Hashtag> tweets = mongoCRUD.findAllTweets2Hashtag();

        mongoCRUD.setCollection("word");

        mongoCRUD.convertTweetsInWords(tweets, soglia);
    }
}
