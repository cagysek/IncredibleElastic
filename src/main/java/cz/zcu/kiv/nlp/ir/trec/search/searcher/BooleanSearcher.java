package cz.zcu.kiv.nlp.ir.trec.search.searcher;

import cz.zcu.kiv.nlp.ir.trec.data.result.Result;

import java.util.List;

public class BooleanSearcher implements Searcher
{
    private int resultCount = 10;

    @Override
    public List<Result> search(String query)
    {
        return null;
    }

    @Override
    public void setResultCount(int resultCount)
    {
        this.resultCount = resultCount;
    }
}
