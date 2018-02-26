package controller;

import api.nlp.NLPExtractor;
import db.MongoCRUD;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import java.util.HashMap;

public class TwitterSmoothingController {
    private HashMap<String, Double> word2prob;
    private double alpha = 0.5;
    private int size = 1000;
    private NLPExtractor nlp;
    private CircularFifoQueue<String> queue;
    private int dimensionWordsQueue;
    private int sogliaPerplexity = 500000;
    private double zeroProbability = 0.0000001;
    private int maxPerplexity = 100000000;

    public TwitterSmoothingController() {
        MongoCRUD mongoCRUD = new MongoCRUD();
        mongoCRUD.setDbName("twitterDB");
        mongoCRUD.setCollection("wordsONtopic");
        this.word2prob = mongoCRUD.getBackgroundModel();
        this.nlp = new NLPExtractor();
        this.queue = new CircularFifoQueue<>(this.size);
        this.dimensionWordsQueue = 0;
    }
    public boolean check(String tweet){
        String tweet_m = this.nlp.removeUsersAndLink(tweet);
        String tweet_cleaned = this.nlp.removeStopwords(tweet_m);
        int perp = this.getPerplexityForeground(tweet_cleaned);

        if (perp > this.sogliaPerplexity) {
            return false;
        }
        System.out.println(tweet_cleaned + " ---> " + perp);
        return true;
    }
    public void saveHistory(String tweet){
        String tweet_m = this.nlp.removeUsersAndLink(tweet);
        String tweet_cleaned = this.nlp.removeStopwords(tweet_m);
        this.queue.add(tweet_cleaned);
        this.dimensionWordsQueue = this.countTotalWords();
    }
    public int countWordsEqualsTo(String word){
        if (this.dimensionWordsQueue < 1)
            return 0;

        int counter = 0;
        for (String tweet : queue) {
            if (tweet.contains(word)){
                counter++;
            }
        }
        return counter;
    }
    public int countTotalWords(){
        int counter = 0;
        for (String tweet : queue) {
            for (String s: tweet.split(" ")) {
                if (s.length() > 1)
                    counter++;
            }
        }
        return counter;
    }
    public double getProbabilityForeground(String word){
        if (this.countWordsEqualsTo(word) > 0){
            return (this.alpha * (this.countWordsEqualsTo(word) / this.dimensionWordsQueue));
        } else {
            if (this.word2prob.containsKey(word)){
                double p = this.word2prob.get(word);
                double result = this.alpha * p ;
                return result;
            }
            return zeroProbability;
        }
    }
    public int getPerplexityForeground(String tweet){
        String[] words = tweet.split("[^A-Za-z]");

        if (words.length < 6)
            return this.maxPerplexity;

        double sommatoria = 0d;
        int size = 0;
        for (String word: words) {
            if (word.length() > 2) {
                //System.out.println(word);
                sommatoria += (Math.log(this.getProbabilityForeground(word)) / Math.log(2));
                size++;
            }
        }

        if (size == 0)
            return this.maxPerplexity;

        sommatoria /= size*(-1);
        return (int) Math.pow(2,sommatoria);
    }
}
