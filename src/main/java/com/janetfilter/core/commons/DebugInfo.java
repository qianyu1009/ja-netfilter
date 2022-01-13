package com.janetfilter.core.commons;

import com.janetfilter.core.utils.DateUtils;

public class DebugInfo {
    private static final boolean DEBUG = "1".equals(System.getenv("JANF_DEBUG")) || "1".equals(System.getProperty("janf.debug"));
    private static final String CLASS_NAME = DebugInfo.class.getName();
    private static final String LOG_TEMPLATE = "[%s] %s DEBUG : %s%n";

    public static void output(String content) {
        output(content, null);
    }

    public static void output(String content, Throwable e) { // No logger lib required
        if (!DEBUG) {
            return;
        }

        String caller = "UNKNOWN";
        StackTraceElement[] traces = new Throwable().getStackTrace();
        for (int i = 1, l = traces.length; i < l; i++) {    // thank RayGicEFL
            StackTraceElement element = traces[i];
            if (!CLASS_NAME.equals(element.getClassName())) {
                caller = element.toString();
                break;
            }
        }

        String outContent = String.format(LOG_TEMPLATE, DateUtils.formatNow(), caller, content);
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
