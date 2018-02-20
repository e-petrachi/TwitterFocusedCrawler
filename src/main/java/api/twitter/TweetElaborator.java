package api.twitter;

import db.MongoCRUD;
import model.Tweet;
import model.Tweet2Hashtag;

import java.util.ArrayList;

public class TweetElaborator {

    public TweetElaborator(){ }

    public void elaborateBackground(){

        MongoCRUD mongoCRUD = new MongoCRUD();
        mongoCRUD.setDbName("twitterDB");
        mongoCRUD.setCollection("en");

        ArrayList<Tweet> tweets = mongoCRUD.findAllTweets();

        mongoCRUD.setCollection("tweet2hashtag");
        mongoCRUD.convertTweets(tweets);
        mongoCRUD.cleanTweet2Hashtag();
    }
    public void createHashtag2vec(int soglia){
        MongoCRUD mongoCRUD = new MongoCRUD();
        mongoCRUD.setDbName("twitterDB");
        mongoCRUD.setCollection("tweet2hashtag");

        ArrayList<Tweet2Hashtag> tweets = mongoCRUD.findAllTweets2Hashtag();

        mongoCRUD.setCollection("hashtag2vec");
        mongoCRUD.convertTweet2HashtagInHashtag(tweets, soglia);
    }
    public void createWord2vec(int soglia){
        MongoCRUD mongoCRUD = new MongoCRUD();
        mongoCRUD.setDbName("twitterDB");
        mongoCRUD.setCollection("tweetONtopic");

        ArrayList<Tweet2Hashtag> tweets = mongoCRUD.findAllTweets2Hashtag();

        // TODO fix here
        mongoCRUD.setCollection("wordsONtopic");
        mongoCRUD.clearCollection();
        mongoCRUD.convertTweetsInWords(tweets, soglia);
    }
    public void createBackgroundForTopic(String topic){
        MongoCRUD mongoCRUD = new MongoCRUD();
        mongoCRUD.setDbName("twitterDB");
        mongoCRUD.setCollection("tweet2hashtag");

        ArrayList<Tweet2Hashtag> tweets = mongoCRUD.findAllTweets2Hashtag();

        mongoCRUD.setCollection("tweetONtopic");
        mongoCRUD.clearCollection();

        for (Tweet2Hashtag tweet2Hashtag: tweets) {
            for (String hashtag : tweet2Hashtag.getHashtags()) {
                if (this.compareTopicToHashtag(hashtag,topic))
                    mongoCRUD.saveTweet2Hashtag(tweet2Hashtag);
            }
        }
    }
    public String findCommonTopic(ArrayList<String> topics){
        MongoCRUD mongoCRUD = new MongoCRUD();
        mongoCRUD.setDbName("twitterDB");
        mongoCRUD.setCollection("hashtag2vec");

        ArrayList<String> hashtags = mongoCRUD.getTopHashtags();

        for (String topic : topics) {
            for (String hashtag : hashtags) {
                String t = topic.replaceAll("_", "");
                //System.out.println(t + " : " + hashtag);
                if (this.compareTopicToHashtag(hashtag,t))
                    return hashtag;
            }
        }

        return null;
    }


    private boolean compareTopicToHashtag(String s1, String s2){
        if (s1.equalsIgnoreCase(s2))
            return true;
        if (s1.toLowerCase().contains(s2.toLowerCase()))
            return true;
        if (s2.toLowerCase().contains(s1.toLowerCase()))
            return true;
        return false;
    }
}
