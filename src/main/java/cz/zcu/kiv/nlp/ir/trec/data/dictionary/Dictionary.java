package cz.zcu.kiv.nlp.ir.trec.data.dictionary;

import cz.zcu.kiv.nlp.ir.trec.model.VectorSpaceModel;

import java.io.Serializable;
import java.util.HashMap;

public class Dictionary implements Serializable
{
    private HashMap<String, DictionaryItem> dictionary = new HashMap<>();

    final static long serialVersionUID = -5097715898427114010L;

    private int indexedDocumentCount = 0;

    public void add(String word, String documentId)
    {
        if (this.dictionary.containsKey(word))
        {
            this.dictionary.get(word).addWordStats(documentId);
        }
        else
        {
            this.dictionary.put(word, new DictionaryItem(documentId));
        }
    }

    public void setUpIDF(DictionaryItem item)
    {
        item.setIdf(VectorSpaceModel.getIDF(this.indexedDocumentCount, item.getDf()));
    }

    public void setUpTFIDF(DictionaryItem item)
    {
        for (String documentId : item.getDocumentWordStats().keySet())
        {
            WordStats wordStat = item.getDocumentWordStats().get(documentId);

            wordStat.setTfIdf(VectorSpaceModel.getTFIDF(wordStat.getTf(), item.getIdf()));
        }
    }

    public void setUpDictionaryItemScales()
    {
        for (String name: dictionary.keySet()){

            DictionaryItem item = dictionary.get(name);

            this.setUpIDF(item);

            this.setUpTFIDF(item);
        }
    }

    public void print()
    {
        for (String name: dictionary.keySet()){
            String key = name.toString();
            int value = dictionary.get(name).getDocumentWordStats().size();

            String ids = "";
            HashMap<String, WordStats> map = dictionary.get(name).getDocumentWordStats();
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
