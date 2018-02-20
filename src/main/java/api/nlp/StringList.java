package api.nlp;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class StringList implements Iterable<String> {

  private String[] tokens;

  public StringList(String singleToken) {
    tokens = new String[]{singleToken.intern()};
  }

  public StringList(String... tokens) {

    Objects.requireNonNull(tokens, "tokens must not be null");

    if (tokens.length == 0) {
      throw new IllegalArgumentException("tokens must not be empty");
    }

    this.tokens = new String[tokens.length];

    for (int i = 0; i < tokens.length; i++) {
      this.tokens[i] = tokens[i].intern();
    }
  }

  public String getToken(int index) {
    return tokens[index];
  }

  
  public int size() {
    return tokens.length;
  }

  public Iterator<String> iterator() {
    return new Iterator<String>() {

      private int index;

      public boolean hasNext() {
        return index < size();
      }

      public String next() {

        if (hasNext()) {
          return getToken(index++);
        }
        else {
          throw new NoSuchElementException();
        }
      }

      public void remove() {
        throw new UnsupportedOperationException();
      }

    };
  }

  public boolean compareToIgnoreCase(StringList tokens) {

    if (size() == tokens.size()) {
      for (int i = 0; i < size(); i++) {

        if (getToken(i).compareToIgnoreCase(
            tokens.getToken(i)) != 0) {
          return false;
        }
      }
    }
    else {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(tokens);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj instanceof StringList) {
      StringList tokenList = (StringList) obj;

      return Arrays.equals(tokens, tokenList.tokens);
    }

    return false;
  }

  @Override
  public String toString() {
    StringBuilder string = new StringBuilder();

    string.append('[');

    for (int i = 0; i < size(); i++) {
      string.append(getToken(i));

      if (i < size() - 1) {
        string.append(',');
      }
    }

    string.append(']');

    return string.toString();
  }
}
