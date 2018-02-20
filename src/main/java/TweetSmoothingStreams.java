import api.twitter.TweetExtractor;
import controller.TwitterSmoothingController;

public class TweetSmoothingStreams {
    public static void main(String[] args) throws Exception {
        System.out.println("\n------------------------\tTWEET SMOOTHING STREAMS\t------------------------\n");
        //TweetExtractor tweetExtractor = new TweetExtractor();
        //tweetExtractor.listenSmooth();

        TwitterSmoothingController tsc = new TwitterSmoothingController();
        double s = tsc.getPerplexityForeground("non sono sicuro che sia un tweet");
        System.out.println(s);

    }
}
