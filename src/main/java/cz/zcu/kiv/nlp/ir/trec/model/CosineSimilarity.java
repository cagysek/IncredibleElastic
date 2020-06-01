package cz.zcu.kiv.nlp.ir.trec.model;


/**
 * Model for Cosine similarity.
 */
public class CosineSimilarity
{
    /**
     * Gets cosine similarity.
     *
     * @param documentQueryScalar the document query scalar
     * @param documentEuclidValue the document euclid value
     * @param queryEuclidValue    the query euclid value
     * @return the cosine similarity
     */
    public static float getCosineSimilarity(float documentQueryScalar, float documentEuclidValue, float queryEuclidValue)
    {
        return ( ( documentQueryScalar ) / ( documentEuclidValue * queryEuclidValue ) );
    }
}
