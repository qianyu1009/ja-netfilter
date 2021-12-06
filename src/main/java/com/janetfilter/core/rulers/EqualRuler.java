package com.janetfilter.core.rulers;

public class EqualRuler implements Ruler {
    @Override
    public boolean test(String rule, String content) {
        return content.equals(rule);
    }
}
