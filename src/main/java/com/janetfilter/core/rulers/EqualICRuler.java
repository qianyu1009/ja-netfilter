package com.janetfilter.core.rulers;

public class EqualICRuler implements Ruler {
    @Override
    public boolean test(String rule, String content) {
        return content.equalsIgnoreCase(rule);
    }
}
