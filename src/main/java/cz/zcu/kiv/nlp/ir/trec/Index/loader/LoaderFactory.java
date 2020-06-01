package cz.zcu.kiv.nlp.ir.trec.Index.loader;

import cz.zcu.kiv.nlp.ir.trec.config.Config;
import cz.zcu.kiv.nlp.ir.trec.data.enums.ELoaderType;

/**
 * Factory for get loader depends on type
 */
public class LoaderFactory
{
    /**
     * Gets loader.
     *
     * @param loaderType the loader type
     * @return the loader
     */
    public ILoader getLoader(ELoaderType loaderType)
    {
        if (loaderType == null)
        {
            return null;
        }

        if (loaderType == ELoaderType.CZECH)
        {
            return new CzechLoader(Config.getCzechDataPath());
        }
        else if (loaderType == ELoaderType.ARTICLE)
        {
            return new ArticleLoader(Config.getArticleDataPath());
        }
        else if (loaderType == ELoaderType.TEST)
        {
            return new TestLoader();
        }

        return null;

    }
}
