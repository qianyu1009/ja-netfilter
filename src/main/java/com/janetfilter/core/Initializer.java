package com.janetfilter.core;

import com.janetfilter.core.commons.ConfigDetector;
import com.janetfilter.core.commons.ConfigParser;
import com.janetfilter.core.commons.DebugInfo;
import com.janetfilter.core.models.FilterConfig;
import com.janetfilter.core.plugin.PluginManager;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.util.Set;

public class Initializer {
    public static void init(String args, Instrumentation inst, Environment environment) {
        File configFile = ConfigDetector.detect(environment.getBaseDir(), args);
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

        Dispatcher dispatcher = new Dispatcher();
        new PluginManager(dispatcher, environment).loadPlugins(inst);

        inst.addTransformer(dispatcher, true);

        Set<String> classSet = dispatcher.getHookClassNames();
        for (Class<?> c : inst.getAllLoadedClasses()) {
            String name = c.getName();
            if (!classSet.contains(name)) {
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
