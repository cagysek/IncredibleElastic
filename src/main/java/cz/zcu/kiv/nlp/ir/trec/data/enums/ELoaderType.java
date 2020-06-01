package cz.zcu.kiv.nlp.ir.trec.data.enums;

/**
 * The enum E loader type.
 */
public enum ELoaderType
{
    /**
     * Loader types
     */
    CZECH,
    ARTICLE,
    TEST;

    /**
     * Gets data type by id.
     *
     * @param value the value
     * @return the data type by id
     */
    public static ELoaderType getDataTypeById(String value)
    {
        if (value.equals("czechDataRadio"))
        {
            return CZECH;
        }
        else if (value.equals("ownDataRadio"))
        {
            return ARTICLE;
        }
        else if (value.equals("testDataRadio"))
        {
            return TEST;
        }

        return null;
    }
}
