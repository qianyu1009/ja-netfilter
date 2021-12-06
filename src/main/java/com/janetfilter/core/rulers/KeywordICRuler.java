package com.janetfilter.core.rulers;

public class KeywordICRuler implements Ruler {
    @Override
    public boolean test(String rule, String content) {
        return content.toLowerCase().contains(rule.toLowerCase());
    }
}
