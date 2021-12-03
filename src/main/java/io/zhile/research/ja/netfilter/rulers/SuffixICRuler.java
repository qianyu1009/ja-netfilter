package io.zhile.research.ja.netfilter.rulers;

public class SuffixICRuler implements Ruler {
    @Override
    public boolean test(String rule, String content) {
        return content.toLowerCase().endsWith(rule.toLowerCase());
    }
}
