package com.janetfilter.core.rulers;

import java.util.regex.Pattern;

public class RegExpRuler implements Ruler {
    @Override
    public boolean test(String rule, String content) {
        return Pattern.matches(rule, content);
    }
}
