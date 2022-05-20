package com.janetfilter.core;

import com.janetfilter.core.commons.DebugInfo;
import com.janetfilter.core.plugin.PluginManager;

import java.lang.instrument.Instrumentation;
import java.util.Set;

public class Initializer {
    public static void init(Environment environment) {
        DebugInfo.useFile(environment.getLogsDir());
        DebugInfo.info(environment.toString());

        Dispatcher dispatcher = new Dispatcher(environment);
        new PluginManager(dispatcher, environment).loadPlugins();

        Instrumentation inst = environment.getInstrumentation();
        inst.addTransformer(dispatcher, true);
        inst.setNativeMethodPrefix(dispatcher, environment.getNativePrefix());

        Set<String> classSet = dispatcher.getHookClassNames();
        for (Class<?> c : inst.getAllLoadedClasses()) {
            String name = c.getName();
            if (!classSet.contains(name)) {
                continue;
            }

            try {
                c.getGenericSuperclass();
                inst.retransformClasses(c);
            } catch (Throwable e) {
                DebugInfo.error("Retransform class failed: " + name, e);
            }
        }
    }
}
