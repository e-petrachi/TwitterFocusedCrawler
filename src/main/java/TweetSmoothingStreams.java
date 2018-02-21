import api.twitter.TweetExtractor;

public class TweetSmoothingStreams {
    public static void main(String[] args) throws Exception {
        System.out.println("\n------------------------\tTWEET SMOOTHING STREAMS\t------------------------\n");
        TweetExtractor tweetExtractor = new TweetExtractor();
        tweetExtractor.listenSmooth();
    }
}
