package cz.zcu.kiv.nlp.ir.trec.data.invertedIndex;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class InvertedIndexItem implements Serializable
{
    final static long serialVersionUID = -5097715898427114008L;

    private HashMap<String, WordStats> documentWordStats = new HashMap<>();


    private float idf;

    private String word;


    public InvertedIndexItem(String word, DocumentBag documentBag)
    {
        this.word = word;
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

    public float getDocumentWordTFIDF(String documentId)
    {
        if (this.documentWordStats.containsKey(documentId))
        {
            return this.documentWordStats.get(documentId).getTfIdf();
        }

        return 0;
    }

    public WordStats getDocumentWordStat(String documentId)
    {
        if (this.documentWordStats.containsKey(documentId))
        {
            return  this.documentWordStats.get(documentId);
        }

        return null;
    }

    public Set<String> getDocumentsIds()
    {
        return new HashSet<>(this.documentWordStats.keySet());
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

    public String getWord()
    {
        return word;
    }

}
