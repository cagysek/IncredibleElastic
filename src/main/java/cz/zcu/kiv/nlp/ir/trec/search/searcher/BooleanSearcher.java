package cz.zcu.kiv.nlp.ir.trec.search.searcher;

import cz.zcu.kiv.nlp.ir.trec.Index.preprocesing.Preprocessing;
import cz.zcu.kiv.nlp.ir.trec.data.invertedIndex.InvertedIndex;
import cz.zcu.kiv.nlp.ir.trec.data.result.Result;
import cz.zcu.kiv.nlp.ir.trec.data.result.ResultImpl;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.nodes.BooleanQueryNode;
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

public class BooleanSearcher implements Searcher
{
    private final InvertedIndex invertedIndex;
    private final Preprocessing preprocessing;

    private int resultCount = 10;

    private Set<String> allDocumentIds;

    public BooleanSearcher(Preprocessing preprocessing, InvertedIndex invertedIndex)
    {
        this.preprocessing = preprocessing;
        this.invertedIndex = invertedIndex;
    }

    @Override
    public List<Result> search(String queryString)
    {
        this.allDocumentIds = this.invertedIndex.getAllDocumentsIds();

        return this.getResults(queryString);
    }

    @Override
    public void setResultCount(int resultCount)
    {
        this.resultCount = resultCount;
    }

    private List<Result> getResults(String query)
    {
        try
        {
            PrecedenceQueryParser parser = new PrecedenceQueryParser();

            query = "(czechia OR NOT aquarium) AND NOT (fish AND NOT tropical) OR NOT found";
            //query = "(sea AND NOT live) OR NOT (fish OR NOT popular)";
            query = "(live OR NOT czechia) AND NOT (tropical AND NOT also)";
            query = "found OR sea AND also OR (country AND NOT environment) AND NOT popular";
           // query = "";

            Query q = parser.parse(query, "");

            BooleanQuery booleanQuery = (BooleanQuery)q;

            Set<String> result = new HashSet<>();

            boolean firstLoop = true;

            BooleanClause.Occur lastOccur = null;

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

            return tmp;

        }
        catch (QueryNodeException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    private Set<String> processQuery(BooleanClause booleanClause)
    {
            if (booleanClause.getQuery() instanceof TermQuery)
            {
                String preprocessedTerm = this.preprocessing.processText(booleanClause.getQuery().toString()).get(0);

                return this.getRelatedDocumentIds(preprocessedTerm);
            }
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

                            result = this.and(result, this.processQuery(clause), i == 0);

                            break;
                        }
                        case SHOULD:
                        {
                            lastOccur = BooleanClause.Occur.SHOULD;

                            result = this.or(result, this.processQuery(clause));

                            break;
                        }
                        case MUST_NOT:
                        {

                            Set<String> tmp;
                            tmp = this.processQuery(clause);

                            if (lastOccur == BooleanClause.Occur.SHOULD)
                            {
                                result = this.notOr(result, tmp);
                            }
                            else
                            {
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


    private Set<String> getRelatedDocumentIds(String term)
    {
        if (this.invertedIndex.getIndexItem(term) != null)
        {
            return this.invertedIndex.getIndexItem(term).getDocumentsIds();
        }

        return new HashSet<>();
    }

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

    private Set<String> or(Set<String> documentIds1, Set<String> documentIds2)
    {
        documentIds1.addAll(documentIds2);

        return documentIds1;
    }

    private Set<String> and(Set<String> documentIds1, Set<String> documentIds2, boolean justReturnDocument2)
    {
        if (justReturnDocument2)
        {
            return documentIds2;
        }

        return this.and(documentIds1, documentIds2);
    }

    private Set<String> and(Set<String> documentIds1, Set<String> documentIds2)
    {
        return new HashSet<>(documentIds1.stream()
                .filter(documentIds2::contains)
                .collect(Collectors.toSet()));
    }

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

    private Set<String> notOr(Set<String> documentIds1, Set<String> documentIds2)
    {
        Set<String> notList = new HashSet<>(this.allDocumentIds);
        notList.removeAll(documentIds2);

        notList.addAll(documentIds1);

        return notList;
    }

}
