package cz.zcu.kiv.nlp.ir.trec.Index;

import cz.zcu.kiv.nlp.ir.trec.Index.preprocesing.Preprocessing;
import cz.zcu.kiv.nlp.ir.trec.data.invertedIndex.InvertedIndex;
import cz.zcu.kiv.nlp.ir.trec.data.invertedIndex.DocumentBag;
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

    public InvertedIndex invertedIndex = new InvertedIndex();

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

                    this.invertedIndex.add(word, documentBag);
                }

                documentBags.add(documentBag);
            }

            // nastavím do dictionary počet dokumentů kolik se indexovalo
            this.invertedIndex.setIndexedDocumentCount(documents.size());

            // reindex IDF
            this.invertedIndex.setUpDictionaryItemScales();

        }
        else
        {
            System.out.println("Načítám data");
            this.invertedIndex = SerializedDataHelper.loadIndex();
            System.out.println("Načteno");

            this.invertedIndex.print();
        }



    }






    public List<Result> search(String query)
    {
        //  todo implement
        return null;
    }
}
