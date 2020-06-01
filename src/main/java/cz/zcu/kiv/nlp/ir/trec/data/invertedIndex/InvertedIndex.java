package cz.zcu.kiv.nlp.ir.trec.data.invertedIndex;

import cz.zcu.kiv.nlp.ir.trec.data.enums.ELoaderType;
import cz.zcu.kiv.nlp.ir.trec.model.VectorSpaceModel;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Represents invert index, contains map <term,InvertedIndexItem>
 */
public class InvertedIndex implements Serializable
{
    /**
     * The Inverted index.
     * pair term, InvertedIndexItem
     */
    protected HashMap<String, InvertedIndexItem> invertedIndex = new HashMap<>();

    /**
     * The constant serialVersionUID.
     */
    final static long serialVersionUID = -5097715898427114010L;

    /**
     * How many documents are indexed
     */
    private int indexedDocumentCount = 0;

    /**
     * Which data is stored
     */
    private ELoaderType dataType;

    /**
     * Add item/increase term count to/in map
     *
     * @param word        the word
     * @param documentBag the document bag reference for document
     */
    public void add(String word, DocumentBag documentBag)
    {
        // if map contains key, increase count in wordStats
        if (this.invertedIndex.containsKey(word))
        {
            this.invertedIndex.get(word).addWordStats(documentBag);
        }
        else
        {
            this.invertedIndex.put(word, new InvertedIndexItem(word, documentBag));
        }
    }

    /**
     * Sets up idf.
     *
     * @param item the item
     */
    public void setUpIDF(InvertedIndexItem item)
    {
        item.setIdf(VectorSpaceModel.getIDF(this.indexedDocumentCount, item.getDf()));
    }

    /**
     * Sets up tfidf.
     *
     * @param item the item
     */
    public void setUpTFIDF(InvertedIndexItem item)
    {
        for (String documentId : item.getDocumentWordStats().keySet())
        {
            WordStats wordStat = item.getDocumentWordStats().get(documentId);

            wordStat.setTfIdf(VectorSpaceModel.getTFIDF(wordStat.getTf(), item.getIdf()));
        }
    }

    /**
     * Sets up dictionary item scales.
     */
    public void setUpDictionaryItemScales()
    {
        for (String word: invertedIndex.keySet()){

            InvertedIndexItem item = invertedIndex.get(word);

            this.setUpIDF(item);

            this.setUpTFIDF(item);
        }
    }

    /**
     * Calculate euclid value for all documents
     *
     * @param documentBags the document bags list of references to documents
     */
    public void setUpDocumentEuclidValueList(ArrayList<DocumentBag> documentBags)
    {
        for (DocumentBag documentBag : documentBags)
        {
            this.setUpDocumentEuclidValueBag(documentBag);
        }
    }

    /**
     * Sets up document euclid value for single document.
     *
     * @param documentBag the document bag
     */
    public void setUpDocumentEuclidValueBag(DocumentBag documentBag)
    {
        float euclidValue = 0;
        for (String documentWord : documentBag.getWords())
        {
            float wordDocumentTfIdgValue = this.getDocumentWordTFIDF(documentWord, documentBag.getId());
            euclidValue += Math.pow(wordDocumentTfIdgValue, 2);
        }

        documentBag.setEuclidDocumentValue((float)Math.sqrt(euclidValue));
    }

    /**
     * Gets document word tfidf.
     *
     * @param word       the word
     * @param documentId the document id
     * @return the document word tfidf
     */
    public float getDocumentWordTFIDF(String word, String documentId)
    {
        if (this.invertedIndex.containsKey(word))
        {
            return this.invertedIndex.get(word).getDocumentWordTFIDF(documentId);
        }

        return 0;

    }

    /**
     * Gets specific words from index.
     *
     * @param words the words
     * @return the specific words
     */
    public Set<InvertedIndexItem> getSpecificWords(Set<String> words)
    {
        Set<InvertedIndexItem> items = new HashSet<>();

        for (String word : words)
        {
            if (this.invertedIndex.containsKey(word))
            {
                items.add(this.invertedIndex.get(word));
            }
        }

        return items;
    }

    /**
     * Gets relevant document bags for query.
     *
     * @param queryWords the query words
     * @return the relevant document bags for query
     */
    public Set<DocumentBag> getRelevantDocumentBagsForQuery(Set<String> queryWords)
    {
        Set<DocumentBag> documentBags = new HashSet<>();

        // postupně projdu slova v query a najdu k nim slova v indexu (invertedItemIndex)
        for (String word : queryWords)
        {
            if (this.invertedIndex.containsKey(word))
            {
                InvertedIndexItem item = this.invertedIndex.get(word);

                // z jednotlivých invertedItemIndex projdu dokumenty v kterých se nachází slova
                for (String documentId : item.getDocumentWordStats().keySet())
                {
                    // budu ukládat jejich bagy
                    WordStats wordStat = item.getDocumentWordStat(documentId);

                    documentBags.add(wordStat.getDocumentBag());
                }
            }
        }

        return documentBags;
    }

    /**
     * Gets index item by word.
     *
     * @param word the word
     * @return the index item
     */
    public InvertedIndexItem getIndexItem(String word)
    {
        if (this.invertedIndex.containsKey(word))
        {
            return this.invertedIndex.get(word);
        }

        return null;
    }

    /**
     * Gets all documents ids.
     *
     * @return the all documents ids
     */
    public Set<String> getAllDocumentsIds()
    {
        Set<String> documentIds = new HashSet<>();

        for (String term : invertedIndex.keySet())
        {
            documentIds.addAll(invertedIndex.get(term).getDocumentsIds());
        }

        return documentIds;
    }

    /**
     * Gets indexed document count.
     *
     * @return the indexed document count
     */
    public int getIndexedDocumentCount()
    {
        return indexedDocumentCount;
    }

    /**
     * Sets indexed document count.
     *
     * @param count the count
     */
    public void setIndexedDocumentCount(int count)
    {
        this.indexedDocumentCount = count;
    }

    /**
     * Gets inverted index size.
     *
     * @return the inverted index size
     */
    public int getInvertedIndexSize()
    {
        return this.invertedIndex.size();
    }

    /**
     * Gets data type.
     *
     * @return the data type
     */
    public ELoaderType getDataType()
    {
        return dataType;
    }

    /**
     * Sets data type.
     *
     * @param dataType the data type
     */
    public void setDataType(ELoaderType dataType)
    {
        this.dataType = dataType;
    }
}
