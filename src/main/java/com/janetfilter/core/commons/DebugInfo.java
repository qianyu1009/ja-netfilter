package com.janetfilter.core.commons;

import com.janetfilter.core.utils.DateUtils;

public class DebugInfo {
    private static final boolean DEBUG = "1".equals(System.getenv("JANF_DEBUG")) || "1".equals(System.getProperty("janf.debug"));

    public static void output(String content) {
        output(content, null);
    }

    public static void output(String content, Throwable e) { // No logger lib required
        if (!DEBUG) {
            return;
        }

        String template = "[%s] %s DEBUG : %s%n";

        StackTraceElement[] traces = new Throwable().getStackTrace();
        String caller = traces.length < 2 ? "UNKNOWN" : traces[1].toString();

        String outContent = String.format(template, DateUtils.formatNow(), caller, content);
        if (null == e) {
            System.out.print(outContent);
            return;
        }

        synchronized (DebugInfo.class) {
            System.out.print(outContent);
            e.printStackTrace(System.err);
        }
    }
}
