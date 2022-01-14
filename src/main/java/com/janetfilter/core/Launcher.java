package com.janetfilter.core;

import com.janetfilter.core.commons.DebugInfo;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.net.URI;
import java.net.URL;
import java.util.jar.JarFile;

public class Launcher {
    private static final String VERSION = "v2.1.1";

    private static boolean loaded = false;

    public static void main(String[] args) {
        printUsage();
    }

    public static void premain(String args, Instrumentation inst) {
        if (loaded) {
            DebugInfo.output("WARN: You have multiple `ja-netfilter` as -javaagent.");
            return;
        }

        printUsage();

        URI jarURI;
        try {
            loaded = true;
            jarURI = getJarURI();
        } catch (Throwable e) {
            DebugInfo.output("ERROR: Can not locate ja-netfilter jar file.", e);
            return;
        }

        File agentFile = new File(jarURI.getPath());
        try {
            inst.appendToBootstrapClassLoaderSearch(new JarFile(agentFile));
        } catch (Throwable e) {
            DebugInfo.output("ERROR: Can not access ja-netfilter jar file.", e);
            return;
        }

        Initializer.init(inst, new Environment(agentFile, args)); // for some custom UrlLoaders
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
        System.out.flush();
    }

    private static URI getJarURI() throws Exception {
        URL url = Launcher.class.getProtectionDomain().getCodeSource().getLocation();
        if (null != url) {
            return url.toURI();
        }

        String resourcePath = "/4cc9c353c626d6510ca855ab6907ed7f64400257.txt";
        url = Launcher.class.getResource(resourcePath);
        if (null == url) {
            throw new Exception("Can not locate resource file.");
        }

        String path = url.getPath();
        if (!path.endsWith("!" + resourcePath)) {
            throw new Exception("Invalid resource path.");
        }

        path = path.substring(0, path.length() - resourcePath.length() - 1);

        return new URI(path);
    }
}
