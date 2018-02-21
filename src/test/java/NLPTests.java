import api.nlp.NLPExtractor;
import org.junit.*;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NLPTests {
    private NLPExtractor tester;
    private String word;
    private ArrayList<String> vocabulary;

    @Before
    public void instances(){
        tester = new NLPExtractor();
        word = "parola1";
        vocabulary = new ArrayList<>();
        vocabulary.add("parola1");
        vocabulary.add("parola2");
        vocabulary.add("parola3");
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
}

