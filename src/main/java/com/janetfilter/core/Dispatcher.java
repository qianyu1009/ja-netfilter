package com.janetfilter.core;

import com.janetfilter.core.commons.DebugInfo;
import com.janetfilter.core.plugin.MyTransformer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.*;

public final class Dispatcher implements ClassFileTransformer {
    private final Set<String> classSet = new TreeSet<>();
    private final Map<String, List<MyTransformer>> transformerMap = new HashMap<>();
    private final List<MyTransformer> globalTransformers = new ArrayList<>();

    public synchronized void addTransformer(MyTransformer transformer) {
        String className = transformer.getHookClassName();
        if (null == className) {
            globalTransformers.add(transformer);
            return;
        }

        classSet.add(className.replace('/', '.'));
        List<MyTransformer> transformers = transformerMap.computeIfAbsent(className, k -> new ArrayList<>());

        transformers.add(transformer);
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
        do {
            if (null == className) {
                break;
            }

            List<MyTransformer> transformers = transformerMap.get(className);
            if (null == transformers) {
                break;
            }

            int order = 0;

            try {
                for (MyTransformer transformer : globalTransformers) {
                    classFileBuffer = transformer.transform(className, classFileBuffer, order++);
                }

                for (MyTransformer transformer : transformers) {
                    classFileBuffer = transformer.transform(className, classFileBuffer, order++);
                }
            } catch (Throwable e) {
                DebugInfo.error("Transform class failed: " + className, e);
            }
        } while (false);

        return classFileBuffer;
    }
}
