package com.janetfilter.core.plugin;

import com.janetfilter.core.Environment;
import com.janetfilter.core.models.FilterRule;

import java.util.List;

public interface PluginEntry {
    default void init(Environment environment, List<FilterRule> filterRules) {
        // get plugin config
    }

    String getName();

    String getAuthor();

    default String getVersion() {
        return "v1.0.0";
    }

    default String getDescription() {
        return "A ja-netfilter plugin.";
    }

    List<MyTransformer> getTransformers();
}
