package cz.zcu.kiv.nlp.ir.trec.Index.loader;

import cz.zcu.kiv.nlp.ir.trec.config.Config;
import cz.zcu.kiv.nlp.ir.trec.data.enums.ELoaderType;

public class LoaderFactory
{
    public ILoader getLoader(ELoaderType loaderType)
    {
        if (loaderType == null)
        {
            return null;
        }

        if (loaderType == ELoaderType.Czech)
        {
            return new CzechLoader(Config.getCzechDataPath());
        }
        else if (loaderType == ELoaderType.Article)
        {
            return new ArticleLoader(Config.getArticleDataPath());
        }
        else if (loaderType == ELoaderType.Test)
        {
            return new TestLoader();
        }

        return null;

    }
}
