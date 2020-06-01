package cz.zcu.kiv.nlp.ir.trec.search.searcher;

import cz.zcu.kiv.nlp.ir.trec.data.result.Result;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;

import java.util.List;

/**
 * Created by Tigi on 6.1.2015.
 *
 * Rozhraní umožňující vyhledávat v zaindexovaných dokumentech.
 */
public interface Searcher {
    List<Result> search(String query) throws QueryNodeException;

    public void setResultCount(int resultCount);

    public int getTotalResultCount();
}
