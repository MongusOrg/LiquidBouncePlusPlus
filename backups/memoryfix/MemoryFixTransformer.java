package net.ccbluex.liquidbounce.injection.transformers;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.RemappingClassAdapter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Iterator;
import java.util.function.BiConsumer;

public class MemoryFixTransformer implements IClassTransformer {

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (name.equals("CapeUtils")) {
            // Use our CapeImageBuffer instead of OptiFine's
            return transformCapeUtils(bytes);
        } else if (name.equals("net.ccbluex.liquidbounce.memoryfix.CapeImageBuffer")) {
            // Redirect our stub calls to optifine
            return transformMethods(bytes, this::transformCapeImageBuffer);
        } else if (transformedName.equals("net.minecraft.client.resources.AbstractResourcePack")) {
            return transformMethods(bytes, this::transformAbstractResourcePack);
        } else if (transformedName.equals("net.minecraft.client.Minecraft")) {
            // Remove System.gc calls (they all happen in this class)
            return transformMethods(bytes, this::transformMinecraft);
        } else {
            return bytes;
        }
    }

    private byte[] transformMethods(byte[] bytes, BiConsumer<ClassNode, MethodNode> transformer) {
        ClassReader classReader = new ClassReader(bytes);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        classNode.methods.forEach(m -> transformer.accept(classNode, m));

        ClassWriter classWriter = new ClassWriter(0);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    private byte[] transformCapeUtils(byte[] bytes) {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        RemappingClassAdapter adapter = new RemappingClassAdapter(classWriter, new Remapper() {
            @Override
            public String map(String typeName) {
                if (typeName.equals("CapeUtils$1")) {
                    return "net.ccbluex.liquidbounce.memoryfix.CapeImageBuffer".replace('.', '/');
                }
                return typeName;
            }
        });

        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(adapter, ClassReader.EXPAND_FRAMES);
        return classWriter.toByteArray();
    }

    private void transformCapeImageBuffer(ClassNode clazz, MethodNode method) {
        Iterator<AbstractInsnNode> iter = method.instructions.iterator();
        while (iter.hasNext()) {
            AbstractInsnNode insn = iter.next();
            if (insn instanceof MethodInsnNode) {
                MethodInsnNode methodInsn = (MethodInsnNode) insn;
                if (methodInsn.name.equals("parseCape")) {
                    methodInsn.owner = "CapeUtils";
                } else if (methodInsn.name.equals("setLocationOfCape")) {
                    methodInsn.setOpcode(Opcodes.INVOKEVIRTUAL);
                    methodInsn.owner = "net/minecraft/client/entity/AbstractClientPlayer";
                    methodInsn.desc = "(Lnet/minecraft/util/ResourceLocation;)V";
                }
            }
        }
    }

    private void transformAbstractResourcePack(ClassNode clazz, MethodNode method) {
        if ((method.name.equals("getPackImage") || method.name.equals("func_110586_a")) && method.desc.equals("()Ljava/awt/image/BufferedImage;")) {
            Iterator<AbstractInsnNode> iter = method.instructions.iterator();
            while (iter.hasNext()) {
                AbstractInsnNode insn = iter.next();
                if (insn.getOpcode() == Opcodes.ARETURN) {
                    method.instructions.insertBefore(insn, new MethodInsnNode(
                            Opcodes.INVOKESTATIC,
                            "net.ccbluex.liquidbounce.memoryfix.ResourcePackImageScaler".replace('.', '/'),
                            "scalePackImage",
                            "(Ljava/awt/image/BufferedImage;)Ljava/awt/image/BufferedImage;",
                            false));
                }
            }
        }
    }

    private void transformMinecraft(ClassNode clazz, MethodNode method) {
        Iterator<AbstractInsnNode> iter = method.instructions.iterator();
        while (iter.hasNext()) {
            AbstractInsnNode insn = iter.next();
            if (insn.getOpcode() == Opcodes.INVOKESTATIC) {
                MethodInsnNode methodInsn = (MethodInsnNode) insn;
                if (methodInsn.owner.equals("java/lang/System") && methodInsn.name.equals("gc")) {
                    iter.remove();
                }
            }
        }
    }
}