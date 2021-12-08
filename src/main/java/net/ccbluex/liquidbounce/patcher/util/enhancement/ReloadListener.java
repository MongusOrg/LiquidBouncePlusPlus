/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.patcher.util.enhancement;

import net.ccbluex.liquidbounce.patcher.hooks.font.FontRendererHook;
import net.ccbluex.liquidbounce.patcher.util.enhancement.text.EnhancedFontRenderer;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;

public class ReloadListener implements IResourceManagerReloadListener {
    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        for (EnhancedFontRenderer enhancedFontRenderer : EnhancedFontRenderer.getInstances()) {
            enhancedFontRenderer.invalidateAll();
        }

        FontRendererHook.forceRefresh = true;
    }
}