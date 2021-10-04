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

    private static int lastFactor;
    private static int lastWidth;
    private static int lastHeight;

	private static float lastStrength = 5F;

	private static ResourceLocation blurShader = new ResourceLocation("liquidbounce+/mahiro.json");

    public static void init() {
        try {
            shaderGroup = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), blurShader);
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

        framebuffer.framebufferClear();

        framebuffer.bindFramebuffer(true);
        shaderGroup.loadShaderGroup(mc.timer.renderPartialTicks);

        mc.getFramebuffer().bindFramebuffer(true);

        RenderUtils.makeScissorBox(x, y, x2, y2);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GlStateManager.enableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableAlpha();

        float f2 = (float)framebuffer.framebufferWidth / (float)framebuffer.framebufferTextureWidth;
        float f3 = (float)framebuffer.framebufferHeight / (float)framebuffer.framebufferTextureHeight;

        GL11.glColor4f(1, 1, 1, 1);
        mc.getFramebuffer().bindFramebufferTexture();
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex2f(0, height);
        GL11.glTexCoord2f(f2, 0);
        GL11.glVertex2f(width, height);
        GL11.glTexCoord2f(f2, f3);
        GL11.glVertex2f(width, 0);
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex2f(0, height);
        GL11.glEnd();
        mc.getFramebuffer().unbindFramebufferTexture();
        GL11.glDisable(GL11.GL_SCISSOR_TEST);


        mc.getFramebuffer().unbindFramebuffer();

        GlStateManager.enableAlpha();
    }

    private static boolean sizeHasChanged(int scaleFactor, int width, int height) {
        return (lastFactor != scaleFactor || lastWidth != width || lastHeight != height);
    }
}

