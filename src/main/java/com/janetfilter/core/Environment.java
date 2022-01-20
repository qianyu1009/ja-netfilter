package com.janetfilter.core;

import com.janetfilter.core.utils.ProcessUtils;
import com.janetfilter.core.utils.StringUtils;

import java.io.File;

public final class Environment {
    private final String pid;
    private final File baseDir;
    private final File agentFile;
    private final File configDir;
    private final File pluginsDir;
    private final File logsDir;
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
            logsDir = new File(baseDir, "logs");
        } else {
            app = app.toLowerCase();
            configDir = new File(baseDir, "config-" + app);
            pluginsDir = new File(baseDir, "plugins-" + app);
            logsDir = new File(baseDir, "logs-" + app);
        }

        nativePrefix = StringUtils.randomMethodName(15) + "_";
        pid = ProcessUtils.currentId();
    }

    public String getPid() {
        return pid;
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

    public File getLogsDir() {
        return logsDir;
    }

    public String getNativePrefix() {
        return nativePrefix;
    }

    @Override
    public String toString() {
        return "Environment: {" +
                "\n\tpid = " + pid +
                ", \n\tbaseDir = " + baseDir +
                ", \n\tagentFile = " + agentFile +
                ", \n\tconfigDir = " + configDir +
                ", \n\tpluginsDir = " + pluginsDir +
                ", \n\tlogsDir = " + logsDir +
                ", \n\tnativePrefix = " + nativePrefix +
                "\n}";
    }
}
