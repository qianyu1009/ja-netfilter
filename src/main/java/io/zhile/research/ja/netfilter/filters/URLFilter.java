package io.zhile.research.ja.netfilter.filters;

import io.zhile.research.ja.netfilter.commons.DebugInfo;
import io.zhile.research.ja.netfilter.models.FilterConfig;
import io.zhile.research.ja.netfilter.models.FilterRule;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;

public class URLFilter {
    public static final String SECTION_NAME = "URL";

    public static URL testURL(URL url) throws IOException {
        if (null == url) {
            return null;
        }

        for (FilterRule rule : FilterConfig.getBySection(SECTION_NAME)) {
            if (!rule.test(url.toString())) {
                continue;
            }

            DebugInfo.output("Reject url: " + url + ", rule: " + rule);
            throw new SocketTimeoutException("connect timed out");
        }

        return url;
    }
}
