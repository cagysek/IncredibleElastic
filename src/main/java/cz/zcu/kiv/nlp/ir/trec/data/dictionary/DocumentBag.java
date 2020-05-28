package cz.zcu.kiv.nlp.ir.trec.data.dictionary;

import java.io.Serializable;
import java.util.ArrayList;

public class DocumentBag implements Serializable
{
    final static long serialVersionUID = -4201115898427114420L;

    private String id;

    private ArrayList<String> words = new ArrayList<>();

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

    public ArrayList<String> getWords()
    {
        return words;
    }

    public void setWords(ArrayList<String> words)
    {
        this.words = words;
    }
}
