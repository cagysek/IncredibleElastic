package cz.zcu.kiv.nlp.ir.trec.data.invertedIndex;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * The type Inverted index item. Contains map <documentId, wordStat (statistics)>
 */
public class InvertedIndexItem implements Serializable
{
    /**
     * The constant serialVersionUID.
     */
    final static long serialVersionUID = -5097715898427114008L;

    /**
     * Map <documentId,WordStats>
     */
    private HashMap<String, WordStats> documentWordStats = new HashMap<>();

    /**
     * IDF value for word
     */
    private float idf;

    /**
     * word value (key value from InvertedIndex)
     */
    private String word;


    /**
     * Instantiates a new Inverted index item.
     *
     * @param word        the word
     * @param documentBag the document bag
     */
    public InvertedIndexItem(String word, DocumentBag documentBag)
    {
        this.word = word;
        this.documentWordStats.put(documentBag.getId(), new WordStats(documentBag));
    }

    /**
     * Gets document word stats.
     *
     * @return the document word stats
     */
    public HashMap<String, WordStats> getDocumentWordStats()
    {
        return documentWordStats;
    }

    /**
     * Add word stats.
     *
     * @param documentBag the document bag
     */
    public void addWordStats(DocumentBag documentBag)
    {
        // if document exists for term, increase count, else add document to term
        if (this.documentWordStats.containsKey(documentBag.getId()))
        {
            this.documentWordStats.get(documentBag.getId()).incrementCount();
        }
        else
        {
            this.documentWordStats.put(documentBag.getId(), new WordStats(documentBag));
        }
    }

    /**
     * Gets document word tfidf.
     *
     * @param documentId the document id
     * @return the document word tfidf
     */
    public float getDocumentWordTFIDF(String documentId)
    {
        if (this.documentWordStats.containsKey(documentId))
        {
            return this.documentWordStats.get(documentId).getTfIdf();
        }

        return 0;
    }

    /**
     * Gets document word stat.
     *
     * @param documentId the document id
     * @return the document word stat
     */
    public WordStats getDocumentWordStat(String documentId)
    {
        if (this.documentWordStats.containsKey(documentId))
        {
            return  this.documentWordStats.get(documentId);
        }

        return null;
    }

    /**
     * Gets documents ids.
     *
     * @return the documents ids
     */
    public Set<String> getDocumentsIds()
    {
        return new HashSet<>(this.documentWordStats.keySet());
    }

    /**
     * Gets df.
     *
     * @return the df
     */
    public int getDf()
    {
        return this.documentWordStats.size();
    }

    /**
     * Gets idf.
     *
     * @return the idf
     */
    public float getIdf()
    {
        return idf;
    }

    /**
     * Sets idf.
     *
     * @param idf the idf
     */
    public void setIdf(float idf)
    {
        this.idf = idf;
    }

    /**
     * Gets word.
     *
     * @return the word
     */
    public String getWord()
    {
        return word;
    }

}
