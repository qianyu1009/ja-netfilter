package com.janetfilter.core.plugin;

import java.security.ProtectionDomain;

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
     * whether it is a management transformer
     *
     * @return return true to handle the transform of all classes
     */
    default boolean isManager() {
        return false;
    }

    /**
     * for global transformers only
     */
    default void before(ClassLoader loader, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, String className, byte[] classBytes) throws Exception {
        before(className, classBytes);
    }

    /**
     * for global transformers only
     */
    default void before(String className, byte[] classBytes) throws Exception {

    }

    /**
     * for global transformers only
     */
    default byte[] preTransform(ClassLoader loader, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, String className, byte[] classBytes, int order) throws Exception {
        return preTransform(className, classBytes, order);
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
    default byte[] transform(ClassLoader loader, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, String className, byte[] classBytes, int order) throws Exception {
        return transform(className, classBytes, order);
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
    default byte[] postTransform(ClassLoader loader, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, String className, byte[] classBytes, int order) throws Exception {
        return postTransform(className, classBytes, order);
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
    default void after(ClassLoader loader, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, String className, byte[] classBytes) throws Exception {
        after(className, classBytes);
    }

    /**
     * for global transformers only
     */
    default void after(String className, byte[] classBytes) throws Exception {

    }
}
