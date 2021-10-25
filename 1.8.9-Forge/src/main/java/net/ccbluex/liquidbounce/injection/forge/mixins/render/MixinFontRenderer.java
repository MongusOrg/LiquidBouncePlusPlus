/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.render;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.TextEvent;
import net.ccbluex.liquidbounce.features.module.modules.misc.Patcher;
import net.ccbluex.liquidbounce.patcher.ducks.FontRendererExt;
import net.ccbluex.liquidbounce.patcher.hooks.font.FontRendererHook;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FontRenderer.class)
@SideOnly(Side.CLIENT)
public class MixinFontRenderer implements FontRendererExt {

    @Unique
    private final FontRendererHook patcher$fontRendererHook = new FontRendererHook((FontRenderer) (Object) this);

    /**
     * @author asbyth
     * @reason Use a string width cache
     */
    @Overwrite
    public int getStringWidth(String text) {
        if (text == null || LiquidBounce.eventManager == null)
            return this.patcher$fontRendererHook.getStringWidth(text);

        final TextEvent textEvent = new TextEvent(text);
        LiquidBounce.eventManager.callEvent(textEvent);

        return this.patcher$fontRendererHook.getStringWidth(textEvent.getText());
    }

    @Inject(method = "renderStringAtPos", at = @At("HEAD"), cancellable = true)
    private void patcher$useOptimizedRendering(String text, boolean shadow, CallbackInfo ci) {
        /*if (string != null && LiquidBounce.eventManager != null && Patcher.getPatcherSetting(0)) {
            final TextEvent textEvent = new TextEvent(text);
            LiquidBounce.eventManager.callEvent(textEvent);

            text = textEvent.getText();
        }*/ // this is mostly useless since the renderStringAtPos method is only be used inside FontRenderer class under renderString method.

        if (this.patcher$fontRendererHook.renderStringAtPos(text, shadow)) {
            ci.cancel();
        }
    }
    
    @ModifyVariable(method = "renderString", at = @At("HEAD"), ordinal = 0)
    private String renderString(final String string) {
        if (string == null || LiquidBounce.eventManager == null)
            return string;

        final TextEvent textEvent = new TextEvent(string);
        LiquidBounce.eventManager.callEvent(textEvent);
        return textEvent.getText();
    }

    @Override
    public FontRendererHook patcher$getFontRendererHook() {
        return patcher$fontRendererHook;
    }

}
