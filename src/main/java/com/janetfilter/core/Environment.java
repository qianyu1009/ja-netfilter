package com.janetfilter.core;

import java.io.File;

public final class Environment {
    private final File baseDir;
    private final File agentFile;
    private final File configDir;
    private final File pluginsDir;

    public Environment(File agentFile) {
        this.agentFile = agentFile;
        baseDir = agentFile.getParentFile();
        configDir = new File(baseDir, "config");
        pluginsDir = new File(baseDir, "plugins");
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
}
