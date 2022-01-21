package com.janetfilter.core.commons;

import com.janetfilter.core.utils.DateUtils;
import com.janetfilter.core.utils.ProcessUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DebugInfo {
    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
    private static final String CLASS_NAME = DebugInfo.class.getName();
    private static final String LOG_TEMPLATE = "%s %-5s [%s] %s-%d : %s%n";
    private static final Level LOG_LEVEL;
    private static File logFile;

    static {
        Level level = Level.of(System.getProperty("janf.debug"));
        LOG_LEVEL = Level.NONE == level ? Level.of(System.getenv("JANF_DEBUG")) : level;
    }

    public static void useFile(File dir) {
        if (LOG_LEVEL == Level.NONE || null == dir) {
            return;
        }

        if (!dir.exists() && !dir.mkdirs()) {
            error("Can't make directory: " + dir);
            return;
        }

        if (!dir.isDirectory()) {
            error("It's not a directory: " + dir);
            return;
        }

        if (!dir.canWrite()) {
            error("Read-only directory: " + dir);
            return;
        }

        File file = new File(dir, String.format("%s-%s.log", DateUtils.formatDate(), ProcessUtils.currentId()));
        if (file.exists()) {
            error("Log file exists: " + file);
            return;
        }

        logFile = file;
    }

    public static void debug(String content, Throwable e) {
        output(Level.DEBUG, content, e);
    }

    public static void debug(String content) {
        debug(content, null);
    }

    public static void info(String content, Throwable e) {
        output(Level.INFO, content, e);
    }

    public static void info(String content) {
        info(content, null);
    }

    public static void warn(String content, Throwable e) {
        output(Level.WARN, content, e);
    }

    public static void warn(String content) {
        warn(content, null);
    }

    public static void error(String content, Throwable e) {
        output(Level.ERROR, content, e);
    }

    public static void error(String content) {
        error(content, null);
    }

    public static void output(String content) {
        debug(content);
    }

    public static void output(String content, Throwable e) { // No logger lib required
        debug(content, e);
    }

    public static void output(Level level, String content, Throwable e) { // No logger lib required
        if (Level.NONE == LOG_LEVEL || level.ordinal() < LOG_LEVEL.ordinal()) {
            return;
        }

        EXECUTOR.execute(new WriteTask(level, content, e));
    }

    private static void writeContent(String content) {
        writeContent(content, System.out);
    }

    private static void writeContent(String content, PrintStream fallback) {
        if (null == logFile) {
            fallback.print(content);
            return;
        }

        try (PrintStream ps = new PrintStream(new FileOutputStream(logFile, true))) {
            ps.print(content);
        } catch (IOException e) {
            fallback.println(content);
        }
    }

    private static void writeException(Throwable e) {
        writeException(e, System.err);
    }

    private static void writeException(Throwable e, PrintStream fallback) {
        if (null == logFile) {
            e.printStackTrace(fallback);
            return;
        }

        try (PrintStream ps = new PrintStream(new FileOutputStream(logFile, true))) {
            e.printStackTrace(ps);
        } catch (IOException ex) {
            e.printStackTrace(fallback);
        }
    }

    private enum Level {
        NONE, DEBUG, INFO, WARN, ERROR;

        public static Level of(String valueStr) {
            if (null == valueStr) {
                return NONE;
            }

            int value;
            try {
                value = Integer.parseInt(valueStr);
            } catch (NumberFormatException e) {
                return NONE;
            }

            for (Level level : values()) {
                if (level.ordinal() == value) {
                    return level;
                }
            }

            return NONE;
        }
    }

    private static class WriteTask implements Runnable {
        private final Level level;
        private final String content;
        private final Throwable exception;
        private final Throwable stackException;

        private final String threadName;

        WriteTask(Level level, String content, Throwable exception) {
            this.level = level;
            this.content = content;
            this.exception = exception;
            this.stackException = new Throwable();
            this.threadName = Thread.currentThread().getName();
        }

        @Override
        public void run() {
            int line = 0;
            String caller = "UNKNOWN";
            StackTraceElement[] traces = stackException.getStackTrace();
            for (int i = 1, l = traces.length; i < l; i++) {    // thank RayGicEFL
                StackTraceElement element = traces[i];
                if (!CLASS_NAME.equals(element.getClassName())) {
                    line = element.getLineNumber();
                    caller = element.getClassName();
                    break;
                }
            }

            String outContent = String.format(LOG_TEMPLATE, DateUtils.formatDateTimeMicro(), level, threadName, caller, line, content);
            if (null == exception) {
                writeContent(outContent);
                return;
            }

            synchronized (DebugInfo.class) {
                writeContent(outContent, System.err);
                writeException(exception);
            }
        }
    }
}
