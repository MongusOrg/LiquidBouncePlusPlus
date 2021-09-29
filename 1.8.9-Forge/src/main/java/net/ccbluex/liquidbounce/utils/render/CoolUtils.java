package net.ccbluex.liquidbounce.utils.render;

import net.ccbluex.liquidbounce.utils.MinecraftInstance;
import net.minecraft.client.shader.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;

import org.lwjgl.opengl.GL11;

public class CoolUtils extends MinecraftInstance {

    private static ShaderGroup blurShader;
	private static Framebuffer buffer;
	private static int lastScale;
	private static int lastScaleWidth;
	private static int lastScaleHeight;

	private static final ResourceLocation shader = new ResourceLocation("liquidbounce+/mahiro.json");

    public static void initFboAndShader() {
		try {
			blurShader = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), shader);
			blurShader.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
			buffer = blurShader.mainFramebuffer;
				
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    private static void setShaderConfigs(float intensity) {
		blurShader.listShaders.get(0).getShaderManager().getShaderUniform("Radius").set(intensity);
		blurShader.listShaders.get(1).getShaderManager().getShaderUniform("Radius").set(intensity);
	}

    public void blurArea(float x, float y, float x2, float y2, float density) {
		setShaderConfigs(density);

		ScaledResolution scale = new ScaledResolution(mc);
		int factor = scale.getScaleFactor();
		int factor2 = scale.getScaledWidth();
		int factor3 = scale.getScaledHeight();

		if (lastScale != factor || lastScaleWidth != factor2 || lastScaleHeight != factor3 || buffer == null
				|| blurShader == null) {
			initFboAndShader();
		}

		lastScale = factor;
		lastScaleWidth = factor2;
		lastScaleHeight = factor3;

		if (OpenGlHelper.isFramebufferEnabled()) {
			buffer.framebufferClear();
			GL11.glScissor(x * factor, (mc.displayHeight - (y * factor) - height * factor), width * factor,
				height * factor);
			GL11.glEnable(GL11.GL_SCISSOR_TEST);
			buffer.bindFramebuffer(true);
			blurShader.loadShaderGroup(mc.timer.renderPartialTicks);

			mc.getFramebuffer().bindFramebuffer(true);

			GL11.glDisable(GL11.GL_SCISSOR_TEST);
			GL11.glScalef(factor, factor, 0);
		}
    }

}

