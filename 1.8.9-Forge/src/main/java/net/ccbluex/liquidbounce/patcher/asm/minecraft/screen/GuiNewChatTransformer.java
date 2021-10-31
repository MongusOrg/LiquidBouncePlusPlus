/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.patcher.asm.minecraft.screen;

import net.ccbluex.liquidbounce.patcher.tweaker.transform.PatcherTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Iterator;
import java.util.ListIterator;

public class GuiNewChatTransformer extends PatcherTransformer {
    /**
     * The class name that's being transformed
     *
     * @return the class name
     */
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraft.client.gui.GuiNewChat"};
    }

    /**
     * Perform any asm in order to transform code
     *
     * @param classNode the transformed class node
     * @param name      the transformed class name
     */
    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode methodNode : classNode.methods) {
            String methodName = mapMethodName(classNode, methodNode);

            switch (methodName) {
                case "getChatComponent":
                case "func_146236_a": {
                    this.changeChatComponentHeight(methodNode);
                    break;
                }
            }
        }
    }

    default void changeChatComponentHeight(MethodNode methodNode) {
        Iterator<AbstractInsnNode> iterator = methodNode.instructions.iterator();
        while (iterator.hasNext()) {
            AbstractInsnNode node = iterator.next();
            if (node instanceof MethodInsnNode && node.getOpcode() == Opcodes.INVOKESTATIC) {
                String methodInsnName = mapMethodNameFromNode(node);

                if (methodInsnName.equals("floor_float") || methodInsnName.equals("func_76141_d")) {
                    for (int i = 0; i < 4; ++i) {
                        node = node.getPrevious();
                    }

                    methodNode.instructions.insertBefore(node, minus12());
                    break;
                }
            }
        }
    }

    default InsnList minus12() {
        InsnList list = new InsnList();
        LabelNode afterSub = new LabelNode();
        list.add(getPatcherSetting("chatPosition", "Z"));
        list.add(new JumpInsnNode(Opcodes.IFEQ, afterSub));
        list.add(new IincInsnNode(7, -12));
        list.add(afterSub);
        return list;
    }
}
