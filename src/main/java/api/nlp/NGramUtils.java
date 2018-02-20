package api.nlp;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

public class NGramUtils {

  /**
   * calculate the probability of a unigram in a vocabulary using maximum likelihood estimation
   *
   * @param word the only word in the unigram
   * @param set  the vocabulary
   * @return the maximum likelihood probability
   */
  public double calculateUnigramMLProbability(String word, Collection<StringList> set) {
    double vocSize = 0d;
    for (StringList s : set) {
      vocSize += s.size();
    }
    return count(new StringList(word), set) / vocSize;
  }

  /**
   * calculate the probability of a bigram in a vocabulary using maximum likelihood estimation
   *
   * @param x0  first word in the bigram
   * @param x1  second word in the bigram
   * @param set the vocabulary
   * @return the maximum likelihood probability
   */
  public static double calculateBigramMLProbability(String x0, String x1, Collection<StringList> set) {
    return calculateNgramMLProbability(new StringList(x0, x1), set);
  }

  /**
   * calculate the probability of a trigram in a vocabulary using maximum likelihood estimation
   *
   * @param x0  first word in the trigram
   * @param x1  second word in the trigram
   * @param x2  third word in the trigram
   * @param set the vocabulary
   * @return the maximum likelihood probability
   */
  public static double calculateTrigramMLProbability(String x0, String x1, String x2,
                                                     Iterable<StringList> set) {
    return calculateNgramMLProbability(new StringList(x0, x1, x2), set);
  }

  /**
   * calculate the probability of a ngram in a vocabulary using maximum likelihood estimation
   *
   * @param ngram a ngram
   * @param set   the vocabulary
   * @return the maximum likelihood probability
   */
  public static double calculateNgramMLProbability(StringList ngram, Iterable<StringList> set) {
    StringList ngramMinusOne = getNMinusOneTokenFirst(ngram);
    return count(ngram, set) / count(ngramMinusOne, set);
  }

  /**
   * get the (n-1)th ngram of a given ngram, that is the same ngram except the last word in the ngram
   *
   * @param ngram a ngram
   * @return a ngram
   */
  public static StringList getNMinusOneTokenFirst(StringList ngram) {
    String[] tokens = new String[ngram.size() - 1];
    for (int i = 0; i < ngram.size() - 1; i++) {
      tokens[i] = ngram.getToken(i);
    }
    return tokens.length > 0 ? new StringList(tokens) : null;
  }

  private static Double count(StringList ngram, Iterable<StringList> sentences) {
    Double count = 0d;
    for (StringList sentence : sentences) {
      int idx0 = indexOf(sentence, ngram.getToken(0));
      if (idx0 >= 0 && sentence.size() >= idx0 + ngram.size()) {
        boolean match = true;
        for (int i = 1; i < ngram.size(); i++) {
          String sentenceToken = sentence.getToken(idx0 + i);
          String ngramToken = ngram.getToken(i);
          match &= sentenceToken.equals(ngramToken);
        }
        if (match) {
          count++;
        }
      }
    }
    return count;
  }

  private static int indexOf(StringList sentence, String token) {
    for (int i = 0; i < sentence.size(); i++) {
      if (token.equals(sentence.getToken(i))) {
        return i;
      }
    }
    return -1;
  }

  private static Collection<String> flatSet(Iterable<StringList> set) {
    Collection<String> flatSet = new HashSet<>();
    for (StringList sentence : set) {
      for (String word : sentence) {
        flatSet.add(word);
      }
    }
    return flatSet;
  }

  /**
   * Get the ngrams of dimension n of a certain input sequence of tokens.
   *
   * @param sequence a sequence of tokens
   * @param size     the size of the resulting ngrmams
   * @return all the possible ngrams of the given size derivable from the input sequence
   */
  public static Collection<StringList> getNGrams(StringList sequence, int size) {
    Collection<StringList> ngrams = new LinkedList<>();
    if (size == -1 || size >= sequence.size()) {
      ngrams.add(sequence);
    } else {
      String[] ngram = new String[size];
      for (int i = 0; i < sequence.size() - size + 1; i++) {
        ngram[0] = sequence.getToken(i);
        for (int j = 1; j < size; j++) {
          ngram[j] = sequence.getToken(i + j);
        }
        ngrams.add(new StringList(ngram));
      }
    }
    return ngrams;
  }

  /**
   * Get the ngrams of dimension n of a certain input sequence of tokens.
   *
   * @param sequence a sequence of tokens
   * @param size     the size of the resulting ngrmams
   * @return all the possible ngrams of the given size derivable from the input sequence
   */
  public static Collection<String[]> getNGrams(String[] sequence, int size) {
    Collection<String[]> ngrams = new LinkedList<>();
    if (size == -1 || size >= sequence.length) {
      ngrams.add(sequence);
    } else {
      for (int i = 0; i < sequence.length - size + 1; i++) {
        String[] ngram = new String[size];
        ngram[0] = sequence[i];
        for (int j = 1; j < size; j++) {
          ngram[j] = sequence[i + j];
        }
        ngrams.add(ngram);
      }
    }

    return ngrams;
  }
}
