package io.zhile.research.ja.netfilter.rulers;

public interface Ruler {
    default boolean test(String rule, String content) {
        return false;
    }
}
