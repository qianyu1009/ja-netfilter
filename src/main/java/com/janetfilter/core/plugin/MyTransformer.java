package com.janetfilter.core.plugin;

public interface MyTransformer {
    String getHookClassName();

    default byte[] transform(String className, byte[] classBytes, int order) throws Exception {
        return classBytes;
    }
}
