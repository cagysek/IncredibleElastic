package cz.zcu.kiv.nlp.ir.trec.search.searcher;

import cz.zcu.kiv.nlp.ir.trec.data.enums.ESearcherType;

public class SearcherFactory
{
    public Searcher getSearcher(ESearcherType searcherType)
    {
        if (searcherType == ESearcherType.VSM)
        {
            return new VectorSpaceModelSearcher();
        }
        else if (searcherType == ESearcherType.BOOLEAN)
        {
            return new BooleanSearcher();
        }

        return null;
    }
}
