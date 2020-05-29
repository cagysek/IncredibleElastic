package cz.zcu.kiv.nlp.ir.trec.data.invertedIndex;

import cz.zcu.kiv.nlp.ir.trec.model.VectorSpaceModel;

import java.io.Serializable;
import java.util.HashMap;

public class InvertedIndex implements Serializable
{
    private HashMap<String, InvertedIndexItem> invertedIndex = new HashMap<>();

    final static long serialVersionUID = -5097715898427114010L;

    private int indexedDocumentCount = 0;

    public void add(String word, DocumentBag documentBag)
    {
        if (this.invertedIndex.containsKey(word))
        {
            this.invertedIndex.get(word).addWordStats(documentBag);
        }
        else
        {
            this.invertedIndex.put(word, new InvertedIndexItem(documentBag));
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
        for (String name: invertedIndex.keySet()){

            InvertedIndexItem item = invertedIndex.get(name);

            this.setUpIDF(item);

            this.setUpTFIDF(item);
        }
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
}
