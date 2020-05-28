package cz.zcu.kiv.nlp.ir.trec.Index.loader;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import cz.zcu.kiv.nlp.ir.trec.data.document.Document;
import cz.zcu.kiv.nlp.ir.trec.data.DocumentNew;
import cz.zcu.kiv.nlp.ir.trec.data.object.Article;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ArticleLoader implements ILoader
{
    private String filename;

    public ArticleLoader(String filename)
    {
        this.filename = filename;
    }

    @Override
    public List<Document> loadDocuments()
    {
        List<Document> documents = new ArrayList<>();

        SimpleDateFormat formatter =new SimpleDateFormat("dd.MM.yyyy");

        int i = 0;
        for(Article article : this.getArticleArray(this.filename))
        {
            DocumentNew document = new DocumentNew();
            document.setId("" + i);
            document.setText(article.getContent());
            document.setTitle(article.getTitle());

            try
            {
                document.setDate(formatter.parse(article.getPublishDate()));
            }
            catch (ParseException e)
            {
                document.setDate(null);
            }

            documents.add(document);

            i++;
        }

        return documents;
    }

    private Article[] getArticleArray(String filename)
    {
        Gson g = new Gson();
        Article[] articles = null;
        try
        {
            JsonReader jsonReader = new JsonReader( new FileReader(filename) );
            articles = g.fromJson(jsonReader, Article[].class);

        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        return articles;
    }
}
