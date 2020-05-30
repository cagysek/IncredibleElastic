package cz.zcu.kiv.nlp.ir.trec.Index.preprocesing.tokenizer;

/**
 * Created by tigi on 29.2.2016.
 */
public interface Tokenizer {
    String[] tokenize(String text);
}
