package com.janetfilter.core.rulers;

public interface Ruler {
    default boolean test(String rule, String content) {
        return false;
    }
}
