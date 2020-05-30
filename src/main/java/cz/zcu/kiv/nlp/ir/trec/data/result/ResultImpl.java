package cz.zcu.kiv.nlp.ir.trec.data.result;

import cz.zcu.kiv.nlp.ir.trec.data.result.AbstractResult;

/**
 * Created by Tigi on 8.1.2015.
 *
 * Třída představuje výsledek vrácený po vyhledávání.
 * Třídu můžete libovolně upravovat, popř. si můžete vytvořit vlastní třídu,
 * která dědí od abstraktní třídy {@link AbstractResult}
 */
public class ResultImpl extends AbstractResult {

    public ResultImpl(String documentId, float score)
    {
        this.documentID = documentId;
        this.score = score;
    }
}
