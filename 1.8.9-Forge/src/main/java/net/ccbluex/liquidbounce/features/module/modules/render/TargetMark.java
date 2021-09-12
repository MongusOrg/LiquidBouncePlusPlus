/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 * 
 * This code belongs to WYSI-Foundation. Please give credits when using this in your repository.
 */
package net.ccbluex.liquidbounce.features.module.modules.render;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.Render3DEvent;
import net.ccbluex.liquidbounce.features.module.modules.color.ColorMixer;
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.ui.font.GameFontRenderer;
import net.ccbluex.liquidbounce.utils.AnimationUtils;
import net.ccbluex.liquidbounce.utils.render.ColorUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.value.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;

import org.lwjgl.opengl.GL11;
import java.awt.Color;

@ModuleInfo(name = "TargetMark", spacedName = "Target Mark", description = "Display your KillAura's target in 3D.", category = ModuleCategory.RENDER)
public class TargetMark extends Module {

    public final ListValue modeValue = new ListValue("Mode", new String[]{"Default", "Box", "Jello"}, "Default");
    private final ListValue colorModeValue = new ListValue("Color", new String[] {"Custom", "Rainbow", "Sky", "LiquidSlowly", "Fade", "Mixer"}, "Custom");
	private final IntegerValue colorRedValue = new IntegerValue("Red", 255, 0, 255);
	private final IntegerValue colorGreenValue = new IntegerValue("Green", 255, 0, 255);
	private final IntegerValue colorBlueValue = new IntegerValue("Blue", 255, 0, 255);
	private final IntegerValue colorAlphaValue = new IntegerValue("Alpha", 255, 0, 255);
	private final FloatValue saturationValue = new FloatValue("Saturation", 1F, 0F, 1F);
	private final FloatValue brightnessValue = new FloatValue("Brightness", 1F, 0F, 1F);
	private final IntegerValue mixerSecondsValue = new IntegerValue("Seconds", 2, 1, 10);
   	private final BoolValue colorTeam = new BoolValue("Team", false);

	private EntityLivingBase entity;
	
	private double direction = 1,
				   yPos, progress = 0;
	
	private float al = 0;
	
	private AxisAlignedBB bb;

	private KillAura aura;

    @Override
    public void onInitialize() {
        aura = (KillAura) LiquidBounce.moduleManager.getModule(KillAura.class);
    }
	
	@EventTarget
	public void onRender3D(Render3DEvent event) {
        if (modeValue.get().equalsIgnoreCase("jello") && !aura.getTargetModeValue().get().equalsIgnoreCase("multi")) {
            al = AnimationUtils.changer(al, (aura.getTarget() != null ? 0.075F : -0.075F) * (1.25F - event.getPartialTicks()), 0F, .75F);

		    double lastY = yPos;

		    if (al > 0F) {
			    progress = AnimationUtils.changer(progress, 0.035F * (1.25F - event.getPartialTicks()) * direction, 0F, 1F);
    			if (progress == 0 || progress == 1) direction = -direction;
		    }

		    if (aura.getTarget() != null) {
			    entity = aura.getTarget();
    			bb = entity.getEntityBoundingBox();
		    }
		
		    if (bb == null || entity == null) return;
		
		    double radius = bb.maxX - bb.minX;
		    double height = bb.maxY - bb.minY;
		    double posX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks;
	        double posY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks;
	        double posZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks;

	        yPos = easeInOutQuart(progress) * height;

	        double deltaY = (direction > 0 ? yPos - lastY : lastY - yPos) * -direction * 5F;
    
	        if (al <= 0 && entity != null) {
                entity = null;
                return;
            }

			Color colour = getColor(entity);
            float r = colour.getRed() / 255.0F;
            float g = colour.getGreen() / 255.0F;
            float b = colour.getBlue() / 255.0F;

		    pre3D();
		    //post circles
		    GL11.glLineWidth(1F);
		    GL11.glShadeModel(7425);
		    GL11.glBegin(GL11.GL_LINE_LOOP);

		    for (float i = 0; i <= 360; i += 0.1F) {
			    double posX2 = posX - Math.sin(i * Math.PI / 180) * radius;
			    double posZ2 = posZ + Math.cos(i * Math.PI / 180) * radius;

			    if (direction > 0) {
				    GL11.glColor4f(r, g, b, 0);
				    GL11.glVertex3d(posX2 - mc.getRenderManager().viewerPosX, posY + yPos + deltaY - mc.getRenderManager().viewerPosY, posZ2 - mc.getRenderManager().viewerPosZ);
				    GL11.glColor4f(r, g, b, al * 0.35F);
				    GL11.glVertex3d(posX2 - mc.getRenderManager().viewerPosX, posY + yPos - mc.getRenderManager().viewerPosY, posZ2 - mc.getRenderManager().viewerPosZ);
				    GL11.glColor4f(r, g, b, 0);
    				GL11.glVertex3d(posX2 - mc.getRenderManager().viewerPosX, posY + yPos + deltaY - mc.getRenderManager().viewerPosY, posZ2 - mc.getRenderManager().viewerPosZ);
			    } else {
				    GL11.glColor4f(r, g, b, al * 0.35F);
				    GL11.glVertex3d(posX2 - mc.getRenderManager().viewerPosX, posY + yPos - mc.getRenderManager().viewerPosY, posZ2 - mc.getRenderManager().viewerPosZ);
				    GL11.glColor4f(r, g, b, 0);
				    GL11.glVertex3d(posX2 - mc.getRenderManager().viewerPosX, posY + yPos + deltaY - mc.getRenderManager().viewerPosY, posZ2 - mc.getRenderManager().viewerPosZ);
				    GL11.glColor4f(r, g, b, al * 0.35F);
				    GL11.glVertex3d(posX2 - mc.getRenderManager().viewerPosX, posY + yPos - mc.getRenderManager().viewerPosY, posZ2 - mc.getRenderManager().viewerPosZ);
    			}
		    }

		    GL11.glEnd();
		    GL11.glShadeModel(7424);

		    drawCircle(posX, posY + yPos, posZ, 2F, radius, r, g, b, al);

		    post3D();
        } else if (modeValue.get().equalsIgnoreCase("default")) {
            if (!aura.getTargetModeValue().get().equalsIgnoreCase("multi") && aura.getTarget() != null) RenderUtils.drawPlatform(aura.getTarget(), (aura.getHitable()) ? ColorUtils.reAlpha(getColor(entity), colorAlphaValue.get()) : new Color(255, 0, 0, colorAlphaValue.get()));
        } else {
            if (!aura.getTargetModeValue().get().equalsIgnoreCase("multi") && aura.getTarget() != null) RenderUtils.drawEntityBox(aura.getTarget(), (aura.getHitable()) ? ColorUtils.reAlpha(getColor(entity), colorAlphaValue.get()) : new Color(255, 0, 0, colorAlphaValue.get()), false);
        }
	}

	public final Color getColor(final Entity ent) {
		if (ent instanceof EntityLivingBase) {
			final EntityLivingBase entityLivingBase = (EntityLivingBase) ent;

			if (colorTeam.get()) {
				final char[] chars = entityLivingBase.getDisplayName().getFormattedText().toCharArray();
				int color = Integer.MAX_VALUE;

				for (int i = 0; i < chars.length; i++) {
					if (chars[i] != '§' || i + 1 >= chars.length)
						continue;

					final int index = GameFontRenderer.getColorIndex(chars[i + 1]);

					if (index < 0 || index > 15)
						continue;

					color = ColorUtils.hexColors[index];
					break;
				}

				return new Color(color);
			}
		}

		switch (colorModeValue.get()) {
			case "Custom":
				return new Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get());
			case "Rainbow":
			 	return new Color(RenderUtils.getRainbowOpaque(mixerSecondsValue.get(), saturationValue.get(), brightnessValue.get(), 0));
			case "Sky":
				return RenderUtils.skyRainbow(0, saturationValue.get(), brightnessValue.get());
			case "LiquidSlowly":
				return ColorUtils.LiquidSlowly(System.nanoTime(), 0, saturationValue.get(), brightnessValue.get());
			case "Mixer":
				return ColorMixer.getMixedColor(0, mixerSecondsValue.get());
			default:
				return ColorUtils.fade(new Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get()), 0, 100);
		}
	}

    public static void pre3D() {
       GL11.glPushMatrix();
       GL11.glEnable(GL11.GL_BLEND);
       GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
       GL11.glShadeModel(GL11.GL_SMOOTH);
       GL11.glDisable(GL11.GL_TEXTURE_2D);
       GL11.glEnable(GL11.GL_LINE_SMOOTH);
       GL11.glDisable(GL11.GL_DEPTH_TEST);
       GL11.glDisable(GL11.GL_LIGHTING);
       GL11.glDepthMask(false);
       GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
   }

   public static void post3D() {
       GL11.glDepthMask(true);
       GL11.glEnable(GL11.GL_DEPTH_TEST);
       GL11.glDisable(GL11.GL_LINE_SMOOTH);
       GL11.glEnable(GL11.GL_TEXTURE_2D);
       GL11.glDisable(GL11.GL_BLEND);
       GL11.glPopMatrix();
       GL11.glColor4f(1, 1, 1, 1);
   }
	
	private void drawCircle(double x, double y, double z, float width, double radius, float red, float green, float blue, float alp) {
		GL11.glLineWidth(width);
		GL11.glBegin(GL11.GL_LINE_LOOP);
		GL11.glColor4f(red, green, blue, alp);

		for (double i = 0; i <= 360; i += 0.1) {
			double posX = x - Math.sin(i * Math.PI / 180) * radius;
			double posZ = z + Math.cos(i * Math.PI / 180) * radius;
			GL11.glVertex3d(posX - mc.getRenderManager().viewerPosX, y - mc.getRenderManager().viewerPosY, posZ - mc.getRenderManager().viewerPosZ);
		}

		GL11.glEnd();
	}

	private double easeInOutQuart(double x) {
		return (x < 0.5) ? 8 * x * x * x * x : 1 - Math.pow(-2 * x + 2, 4) / 2;
	}

}