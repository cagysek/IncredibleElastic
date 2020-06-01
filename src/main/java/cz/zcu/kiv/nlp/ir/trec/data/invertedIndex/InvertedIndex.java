package cz.zcu.kiv.nlp.ir.trec.data.invertedIndex;

import cz.zcu.kiv.nlp.ir.trec.data.enums.ELoaderType;
import cz.zcu.kiv.nlp.ir.trec.model.VectorSpaceModel;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;

public class InvertedIndex implements Serializable
{
    protected HashMap<String, InvertedIndexItem> invertedIndex = new HashMap<>();

    final static long serialVersionUID = -5097715898427114010L;

    private int indexedDocumentCount = 0;

    private ELoaderType dataType;

    public void add(String word, DocumentBag documentBag)
    {
        if (this.invertedIndex.containsKey(word))
        {
            this.invertedIndex.get(word).addWordStats(documentBag);
        }
        else
        {
            this.invertedIndex.put(word, new InvertedIndexItem(word, documentBag));
        }
    }

    public void setUpIDF(InvertedIndexItem item)
    {
        item.setIdf(VectorSpaceModel.getIDF(this.indexedDocumentCount, item.getDf()));
    }

    public void setUpTFIDF(InvertedIndexItem item)
    {
        for (String documentId : item.getDocumentWordStats().keySet())
        {
            WordStats wordStat = item.getDocumentWordStats().get(documentId);

            wordStat.setTfIdf(VectorSpaceModel.getTFIDF(wordStat.getTf(), item.getIdf()));
        }
    }

    public void setUpDictionaryItemScales()
    {
        for (String word: invertedIndex.keySet()){

            InvertedIndexItem item = invertedIndex.get(word);

            this.setUpIDF(item);

            this.setUpTFIDF(item);
        }
    }

    /**
     * Vypočítá euklidovskou hodnotu ke každému dokumentu
     * @param documentBags
     */
    public void setUpDocumentEuclidValueList(ArrayList<DocumentBag> documentBags)
    {
        for (DocumentBag documentBag : documentBags)
        {
            this.setUpDocumentEuclidValueBag(documentBag);
        }
    }

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

    public float getDocumentWordTFIDF(String word, String documentId)
    {
        if (this.invertedIndex.containsKey(word))
        {
            return this.invertedIndex.get(word).getDocumentWordTFIDF(documentId);
        }

        return 0;

    }

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

    public InvertedIndexItem getIndexItem(String word)
    {
        if (this.invertedIndex.containsKey(word))
        {
            return this.invertedIndex.get(word);
        }

        return null;
    }

    public Set<String> getAllDocumentsIds()
    {
        Set<String> documentIds = new HashSet<>();

        for (String term : invertedIndex.keySet())
        {
            documentIds.addAll(invertedIndex.get(term).getDocumentsIds());
        }

        return documentIds;
    }

    public void print()
    {
        for (String name: invertedIndex.keySet()){
            String key = name.toString();
            int value = invertedIndex.get(name).getDocumentWordStats().size();

            String ids = "";
            HashMap<String, WordStats> map = invertedIndex.get(name).getDocumentWordStats();
            for (String stats : map.keySet())
            {
                WordStats sta = map.get(stats);
                ids = ids + " " + stats + " count " + sta.getCount();
            }

            System.out.println(key + " " + value + " " + ids);
        }
    }

    public int getIndexedDocumentCount()
    {
        return indexedDocumentCount;
    }

    public void setIndexedDocumentCount(int count)
    {
        this.indexedDocumentCount = count;
    }

    public int getInvertedIndexSize()
    {
        return this.invertedIndex.size();
    }

    public ELoaderType getDataType()
    {
        return dataType;
    }

    public void setDataType(ELoaderType dataType)
    {
        this.dataType = dataType;
    }
}
