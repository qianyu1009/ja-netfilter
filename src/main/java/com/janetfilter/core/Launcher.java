package com.janetfilter.core;

import com.janetfilter.core.attach.VMLauncher;
import com.janetfilter.core.attach.VMSelector;
import com.janetfilter.core.commons.DebugInfo;
import com.janetfilter.core.utils.WhereIsUtils;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.net.URI;
import java.util.jar.JarFile;

public class Launcher {
    public static final String ATTACH_ARG = "--attach";
    public static final String VERSION = "v2.2.1";

    private static boolean loaded = false;

    public static void main(String[] args) {
        URI jarURI;
        try {
            jarURI = WhereIsUtils.getJarURI();
        } catch (Throwable e) {
            DebugInfo.error("Can not locate `ja-netfilter` jar file.", e);
            return;
        }

        String jarPath = jarURI.getPath();
        if (args.length > 1 && args[0].equals(ATTACH_ARG)) {
            VMLauncher.attachVM(jarPath, args[1], args.length > 2 ? args[2] : null);
            return;
        }

        printUsage();

        try {
            new VMSelector(new File(jarPath)).select();
        } catch (Throwable e) {
            System.err.println("  ERROR: Select virtual machine failed.");
            e.printStackTrace(System.err);
        }
    }

    public static void premain(String args, Instrumentation inst) {
        if (loaded) {
            DebugInfo.warn("You have multiple `ja-netfilter` as javaagent.");
            return;
        }

        printUsage();

        URI jarURI;
        try {
            loaded = true;
            jarURI = WhereIsUtils.getJarURI();
        } catch (Throwable e) {
            DebugInfo.error("Can not locate `ja-netfilter` jar file.", e);
            return;
        }

        File agentFile = new File(jarURI.getPath());
        try {
            inst.appendToBootstrapClassLoaderSearch(new JarFile(agentFile));
        } catch (Throwable e) {
            DebugInfo.error("Can not access `ja-netfilter` jar file.", e);
            return;
        }

        Initializer.init(inst, new Environment(agentFile, args)); // for some custom UrlLoaders
    }

    public static void agentmain(String args, Instrumentation inst) {
        premain(args, inst);
    }

    private static void printUsage() {
        String content = "\n  ============================================================================  \n" +
                "\n" +
                "    ja-netfilter-" + VERSION +
                "\n\n" +
                "    A javaagent framework :)\n" +
                "\n" +
                "    https://github.com/ja-netfilter/ja-netfilter\n" +
                "\n" +
                "  ============================================================================  \n\n";

        System.out.print(content);
    }
}
