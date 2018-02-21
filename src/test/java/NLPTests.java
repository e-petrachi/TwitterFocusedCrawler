import api.nlp.NLPExtractor;
import org.junit.*;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NLPTests {
    private NLPExtractor tester;
    private String word;
    private ArrayList<String> vocabulary;
    private String tweet;

    @Before
    public void instances(){
        tester = new NLPExtractor();
        word = "parola1";
        vocabulary = new ArrayList<>();
        vocabulary.add("parola1");
        vocabulary.add("parola2");
        vocabulary.add("parola3");
        tweet = "bellaparola @utente #hashtag   uèw+p3r4g   p-wàg,,, èwòobwbè+httpnvwe8923 b$g2p3o£g";
    }
    @Test
    public void probabilityExisting() {
        double result = tester.calculateBackgroundProbability(word,vocabulary);
        assertTrue(result > 0 && result <= 1);
    }
    @Test
    public void probabilityNotExisting() {
        double result = tester.calculateBackgroundProbability("no",vocabulary);
        assertTrue(result == 0);
    }
    @Test
    public void removeSpaceForRemovingStopwords(){
        String s = tester.removeUsersAndLink(tweet);
        String t = tester.removeStopwords(s);
        System.out.println("|" + t + "|");
        assertTrue(t.contains("bellaparola") && t.contains("hashtag") && t.contains(" "));
    }
}

