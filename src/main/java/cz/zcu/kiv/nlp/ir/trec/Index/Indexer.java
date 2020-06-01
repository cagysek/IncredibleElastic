package cz.zcu.kiv.nlp.ir.trec.Index;

import cz.zcu.kiv.nlp.ir.trec.Index.Index;
import cz.zcu.kiv.nlp.ir.trec.data.document.Document;
import cz.zcu.kiv.nlp.ir.trec.data.enums.ELoaderType;

import java.util.List;

/**
 * Created by Tigi on 6.1.2015.
 *
 * Rozhraní, pro indexaci dokumentů.
 *
 * Pokud potřebujete/chcete můžete přidat další metody např. pro indexaci po jednotlivých dokumentech
 * a jiné potřebné metody (např. CRUD operace update, delete, ... dokumentů), ale zachovejte původní metodu.
 *
 * metodu index implementujte ve třídě {@link Index}
 */
public interface Indexer {

    /**
     * Metoda zaindexuje zadaný seznam dokumentů
     *
     * @param documents list dokumentů
     */
    void index(List<Document> documents, ELoaderType loaderType);
}
