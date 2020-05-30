package cz.zcu.kiv.nlp.ir.trec.search.searcher;

import cz.zcu.kiv.nlp.ir.trec.Index.preprocesing.Preprocessing;
import cz.zcu.kiv.nlp.ir.trec.data.DocumentNew;
import cz.zcu.kiv.nlp.ir.trec.data.invertedIndex.*;
import cz.zcu.kiv.nlp.ir.trec.data.result.Result;
import cz.zcu.kiv.nlp.ir.trec.data.result.ResultImpl;
import cz.zcu.kiv.nlp.ir.trec.model.CosineSimilarity;
import cz.zcu.kiv.nlp.ir.trec.model.VectorSpaceModel;

import java.util.*;

public class VectorSpaceModelSearcher implements Searcher
{
    private Preprocessing preprocessing;
    private InvertedIndex invertedIndex;

    private QueryInvertedIndex queryInvertedIndex = new QueryInvertedIndex();

    final private String QUERY_ID = "query";

    private int limitResultCount;

    public VectorSpaceModelSearcher(Preprocessing preprocessing, InvertedIndex invertedIndex)
    {
        this.preprocessing = preprocessing;
        this.invertedIndex = invertedIndex;
    }

    @Override
    public List<Result> search(String query)
    {
        List<Result> results = new ArrayList<>();

        // zaindexování query
        DocumentBag queryDocumentBag = this.indexQuery(query);

        // získání relevantních dokumentů ke query
        Set<DocumentBag> relevantDocumentBags = this.invertedIndex.getRelevantDocumentBagsForQuery(queryDocumentBag.getWords());

        return this.getResults(relevantDocumentBags, queryDocumentBag);
    }

    @Override
    public void setResultCount(int resultCount)
    {
        this.limitResultCount = resultCount;
    }

    /**
     * Zaindexuje slova z query do indexu
     * @param query
     * @return
     */
    private DocumentBag indexQuery(String query){

        DocumentBag documentBag = new DocumentBag(this.QUERY_ID);

        for (String word : this.preprocessing.processText(query))
        {
            documentBag.addWord(word);

            this.queryInvertedIndex.add(word, documentBag);
        }

        // nastavím do dictionary počet dokumentů kolik se indexovalo
        this.queryInvertedIndex.setIndexedDocumentCount(1);

        // nastavím váhy TF-IDF ve slovníku query
        this.setUpQueryTFIDF(documentBag);

        // nastavím euklidovskou střední hodnotu
        this.queryInvertedIndex.setUpDocumentEuclidValueBag(documentBag);

        return documentBag;
    }

    /**
     * Nastaví TF-IDF hodnoty v query indexu.
     * @param queryDocumentBag
     */
    private void setUpQueryTFIDF(DocumentBag queryDocumentBag)
    {
        // z původního indexu získám průnik slov s query
        Set<InvertedIndexItem> invertedIndexItems = this.invertedIndex.getSpecificWords(queryDocumentBag.getWords());

        // spočítám váhy (projdu jen společný složky)
        // úplně ignoruju ty slova co nejsou v původním indexu, viz getSpecificWords()
        for (InvertedIndexItem indexItem : invertedIndexItems)
        {
            InvertedIndexItem queryItem = this.queryInvertedIndex.getIndexItem(indexItem.getWord());

            WordStats queryWordStat = queryItem.getDocumentWordStat(this.QUERY_ID);

            queryWordStat.setTfIdf(VectorSpaceModel.getTFIDF(queryWordStat.getTf(), indexItem.getIdf()));
        }
    }

    private float getDocumentAndQueryScalar(DocumentBag relevantDocumentBag, DocumentBag queryDocumentBag)
    {
        // z původního indexu získám průnik slov s query
        Set<InvertedIndexItem> invertedIndexItems = this.invertedIndex.getSpecificWords(queryDocumentBag.getWords());

        float scalar = 0;

        for (InvertedIndexItem item : invertedIndexItems)
        {
            float relevantDocumentWordTfIdf = this.invertedIndex.getDocumentWordTFIDF(item.getWord(), relevantDocumentBag.getId());

            float queryDocumentWordTfIdf = this.queryInvertedIndex.getDocumentWordTFIDF(item.getWord(), this.QUERY_ID);

            scalar += (relevantDocumentWordTfIdf * queryDocumentWordTfIdf);
        }

        return scalar;
    }


    private List<Result> getResults(Set<DocumentBag> relevantDocuments, DocumentBag queryDocument)
    {
        List<Result> tmp = new ArrayList<>();

        for (DocumentBag relevantDocument : relevantDocuments)
        {
            float documentAndQueryScalar = this.getDocumentAndQueryScalar(relevantDocument, queryDocument);

            float cosineSimilarity = CosineSimilarity.getCosineSimilarity(documentAndQueryScalar, relevantDocument.getEuclidDocumentValue(), queryDocument.getEuclidDocumentValue());

            tmp.add(new ResultImpl(relevantDocument.getId(), cosineSimilarity));
        }

        Comparator<Result> cmp = new Comparator<Result>() {
            public int compare(Result o1, Result o2) {
                if (o1.getScore() > o2.getScore()) return -1;
                if (o1.getScore() == o2.getScore()) return 0;
                return 1;
            }
        };

        Collections.sort(tmp, cmp);

        int i = 1;
        for (Result result : tmp)
        {
            ((ResultImpl)result).setRank(i);
            i++;
        }

        return tmp;
        /*
        List<Result> results = tmp.subList(0, this.resultCount);

        int i = 1;
        for (Result result : results)
        {
            ((ResultImpl)result).setRank(i);
            i++;
        }

        return results;

         */
    }


}
