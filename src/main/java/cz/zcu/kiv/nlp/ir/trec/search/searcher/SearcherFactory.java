package cz.zcu.kiv.nlp.ir.trec.search.searcher;

import cz.zcu.kiv.nlp.ir.trec.Index.preprocesing.Preprocessing;
import cz.zcu.kiv.nlp.ir.trec.data.enums.ESearcherType;
import cz.zcu.kiv.nlp.ir.trec.data.invertedIndex.InvertedIndex;

public class SearcherFactory
{
    public Searcher getSearcher(ESearcherType searcherType, Preprocessing preprocessing, InvertedIndex invertedIndex)
    {
        if (searcherType == ESearcherType.VSM)
        {
            return new VectorSpaceModelSearcher(preprocessing, invertedIndex);
        }
        else if (searcherType == ESearcherType.BOOLEAN)
        {
            return new BooleanSearcher();
        }

        return null;
    }
}
