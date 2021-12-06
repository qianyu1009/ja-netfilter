package com.janetfilter.core.plugin;

import com.janetfilter.core.Dispatcher;
import com.janetfilter.core.Environment;
import com.janetfilter.core.commons.DebugInfo;
import com.janetfilter.core.models.FilterConfig;
import com.janetfilter.core.utils.StringUtils;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public final class PluginManager {
    private static final String ENTRY_NAME = "JANF-Plugin-Entry";

    private final Dispatcher dispatcher;
    private final Environment environment;

    public PluginManager(Dispatcher dispatcher, Environment environment) {
        this.dispatcher = dispatcher;
        this.environment = environment;
    }

    public void loadPlugins(Instrumentation inst) {
        for (Class<? extends PluginEntry> klass : getAllPluginClasses(inst)) {
            try {
                addPluginEntry(klass);
            } catch (Throwable e) {
                DebugInfo.output("Init plugin failed: " + e.getMessage());
            }
        }
    }

    private List<Class<? extends PluginEntry>> getAllPluginClasses(Instrumentation inst) {
        List<Class<? extends PluginEntry>> classes = new ArrayList<>();

        do {
            File pluginsDirectory = environment.getPluginsDir();
            if (!pluginsDirectory.exists() || !pluginsDirectory.isDirectory()) {
                break;
            }

            File[] pluginFiles = pluginsDirectory.listFiles((d, n) -> n.endsWith(".jar"));
            if (null == pluginFiles) {
                break;
            }

            for (File pluginFile : pluginFiles) {
                try {
                    JarFile jarFile = new JarFile(pluginFile);
                    Manifest manifest = jarFile.getManifest();
                    String entryClass = manifest.getMainAttributes().getValue(ENTRY_NAME);
                    if (StringUtils.isEmpty(entryClass)) {
                        continue;
                    }

                    PluginClassLoader classLoader = new PluginClassLoader(jarFile);
                    Class<?> klass = Class.forName(entryClass, false, classLoader);
                    if (!Arrays.asList(klass.getInterfaces()).contains(PluginEntry.class)) {
                        continue;
                    }

                    inst.appendToBootstrapClassLoaderSearch(jarFile);
                    classes.add((Class<? extends PluginEntry>) Class.forName(entryClass));
                } catch (Throwable e) {
                    DebugInfo.output("Load plugin failed: " + e.getMessage());
                }
            }
        } while (false);

        return classes;
    }

    private void addPluginEntry(Class<? extends PluginEntry> entryClass) throws Exception {
        PluginEntry pluginEntry = entryClass.newInstance();

        pluginEntry.init(environment, FilterConfig.getBySection(pluginEntry.getName()));
        dispatcher.addTransformers(pluginEntry.getTransformers());

        DebugInfo.output("Plugin loaded: {name=" + pluginEntry.getName() + ", version=" + pluginEntry.getVersion() + ", author=" + pluginEntry.getAuthor() + "}");
    }
}
