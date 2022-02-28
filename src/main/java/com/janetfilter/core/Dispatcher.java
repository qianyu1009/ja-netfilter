package com.janetfilter.core;

import com.janetfilter.core.commons.DebugInfo;
import com.janetfilter.core.plugin.MyTransformer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.*;

public final class Dispatcher implements ClassFileTransformer {
    private final Environment environment;
    private final Set<String> classSet = new TreeSet<>();
    private final Map<String, List<MyTransformer>> transformerMap = new HashMap<>();
    private final List<MyTransformer> globalTransformers = new ArrayList<>();
    private final List<MyTransformer> manageTransformers = new ArrayList<>();

    public Dispatcher(Environment environment) {
        this.environment = environment;
    }

    public void addTransformer(MyTransformer transformer) {
        if (null == transformer) {
            return;
        }

        if (environment.isAttachMode() && !transformer.attachMode()) {
            DebugInfo.debug("Transformer: " + transformer.getClass().getName() + " is set to not load in attach mode, ignored.");
            return;
        }

        if (environment.isJavaagentMode() && !transformer.javaagentMode()) {
            DebugInfo.debug("Transformer: " + transformer.getClass().getName() + " is set to not load in -javaagent mode, ignored.");
            return;
        }

        synchronized (this) {
            String className = transformer.getHookClassName();
            if (null == className) {
                globalTransformers.add(transformer);

                if (transformer.isManager()) {
                    manageTransformers.add(transformer);
                }

                return;
            }

            classSet.add(className.replace('/', '.'));
            List<MyTransformer> transformers = transformerMap.computeIfAbsent(className, k -> new ArrayList<>());

            transformers.add(transformer);
        }
    }

    public void addTransformers(List<MyTransformer> transformers) {
        if (null == transformers) {
            return;
        }

        for (MyTransformer transformer : transformers) {
            addTransformer(transformer);
        }
    }

    public void addTransformers(MyTransformer[] transformers) {
        if (null == transformers) {
            return;
        }

        addTransformers(Arrays.asList(transformers));
    }

    public Set<String> getHookClassNames() {
        return classSet;
    }

    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classFileBuffer) throws IllegalClassFormatException {
        if (null == className) {
            return classFileBuffer;
        }

        List<MyTransformer> transformers = transformerMap.get(className);
        List<MyTransformer> globalTransformers = null == transformers ? this.manageTransformers : this.globalTransformers;

        int order = 0;

        try {
            for (MyTransformer transformer : globalTransformers) {
                transformer.before(loader, classBeingRedefined, protectionDomain, className, classFileBuffer);
            }

            for (MyTransformer transformer : globalTransformers) {
                classFileBuffer = transformer.preTransform(loader, classBeingRedefined, protectionDomain, className, classFileBuffer, order++);
            }

            if (null != transformers) {
                for (MyTransformer transformer : transformers) {
                    classFileBuffer = transformer.transform(loader, classBeingRedefined, protectionDomain, className, classFileBuffer, order++);
                }
            }

            for (MyTransformer transformer : globalTransformers) {
                classFileBuffer = transformer.postTransform(loader, classBeingRedefined, protectionDomain, className, classFileBuffer, order++);
            }

            for (MyTransformer transformer : globalTransformers) {
                transformer.after(loader, classBeingRedefined, protectionDomain, className, classFileBuffer);
            }
        } catch (Throwable e) {
            DebugInfo.error("Transform class failed: " + className, e);
        }

        return classFileBuffer;
    }
}
