/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.patcher.asm.optifine;

import net.ccbluex.liquidbounce.patcher.tweaker.transform.PatcherTransformer;
import net.ccbluex.liquidbounce.patcher.tweaker.ClassTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class OptiFineHookTransformer implements PatcherTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.ccbluex.liquidbounce.patcher.hooks.font.OptiFineHook"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        if (ClassTransformer.smoothFontDetected) {
            return;
        }

        for (MethodNode method : classNode.methods) {
            final String methodName = method.name;
            if (methodName.equals("getOptifineBoldOffset")) {
                method.instructions.clear();
                final InsnList insns = new InsnList();
                insns.add(new VarInsnNode(Opcodes.ALOAD, 1));
                insns.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/gui/FontRenderer", "offsetBold", "F"));
                insns.add(new InsnNode(Opcodes.FRETURN));
                method.instructions.add(insns);
            } else if (methodName.equals("getCharWidth")) {
                method.instructions.clear();
                final InsnList insns = new InsnList();
                insns.add(new VarInsnNode(Opcodes.ALOAD, 1));
                insns.add(new VarInsnNode(Opcodes.ILOAD, 2));
                insns.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/client/gui/FontRenderer", "getCharWidthFloat", "(C)F", false));
                insns.add(new InsnNode(Opcodes.FRETURN));
                method.instructions.add(insns);
            }
        }
    }
}