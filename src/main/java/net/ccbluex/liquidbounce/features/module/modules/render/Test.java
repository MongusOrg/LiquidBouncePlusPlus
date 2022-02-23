/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 * 
 * This code belongs to WYSI-Foundation. Please give credits when using this in your repository.
 */
package net.ccbluex.liquidbounce.features.module.modules.render;

import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.Render2DEvent;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import static net.ccbluex.liquidbounce.utils.render.shader.shaders.GlowShader.GLOW_SHADER;

import java.awt.Color;

@ModuleInfo(name = "Test", description = "ok.", category = ModuleCategory.RENDER)
public class Test extends Module {

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        // testing only
        GLOW_SHADER.startDraw(event.getPartialTicks());
        ESP.renderNameTags = false;
        RenderUtils.drawRect(100, 100, 300, 200, 0xA0000000);
        GLOW_SHADER.stopDraw(Color.black, 2F, 1F);
        ESP.renderNameTags = true;
    }

}