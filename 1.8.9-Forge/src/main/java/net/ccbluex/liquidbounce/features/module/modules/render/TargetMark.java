/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.render;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.Render3DEvent;
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.utils.AnimationUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.value.*;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;

import org.lwjgl.opengl.GL11;
import java.awt.Color;

@ModuleInfo(name = "TargetMark", description = "Display your KillAura's target in 3D. (lol)", category = ModuleCategory.RENDER)
public class TargetMark extends Module {

    public final ListValue modeValue = new ListValue("Mode", new String[]{"Default", "Box", "Jello"}, "Normal");
    public final IntegerValue red = new IntegerValue("Red", 255, 0, 255);
    public final IntegerValue green = new IntegerValue("Green", 255, 0, 255);
    public final IntegerValue blue = new IntegerValue("Blue", 255, 0, 255);
    public final IntegerValue alpha = new IntegerValue("Alpha", 255, 0, 255);

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
            al = AnimationUtils.changer(al, (aura.getTarget() != null ? 0.075F : -0.075F) * (1.25F - event.getPartialTicks()), 0F, .65F);

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

            float r = red.get() / 255.0F;
            float g = green.get() / 255.0F;
            float b = blue.get() / 255.0F;

		    pre3D();
		    //post circles
		    GL11.glLineWidth(1F);
		    GL11.glShadeModel(7425);
		    GL11.glBegin(GL11.GL_LINE_LOOP);

		    for (float i = 0; i <= 360; i += 0.1F) {
			    double posX2 = posX - Math.sin(i * Math.PI / 180) * (radius - 0.2F);
			    double posZ2 = posZ + Math.cos(i * Math.PI / 180) * (radius - 0.2F);

			    if (direction > 0) {
				    GL11.glColor4f(r, g, b, 0);
				    GL11.glVertex3d(posX2 - mc.getRenderManager().viewerPosX, posY + yPos + deltaY - mc.getRenderManager().viewerPosY, posZ2 - mc.getRenderManager().viewerPosZ);
				    GL11.glColor4f(r, g, b, al * 0.25F);
				    GL11.glVertex3d(posX2 - mc.getRenderManager().viewerPosX, posY + yPos - mc.getRenderManager().viewerPosY, posZ2 - mc.getRenderManager().viewerPosZ);
				    GL11.glColor4f(r, g, b, 0);
    				GL11.glVertex3d(posX2 - mc.getRenderManager().viewerPosX, posY + yPos + deltaY - mc.getRenderManager().viewerPosY, posZ2 - mc.getRenderManager().viewerPosZ);
			    } else {
				    GL11.glColor4f(r, g, b, al * 0.25F);
				    GL11.glVertex3d(posX2 - mc.getRenderManager().viewerPosX, posY + yPos - mc.getRenderManager().viewerPosY, posZ2 - mc.getRenderManager().viewerPosZ);
				    GL11.glColor4f(r, g, b, 0);
				    GL11.glVertex3d(posX2 - mc.getRenderManager().viewerPosX, posY + yPos + deltaY - mc.getRenderManager().viewerPosY, posZ2 - mc.getRenderManager().viewerPosZ);
				    GL11.glColor4f(r, g, b, al * 0.25F);
				    GL11.glVertex3d(posX2 - mc.getRenderManager().viewerPosX, posY + yPos - mc.getRenderManager().viewerPosY, posZ2 - mc.getRenderManager().viewerPosZ);
    			}
		    }

		    GL11.glEnd();
		    GL11.glShadeModel(7424);

		    drawCircle(posX, posY + yPos, posZ, 2.25F, radius - 0.2F, r, g, b, al);

		    post3D();
        } else if (modeValue.get().equalsIgnoreCase("default")) {
            if (!aura.getTargetModeValue().get().equalsIgnoreCase("multi") && aura.getTarget() != null) RenderUtils.drawPlatform(aura.getTarget(), (aura.getHitable()) ? new Color(red.get(), green.get(), blue.get(), alpha.get()) : new Color(255, 0, 0, alpha.get()));
        } else {
            if (!aura.getTargetModeValue().get().equalsIgnoreCase("multi") && aura.getTarget() != null) RenderUtils.drawEntityBox(aura.getTarget(), (aura.getHitable()) ? new Color(red.get(), green.get(), blue.get(), alpha.get()) : new Color(255, 0, 0, alpha.get()), false);
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

		for (double i = 0; i <= 360; i += 1) {
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