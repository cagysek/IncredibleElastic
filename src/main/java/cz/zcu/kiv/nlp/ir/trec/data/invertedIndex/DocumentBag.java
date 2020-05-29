package cz.zcu.kiv.nlp.ir.trec.data.invertedIndex;

import java.io.Serializable;
import java.util.HashSet;

public class DocumentBag implements Serializable
{
    final static long serialVersionUID = -4201115898427114420L;

    private String id;

    private HashSet<String> words = new HashSet<>();

    private float euclidDocumentValue = 0;

    public DocumentBag(String id)
    {
        this.id = id;
    }

    public void addWord(String word)
    {
        this.words.add(word);
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public HashSet<String> getWords()
    {
        return words;
    }

    public float getEuclidDocumentValue()
    {
        return euclidDocumentValue;
    }

    public void setEuclidDocumentValue(float euclidDocumentValue)
    {
        this.euclidDocumentValue = euclidDocumentValue;
    }
}
