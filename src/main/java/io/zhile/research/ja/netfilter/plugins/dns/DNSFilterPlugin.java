package io.zhile.research.ja.netfilter.plugins.dns;

import io.zhile.research.ja.netfilter.models.FilterRule;
import io.zhile.research.ja.netfilter.plugin.MyTransformer;
import io.zhile.research.ja.netfilter.plugin.PluginEntry;

import java.util.ArrayList;
import java.util.List;

public class DNSFilterPlugin implements PluginEntry {
    private final List<MyTransformer> transformers = new ArrayList<>();

    @Override
    public void init(List<FilterRule> filterRules) {
        transformers.add(new InetAddressTransformer(filterRules));
    }

    @Override
    public String getName() {
        return "DNS";
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
        return "ja-netfilter core: dns plugin";
    }

    @Override
    public List<MyTransformer> getTransformers() {
        return transformers;
    }
}
