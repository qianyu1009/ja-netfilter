package com.janetfilter.core.rulers;

public class PrefixICRuler implements Ruler {
    @Override
    public boolean test(String rule, String content) {
        return content.toLowerCase().startsWith(rule.toLowerCase());
    }
}
