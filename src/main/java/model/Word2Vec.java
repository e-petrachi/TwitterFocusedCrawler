package model;

import java.util.TreeMap;

public class Word2Vec {
    private TreeMap<String,Integer> word2vec;

    public Word2Vec(){ }
    public Word2Vec(TreeMap<String,Integer> word2vec, int soglia){
        this.word2vec = new TreeMap<>();
        for (String key:word2vec.keySet()) {
            if (word2vec.get(key) >= soglia)
                this.word2vec.put(key,word2vec.get(key));
        }
    }

    public TreeMap<String, Integer> getWord2vec() {
        return word2vec;
    }

    public void setWord2vec(TreeMap<String, Integer> word2vec) {
        this.word2vec = word2vec;
    }
}
