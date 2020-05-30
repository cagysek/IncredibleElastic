package cz.zcu.kiv.nlp.ir.trec.model;

public class CosineSimilarity
{
    public static float getCosineSimilarity(float documentQueryScalar, float documentEuclidValue, float queryEuclidValue)
    {
        return ( ( documentQueryScalar ) / ( documentEuclidValue * queryEuclidValue ) );
    }
}
