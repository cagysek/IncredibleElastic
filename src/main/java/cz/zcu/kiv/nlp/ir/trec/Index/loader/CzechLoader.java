package cz.zcu.kiv.nlp.ir.trec.Index.loader;

import cz.zcu.kiv.nlp.ir.trec.data.document.Document;
import cz.zcu.kiv.nlp.ir.trec.utils.SerializedDataHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for load list of documents
 */
public class CzechLoader implements ILoader
{
    /**
     * path to file
     */
    private String filename;

    /**
     * Instantiates a new Czech loader.
     *
     * @param filename the filename
     */
    public CzechLoader(String filename)
    {
        this.filename = filename;
    }

    /**
     * Method to load documents from file
     * @return list of documents
     */
    @Override
    public List<Document> loadDocuments()
    {
        List<Document> documents = new ArrayList<>();

        File file = new File(this.filename);

        try {
            if (file.exists())
            {
                documents = SerializedDataHelper.loadDocument(file);
            }
            else
            {
                System.out.println("Soubor " + this.filename + " nenalezen");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        return documents;
    }
}
