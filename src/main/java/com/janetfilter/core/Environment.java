package com.janetfilter.core;

import com.janetfilter.core.utils.StringUtils;

import java.io.File;

public final class Environment {
    private final File baseDir;
    private final File agentFile;
    private final File configDir;
    private final File pluginsDir;
    private final String nativePrefix;

    public Environment(File agentFile) {
        this(agentFile, null);
    }

    public Environment(File agentFile, String app) {
        this.agentFile = agentFile;
        baseDir = agentFile.getParentFile();

        if (StringUtils.isEmpty(app)) {
            configDir = new File(baseDir, "config");
            pluginsDir = new File(baseDir, "plugins");
        } else {
            app = app.toLowerCase();
            configDir = new File(baseDir, "config-" + app);
            pluginsDir = new File(baseDir, "plugins-" + app);
        }

        nativePrefix = StringUtils.randomMethodName(15) + "_";
    }

    public File getBaseDir() {
        return baseDir;
    }

    public File getAgentFile() {
        return agentFile;
    }

    public File getConfigDir() {
        return configDir;
    }

    public File getPluginsDir() {
        return pluginsDir;
    }

    public String getNativePrefix() {
        return nativePrefix;
    }

    @Override
    public String toString() {
        return "Environment: {" +
                "\n\tbaseDir=" + baseDir +
                ", \n\tagentFile=" + agentFile +
                ", \n\tconfigDir=" + configDir +
                ", \n\tpluginsDir=" + pluginsDir +
                ", \n\tnativePrefix=" + nativePrefix +
                "\n}";
    }
}
