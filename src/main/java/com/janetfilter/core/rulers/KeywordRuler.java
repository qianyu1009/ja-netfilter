package com.janetfilter.core.rulers;

public class KeywordRuler implements Ruler {
    @Override
    public boolean test(String rule, String content) {
        return content.contains(rule);
    }
}
