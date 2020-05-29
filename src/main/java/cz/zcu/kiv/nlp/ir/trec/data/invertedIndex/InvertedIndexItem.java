package cz.zcu.kiv.nlp.ir.trec.data.invertedIndex;

import java.io.Serializable;
import java.util.HashMap;

public class InvertedIndexItem implements Serializable
{
    final static long serialVersionUID = -5097715898427114008L;

    private HashMap<String, WordStats> documentWordStats = new HashMap<>();


    private float idf;


    public InvertedIndexItem(DocumentBag documentBag)
    {
        this.documentWordStats.put(documentBag.getId(), new WordStats(documentBag));
    }

    public HashMap<String, WordStats> getDocumentWordStats()
    {
        return documentWordStats;
    }

    public void addWordStats(DocumentBag documentBag)
    {
        if (this.documentWordStats.containsKey(documentBag.getId()))
        {
            this.documentWordStats.get(documentBag.getId()).incrementCount();
        }
        else
        {
            this.documentWordStats.put(documentBag.getId(), new WordStats(documentBag));
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
