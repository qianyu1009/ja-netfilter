package com.janetfilter.core;

import com.janetfilter.core.utils.StringUtils;

import java.io.File;

public final class Environment {
    private final File baseDir;
    private final File agentFile;
    private final File configDir;
    private final File pluginsDir;

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

    @Override
    public String toString() {
        return "Environment: {" +
                "\n\tbaseDir=" + baseDir +
                ", \n\tagentFile=" + agentFile +
                ", \n\tconfigDir=" + configDir +
                ", \n\tpluginsDir=" + pluginsDir +
                "\n}";
    }
}
