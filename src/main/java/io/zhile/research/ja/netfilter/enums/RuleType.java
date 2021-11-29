package io.zhile.research.ja.netfilter.enums;

import io.zhile.research.ja.netfilter.rulers.*;

public enum RuleType {
    PREFIX(new PrefixRuler()),
    SUFFIX(new SuffixRuler()),
    KEYWORD(new KeywordRuler()),
    REGEXP(new RegExpRuler()),
    EQUAL(new EqualRuler());

    private final Ruler ruler;

    RuleType(Ruler ruler) { // Lazy here. No lazy loading
        this.ruler = ruler;
    }

    public Ruler getRuler() {
        return ruler;
    }
}
