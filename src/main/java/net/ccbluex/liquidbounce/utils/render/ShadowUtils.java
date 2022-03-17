package net.ccbluex.liquidbounce.utils.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.ResourceLocation;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
//import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_SRGB; i love apple

import java.io.IOException;

public class ShadowUtils {

    private static Framebuffer initialFB, frameBuffer, blackBuffer;
    private static ShaderGroup mainShader = null;
    private static float lastWidth = 0, lastHeight = 0, lastStrength = 0;
    private static float lastX = 0, lastY = 0, lastW = 0, lastH = 0;
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final ResourceLocation blurDirectory = new ResourceLocation("liquidbounce+/shadow.json");

    public static void initBlur(final ScaledResolution sc) throws IOException {
        int w = sc.getScaledWidth();
        int h = sc.getScaledHeight();
        int f = sc.getScaleFactor();
        if (lastWidth != w || lastHeight != h || initialFB == null || frameBuffer == null || mainShader == null) {
            initialFB = new Framebuffer(w * f, h * f, true);
            initialFB.setFramebufferColor(0, 0, 0, 0);
            initialFB.setFramebufferFilter(GL_LINEAR);
            mainShader = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), initialFB, blurDirectory);
            mainShader.createBindFramebuffers(w * f , h * f);
            frameBuffer = mainShader.mainFramebuffer;
            blackBuffer = mainShader.getFramebufferRaw("braindead");

            
        }
        lastWidth = w;
        lastHeight = h;
    }

    public static void setShadowStrength(float strength) {
        if (strength != lastStrength) {
            lastStrength = strength;
            for (int i = 0; i < 2; i++) {
                mainShader.listShaders.get(i).getShaderManager().getShaderUniform("Radius").set(strength);
            }
        }
    }
    
    public static void setShadowPosition(float x, float y, float x2, float y2) {
        if (x > x2) {
            float z = x;
            x = x2;
            x2 = z;
        }
        if (y > y2) {
            float z = y;
            y = y2;
            y2 = z;
        }
        if (x != lastX || y != lastY || x2 != lastW || y2 != lastH) {
            lastX = x;
            lastY = y;
            lastW = x2;
            lastH = y2;
            for (int i = 0; i < 2; i++) {
                mainShader.listShaders.get(i).getShaderManager().getShaderUniform("ShadowCoord").set(x, y);
                mainShader.listShaders.get(i).getShaderManager().getShaderUniform("ShadowWH").set(x2, y2);
            }
        }
    }

    public static void processShadow(boolean begin, float strength, float x, float y, float x2, float y2) throws IOException {
        if (!OpenGlHelper.isFramebufferEnabled())
            return;

        final ScaledResolution sc = new ScaledResolution(mc);
        initBlur(sc);

        setShadowStrength(strength);
        setShadowPosition(x, y, x2, y2);

        if (begin) {
            mc.getFramebuffer().unbindFramebuffer();
            initialFB.framebufferClear();
            blackBuffer.framebufferClear();
            initialFB.bindFramebuffer(true);
            //glEnable(GL_FRAMEBUFFER_SRGB);
            //GlStateManager.blendFunc(770, 771);
        } else {
            frameBuffer.bindFramebuffer(true);
            mainShader.loadShaderGroup(mc.timer.renderPartialTicks);
            mc.getFramebuffer().bindFramebuffer(true);
            //glDisable(GL_FRAMEBUFFER_SRGB);
            // Variables
            float f = (float)sc.getScaledWidth();
            float f1 = (float)sc.getScaledHeight();
            float f2 = (float)blackBuffer.framebufferWidth / (float)blackBuffer.framebufferTextureWidth;
            float f3 = (float)blackBuffer.framebufferHeight / (float)blackBuffer.framebufferTextureHeight;

            // Enable/Disable required things
            GlStateManager.pushMatrix();
            GlStateManager.disableLighting();
            GlStateManager.disableAlpha();
            GlStateManager.enableTexture2D();
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
            GlStateManager.colorMask(true, true, true, true);

            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            //glEnable(GL_ALPHA_TEST);
            //glAlphaFunc(GL_GREATER, 0.2F);

            blackBuffer.bindFramebufferTexture();
            glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            worldrenderer.pos(0.0D, f1, 0.0D).tex(0.0D, 0.0D).color(255, 255, 255, 255).endVertex();
            worldrenderer.pos(f, f1, 0.0D).tex((double)f2, 0.0D).color(255, 255, 255, 255).endVertex();
            worldrenderer.pos(f, 0.0D, 0.0D).tex((double)f2, f3).color(255, 255, 255, 255).endVertex();
            worldrenderer.pos(0.0D, 0.0D, 0.0D).tex(0.0D, f3).color(255, 255, 255, 255).endVertex();
            tessellator.draw();
            blackBuffer.unbindFramebufferTexture();

            //glDisable(GL_ALPHA_TEST);

            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture2D();
            GlStateManager.popMatrix();

            GlStateManager.resetColor();
            GlStateManager.color(1F, 1F, 1F, 1F);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
        }
    }

}
