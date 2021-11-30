package io.zhile.research.ja.netfilter;

import io.zhile.research.ja.netfilter.commons.DebugInfo;
import io.zhile.research.ja.netfilter.plugin.MyTransformer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.*;

public class Dispatcher implements ClassFileTransformer {
    private static Dispatcher INSTANCE;

    private final Map<String, List<MyTransformer>> transformerMap = new HashMap<>();

    public static synchronized Dispatcher getInstance() {
        if (null == INSTANCE) {
            INSTANCE = new Dispatcher();
        }

        return INSTANCE;
    }

    public void addTransformer(MyTransformer transformer) {
        List<MyTransformer> transformers = transformerMap.computeIfAbsent(transformer.getHookClassName(), k -> new ArrayList<>());

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
                for (MyTransformer transformer : transformers) {
                    classFileBuffer = transformer.transform(className, classFileBuffer, order++);
                }
            } catch (Throwable e) {
                DebugInfo.output("Transform class failed: " + e.getMessage());
            }
        } while (false);

        return classFileBuffer;
    }
}
