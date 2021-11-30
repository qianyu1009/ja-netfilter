package io.zhile.research.ja.netfilter.transformers;

public interface MyTransformer {
    String getHookClassName();

    default byte[] transform(String className, byte[] classBytes, int order) throws Exception {
        return classBytes;
    }
}
