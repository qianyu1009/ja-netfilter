package io.zhile.research.ja.netfilter;

import io.zhile.research.ja.netfilter.commons.ConfigDetector;
import io.zhile.research.ja.netfilter.commons.ConfigParser;
import io.zhile.research.ja.netfilter.commons.DebugInfo;
import io.zhile.research.ja.netfilter.models.FilterConfig;
import io.zhile.research.ja.netfilter.plugin.PluginManager;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.util.Set;

public class Initializer {
    public static void init(String args, Instrumentation inst, File currentDirectory) {
        File configFile = ConfigDetector.detect(currentDirectory, args);
        if (null == configFile) {
            DebugInfo.output("Could not find any configuration files.");
        } else {
            DebugInfo.output("Current config file: " + configFile.getPath());
        }

        try {
            FilterConfig.setCurrent(new FilterConfig(ConfigParser.parse(configFile)));
        } catch (Throwable e) {
            DebugInfo.output(e.getMessage());
        }

        PluginManager.getInstance().loadPlugins(inst, currentDirectory);

        inst.addTransformer(Dispatcher.getInstance(), true);

        Set<String> classSet = Dispatcher.getInstance().getHookClassNames();
        for (Class<?> c : inst.getAllLoadedClasses()) {
            String name = c.getName();
            if (!classSet.contains(name.replace('.', '/'))) {
                continue;
            }

            try {
                inst.retransformClasses(c);
            } catch (Throwable e) {
                DebugInfo.output("Retransform class failed: " + name);
            }
        }
    }
}
