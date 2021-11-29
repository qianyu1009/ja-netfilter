package io.zhile.research.ja.netfilter.transformers;

import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.tree.*;

import static jdk.internal.org.objectweb.asm.Opcodes.*;

public class HttpClientTransformer implements MyTransformer {
    @Override
    public byte[] transform(String className, byte[] classBytes) throws Exception {
        ClassReader reader = new ClassReader(classBytes);
        ClassNode node = new ClassNode(ASM5);
        reader.accept(node, 0);

        for (MethodNode mn : node.methods) {
            if ("openServer".equals(mn.name) && "()V".equals(mn.desc)) {
                InsnList list = new InsnList();
                list.add(new VarInsnNode(ALOAD, 0));
                list.add(new FieldInsnNode(GETFIELD, "sun/net/www/http/HttpClient", "url", "Ljava/net/URL;"));
                list.add(new MethodInsnNode(INVOKESTATIC, "io/zhile/research/ja/netfilter/filters/URLFilter", "testURL", "(Ljava/net/URL;)Ljava/net/URL;", false));
                list.add(new InsnNode(POP));

                mn.instructions.insert(list);
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        node.accept(writer);

        return writer.toByteArray();
    }
}
