package com.janetfilter.core.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

public class ProcessUtils {
    private static String processId;

    public synchronized static String currentId() {
        if (null == processId) {
            String name = ManagementFactory.getRuntimeMXBean().getName() + "@";

            processId = name.split("@", 2)[0];
        }

        return processId;
    }

    public static int start(ProcessBuilder pb) throws Exception {
        return start(pb, System.out, System.err);
    }

    public static int start(ProcessBuilder pb, OutputStream out) throws Exception {
        return start(pb, out, null);
    }

    public static int start(ProcessBuilder pb, OutputStream out, OutputStream err) throws Exception {
        Process p = pb.start();

        List<Thread> threads = new ArrayList<>();
        if (null != out) {
            threads.add(new Thread(new RedirectOutput(p.getInputStream(), out)));
        }
        if (null != err) {
            threads.add(new Thread(new RedirectOutput(p.getErrorStream(), err)));
        }

        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }

        return p.waitFor();
    }

    static class RedirectOutput implements Runnable {
        private static final int BUFF_SIZE = 1024;
        private final InputStream origin;
        private final OutputStream dest;

        RedirectOutput(InputStream origin, OutputStream dest) {
            this.origin = origin;
            this.dest = dest;
        }

        public void run() {
            int length;
            byte[] buffer = new byte[BUFF_SIZE];

            try {
                while ((length = origin.read(buffer)) != -1) {
                    dest.write(buffer, 0, length);
                }
            } catch (IOException e) {
                throw new RuntimeException("ERROR: Redirect output failed.", e);
            }
        }
    }
}
