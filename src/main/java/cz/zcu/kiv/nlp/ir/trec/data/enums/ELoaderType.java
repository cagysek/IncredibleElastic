package cz.zcu.kiv.nlp.ir.trec.data.enums;

public enum ELoaderType
{
    CZECH, ARTICLE, TEST;

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
