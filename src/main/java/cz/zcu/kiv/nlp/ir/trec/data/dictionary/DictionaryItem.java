package cz.zcu.kiv.nlp.ir.trec.data.dictionary;

import java.io.Serializable;
import java.util.HashSet;

public class DictionaryItem implements Serializable
{
    private int totalCount = 1;

    private HashSet<String> inDocumentList = new HashSet<>();

    final static long serialVersionUID = -5097715898427114008L;

    private float idf;


    public DictionaryItem(String documentId)
    {
        this.inDocumentList.add(documentId);
    }

    public int getTotalCount()
    {
        return totalCount;
    }

    public HashSet<String> getInLists()
    {
        return inDocumentList;
    }

    public void incrementTotalCount()
    {
        this.totalCount = this.totalCount + 1;
    }

    public void addDocumentId(String id)
    {
        this.inDocumentList.add(id);
    }

    public int getDf()
    {
        return this.inDocumentList.size();
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
