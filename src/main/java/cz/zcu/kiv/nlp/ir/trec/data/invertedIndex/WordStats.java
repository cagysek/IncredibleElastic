package cz.zcu.kiv.nlp.ir.trec.data.invertedIndex;

import java.io.Serializable;

public class WordStats implements Serializable
{
    final static long serialVersionUID = -4201115898427114008L;

    private int count = 1;

    private float logTf;

    private float tfIdf;

    private DocumentBag documentBag;

    public int getCount()
    {
        return count;
    }

    public void incrementCount()
    {
        this.count = this.count + 1;
    }

    public int getTf()
    {
        return count;
    }

    public float getTfIdf()
    {
        return tfIdf;
    }

    public void setTfIdf(float tfIdf)
    {
        this.tfIdf = tfIdf;
    }

    public WordStats(DocumentBag documentBag)
    {
        this.documentBag = documentBag;
    }

    public DocumentBag getDocumentBag()
    {
        return documentBag;
    }

    public void setDocumentBag(DocumentBag documentBag)
    {
        this.documentBag = documentBag;
    }
}
