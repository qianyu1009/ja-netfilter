package io.zhile.research.ja.netfilter.rulers;

public class SuffixRuler implements Ruler {
    @Override
    public boolean test(String rule, String content) {
        return content.endsWith(rule);
    }
}
