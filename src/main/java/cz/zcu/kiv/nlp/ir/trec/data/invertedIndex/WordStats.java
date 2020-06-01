package cz.zcu.kiv.nlp.ir.trec.data.invertedIndex;

import java.io.Serializable;

/**
 * Statistics for single word in document
 * contains information how many times document contains word, reference for document, ....
 */
public class WordStats implements Serializable
{
    /**
     * The constant serialVersionUID.
     */
    final static long serialVersionUID = -4201115898427114008L;

    /**
     * How many times is word in document
     */
    private int count = 1;

    /**
     * TF IDF value
     */
    private float tfIdf;

    /**
     * Reference for document
     */
    private DocumentBag documentBag;

    /**
     * Gets count.
     *
     * @return the count
     */
    public int getCount()
    {
        return count;
    }

    /**
     * Increment count.
     */
    public void incrementCount()
    {
        this.count = this.count + 1;
    }

    /**
     * Gets tf.
     *
     * @return the tf
     */
    public int getTf()
    {
        return count;
    }

    /**
     * Gets tf idf.
     *
     * @return the tf idf
     */
    public float getTfIdf()
    {
        return tfIdf;
    }

    /**
     * Sets tf idf.
     *
     * @param tfIdf the tf idf
     */
    public void setTfIdf(float tfIdf)
    {
        this.tfIdf = tfIdf;
    }

    /**
     * Instantiates a new Word stats.
     *
     * @param documentBag the document bag
     */
    public WordStats(DocumentBag documentBag)
    {
        this.documentBag = documentBag;
    }

    /**
     * Gets document bag.
     *
     * @return the document bag
     */
    public DocumentBag getDocumentBag()
    {
        return documentBag;
    }

    /**
     * Sets document bag.
     *
     * @param documentBag the document bag
     */
    public void setDocumentBag(DocumentBag documentBag)
    {
        this.documentBag = documentBag;
    }
}
