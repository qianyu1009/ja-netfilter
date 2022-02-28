package com.janetfilter.core;

import com.janetfilter.core.utils.ProcessUtils;
import com.janetfilter.core.utils.StringUtils;

import java.io.File;
import java.lang.instrument.Instrumentation;

public final class Environment {
    private final String pid;
    private final String version;
    private final int versionNumber;
    private final String appName;
    private final File baseDir;
    private final File agentFile;
    private final File configDir;
    private final File pluginsDir;
    private final File logsDir;
    private final String nativePrefix;
    private final String disabledPluginSuffix;
    private final boolean attachMode;

    private final Instrumentation instrumentation;

    public Environment(Instrumentation instrumentation, File agentFile, boolean attachMode) {
        this(instrumentation, agentFile, null, attachMode);
    }

    public Environment(Instrumentation instrumentation, File agentFile, String app, boolean attachMode) {
        this.instrumentation = instrumentation;
        this.agentFile = agentFile;
        baseDir = agentFile.getParentFile();

        if (StringUtils.isEmpty(app)) {
            appName = "";
            configDir = new File(baseDir, "config");
            pluginsDir = new File(baseDir, "plugins");
            logsDir = new File(baseDir, "logs");
        } else {
            appName = app.toLowerCase();
            configDir = new File(baseDir, "config-" + appName);
            pluginsDir = new File(baseDir, "plugins-" + appName);
            logsDir = new File(baseDir, "logs-" + appName);
        }

        pid = ProcessUtils.currentId();
        version = Launcher.VERSION;
        versionNumber = Launcher.VERSION_NUMBER;
        nativePrefix = StringUtils.randomMethodName(15) + "_";
        disabledPluginSuffix = ".disabled.jar";
        this.attachMode = attachMode;
    }

    public String getPid() {
        return pid;
    }

    public String getVersion() {
        return version;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public String getAppName() {
        return appName;
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

    public String getDisabledPluginSuffix() {
        return disabledPluginSuffix;
    }

    public boolean isAttachMode() {
        return attachMode;
    }

    public boolean isJavaagentMode() {
        return !attachMode;
    }

    public Instrumentation getInstrumentation() {
        return instrumentation;
    }

    @Override
    public String toString() {
        return "Environment: {" +
                "\n\tpid = " + pid +
                ", \n\tversion = " + version +
                ", \n\tversionNumber = " + versionNumber +
                ", \n\tappName = " + appName +
                ", \n\tbaseDir = " + baseDir +
                ", \n\tagentFile = " + agentFile +
                ", \n\tconfigDir = " + configDir +
                ", \n\tpluginsDir = " + pluginsDir +
                ", \n\tlogsDir = " + logsDir +
                ", \n\tnativePrefix = " + nativePrefix +
                ", \n\tdisabledPluginSuffix = " + disabledPluginSuffix +
                ", \n\tattachMode = " + attachMode +
                "\n}";
    }
}
