/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.bugfixes;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.GuiIngameForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngameForge.class)
public class GuiIngameForgeMixin_HotbarAlpha {
    @Inject(method = "renderExperience", at = @At("HEAD"), remap = false)
    private void patcher$enableExperienceAlpha(int filled, int top, CallbackInfo ci) {
        GlStateManager.enableAlpha();
    }

    @Inject(method = "renderExperience", at = @At("RETURN"), remap = false)
    private void patcher$disableExperienceAlpha(int filled, int top, CallbackInfo ci) {
        GlStateManager.disableAlpha();
    }
}
