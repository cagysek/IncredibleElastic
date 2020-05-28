package cz.zcu.kiv.nlp.ir.trec.data.dictionary;

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
            this.dictionary.get(word).incrementTotalCount();
            this.dictionary.get(word).addDocumentId(documentId);
        }
        else
        {
            this.dictionary.put(word, new DictionaryItem(documentId));
        }
    }

    public void reindexIDF()
    {
        for (String name: dictionary.keySet()){

            DictionaryItem item = dictionary.get(name);

            item.setIdf( (float) (Math.log10( (float)(this.indexedDocumentCount / item.getDf()) )) );
        }
    }

    public void print()
    {
        for (String name: dictionary.keySet()){
            String key = name.toString();
            int value = dictionary.get(name).getTotalCount();

            String ids = "";
            for (String id : dictionary.get(name).getInLists())
            {
                ids = ids + ", " + id;
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
