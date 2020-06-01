package cz.zcu.kiv.nlp.ir.trec.search.searcher;

import cz.zcu.kiv.nlp.ir.trec.Index.preprocesing.Preprocessing;
import cz.zcu.kiv.nlp.ir.trec.data.DocumentNew;
import cz.zcu.kiv.nlp.ir.trec.data.invertedIndex.*;
import cz.zcu.kiv.nlp.ir.trec.data.result.Result;
import cz.zcu.kiv.nlp.ir.trec.data.result.ResultImpl;
import cz.zcu.kiv.nlp.ir.trec.model.CosineSimilarity;
import cz.zcu.kiv.nlp.ir.trec.model.VectorSpaceModel;

import java.util.*;

/**
 * Searcher which is using vector space model
 */
public class VectorSpaceModelSearcher implements Searcher
{
    /**
     * Preprocessing
     */
    private Preprocessing preprocessing;

    /**
     * Inverted index for documents
     */
    private InvertedIndex invertedIndex;

    /**
     * Inverted index for query
     */
    private QueryInvertedIndex queryInvertedIndex = new QueryInvertedIndex();

    /**
     * Constant for query index
     */
    final private String QUERY_ID = "query";

    /**
     * Result limit
     * if -1, limit is not set
     */
    private int limitResultCount = -1;

    private int totalResultCount;

    /**
     * Instantiates a new Vector space model searcher.
     *
     * @param preprocessing the preprocessing
     * @param invertedIndex the inverted index
     */
    public VectorSpaceModelSearcher(Preprocessing preprocessing, InvertedIndex invertedIndex)
    {
        this.preprocessing = preprocessing;
        this.invertedIndex = invertedIndex;
    }

    /**
     * Search method
     * @param query given query
     * @return list of results
     */
    @Override
    public List<Result> search(String query)
    {
        // index query
        DocumentBag queryDocumentBag = this.indexQuery(query);

        // get relative documents for query
        Set<DocumentBag> relevantDocumentBags = this.invertedIndex.getRelevantDocumentBagsForQuery(queryDocumentBag.getWords());

        return this.getResults(relevantDocumentBags, queryDocumentBag);
    }

    /**
     * Sets result limit
     * @param resultCount limit
     */
    @Override
    public void setResultCount(int resultCount)
    {
        this.limitResultCount = resultCount;
    }

    /**
     * Index query to index
     * @param query given query
     * @return document bag
     */
    private DocumentBag indexQuery(String query){

        DocumentBag documentBag = new DocumentBag(this.QUERY_ID);

        // save words
        for (String word : this.preprocessing.processText(query))
        {
            documentBag.addWord(word);

            this.queryInvertedIndex.add(word, documentBag);
        }

        // set up document count
        this.queryInvertedIndex.setIndexedDocumentCount(1);

        // count TF-IDF
        this.setUpQueryTFIDF(documentBag);

        // set up euclid value
        this.queryInvertedIndex.setUpDocumentEuclidValueBag(documentBag);

        return documentBag;
    }

    /**
     * Set up TF-IDF values for query
     * @param queryDocumentBag document bag
     */
    private void setUpQueryTFIDF(DocumentBag queryDocumentBag)
    {
        // get word intersection (query x inverse index)
        Set<InvertedIndexItem> invertedIndexItems = this.invertedIndex.getSpecificWords(queryDocumentBag.getWords());

        // calculate scales
        // loop is ignoring words which is not in intersection
        for (InvertedIndexItem indexItem : invertedIndexItems)
        {
            InvertedIndexItem queryItem = this.queryInvertedIndex.getIndexItem(indexItem.getWord());

            // word stat query
            WordStats queryWordStat = queryItem.getDocumentWordStat(this.QUERY_ID);

            // set tf-idf value
            // query TF, word in invert index idf
            queryWordStat.setTfIdf(VectorSpaceModel.getTFIDF(queryWordStat.getTf(), indexItem.getIdf()));
        }
    }

    /**
     * Gets euclid value for query multiplied by index
     * @param relevantDocumentBag
     * @param queryDocumentBag
     * @return
     */
    private float getDocumentAndQueryScalar(DocumentBag relevantDocumentBag, DocumentBag queryDocumentBag)
    {
        // get word intersection (query x inverse index)
        Set<InvertedIndexItem> invertedIndexItems = this.invertedIndex.getSpecificWords(queryDocumentBag.getWords());

        float scalar = 0;

        // loop through shared items
        for (InvertedIndexItem item : invertedIndexItems)
        {
            // tf idf value from inverse index
            float relevantDocumentWordTfIdf = this.invertedIndex.getDocumentWordTFIDF(item.getWord(), relevantDocumentBag.getId());

            // tf-idf from query index
            float queryDocumentWordTfIdf = this.queryInvertedIndex.getDocumentWordTFIDF(item.getWord(), this.QUERY_ID);

            scalar += (relevantDocumentWordTfIdf * queryDocumentWordTfIdf);
        }

        return scalar;
    }

    /**
     * Gets relative results, using cosine similarity
     * @param relevantDocuments relevant documents
     * @param queryDocument query document bag
     * @return
     */
    private List<Result> getResults(Set<DocumentBag> relevantDocuments, DocumentBag queryDocument)
    {
        List<Result> tmp = new ArrayList<>();

        // count cosine similarity for relevant documents
        for (DocumentBag relevantDocument : relevantDocuments)
        {
            // document and query scalar
            float documentAndQueryScalar = this.getDocumentAndQueryScalar(relevantDocument, queryDocument);

            // cosine similarity
            float cosineSimilarity = CosineSimilarity.getCosineSimilarity(documentAndQueryScalar, relevantDocument.getEuclidDocumentValue(), queryDocument.getEuclidDocumentValue());

            // add result
            tmp.add(new ResultImpl(relevantDocument.getId(), cosineSimilarity));
        }

        // comparator by score
        Comparator<Result> cmp = new Comparator<Result>() {
            public int compare(Result o1, Result o2) {
                if (o1.getScore() > o2.getScore()) return -1;
                if (o1.getScore() == o2.getScore()) return 0;
                return 1;
            }
        };

        Collections.sort(tmp, cmp);

        // set up rank
        int i = 1;
        for (Result result : tmp)
        {
            ((ResultImpl)result).setRank(i);
            i++;
        }

        this.totalResultCount = tmp.size();

        if (limitResultCount < 0 || tmp.size() < this.limitResultCount)
        {
            return tmp;
        }

        return tmp.subList(0, this.limitResultCount);
    }

    /**
     * Return result total count
     * @return total count
     */
    public int getTotalResultCount()
    {
        return totalResultCount;
    }



}
