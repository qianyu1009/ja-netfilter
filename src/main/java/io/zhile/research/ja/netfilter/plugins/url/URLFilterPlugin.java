package io.zhile.research.ja.netfilter.plugins.url;

import io.zhile.research.ja.netfilter.models.FilterRule;
import io.zhile.research.ja.netfilter.plugin.MyTransformer;
import io.zhile.research.ja.netfilter.plugin.PluginEntry;

import java.util.ArrayList;
import java.util.List;

public class URLFilterPlugin implements PluginEntry {
    private final List<MyTransformer> transformers = new ArrayList<>();

    @Override
    public void init(List<FilterRule> filterRules) {
        transformers.add(new HttpClientTransformer(filterRules));
    }

    @Override
    public String getName() {
        return "URL";
    }

    @Override
    public String getAuthor() {
        return "neo";
    }

    @Override
    public String getVersion() {
        return "v1.0.0";
    }

    @Override
    public String getDescription() {
        return "ja-netfilter core: url plugin";
    }

    @Override
    public List<MyTransformer> getTransformers() {
        return transformers;
    }
}
