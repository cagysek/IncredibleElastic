package cz.zcu.kiv.nlp.ir.trec.model;

public class VectorSpaceModel
{
    public static float getIDF(int documentsCount, int df)
    {
        return (float) (Math.log10( (float)(documentsCount / df) ));
    }

    public static float getTFIDF(float tf, float idf)
    {
        return (1 + (float) Math.log10(tf)) * idf;
    }
}
