package cz.zcu.kiv.nlp.ir.trec.data.enums;

/**
 * The enum E searcher type.
 */
public enum ESearcherType
{
    /**
     * Searcher types
     */
    VSM,
    BOOLEAN;

    /**
     * Gets search type by text.
     *
     * @param value the value
     * @return the search type by text
     */
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
