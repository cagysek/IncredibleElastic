package cz.zcu.kiv.nlp.ir.trec.data.enums;

public enum ESearcherType
{
    VSM, BOOLEAN;

    public static ESearcherType getSearchTypeByText(String value)
    {
        if (value.equals("VSM"))
        {
            return VSM;
        }
        else if (value.equals("Boolean"))
        {
            return BOOLEAN;
        }

        return null;
    }
}
