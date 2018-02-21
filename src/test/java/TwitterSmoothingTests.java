import api.nlp.NLPExtractor;
import controller.TwitterSmoothingController;
import org.junit.*;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TwitterSmoothingTests {
    private TwitterSmoothingController tester;
    private String tweet;

    @Before
    public void instances(){
        tester = new TwitterSmoothingController();
        tweet = "crypto airdrop bitcoin blockchain cryptocurrency trading binance based current";
    }
    @Test
    public void perplexityExistingPos() {
        double result = tester.getPerplexityForeground(tweet);
        assertTrue(result > 0);
    }
    @Test
    public void perplexityExistingLimit() {
        double result = tester.getPerplexityForeground(tweet);
        System.out.println(result);
        assertTrue(result < 500);
    }
    @Test
    public void perplexityEmptyPos() {
        double result = tester.getPerplexityForeground("");
        assertTrue(result > 0);
    }
    @Test
    public void perplexityNotExistingPos() {
        double result = tester.getPerplexityForeground(tweet.replaceAll("a","b"));
        assertTrue(result > 0);
    }
    @Test
    public void perplexityNotExistingLimit() {
        double result = tester.getPerplexityForeground(tweet.replaceAll("a","b"));
        System.out.println(result);
        assertTrue(result > 500);
    }
}
