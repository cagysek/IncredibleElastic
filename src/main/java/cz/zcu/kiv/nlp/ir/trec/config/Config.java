package cz.zcu.kiv.nlp.ir.trec.config;

import cz.zcu.kiv.nlp.ir.trec.data.enums.ELoaderType;

/**
 * Represents all constants in application
 */
public class Config
{
    /**
     * The constant OUTPUT_DIR.
     */
    public static String OUTPUT_DIR = "./TREC";

    /**
     * The constant FILENAME_CZECH_DATA.
     */
    public static String FILENAME_CZECH_DATA = "/czechData.bin";

    /**
     * The constant EXTERNAL_FILE_DIR.
     */
    public static String EXTERNAL_FILE_DIR = "./externalFiles";

    /**
     * The constant FILENAME_TOPICS.
     */
    public static String FILENAME_TOPICS = "/topicData.bin";

    /**
     * The constant FILENAME_INDEX.
     */
    public static String FILENAME_INDEX = "/index.bin";

    /**
     * The constant FILENAME_ARTICLES_DATA.
     */
    public static String FILENAME_ARTICLES_DATA = "/letemSvetemApplemData.json";

    /**
     * The constant FILENAME_STOP_WORDS.
     */
    public static String FILENAME_STOP_WORDS = "/stopWords.txt";

    /**
     * Gets czech data path.
     *
     * @return the czech data path
     */
    public static String getCzechDataPath()
    {
        return OUTPUT_DIR + FILENAME_CZECH_DATA;
    }

    /**
     * Gets article data path.
     *
     * @return the article data path
     */
    public static String getArticleDataPath()
    {
        return EXTERNAL_FILE_DIR + FILENAME_ARTICLES_DATA;
    }


    /**
     * Gets stop words data path.
     *
     * @return the stop words data path
     */
    public static String getStopWordsDataPath()
    {
        return EXTERNAL_FILE_DIR + FILENAME_STOP_WORDS;
    }

    /**
     * Gets index path.
     *
     * @return the index path
     */
    public static String getIndexPath()
    {
        return OUTPUT_DIR + FILENAME_INDEX;
    }

    /**
     * The constant getDefaultLoader.
     */
    public static ELoaderType getDefaultLoader = ELoaderType.ARTICLE;
}
