package cz.zcu.kiv.nlp.ir.trec.Index.preprocesing;


import cz.zcu.kiv.nlp.ir.trec.Index.stemmer.Stemmer;
import cz.zcu.kiv.nlp.ir.trec.Index.tokenizer.Tokenizer;
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

    public Preprocessing(Stemmer stemmer,
                         Tokenizer tokenizer,
                         Set<String> stopWords,
                         boolean removeAccentsBeforeStemming,
                         boolean removeAccentsAfterStemming,
                         boolean toLowercase
    )
    {
        this.stemmer = stemmer;
        this.tokenizer = tokenizer;
        this.stopWords = stopWords;
        this.removeAccentsBeforeStemming = removeAccentsBeforeStemming;
        this.removeAccentsAfterStemming = removeAccentsAfterStemming;
        this.toLowercase = toLowercase;
    }


    public List<String> process(Document document) {

        String data = document.getRaw();

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

            if (stemmer != null) {
                token = stemmer.stem(token);
            }
            if (removeAccentsAfterStemming) {
                token = removeAccents(token);
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
