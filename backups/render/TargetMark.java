package net.ccbluex.liquidbounce.features.module.modules.render;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.event.*;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.ccbluex.liquidbounce.utils.timer.MSTimer;
import net.ccbluex.liquidbounce.utils.render.AnimationUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;

@ModuleInfo(name = "TargetMark", description = "no sigma hatar allowed (recoded)", category = ModuleCategory.RENDER)
public class TargetMark extends Module { 

    private final IntegerValue red = new IntegerValue("Red", 255, 0, 255);
    private final IntegerValue green = new IntegerValue("Green", 255, 0, 255);
    private final IntegerValue blue = new IntegerValue("Blue", 255, 0, 255);
    
    private List<SmallCircle> smalls = new ArrayList<SmallCircle>();
    private EntityLivingBase entity;
    
    private double direction = 1,
                   yPos = 0,
                   yStep = 0;
    
    private float alpha = 0;
    
    private AxisAlignedBB bb;
    
    private MSTimer stayTimer = new MSTimer();
    
    @EventTarget
    public void onRender3D(Render3DEvent event) {
        int delta = RenderUtils.deltaTime;

        alpha = MathHelper.clamp_float(alpha + (((KillAura) LiquidBounce.moduleManager.getModule(KillAura.class)).getTarget() != null ? 1F : -1F) * (delta / 10.0F), 0F, 1F);
        if (((KillAura) LiquidBounce.moduleManager.getModule(KillAura.class)).getTarget() != null) {
            entity = ((KillAura) LiquidBounce.moduleManager.getModule(KillAura.class)).getTarget();
            bb = entity.getEntityBoundingBox();
        }
        
        if (bb == null || entity == null) return;
        
        double radius = bb.maxX - bb.minX;
        double height = bb.maxY - bb.minY;
        double posX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks;
        double posY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks;
        double posZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks;
        
        double lastY = yPos;
        
        /*if (alpha > 0F) if (yPos <= 0 || yPos >= height) {
            if (stayTimer.hasTimePassed(200L)) {
                direction = -direction;
            }
            yPos = AnimationUtils.changer(yPos, direction * 0.0025, 0, height);
        } else {
            yPos = AnimationUtils.changer(yPos, direction * 0.025, 0, height);
            stayTimer.reset();
        }*/

        if (alpha > 0F) if (yPos > 0 && yPos < height) {
            yPos = (double) AnimationUtils.easeOut((float)yStep, (float)height);
            yStep += direction * (delta / 8.0);

            stayTimer.reset();
        } else {
            yPos = (direction > 0 ? height : 0);
            yStep = (direction > 0 ? height : 0);

            if (stayTimer.hasTimePassed(100L)) {
                direction = -direction;
                yPos = (double) AnimationUtils.easeOut((float)yStep, (float)height);
                yStep += direction * (delta / 8.0);
            }
        }

        if (alpha > 0F) for (double xd = lastY; direction > 0 ? xd <= yPos : xd >= yPos; xd += 0.0005 * direction) {
            smalls.add(new SmallCircle(radius - 0.1, xd));
        }

        initRender(false);
        
        if (alpha > 0F) drawCircle(posX, posY + yPos, posZ, 1F, radius - 0.1, red.get() / 255F, green.get() / 255F, blue.get() / 255F, alpha);
        initSmallCircles(posX, posY, posZ);
            
        initRender(true);
    }
    
    private void initSmallCircles(double posX, double posY, double posZ) {
        List<SmallCircle> deleteQueue = new ArrayList<SmallCircle>();
        
        GL11.glLineWidth(1F);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (SmallCircle c : smalls) {
            if (c.apha <= 0F) {
                deleteQueue.add(c);
            } else {
                c.draw(posX, posY, posZ, red.get() / 255F, green.get() / 255F, blue.get() / 255F, alpha);
            }
        }
        GL11.glEnd();
        
        for (SmallCircle e : deleteQueue) {
            smalls.remove(e);
        }
    }
    
    private void initRender(boolean end) {
        if (end) {
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glColor4f(1F, 1F, 1F, 1F);
            GL11.glPopMatrix();
        } else {
            mc.entityRenderer.disableLightmap();
            GL11.glPushMatrix();
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
        }
    }
    
    private void drawCircle(double x, double y, double z, float width, double radius, float red, float green, float blue, float alpha) {
        GL11.glLineWidth(width);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        GL11.glColor4f(red, green, blue, alpha);
        
        for (int i = 0; i <= 360; i += 9) {
            double posX = x - Math.sin(i * Math.PI / 180) * radius;
            double posZ = z + Math.cos(i * Math.PI / 180) * radius;
            GL11.glVertex3d(posX - mc.getRenderManager().viewerPosX, y - mc.getRenderManager().viewerPosY, posZ - mc.getRenderManager().viewerPosZ);
        }
        
        GL11.glEnd();
    }

    class SmallCircle {
        
        public final double rad, height;
        public float apha;
        
        public SmallCircle(double radius, double height) {
            this.rad = radius;
            this.height = height;
            this.apha = 0.8F;
        }
        
        public void draw(double xPos, double yPos, double zPos, float red, float green, float blue, float al) {
            GL11.glColor4f(red, green, blue, apha * al);
            for (int i = 0; i <= 360; i += 9) {
                double posX = xPos - Math.sin(i * Math.PI / 180) * rad;
                double posZ = zPos + Math.cos(i * Math.PI / 180) * rad;
                GL11.glVertex3d(posX - mc.getRenderManager().viewerPosX, yPos + height - mc.getRenderManager().viewerPosY, posZ - mc.getRenderManager().viewerPosZ);
            }
            //if (alpha <= 0) return;
            apha -= 0.5F;
        }
        
    }
    
}

