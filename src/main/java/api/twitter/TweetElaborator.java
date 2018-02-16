package api.twitter;

import db.MongoCRUD;
import model.Tweet;

import java.util.ArrayList;

public class TweetElaborator {

    public TweetElaborator(){ }

    public void elaborate(){
        MongoCRUD mongoCRUD = new MongoCRUD();
        mongoCRUD.setDbName("twitterDB");
        mongoCRUD.setCollection("en");

        ArrayList<Tweet> tweets = mongoCRUD.findAllTweets();

        mongoCRUD.setCollection("word");
        mongoCRUD.convertTweetsInWords(tweets);
    }
}
