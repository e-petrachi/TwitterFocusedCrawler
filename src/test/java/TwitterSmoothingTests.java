import controller.TwitterSmoothingController;
import org.junit.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TwitterSmoothingTests {
    private TwitterSmoothingController tester;
    private int sogliaPerplexity = 1000000;
    private String tweetWithAllWordPertinent;
    private String tweetWithAllWordPertinentWithRumor;
    private String tweetWithOneWordPertinent;
    private String tweetWithOneWordPertinentLong;
    private String tweetWithTwoWordPertinent;
    private String tweetWithTwoWordPertinentLong;
    private String tweetWithThreeWordPertinent;
    private String tweetWithThreeWordPertinentLong;
    private String realTweet;
    private String realTweetPertinent1;
    private String realTweetPertinent2;

    @Before
    public void instances(){
        tester = new TwitterSmoothingController();
        tweetWithAllWordPertinent = "crypto airdrop bitcoin blockchain cryptocurrency trading";
        tweetWithAllWordPertinentWithRumor = "acrypto airdropped xbitcoinx blockchains cryptocurrencycurrency incurrent";

        tweetWithOneWordPertinent = "crypto tante parole italiane diverse affatto";
        tweetWithOneWordPertinentLong = "crypto tante parole italiane che nonn centrano nulla affatto sono pure tante molte moltissime";

        tweetWithTwoWordPertinent = "crypto tante airdrop parole italiane diverse";
        tweetWithTwoWordPertinentLong = "crypto tante airdrop italiane che nonn centrano nulla affatto sono pure tante molte moltissime";

        tweetWithThreeWordPertinent = "crypto airdrop italiane blockchain nulla affatto";
        tweetWithThreeWordPertinentLong = "crypto tante airdrop italiane che blockchain non centrano nulla affatto pure tante molte moltissime";

        realTweet = "@OroAnnM @CatholicEdNJ Thanks for your help! Ss hoping to gain some feedback to help improve their product! #designthinking #TeachSDGs";
        realTweetPertinent1 = "#SignalsNetwork  for trading crypto currencies Machine intelligence for humanprofit To  democratize machine intelligence  in the crypto trading industry  https://t.co/1TJhftSMoD";
        realTweetPertinent2 = "r/crypto currency's sentiment during the first quarter of 2018 https://t.co/065817tkm5 #bitcoin #cryptocurrency #reddit";
    }
    @Test
    public void perplexityExisting() {
        int result = tester.getPerplexityForeground(tweetWithAllWordPertinent);
        System.out.println(result);
        assertTrue(result > 0 && result < 500);
    }
    @Test
    public void perplexityExistingRumor() {
        int result = tester.getPerplexityForeground(tweetWithAllWordPertinentWithRumor);
        System.out.println(result);
        assertTrue(result > sogliaPerplexity);
    }
    @Test
    public void perplexityOneExisting() {
        int result = tester.getPerplexityForeground(tweetWithOneWordPertinent);
        System.out.println(result);
        assertTrue(result > 0 && result > sogliaPerplexity);
    }
    @Test
    public void perplexityOneExistingLong() {
        int result = tester.getPerplexityForeground(tweetWithOneWordPertinentLong);
        System.out.println(result);
        assertTrue(result > 0 && result > sogliaPerplexity);
    }
    @Test
    public void perplexityTwoExisting() {
        int result = tester.getPerplexityForeground(tweetWithTwoWordPertinent);
        System.out.println(result);
        assertTrue(result > 0 && result < sogliaPerplexity);
    }
    @Test
    public void perplexityTwoExistingLong() {
        int result = tester.getPerplexityForeground(tweetWithTwoWordPertinentLong);
        System.out.println(result);
        assertTrue(result > 0 && result > sogliaPerplexity);
    }
    @Test
    public void perplexityThreeExisting() {
        int result = tester.getPerplexityForeground(tweetWithThreeWordPertinent);
        System.out.println(result);
        assertTrue(result > 0 && result < sogliaPerplexity);
    }
    @Test
    public void perplexityThreeExistingLong() {
        int result = tester.getPerplexityForeground(tweetWithThreeWordPertinentLong);
        System.out.println(result);
        assertTrue(result < sogliaPerplexity);
    }
    @Test
    public void perplexityEmpty() {
        int result = tester.getPerplexityForeground("");
        System.out.println(result);
        assertTrue(result > sogliaPerplexity);
    }
    @Test
    public void perplexityNotExisting() {
        int result = tester.getPerplexityForeground(tweetWithAllWordPertinent.replaceAll("a|o","b"));
        System.out.println(result);
        assertTrue(result > sogliaPerplexity);
    }
    @Test
    public void perplexityReal() {
        int result = tester.getPerplexityForeground(realTweet);
        System.out.println(result);
        assertTrue(result > sogliaPerplexity);
    }
    @Test
    public void perplexityRealPert1() {
        int result = tester.getPerplexityForeground(realTweetPertinent1);
        System.out.println(result);
        assertTrue(result > 0 && result < sogliaPerplexity);
    }
    @Test
    public void perplexityRealPert2() {
        int result = tester.getPerplexityForeground(realTweetPertinent2);
        System.out.println(result);
        assertTrue(result > 0 && result < sogliaPerplexity);
    }
}
