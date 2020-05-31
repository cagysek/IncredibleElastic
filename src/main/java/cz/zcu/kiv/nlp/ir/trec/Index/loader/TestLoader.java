package cz.zcu.kiv.nlp.ir.trec.Index.loader;

import cz.zcu.kiv.nlp.ir.trec.data.DocumentNew;
import cz.zcu.kiv.nlp.ir.trec.data.document.Document;

import java.util.ArrayList;
import java.util.List;

public class TestLoader implements ILoader
{
    @Override
    public List<Document> loadDocuments()
    {
        List<Document> documents = new ArrayList<>();

        DocumentNew doc1 = new DocumentNew();
        doc1.setId("1");
        doc1.setText("tropical fish include fish found in tropical enviroments");

        DocumentNew doc2 = new DocumentNew();
        doc2.setId("2");
        doc2.setText("fish live in a sea");

        DocumentNew doc3 = new DocumentNew();
        doc3.setId("3");
        doc3.setText("tropical fish are popular aquarium fish");

        DocumentNew doc4 = new DocumentNew();
        doc4.setId("4");
        doc4.setText("fish also live in Czechia");

        DocumentNew doc5 = new DocumentNew();
        doc5.setId("5");
        doc5.setText("Czechia is a country");

        documents.add(doc1);
        documents.add(doc2);
        documents.add(doc3);
        documents.add(doc4);
        documents.add(doc5);

        return documents;
    }
}
