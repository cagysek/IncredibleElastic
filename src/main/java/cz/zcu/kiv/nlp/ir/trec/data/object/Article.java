package cz.zcu.kiv.nlp.ir.trec.data.object;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Article
{
    private String title;
    private String publishDate;
    private List<String> images;
    private String author;
    private String content;
    private String downloaded;
    private String category;

    public Article(String title, String publishDate, List<String> images, String author, List<String> content, String category)
    {
        this.title = title;
        this.publishDate = this.checkValidDateFormat(publishDate) ? publishDate : this.getCurrentDate(); // today articles contains "Před x hod"
        this.images = images;
        this.author = author;
        this.content = this.mergeContentParagraphs(content);
        this.downloaded = this.getCurrentDate();
        this.category = category;
    }


    private String mergeContentParagraphs(List<String> paragraphs)
    {
        StringBuilder content = new StringBuilder();

        for (String paragraph : paragraphs)
        {
            content.append(paragraph);
        }

        return content.toString();
    }

    private String getCurrentDate()
    {
        Date dNow = new Date( );
        SimpleDateFormat ft = new SimpleDateFormat ("dd.MM.yyyy");
        return ft.format(dNow);
    }

    private boolean checkValidDateFormat(String date)
    {
        if (date.contains("Před"))
        {
            return false;
        }

        return true;
    }

    public String getTitle()
    {
        return title;
    }

    public String getPublishDate()
    {
        return publishDate;
    }

    public List<String> getImages()
    {
        return images;
    }

    public String getAuthor()
    {
        return author;
    }

    public String getContent()
    {
        return content;
    }

    public String getDownloaded()
    {
        return downloaded;
    }

    public String getCategory()
    {
        return category;
    }
}
