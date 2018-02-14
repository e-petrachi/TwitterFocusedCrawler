package api.news;

import model.Articles;
import model.News;
import org.jongo.MongoCursor;
import weka.core.Stopwords;
import weka.filters.unsupervised.attribute.StringToWordVector;

import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.porterStemmer;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

public class NLPExtractor {

    public NLPExtractor(){}

    public String removeStopwords(String text){
        String result = "";

        Stopwords stopwords = new Stopwords();
        Enumeration enm = stopwords.elements();

        String[] args = text.split("[^A-Za-z]");

        Vector words = new Vector();
        for (int i = 0; i < args.length; i++) {
            if (args[i].trim().length() > 0)
                words.add(args[i].trim());
        }

        if (words.size() > 0) {
            for (int i = 0; i < words.size(); i++) {
                if (!stopwords.is(words.get(i).toString())){
                    String word = (String) words.get(i);
                    result = result + " " + word.toLowerCase();
                }
            }
        }

        return result;

    }
    public String stemming(String textStopped){
        String result = "";

        SnowballStemmer stemmer = new porterStemmer();

        String[] args = textStopped.toLowerCase().split(" ");
        for (String s : args){
            stemmer.setCurrent(s);
            stemmer.stem();
            result = result + " " + stemmer.getCurrent();
        }

        return result;
    }

    public double tfIdf(News news, List<News> newsList, String term, List<String> labels) {
        String[] arg = news.getTextWithStemmer().split(" ");
        ArrayList<String> args = new ArrayList<>();
        for (String a :arg) {
           if (labels.contains(a))
               args.add(a);
        }
        double result = tf(args, term) * idf(newsList, term);
        return Math.floor(result * 1000) / 1000;
    }
    public double tf(ArrayList<String> doc, String term) {
        double result = 0;
        for (String word : doc) {
            if (term.equalsIgnoreCase(word))
                result++;
        }
        return result / doc.size();
    }
    public double idf(List<News> newsList, String term) {

        double n = 0;
        int tot = 0;
        for (News doc : newsList) {
            tot++;
            for (String word : doc.getTextWithStemmer().split(" ")) {
                if (term.equalsIgnoreCase(word)) {
                    n++;
                    break;
                }
            }
        }
        return Math.log(tot / n);
    }
}
