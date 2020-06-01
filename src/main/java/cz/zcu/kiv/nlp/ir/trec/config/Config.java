package cz.zcu.kiv.nlp.ir.trec.config;

import cz.zcu.kiv.nlp.ir.trec.data.enums.ELoaderType;

public class Config
{
    public static String OUTPUT_DIR = "./TREC";

    public static String FILENAME_CZECH_DATA = "/czechData.bin";

    public static String EXTERNAL_FILE_DIR = "./externalFiles";

    public static String FILENAME_TOPICS = "/topicData.bin";

    public static String FILENAME_INDEX = "/index.bin";

    public static String FILENAME_ARTICLES_DATA = "/letemSvetemApplemData.json";

    public static String FILENAME_STOP_WORDS = "/stopWords.txt";

    public static String getCzechDataPath()
    {
        return OUTPUT_DIR + FILENAME_CZECH_DATA;
    }

    public static String getArticleDataPath()
    {
        return EXTERNAL_FILE_DIR + FILENAME_ARTICLES_DATA;
    }


    public static String getStopWordsDataPath()
    {
        return EXTERNAL_FILE_DIR + FILENAME_STOP_WORDS;
    }

    public static String getIndexPath()
    {
        return OUTPUT_DIR + FILENAME_INDEX;
    }

    public static ELoaderType getDefaultLoader = ELoaderType.ARTICLE;
}
