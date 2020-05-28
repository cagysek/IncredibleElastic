package cz.zcu.kiv.nlp.ir.trec.Index;

import cz.zcu.kiv.nlp.ir.trec.Index.preprocesing.Preprocessing;
import cz.zcu.kiv.nlp.ir.trec.data.dictionary.Dictionary;
import cz.zcu.kiv.nlp.ir.trec.data.dictionary.DocumentBag;
import cz.zcu.kiv.nlp.ir.trec.data.document.Document;
import cz.zcu.kiv.nlp.ir.trec.data.result.Result;
import cz.zcu.kiv.nlp.ir.trec.search.searcher.Searcher;
import cz.zcu.kiv.nlp.ir.trec.utils.SerializedDataHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tigi
 *
 * Třída reprezentující index.
 *
 * Tuto třídu doplňte tak aby implementovala rozhranní {@link Indexer} a {@link Searcher}.
 * Pokud potřebujete, přidejte další rozhraní, která tato třída implementujte nebo
 * přidejte metody do rozhraní {@link Indexer} a {@link Searcher}.
 *
 *
 */
public class Index implements Indexer, Searcher
{

    private Preprocessing preprocessing;

    private Dictionary dictionary = new Dictionary();

    public Index(Preprocessing preprocessing)
    {
        this.preprocessing = preprocessing;
    }

    /**
     * Zaindexuje předané dokumenty do slovníku
     * @param documents list dokumentů
     */
    public void index(List<Document> documents)
    {
        if (true)
        {
            // temp list pro držení referencí na dokumenty pro pozdější průchod pro vypočítání střední hodnot dokumentů
            ArrayList<DocumentBag> documentBags = new ArrayList<>();

            for (Document document : documents)
            {
                DocumentBag documentBag = new DocumentBag(document.getId());

                for (String word : this.preprocessing.process(document))
                {
                    documentBag.addWord(word);
                    documentBags.add(documentBag);

                    this.dictionary.add(word, documentBag);
                }
            }

            // nastavím do dictionary počet dokumentů kolik se indexovalo
            this.dictionary.setIndexedDocumentCount(documents.size());

            // reindex IDF
            this.dictionary.setUpDictionaryItemScales();

        //    SerializedDataHelper.saveIndex(this.dictionary);
        }
        else
        {
            System.out.println("Načítám data");
            this.dictionary = SerializedDataHelper.loadIndex();
            System.out.println("Načteno");

            this.dictionary.print();
        }



    }






    public List<Result> search(String query)
    {
        //  todo implement
        return null;
    }
}
