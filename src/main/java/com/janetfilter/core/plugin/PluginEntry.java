package com.janetfilter.core.plugin;

import com.janetfilter.core.Environment;

import java.util.List;

public interface PluginEntry {
    default void init(Environment environment, PluginConfig config) {
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
