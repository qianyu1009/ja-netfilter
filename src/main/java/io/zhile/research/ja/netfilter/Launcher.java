package io.zhile.research.ja.netfilter;

import io.zhile.research.ja.netfilter.commons.ConfigDetector;
import io.zhile.research.ja.netfilter.commons.ConfigParser;
import io.zhile.research.ja.netfilter.commons.DebugInfo;
import io.zhile.research.ja.netfilter.models.FilterConfig;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.JarFile;

public class Launcher {
    public static void main(String[] args) {
        printUsage();
    }

    public static void premain(String args, Instrumentation inst) {
        printUsage();

        URL jarURL = getJarURL();
        if (null == jarURL) {
            throw new RuntimeException("Can not locate ja-netfilter jar file.");
        }

        try {
            inst.appendToBootstrapClassLoaderSearch(new JarFile(jarURL.getPath()));
        } catch (Throwable e) {
            throw new RuntimeException("Can not access ja-netfilter jar file.", e);
        }

        File configFile = ConfigDetector.detect(new File(jarURL.getPath()).getParentFile().getPath(), args);
        if (null == configFile) {
            DebugInfo.output("Could not find any configuration files.");
        } else {
            DebugInfo.output("Current config file: " + configFile.getPath());
        }

        try {
            FilterConfig.setCurrent(new FilterConfig(ConfigParser.parse(configFile)));
        } catch (Exception e) {
            DebugInfo.output(e.getMessage());
        }

        for (Class<?> c : inst.getAllLoadedClasses()) {
            try {
                inst.retransformClasses(c);
            } catch (UnmodifiableClassException e) {
                // ok, ok. just ignore
            }
        }

        inst.addTransformer(new TransformDispatcher(), true);
    }

    private static void printUsage() {
        String content = "\n  ============================================================================  \n" +
                "\n" +
                "    A javaagent lib for network filter :)\n" +
                "\n" +
                "    https://github.com/pengzhile/ja-netfilter\n" +
                "\n" +
                "  ============================================================================  \n\n";

        System.out.print(content);
        System.out.flush();
    }

    private static URL getJarURL() {
        URL url = Launcher.class.getProtectionDomain().getCodeSource().getLocation();
        if (null != url) {
            return url;
        }

        String resourcePath = "/442fcf28466515a81d5434931496ffa64611cc8e.txt";
        url = Launcher.class.getResource(resourcePath);
        if (null == url) {
            return null;
        }

        String path = url.getPath();
        if (!path.endsWith("!" + resourcePath)) {
            return null;
        }

        path = path.substring(0, path.length() - resourcePath.length() - 1);

        try {
            return new URL(path);
        } catch (MalformedURLException e) {
            return null;
        }
    }
}
