package io.zhile.research.ja.netfilter.filters;

import io.zhile.research.ja.netfilter.commons.DebugInfo;
import io.zhile.research.ja.netfilter.models.FilterConfig;
import io.zhile.research.ja.netfilter.models.FilterRule;

import java.io.IOException;
import java.net.InetAddress;

public class DNSFilter {
    private static final String SECTION_NAME = "DNS";

    public static String testQuery(String host) throws IOException {
        if (null == host) {
            return null;
        }

        for (FilterRule rule : FilterConfig.getBySection(SECTION_NAME)) {
            if (!rule.test(host)) {
                continue;
            }

            DebugInfo.output("Reject dns query: " + host + ", rule: " + rule);
            throw new java.net.UnknownHostException();
        }

        return host;
    }

    public static Object testReachable(InetAddress n) throws IOException {
        if (null == n) {
            return null;
        }

        for (FilterRule rule : FilterConfig.getBySection(SECTION_NAME)) {
            if (!rule.test(n.getHostName())) {
                continue;
            }

            DebugInfo.output("Reject dns reachable test: " + n.getHostName() + ", rule: " + rule);
            return false;
        }

        return null;
    }
}
