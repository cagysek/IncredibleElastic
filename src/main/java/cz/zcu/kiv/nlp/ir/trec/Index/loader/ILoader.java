package cz.zcu.kiv.nlp.ir.trec.Index.loader;

import cz.zcu.kiv.nlp.ir.trec.data.document.Document;

import java.util.List;

/**
 * Interface for loaders
 */
public interface ILoader
{
    public List<Document> loadDocuments();
}
