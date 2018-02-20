package api.twitter;

import controller.FileController;
import controller.TwitterSmoothingController;
import db.MongoCRUD;
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
    private TwitterStream twitterStream;
    private boolean authenticated;

    public TweetExtractor(){
        this.accessToken = null;
        this.twitter = TwitterFactory.getSingleton();
        this.twitterStream = new TwitterStreamFactory().getInstance();
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
        storeAccessToken(accessToken);
        Status status = twitter.updateStatus("200 OK");
        System.out.println("Successfully updated the status to [" + status.getText() + "].");
    }
    private static void storeAccessToken(AccessToken accessToken){
        FileController fileController = new FileController();
        fileController.saveTokens(accessToken.getToken(),accessToken.getTokenSecret());
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

    public void lister() throws TwitterException, IOException {
        MongoCRUD mongoCRUD = new MongoCRUD();
        mongoCRUD.setDbName("twitterDB");
        mongoCRUD.setCollection("en");

        StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
                if ( status.getText().charAt(0) != 'R' & status.getText().charAt(1) != 'T' & status.getText().charAt(3) != '@' &
                        status.getText().split(" ").length >= 10 & status.getText().contains("#")) {
                    mongoCRUD.saveTweet("@" + status.getUser().getScreenName(), status.getText());
                    System.out.print(".");
                    if (status.getText().length() == 280)
                        System.out.print("\n");
                }

            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
                System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
                System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }

            @Override
            public void onStallWarning(StallWarning warning) {
                System.out.println("Got stall warning:" + warning);
            }

            @Override
            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        };

        this.twitterStream.addListener(listener);
        this.twitterStream.sample("en");
    }

    public void listenSmooth() throws TwitterException, IOException {
        MongoCRUD mongoCRUD = new MongoCRUD();
        mongoCRUD.setDbName("twitterDB");
        mongoCRUD.setCollection("smooth");

        TwitterSmoothingController tsc = new TwitterSmoothingController();

        StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
                System.out.print(".");
                if (tsc.check(status.getText())) {
                    //mongoCRUD.saveTweet("@" + status.getUser().getScreenName(), status.getText());
                    tsc.saveHistory(status.getText());
                }

            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {}

            @Override
            public void onStallWarning(StallWarning warning) {}

            @Override
            public void onException(Exception ex) {}
        };

        this.twitterStream.addListener(listener);
        this.twitterStream.sample("en");
    }
}
