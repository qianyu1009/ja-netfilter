package io.zhile.research.ja.netfilter;

import io.zhile.research.ja.netfilter.transformers.HttpClientTransformer;
import io.zhile.research.ja.netfilter.transformers.InetAddressTransformer;
import io.zhile.research.ja.netfilter.transformers.MyTransformer;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;

public class TransformDispatcher implements ClassFileTransformer {
    public static final Map<String, MyTransformer> TRANSFORMER_MAP;

    static {
        TRANSFORMER_MAP = new HashMap<>();
        TRANSFORMER_MAP.put("sun/net/www/http/HttpClient", new HttpClientTransformer());
        TRANSFORMER_MAP.put("java/net/InetAddress", new InetAddressTransformer());
    }

    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classFileBuffer) throws IllegalClassFormatException {
        do {
            if (null == className) {
                break;
            }

            MyTransformer transformer = TRANSFORMER_MAP.get(className);
            if (null == transformer) {
                break;
            }

            try {
                return transformer.transform(className, classFileBuffer);
            } catch (Exception e) {
                System.out.println("=== Transform class failed: " + e.getMessage());
            }
        } while (false);

        return classFileBuffer;
    }
}
