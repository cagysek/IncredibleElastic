package cz.zcu.kiv.nlp.ir.trec.data.object;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Object represents Article data
 */
public class Article
{
    /**
     * Title
     */
    private String title;

    /**
     * Publish date
     */
    private String publishDate;

    /**
     * Images
     */
    private List<String> images;

    /**
     * Author
     */
    private String author;

    /**
     * Content
     */
    private String content;

    /**
     * downloaded
     */
    private String downloaded;

    /**
     * category
     */
    private String category;

    /**
     * Instantiates a new Article.
     *
     * @param title       the title
     * @param publishDate the publish date
     * @param images      the images
     * @param author      the author
     * @param content     the content
     * @param category    the category
     */
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

    /**
     * Merge all paragraphs to one
     * @param paragraphs
     * @return
     */
    private String mergeContentParagraphs(List<String> paragraphs)
    {
        StringBuilder content = new StringBuilder();

        for (String paragraph : paragraphs)
        {
            content.append(paragraph);
        }

        return content.toString();
    }

    /**
     * Process date
     * @return date
     */
    private String getCurrentDate()
    {
        Date dNow = new Date( );
        SimpleDateFormat ft = new SimpleDateFormat ("dd.MM.yyyy");
        return ft.format(dNow);
    }

    /**
     * Checks date
     * @param date
     * @return
     */
    private boolean checkValidDateFormat(String date)
    {
        if (date.contains("Před"))
        {
            return false;
        }

        return true;
    }

    /**
     * Gets title.
     *
     * @return the title
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Gets publish date.
     *
     * @return the publish date
     */
    public String getPublishDate()
    {
        return publishDate;
    }

    /**
     * Gets images.
     *
     * @return the images
     */
    public List<String> getImages()
    {
        return images;
    }

    /**
     * Gets author.
     *
     * @return the author
     */
    public String getAuthor()
    {
        return author;
    }

    /**
     * Gets content.
     *
     * @return the content
     */
    public String getContent()
    {
        return content;
    }

    /**
     * Gets downloaded.
     *
     * @return the downloaded
     */
    public String getDownloaded()
    {
        return downloaded;
    }

    /**
     * Gets category.
     *
     * @return the category
     */
    public String getCategory()
    {
        return category;
    }
}
