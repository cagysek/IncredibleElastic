package cz.zcu.kiv.nlp.ir.trec.Index.preprocesing;


import cz.zcu.kiv.nlp.ir.trec.Index.preprocesing.stemmer.Stemmer;
import cz.zcu.kiv.nlp.ir.trec.Index.preprocesing.tokenizer.Tokenizer;
import cz.zcu.kiv.nlp.ir.trec.data.document.Document;

import java.text.Normalizer;
import java.util.*;

/**
 * Created by Tigi on 29.2.2016.
 */
public class Preprocessing
{
    private Stemmer stemmer;
    private Tokenizer tokenizer;
    private Set<String> stopWords;
    private boolean removeAccentsBeforeStemming;
    private boolean removeAccentsAfterStemming;
    private boolean toLowercase;
    private boolean ignoreSingleCharacter;

    public Preprocessing(Stemmer stemmer,
                         Tokenizer tokenizer,
                         Set<String> stopWords,
                         boolean removeAccentsBeforeStemming,
                         boolean removeAccentsAfterStemming,
                         boolean toLowercase,
                         boolean ignoreSingleCharacter
    )
    {
        this.stemmer = stemmer;
        this.tokenizer = tokenizer;
        this.stopWords = stopWords;
        this.removeAccentsBeforeStemming = removeAccentsBeforeStemming;
        this.removeAccentsAfterStemming = removeAccentsAfterStemming;
        this.toLowercase = toLowercase;
        this.ignoreSingleCharacter = ignoreSingleCharacter;
    }


    public List<String> processDocument(Document document)
    {
        return this.processText(document.getRaw());
    }

    public List<String> processText(String data)
    {
        if (toLowercase) {
            data = data.toLowerCase();
        }
        if (removeAccentsBeforeStemming) {
            data = removeAccents(data);
        }

        List<String> processedWords = new ArrayList<>();

        for (String token : tokenizer.tokenize(data)) {

            if (stopWords.contains(token))
            {
                continue;
            }

            if (stemmer != null)
            {
                token = stemmer.stem(token);
            }
            if (removeAccentsAfterStemming)
            {
                token = removeAccents(token);
            }

            if (ignoreSingleCharacter)
            {
                if (token.length() <= 1)
                {
                    continue;
                }
            }

            processedWords.add(token);
        }

        return processedWords;
    }

    private String removeAccents(String text)
    {

        return Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

    }
}
