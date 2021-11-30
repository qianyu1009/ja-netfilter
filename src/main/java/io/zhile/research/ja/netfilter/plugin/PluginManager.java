package io.zhile.research.ja.netfilter.plugin;

import io.zhile.research.ja.netfilter.Dispatcher;
import io.zhile.research.ja.netfilter.commons.DebugInfo;
import io.zhile.research.ja.netfilter.models.FilterConfig;
import io.zhile.research.ja.netfilter.transformers.HttpClientTransformer;
import io.zhile.research.ja.netfilter.transformers.InetAddressTransformer;
import io.zhile.research.ja.netfilter.transformers.MyTransformer;
import io.zhile.research.ja.netfilter.utils.StringUtils;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.util.Arrays;
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
        File pluginsDirectory = new File(currentDirectory, PLUGINS_DIR);
        if (!pluginsDirectory.exists() || !pluginsDirectory.isDirectory()) {
            return;
        }

        File[] pluginFiles = pluginsDirectory.listFiles((d, n) -> n.endsWith(".jar"));
        if (null == pluginFiles) {
            return;
        }

        Dispatcher.getInstance().addTransformers(new MyTransformer[]{   // built-in transformers
                new HttpClientTransformer(),
                new InetAddressTransformer()
        });

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

                PluginEntry pluginEntry = (PluginEntry) Class.forName(entryClass).newInstance();
                pluginEntry.init(FilterConfig.getBySection(pluginEntry.getName()));
                Dispatcher.getInstance().addTransformers(pluginEntry.getTransformers());

                DebugInfo.output("Plugin loaded: {name=" + pluginEntry.getName() + ", version=" + pluginEntry.getVersion() + "}");
            } catch (Exception e) {
                DebugInfo.output("Load plugin failed: " + e.getMessage());
            }
        }
    }
}
