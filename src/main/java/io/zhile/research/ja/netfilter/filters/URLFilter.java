package io.zhile.research.ja.netfilter.filters;

import io.zhile.research.ja.netfilter.enums.RuleType;
import io.zhile.research.ja.netfilter.models.FilterRule;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class URLFilter {
    public static final List<FilterRule> RULES;

    static {
        RULES = new ArrayList<>();      // TODO read from config file
        RULES.add(new FilterRule(RuleType.PREFIX, "https://zhile.io"));
    }

    public static URL testURL(URL url) throws IOException {
        if (null == url) {
            return null;
        }

        for (FilterRule rule : RULES) {
            switch (rule.getType()) {   // TODO rewrite
                case PREFIX:
                    if (url.toString().startsWith(rule.getContent())) {
                        System.out.println("=== reject url: " + url.toString());
                        throw new SocketTimeoutException("connect timed out");
                    }
                default:    // TODO support more rule types
                    return url;
            }
        }

        return url;
    }
}
