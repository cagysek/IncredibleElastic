package cz.zcu.kiv.nlp.ir.trec.data.invertedIndex;

import java.io.Serializable;
import java.util.HashSet;

/**
 * The type Document bag.
 * Reference for document shared across all wordStats. Contains set of words for faster indexing.
 * From word and document id in inverted index its possible to get other words in document -> faster calculation of euclid value
 */
public class DocumentBag implements Serializable
{
    /**
     * The constant serialVersionUID.
     */
    final static long serialVersionUID = -4201115898427114420L;

    /**
     * The document id
     */
    private String id;

    /**
     * The set of words in document
     */
    private HashSet<String> words = new HashSet<>();

    private float euclidDocumentValue = 0;

    /**
     * Instantiates a new Document bag.
     *
     * @param id the id
     */
    public DocumentBag(String id)
    {
        this.id = id;
    }

    /**
     * Add word to document set.
     *
     * @param word the word
     */
    public void addWord(String word)
    {
        this.words.add(word);
    }

    /**
     * Gets document id.
     *
     * @return the id
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets document id.
     *
     * @param id the id
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Gets words.
     *
     * @return the words
     */
    public HashSet<String> getWords()
    {
        return words;
    }

    /**
     * Gets euclid document value.
     *
     * @return the euclid document value
     */
    public float getEuclidDocumentValue()
    {
        return euclidDocumentValue;
    }

    /**
     * Sets euclid document value.
     *
     * @param euclidDocumentValue the euclid document value
     */
    public void setEuclidDocumentValue(float euclidDocumentValue)
    {
        this.euclidDocumentValue = euclidDocumentValue;
    }
}
