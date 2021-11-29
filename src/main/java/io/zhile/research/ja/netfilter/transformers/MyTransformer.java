package io.zhile.research.ja.netfilter.transformers;

public interface MyTransformer {
    byte[] transform(String className, byte[] classBytes) throws Exception;
}
