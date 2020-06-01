package cz.zcu.kiv.nlp.ir.trec.search.searcher;

import cz.zcu.kiv.nlp.ir.trec.Index.Index;
import cz.zcu.kiv.nlp.ir.trec.data.result.Result;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;

import java.util.List;

/**
 * Created by Tigi on 6.1.2015.
 *
 * Rozhraní umožňující vyhledávat v zaindexovaných dokumentech.
 *
 * Pokud potřebujete, můžete přidat další metody k implementaci, ale neměňte signaturu
 * již existující metody search.
 *
 * Metodu search implementujte ve tříde {@link Index}
 */
public interface Searcher {
    List<Result> search(String query) throws QueryNodeException;

    public void setResultCount(int resultCount);

    public int getTotalResultCount();
}
