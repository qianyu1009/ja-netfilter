package com.janetfilter.core;

import com.janetfilter.core.commons.DebugInfo;
import com.janetfilter.core.plugin.PluginManager;

import java.lang.instrument.Instrumentation;
import java.util.Set;

public class Initializer {
    public static void init(Instrumentation inst, Environment environment) {
        DebugInfo.output(environment.toString());

        Dispatcher dispatcher = new Dispatcher();
        new PluginManager(inst, dispatcher, environment).loadPlugins();

        inst.addTransformer(dispatcher, true);
        inst.setNativeMethodPrefix(dispatcher, environment.getNativePrefix());

        Set<String> classSet = dispatcher.getHookClassNames();
        for (Class<?> c : inst.getAllLoadedClasses()) {
            String name = c.getName();
            if (!classSet.contains(name)) {
                continue;
            }

            try {
                inst.retransformClasses(c);
            } catch (Throwable e) {
                DebugInfo.output("Retransform class failed: " + name, e);
            }
        }
    }
}
