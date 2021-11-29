package io.zhile.research.ja.netfilter.rulers;

public class KeywordRuler implements Ruler {
    @Override
    public boolean test(String rule, String content) {
        return content.contains(rule);
    }
}
