import api.twitter.TweetExtractor;
import controller.TwitterSmoothingController;

import java.util.Date;

public class TweetSmoothingStreams {
    public static void main(String[] args) throws Exception {
        System.out.println("\n------------------------\tTWEET SMOOTHING STREAMS\t------------------------\n");
        TweetExtractor tweetExtractor = new TweetExtractor();
        tweetExtractor.listenSmooth();
    }
}
