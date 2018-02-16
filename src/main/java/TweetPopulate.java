import api.twitter.TweetExtractor;

public class TweetPopulate {
    public static void main(String[] args) throws Exception {
        System.out.println("\n------------------------\tTWEETS RECORDING\t------------------------\n");
        TweetExtractor tweetExtractor = new TweetExtractor();
        tweetExtractor.lister();
    }
}
