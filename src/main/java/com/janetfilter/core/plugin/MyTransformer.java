package com.janetfilter.core.plugin;

public interface MyTransformer {
    /**
     * @return class name like this: package/to/className, null means it's a global transformer
     */
    String getHookClassName();

    /**
     * whether to load in attach mode
     */
    default boolean attachMode() {
        return true;
    }

    /**
     * whether to load in -javaagent mode
     */
    default boolean javaagentMode() {
        return true;
    }

    /**
     * for global transformers only
     */
    default void before(String className, byte[] classBytes) throws Exception {

    }

    /**
     * for global transformers only
     */
    default byte[] preTransform(String className, byte[] classBytes, int order) throws Exception {
        return transform(className, classBytes, order);     // for old version
    }

    /**
     * for normal transformers only
     */
    default byte[] transform(String className, byte[] classBytes, int order) throws Exception {
        return classBytes;
    }

    /**
     * for global transformers only
     */
    default byte[] postTransform(String className, byte[] classBytes, int order) throws Exception {
        return classBytes;
    }

    /**
     * for global transformers only
     */
    default void after(String className, byte[] classBytes) throws Exception {

    }
}
