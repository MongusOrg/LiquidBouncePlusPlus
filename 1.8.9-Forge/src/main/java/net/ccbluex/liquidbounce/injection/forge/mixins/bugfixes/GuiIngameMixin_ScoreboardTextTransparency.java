/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.bugfixes;

import net.minecraft.client.gui.GuiIngame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(GuiIngame.class)
public class GuiIngameMixin_ScoreboardTextTransparency {
    @ModifyConstant(method = "renderScoreboard", constant = @Constant(intValue = 553648127))
    private int patcher$fixTextBlending(int original) {
        return -1;
    }
}
