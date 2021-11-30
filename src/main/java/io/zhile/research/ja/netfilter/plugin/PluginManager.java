package io.zhile.research.ja.netfilter.plugin;

import io.zhile.research.ja.netfilter.Dispatcher;
import io.zhile.research.ja.netfilter.commons.DebugInfo;
import io.zhile.research.ja.netfilter.models.FilterConfig;
import io.zhile.research.ja.netfilter.plugins.dns.DNSFilterPlugin;
import io.zhile.research.ja.netfilter.plugins.url.URLFilterPlugin;
import io.zhile.research.ja.netfilter.utils.StringUtils;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class PluginManager {
    private static final String PLUGINS_DIR = "plugins";
    private static final String ENTRY_NAME = "JANF-Plugin-Entry";

    private static PluginManager INSTANCE;

    public static synchronized PluginManager getInstance() {
        if (null == INSTANCE) {
            INSTANCE = new PluginManager();
        }

        return INSTANCE;
    }

    public void loadPlugins(Instrumentation inst, File currentDirectory) {
        for (Class<? extends PluginEntry> klass : getAllPluginClasses(inst, currentDirectory)) {
            try {
                addPluginEntry(klass);
            } catch (Throwable e) {
                DebugInfo.output("Init plugin failed: " + e.getMessage());
            }
        }
    }

    private List<Class<? extends PluginEntry>> getAllPluginClasses(Instrumentation inst, File currentDirectory) {
        List<Class<? extends PluginEntry>> classes = new ArrayList<>();
        classes.add(DNSFilterPlugin.class);
        classes.add(URLFilterPlugin.class);

        do {
            File pluginsDirectory = new File(currentDirectory, PLUGINS_DIR);
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

        pluginEntry.init(FilterConfig.getBySection(pluginEntry.getName()));
        Dispatcher.getInstance().addTransformers(pluginEntry.getTransformers());

        DebugInfo.output("Plugin loaded: {name=" + pluginEntry.getName() + ", version=" + pluginEntry.getVersion() + ", author=" + pluginEntry.getAuthor() + "}");
    }
}
