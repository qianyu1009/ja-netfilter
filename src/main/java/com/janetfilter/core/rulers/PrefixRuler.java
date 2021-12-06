package com.janetfilter.core.rulers;

public class PrefixRuler implements Ruler {
    @Override
    public boolean test(String rule, String content) {
        return content.startsWith(rule);
    }
}
