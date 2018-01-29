package api.twitter;

import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TweetExtractor {

    // TODO cambia le apikey
    private String apikey = "y2Q54l9L1b7Wo4guIlGsFsaoh";
    private String apiSecret = "jEvklEu9Oukw2p2Ea8kNuTaqOLQvM1KKogkjxM70G6FkCtkyD2";

    private AccessToken accessToken;
    private Twitter twitter;

    public TweetExtractor(){
        this.accessToken = null;
        this.twitter = TwitterFactory.getSingleton();
    }

    public void auth() throws TwitterException{
        twitter.setOAuthConsumer(this.apikey, this.apiSecret);
        RequestToken requestToken = twitter.getOAuthRequestToken();


        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (null == accessToken) {
            System.out.println("Open the following URL and grant access to your account:");
            System.out.println(requestToken.getAuthorizationURL());
            System.out.print("Enter the PIN(if aviailable) or just hit enter.[PIN]:");
            String pin = null;
            try {
                pin = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try{
                if(pin.length() > 0){
                    this.accessToken = twitter.getOAuthAccessToken(requestToken, pin);
                }else{
                    this.accessToken = twitter.getOAuthAccessToken();
                }
            } catch (TwitterException te) {
                if(401 == te.getStatusCode()){
                    System.out.println("Unable to get the access token.");
                }else{
                    te.printStackTrace();
                }
            }
        }
    }
    public List<String> searchTweet(String searchQuery) throws TwitterException {

        Query query = new Query(searchQuery);
        QueryResult result = this.twitter.search(query);

        List<String> tweets = new ArrayList<>();

        for (Status state : result.getTweets()) {
            System.out.println("@" + state.getUser().getScreenName() + ":> " + state.getText());
            if (state.getURLEntities().length > 0)
                tweets.add(state.getUser().getScreenName() + ":> " + state.getText());
        }

        return tweets;
    }
}
