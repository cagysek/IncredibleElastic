package cz.zcu.kiv.nlp.ir.trec.data.dictionary;

import java.io.Serializable;
import java.util.HashMap;

public class DictionaryItem implements Serializable
{
    final static long serialVersionUID = -5097715898427114008L;

    private HashMap<String, WordStats> documentWordStats = new HashMap<>();


    private float idf;


    public DictionaryItem(String documentId)
    {
        this.documentWordStats.put(documentId, new WordStats());
    }

    public HashMap<String, WordStats> getDocumentWordStats()
    {
        return documentWordStats;
    }

    public void addWordStats(String documentId)
    {
        if (this.documentWordStats.containsKey(documentId))
        {
            this.documentWordStats.get(documentId).incrementCount();
        }
        else
        {
            this.documentWordStats.put(documentId, new WordStats());
        }
    }

    public int getDf()
    {
        return this.documentWordStats.size();
    }

    public float getIdf()
    {
        return idf;
    }

    public void setIdf(float idf)
    {
        this.idf = idf;
    }
}
