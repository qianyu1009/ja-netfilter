package io.zhile.research.ja.netfilter;

import io.zhile.research.ja.netfilter.commons.DebugInfo;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.net.URI;
import java.net.URL;
import java.util.jar.JarFile;

public class Launcher {
    private static final String VERSION = "v1.1.4";

    public static void main(String[] args) {
        printUsage();
    }

    public static void premain(String args, Instrumentation inst) {
        printUsage();

        URI jarURI;
        try {
            jarURI = getJarURI();
        } catch (Throwable e) {
            DebugInfo.output("ERROR: Can not locate ja-netfilter jar file.");
            return;
        }

        File currentFile = new File(jarURI.getPath());
        File currentDirectory = currentFile.getParentFile();
        try {
            inst.appendToBootstrapClassLoaderSearch(new JarFile(currentFile));
        } catch (Throwable e) {
            DebugInfo.output("ERROR: Can not access ja-netfilter jar file.");
            return;
        }

        Initializer.init(args, inst, currentDirectory); // for some custom UrlLoaders
    }

    private static void printUsage() {
        String content = "\n  ============================================================================  \n" +
                "\n" +
                "    ja-netfilter-" + VERSION +
                "\n\n" +
                "    A javaagent lib for network filter :)\n" +
                "\n" +
                "    https://github.com/pengzhile/ja-netfilter\n" +
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

        String resourcePath = "/442fcf28466515a81d5434931496ffa64611cc8e.txt";
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
