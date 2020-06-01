package cz.zcu.kiv.nlp.ir.trec.search.searcher;

import cz.zcu.kiv.nlp.ir.trec.Index.preprocesing.Preprocessing;
import cz.zcu.kiv.nlp.ir.trec.data.invertedIndex.InvertedIndex;
import cz.zcu.kiv.nlp.ir.trec.data.result.Result;
import cz.zcu.kiv.nlp.ir.trec.data.result.ResultImpl;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.precedence.PrecedenceQueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Searcher for Boolean query.
 */
public class BooleanSearcher implements Searcher
{
    /**
     * Inverted index
     */
    private final InvertedIndex invertedIndex;

    /**
     * Preprocessing
     */
    private final Preprocessing preprocessing;

    /**
     * Limit result count
     * if -1, limit is not used
     */
    private int limitResultCount = -1;

    /**
     * Total result count (before limitation)
     */
    private int totalResultCount;

    /**
     * Set of all document ids
     */
    private Set<String> allDocumentIds;

    /**
     * Instantiates a new Boolean searcher.
     *
     * @param preprocessing the preprocessing
     * @param invertedIndex the inverted index
     */
    public BooleanSearcher(Preprocessing preprocessing, InvertedIndex invertedIndex)
    {
        this.preprocessing = preprocessing;
        this.invertedIndex = invertedIndex;
    }

    /**
     * Search method
     * @param queryString boolean query
     * @return list of relevant results
     * @throws QueryNodeException
     */
    @Override
    public List<Result> search(String queryString) throws QueryNodeException
    {
        // loads all documents ids
        this.allDocumentIds = this.invertedIndex.getAllDocumentsIds();

        return this.getResults(queryString);
    }

    /**
     * Sets result limit
     * @param resultCount
     */
    @Override
    public void setResultCount(int resultCount)
    {
        this.limitResultCount = resultCount;
    }

    /**
     * Method for get relevant results
     * @param query given boolean query
     * @return
     * @throws QueryNodeException
     */
    private List<Result> getResults(String query) throws QueryNodeException
    {
            // lucene parser used here. PrecedenceQueryParser - takes care about boolean priorities
            PrecedenceQueryParser parser = new PrecedenceQueryParser();

           // query = "(czechia OR NOT aquarium) AND NOT (fish AND NOT tropical) OR NOT found";
            //query = "(sea AND NOT live) OR NOT (fish OR NOT popular)";
           // query = "(live OR NOT czechia) AND NOT (tropical AND NOT also)";
           // query = "found OR sea AND also OR (country AND NOT environment) AND NOT popular";
           // query = "";

            Query q = parser.parse(query, "");

            BooleanQuery booleanQuery = (BooleanQuery)q;

            Set<String> result = new HashSet<>();

            boolean firstLoop = true;

            BooleanClause.Occur lastOccur = null;

            // process clauses
            for (BooleanClause clause : booleanQuery.clauses())
            {
                Set<String> tmp;
                switch (clause.getOccur())
                {
                    case MUST:
                        lastOccur = BooleanClause.Occur.MUST;
                        tmp = this.processQuery(clause);
                        result = this.and(result, tmp, firstLoop);
                        break;
                    case SHOULD:
                        lastOccur = BooleanClause.Occur.SHOULD;
                        tmp = this.processQuery(clause);

                        result = this.or(result, tmp);
                        break;
                    case MUST_NOT:
                        tmp = this.processQuery(clause);

                        if (lastOccur == BooleanClause.Occur.SHOULD)
                        {
                            result = this.notOr(result, tmp);
                        }
                        else
                        {
                            result = this.notAnd(result, tmp, firstLoop);
                        }
                }

                firstLoop = false;
            }

            List<Result> tmp = new ArrayList<>();

            for (String id : result)
            {
                ResultImpl tmpResult = new ResultImpl(id);
                tmp.add(tmpResult);
            }

            this.totalResultCount = tmp.size();

            if (limitResultCount < 0 || tmp.size() < this.limitResultCount)
            {
                return tmp;
            }

            return tmp.subList(0, this.limitResultCount);

    }

    /**
     * Process recursive single clauses
     * @param booleanClause clause
     * @return Set of related ids for booleanClause
     */
    private Set<String> processQuery(BooleanClause booleanClause)
    {
            // if clause is term
            if (booleanClause.getQuery() instanceof TermQuery)
            {

                List<String> preprocessedTerm = this.preprocessing.processText(booleanClause.getQuery().toString());

                // if preprocessing removes word (e.g. word is stop word)
                if (preprocessedTerm.size() > 0)
                {
                    // is single term, always on 0 index
                    // get document ids which contains word
                    return this.getRelatedDocumentIds(preprocessedTerm.get(0));
                }
                else
                {
                    return new HashSet<>();
                }

            }
            // is boolean clause
            else
            {
                Set<String> result = new HashSet<>();

                BooleanQuery booleanQuery = (BooleanQuery)booleanClause.getQuery();
                BooleanClause.Occur lastOccur = null;

                int i = 0;
                for (BooleanClause clause : booleanQuery.clauses())
                {
                    switch (clause.getOccur())
                    {
                        case MUST:
                        {
                            lastOccur = BooleanClause.Occur.MUST;
                            // do AND
                            result = this.and(result, this.processQuery(clause), i == 0);

                            break;
                        }
                        case SHOULD:
                        {
                            lastOccur = BooleanClause.Occur.SHOULD;
                            // do OR
                            result = this.or(result, this.processQuery(clause));

                            break;
                        }
                        case MUST_NOT:
                        {
                            Set<String> tmp;
                            tmp = this.processQuery(clause);

                            if (lastOccur == BooleanClause.Occur.SHOULD)
                            {
                                // do OR NOT
                                result = this.notOr(result, tmp);
                            }
                            else
                            {
                                // do AND NOT
                                result = this.notAnd(result, tmp, i == 0);
                            }
                            break;
                        }
                    }
                    i++;
                }
                return result;
            }
    }

    /**
     * Returns document ids which contains work
     * @param term required word
     * @return set of document ids
     */
    private Set<String> getRelatedDocumentIds(String term)
    {
        if (this.invertedIndex.getIndexItem(term) != null)
        {
            return this.invertedIndex.getIndexItem(term).getDocumentsIds();
        }

        return new HashSet<>();
    }

    /**
     * NOT USED
     * Returns set of document ids which not contains word
     * @param term except word
     * @return set of documents
     */
    private Set<String> getDocumentIdsExceptTerm(String term)
    {
        if (this.invertedIndex.getIndexItem(term) == null)
        {
            return new HashSet<>(this.allDocumentIds);
        }

        Set<String> idsToExclude = this.invertedIndex.getIndexItem(term).getDocumentsIds();

        return new HashSet<>(this.allDocumentIds.stream()
                        .filter(str -> !idsToExclude.contains(str))
                        .collect(Collectors.toSet()));
    }

    /**
     * Or action - merge two sets
     * @param documentIds1 set of document ids 1
     * @param documentIds2 set of document ids 2
     * @return merged documents
     */
    private Set<String> or(Set<String> documentIds1, Set<String> documentIds2)
    {
        documentIds1.addAll(documentIds2);

        return documentIds1;
    }

    /**
     * And action - sets intersection
     * @param documentIds1 set of document ids 1
     * @param documentIds2 set of document ids 2
     * @param justReturnDocument2 if is first loop, final list of result is empty. Its used only for first loop, or it will be always empty
     * @return
     */
    private Set<String> and(Set<String> documentIds1, Set<String> documentIds2, boolean justReturnDocument2)
    {
        if (justReturnDocument2)
        {
            return documentIds2;
        }

        return this.and(documentIds1, documentIds2);
    }

    /**
     * And action - sets intersection
     * @param documentIds1 set of document ids 1
     * @param documentIds2 set of document ids 2
     * @return
     */
    private Set<String> and(Set<String> documentIds1, Set<String> documentIds2)
    {
        return new HashSet<>(documentIds1.stream()
                .filter(documentIds2::contains)
                .collect(Collectors.toSet()));
    }

    /**
     * AND NOT action, first do negation of documentIds2, after that, intersection them
     * @param documentIds1 set of document ids 1
     * @param documentIds2 set of document ids 2
     * @param justReturnDocument2 if is first loop, final list of result is empty. Its used only for first loop, or it will be always empty
     * @return list of documents ids
     */
    private Set<String> notAnd(Set<String> documentIds1, Set<String> documentIds2, boolean justReturnDocument2)
    {
        if (justReturnDocument2)
        {
            return documentIds2;
        }
        Set<String> notList = new HashSet<>(this.allDocumentIds);
        notList.removeAll(documentIds2);

        Set<String> results = new HashSet<>();

        for (String docId : documentIds1)
        {
            if (notList.contains(docId))
            {
                results.add(docId);
            }
        }

        return results;
    }

    /**
     * OR NOT action, first do negation of documentIds2, after that, method merge sets
     * @param documentIds1 set of document ids 1
     * @param documentIds2 set of document ids 2
     * @return list of documents ids
     */
    private Set<String> notOr(Set<String> documentIds1, Set<String> documentIds2)
    {
        Set<String> notList = new HashSet<>(this.allDocumentIds);
        notList.removeAll(documentIds2);

        notList.addAll(documentIds1);

        return notList;
    }

    /**
     * Returns total result count
     * @return total count
     */
    public int getTotalResultCount()
    {
        return totalResultCount;
    }
}
