package controller;

import api.nlp.NLPExtractor;
import api.twitter.TweetExtractor;
import db.MongoCRUD;
import model.Tweet;
import model.Word2Vec;
import org.apache.commons.collections4.queue.CircularFifoQueue;

public class TwitterSmoothingController {
    private Word2Vec word2probability;
    private double alpha = 0.5;
    private int size = 1000;
    private NLPExtractor nlp;
    private CircularFifoQueue<String> queue;

    public TwitterSmoothingController() {
        MongoCRUD mongoCRUD = new MongoCRUD();
        mongoCRUD.setDbName("twitterDB");
        mongoCRUD.setCollection("wordsONtopic");
        this.word2probability = mongoCRUD.getBackgroundModel();
        this.nlp = new NLPExtractor();
        this.queue = new CircularFifoQueue<>(this.size);
    }
    public boolean check(String tweet){
        double perp = this.getPerplexityForeground(tweet);
        System.out.println("\n\t\tPeplexity: " + perp);
        if (perp > 5) {
            return true;
        }
        return false;
    }
    public void saveHistory(String tweet){
        String tweet_cleaned = this.nlp.removeStopwords(tweet);
        this.queue.add(tweet_cleaned);
    }
    public int countWordsEqualsTo(String word){
        int counter = 0;
        for (String tweet : queue) {
            if (tweet.toLowerCase().contains(word.toLowerCase())){
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
            double result = this.alpha * this.countWordsEqualsTo(word) / this.countTotalWords();
            return result;
        } else {
            if (this.word2probability.getWord2vec().containsKey(word)){
                double p = this.word2probability.getWord2vec().get(word);
                double result = this.alpha * p ;
                return result;
            }
            return 0;
        }
    }
    public double getPerplexityForeground(String tweet){
        String tweet_cleaned = this.nlp.removeStopwords(tweet);
        String[] words = tweet_cleaned.split(" ");
        double sommatoria = 0d;
        for (String word: words) {
            sommatoria += Math.log(this.getProbabilityForeground(word));
        }
        sommatoria /= words.length;
        return Math.pow(2,sommatoria*(-1));
    }
}
