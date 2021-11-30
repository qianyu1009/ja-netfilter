package io.zhile.research.ja.netfilter.plugin;

import io.zhile.research.ja.netfilter.models.FilterRule;
import io.zhile.research.ja.netfilter.transformers.MyTransformer;

import java.util.List;

public interface PluginEntry {
    default void init(List<FilterRule> filterRules) {
        // get plugin config
    }

    String getName();

    default String getVersion() {
        return "v1.0.0";
    }

    default String getDescription() {
        return "A ja-netfilter plugin.";
    }

    List<MyTransformer> getTransformers();
}
