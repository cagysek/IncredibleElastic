package cz.zcu.kiv.nlp.ir.trec.search.searcher;

import cz.zcu.kiv.nlp.ir.trec.Index.preprocesing.Preprocessing;
import cz.zcu.kiv.nlp.ir.trec.data.enums.ESearcherType;
import cz.zcu.kiv.nlp.ir.trec.data.invertedIndex.InvertedIndex;

/**
 * Factory for get searcher depends on type
 */
public class SearcherFactory
{

    /**
     * Gets searcher.
     *
     * @param searcherType  the searcher type
     * @param preprocessing the preprocessing
     * @param invertedIndex the inverted index
     * @return the searcher
     */
    public Searcher getSearcher(ESearcherType searcherType, Preprocessing preprocessing, InvertedIndex invertedIndex)
    {
        if (searcherType == ESearcherType.VSM)
        {
            return new VectorSpaceModelSearcher(preprocessing, invertedIndex);
        }
        else if (searcherType == ESearcherType.BOOLEAN)
        {
            return new BooleanSearcher(preprocessing, invertedIndex);
        }

        return null;
    }
}
