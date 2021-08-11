package net.ccbluex.liquidbounce.injection.transformers;

import net.ccbluex.liquidbounce.packexploitfix.ResourceTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class NetHandlerPlayClientTransformer implements ResourceTransformer {
    @Override
    public String[] getClassNames() {
        return new String[]{"net.minecraft.client.network.NetHandlerPlayClient"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode methodNode : classNode.methods) {
            String methodName = mapMethodName(classNode, methodNode);

            if (methodName.equals("handleResourcePack") || methodName.equals("func_175095_a")) {
                methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), cancelIfNotSafe());
                break;
            }

            break;
        }
    }

    private InsnList cancelIfNotSafe() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/network/play/server/S48PacketResourcePackSend",
                "func_179784_b", "()Ljava/lang/String;", false));
        list.add(new VarInsnNode(Opcodes.ALOAD, 1));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/network/play/server/S48PacketResourcePackSend",
                "func_179783_a", "()Ljava/lang/String;", false));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/ccbluex/liquidbounce/packexploitfix/hook/NetHandlerPlayClientHook",
                "validateResourcePackUrl", "(Lnet/minecraft/client/network/NetHandlerPlayClient;Ljava/lang/String;Ljava/lang/String;)Z", false));
        LabelNode labelNode = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFNE, labelNode));
        list.add(new InsnNode(Opcodes.RETURN));
        list.add(labelNode);
        return list;
    }
}
