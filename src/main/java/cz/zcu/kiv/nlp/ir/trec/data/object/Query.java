package cz.zcu.kiv.nlp.ir.trec.data.object;

import org.apache.lucene.search.BooleanClause;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Query
{
    private String text;

    private boolean isTerm;

    private String term;
    private Map<BooleanClause.Occur, Collection<Query>> children;

    public Query() {
        this.children = new HashMap<>();
        // this order must be preserved!!
        this.children.put(BooleanClause.Occur.MUST, new ArrayList<Query>());
        this.children.put(BooleanClause.Occur.SHOULD, new ArrayList<Query>());
        this.children.put(BooleanClause.Occur.MUST_NOT, new ArrayList<Query>());
    }

    public void addChild(BooleanClause.Occur occur, Query child) {
        this.children.get(occur).add(child);
    }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public boolean isTerm() { return isTerm; }
    public void setTerm(String term) { term = term; }
    public void setIsTerm(Boolean term) { isTerm = term; }
}
