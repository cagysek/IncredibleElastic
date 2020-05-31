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

            Query q = parser.parse(query, "");

            BooleanQuery booleanQuery = (BooleanQuery)q;

            Set<String> result = new HashSet<>();

            boolean firstLoop = true;

            for (BooleanClause clause : booleanQuery.clauses())
            {
                Set<String> tmp;
                switch (clause.getOccur())
                {
                    case MUST:
                        tmp = this.processQuery(clause, false);
                        result = this.and(result, tmp, firstLoop);
                        break;
                    case SHOULD:
                        tmp = this.processQuery(clause, false);

                        result = this.or(result, tmp);
                        break;
                    case MUST_NOT:
                        tmp = this.processQuery(clause, true);
                        result = this.not(result, tmp, firstLoop);
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

        } catch (QueryNodeException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    private Set<String> processQuery(BooleanClause booleanClause, boolean isMustNot)
    {
            if (booleanClause.getQuery() instanceof TermQuery)
            {
                String preprocessedTerm = this.preprocessing.processText(booleanClause.getQuery().toString()).get(0);

                if (isMustNot)
                {
                    return this.getDocumentIdsExceptTerm(preprocessedTerm);
                }
                else
                {
                    return this.getRelatedDocumentIds(preprocessedTerm);
                }
            }
            else
            {
                Set<String> result = new HashSet<>();

                BooleanQuery booleanQuery = (BooleanQuery)booleanClause.getQuery();

                int i = 0;
                for (BooleanClause clause : booleanQuery.clauses())
                {
                    switch (clause.getOccur())
                    {
                        case MUST:
                        {
                            if (isMustNot)
                            {
                                result = this.or(result, this.processQuery(clause, true));
                            }
                            else
                            {
                                result = this.and(result, this.processQuery(clause, false), i == 0);
                            }

                            break;
                        }
                        case SHOULD:
                        {
                            if (isMustNot)
                            {
                                result = this.and(result, this.processQuery(clause, true), i == 0);

                            }
                            else
                            {
                                result = this.or(result, this.processQuery(clause, false));
                            }

                            break;
                        }
                        case MUST_NOT:
                        {
                            result = this.not(result, this.processQuery(clause, true), i == 0);

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

    private Set<String> not(Set<String> documentIds1, Set<String> documentIds2, boolean justReturnDocument2)
    {
        if (justReturnDocument2)
        {
            return documentIds2;
        }

        return new HashSet<>(documentIds1.stream()
                .filter(str -> documentIds2.contains(str))
                .collect(Collectors.toSet()));
    }

}
