package net.ccbluex.liquidbounce.utils.render;

import com.google.gson.JsonSyntaxException;
import net.ccbluex.liquidbounce.utils.MinecraftInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderUniform;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import java.io.IOException;

public class CoolUtils extends MinecraftInstance {

    private static ShaderGroup shaderGroup;
    private static Framebuffer framebuffer;

    private static Framebuffer frbuffer;

    private static int lastFactor;
    private static int lastWidth;
    private static int lastHeight;

	private static float lastStrength = 5F;

	private static ResourceLocation blurShader = new ResourceLocation("liquidbounce+/mahiro.json");

    public static void init() {
        try {
            frbuffer = new Framebuffer(mc.displayWidth / 4F, mc.displayHeight / 4F, true);
            shaderGroup = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), frbuffer, blurShader);
            shaderGroup.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
            framebuffer = shaderGroup.mainFramebuffer;
        } catch (JsonSyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void setValues(float strength) {
		if (strength == lastStrength) return;
		lastStrength = strength;
        for (int i = 0; i < 4; i++) {
            shaderGroup.listShaders.get(i).getShaderManager().getShaderUniform("Radius").set(strength);
        }
    }

    public static void blurArea(float x, float y, float x2, float y2, float blurStrength) {
		ScaledResolution scaledResolution = new ScaledResolution(mc);
        final int scaleFactor = scaledResolution.getScaleFactor();
        final int width = scaledResolution.getScaledWidth();
        final int height = scaledResolution.getScaledHeight();

        if (sizeHasChanged(scaleFactor, width, height) || framebuffer == null || shaderGroup == null) {
            init();
        }

        lastFactor = scaleFactor;
        lastWidth = width;
        lastHeight = height;

        setValues(blurStrength);

        frbuffer.bindFramebuffer(false);
        mc.getFramebuffer().framebufferRenderExt(width, height, false);
        frbuffer.unbindFramebuffer();

        framebuffer.framebufferClear();

        framebuffer.bindFramebuffer(true);
        shaderGroup.loadShaderGroup(mc.timer.renderPartialTicks);

        frbuffer.bindFramebuffer(true);

        /*Stencil.write(false);
        RenderUtils.quickDrawRect(x, y, x2, y2);
        Stencil.erase(true);
        GL11.glColor4f(1, 1, 1, 1);
        frbuffer.framebufferRenderExt(width, height, false);
        Stencil.dispose();*/

        GlStateManager.enableAlpha();
    }

    private static boolean sizeHasChanged(int scaleFactor, int width, int height) {
        return (lastFactor != scaleFactor || lastWidth != width || lastHeight != height);
    }
}

