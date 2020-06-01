package cz.zcu.kiv.nlp.ir.trec.model;

/**
 * Model for Vector space model.
 */
public class VectorSpaceModel
{
    /**
     * Gets idf.
     *
     * @param documentsCount the documents count
     * @param df             the df
     * @return the idf
     */
    public static float getIDF(int documentsCount, int df)
    {
        return (float) (Math.log10( ((float)documentsCount / (float)(df + 1)) ));
    }

    /**
     * Gets tfidf.
     *
     * @param tf  the tf
     * @param idf the idf
     * @return the tfidf
     */
    public static float getTFIDF(float tf, float idf)
    {
        return ((1 + (float)Math.log10(tf)) * idf);
    }
}
