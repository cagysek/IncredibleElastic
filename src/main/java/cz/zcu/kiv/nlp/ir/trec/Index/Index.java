package cz.zcu.kiv.nlp.ir.trec.Index;

import cz.zcu.kiv.nlp.ir.trec.Index.preprocesing.Preprocessing;
import cz.zcu.kiv.nlp.ir.trec.data.enums.ELoaderType;
import cz.zcu.kiv.nlp.ir.trec.data.invertedIndex.InvertedIndex;
import cz.zcu.kiv.nlp.ir.trec.data.invertedIndex.DocumentBag;
import cz.zcu.kiv.nlp.ir.trec.data.document.Document;
import cz.zcu.kiv.nlp.ir.trec.data.result.Result;
import cz.zcu.kiv.nlp.ir.trec.search.searcher.Searcher;
import cz.zcu.kiv.nlp.ir.trec.utils.IOUtils;
import cz.zcu.kiv.nlp.ir.trec.utils.SerializedDataHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Index.
 *
 * @author tigi Třída reprezentující index.
 */
public class Index implements Indexer
{

    private Preprocessing preprocessing;

    private InvertedIndex invertedIndex = new InvertedIndex();

    /**
     * Instantiates a new Index.
     *
     * @param preprocessing the preprocessing
     */
    public Index(Preprocessing preprocessing)
    {
        this.preprocessing = preprocessing;
    }

    /**
     * Zaindexuje předané dokumenty do slovníku
     * @param documents list dokumentů
     */
    public void index(List<Document> documents, ELoaderType loaderType)
    {
        this.invertedIndex = new InvertedIndex();

        System.out.println("Začínám indexovat");

        // temp list for keep references on documents for later iterating for calculate euclid document values
        ArrayList<DocumentBag> documentBags = new ArrayList<>();

        // iterate given documents
        for (Document document : documents)
        {
            // create document bag (reference for document, shared across all words. Used for obtain from one word other words in document)
            // reference for document stored in all his words.
            DocumentBag documentBag = new DocumentBag(document.getId());

            // adds all words from document to index
            for (String word : this.preprocessing.processDocument(document))
            {
                documentBag.addWord(word);

                this.invertedIndex.add(word, documentBag);
            }

            // save reference to list
            documentBags.add(documentBag);
        }

        System.out.println("Zaindexováno dokumentů: " + documents.size() + ". Počet slov: " + this.invertedIndex.getInvertedIndexSize());

        // set up how many documents is indexed
        this.invertedIndex.setIndexedDocumentCount(documents.size());

        System.out.println("Nastavuji váhy IDF");
        // reindex IDF
        this.invertedIndex.setUpDictionaryItemScales();

        System.out.println("Nastavuji euklidovské střední hodnoty");

        // calculate euclid values for all documents
        // this is why documents bags are stored to list (you dont need iterate over all word and search unique documents ids)
        this.invertedIndex.setUpDocumentEuclidValueList(documentBags);

        // set up which data was loaded
        this.invertedIndex.setDataType(loaderType);

        System.out.println("Ukládám index");

        // save index to file
        SerializedDataHelper.saveIndex(this.invertedIndex);

    }

    /**
     * Loads index stored in file
     */
    public void loadIndex()
    {
        System.out.println("Načítám data");

        this.invertedIndex = SerializedDataHelper.loadIndex();

        System.out.println("Načteno");
    }


    /**
     * Sets data type.
     *
     * @param type the type
     */
    public void setDataType(ELoaderType type)
    {
        this.invertedIndex.setDataType(type);
    }

    /**
     * Gets data type
     * @return type
     */
    public ELoaderType getDataType()
    {
        return this.invertedIndex.getDataType();
    }

    /**
     * Gets inverted index
     * @return inverted index
     */
    public InvertedIndex getInvertedIndex()
    {
        return invertedIndex;
    }
}
