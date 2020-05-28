package cz.zcu.kiv.nlp.ir.trec.data.dictionary;

import java.io.Serializable;

public class WordStats implements Serializable
{
    final static long serialVersionUID = -4201115898427114008L;

    private int count = 1;

    private float logTf;

    private float tfIdf;

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

    public float getLogTf()
    {
        return logTf;
    }

    public void setLogTf(float logTf)
    {
        this.logTf = logTf;
    }

    public float getTfIdf()
    {
        return tfIdf;
    }

    public void setTfIdf(float tfIdf)
    {
        this.tfIdf = tfIdf;
    }
}
