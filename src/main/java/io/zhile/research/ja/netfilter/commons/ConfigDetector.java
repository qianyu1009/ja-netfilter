package io.zhile.research.ja.netfilter.commons;

import io.zhile.research.ja.netfilter.utils.StringUtils;

import java.io.File;

public class ConfigDetector {
    private static final String CONFIG_FILENAME = "janf_config.txt";

    public static File detect(File currentDirectory, String args) {
        return detect(currentDirectory.getPath(), args);
    }

    public static File detect(String currentDirectory, String args) {
        File configFile = tryFile(args);        // by javaagent argument

        if (null == configFile) {
            configFile = tryFile(System.getenv("JANF_CONFIG")); // by env
        }

        if (null == configFile) {
            configFile = tryFile(System.getProperty("janf.config"));    // by -D argument
        }

        if (null == configFile) {
            configFile = searchDirectory(currentDirectory); // in the same dir as the jar
        }

        String userHome = System.getProperty("user.home");
        if (null == configFile) {
            configFile = searchDirectory(userHome, "." + CONFIG_FILENAME);  // $HOME/.janf_config.txt
        }

        if (null == configFile) {
            configFile = searchDirectory(userHome + File.pathSeparator + ".config");    // $HOME/.config/janf_config.txt
        }

        if (null == configFile) {
            configFile = searchDirectory(userHome + File.pathSeparator + ".local" + File.pathSeparator + "/etc");   // $HOME/.local/etc/janf_config.txt
        }

        if (null == configFile) {
            configFile = searchDirectory("/usr/local/etc");     // /usr/local/etc/janf_config.txt
        }

        if (null == configFile) {
            configFile = searchDirectory("/etc");               // /etc/janf_config.txt
        }

        return configFile;
    }

    private static File searchDirectory(String dirPath) {
        return searchDirectory(dirPath, CONFIG_FILENAME);
    }

    private static File searchDirectory(String dirPath, String filename) {
        if (StringUtils.isEmpty(dirPath)) {
            return null;
        }

        File dirFile = new File(dirPath);
        if (!dirFile.isDirectory()) {
            return null;
        }

        return tryFile(new File(dirFile, filename));
    }

    private static File tryFile(String filePath) {
        if (StringUtils.isEmpty(filePath)) {
            return null;
        }

        return tryFile(new File(filePath));
    }

    private static File tryFile(File file) {
        if (!file.exists()) {
            return null;
        }

        if (!file.isFile()) {
            return null;
        }

        if (!file.canRead()) {
            return null;
        }

        return file;
    }
}
