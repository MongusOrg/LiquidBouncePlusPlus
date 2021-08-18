package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.modules.color.ColorMixer
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils.render.ColorUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.AxisAlignedBB
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.cos
import kotlin.math.sin

@ModuleInfo(name = "TargetStrafe", description = "Strafe around your target.", category = ModuleCategory.MOVEMENT)
class TargetStrafe : Module() {
    public val radius = FloatValue("Radius", 2.0f, 0.1f, 4.0f)
    private val render = BoolValue("Render", true)
    private val modeValue = ListValue("KeyMode", arrayOf("Jump", "None"), "None")
    private val radiusMode = ListValue("RadiusMode", arrayOf("TrueRadius", "Simple"), "Simple")
    private val safewalk = BoolValue("SafeWalk", true)
    private val thirdPerson = BoolValue("ThirdPerson", true)
    private val colorType = ListValue("Color", arrayOf("Custom", "Rainbow", "Rainbow2", "Sky", "Fade", "Mixer"), "Custom")
    private val redValue = IntegerValue("Red", 255, 0, 255)
    private val greenValue = IntegerValue("Green", 255, 0, 255)
    private val blueValue = IntegerValue("Blue", 255, 0, 255)
    private val saturationValue = FloatValue("Saturation", 0.7F, 0F, 1F)
    private val brightnessValue = FloatValue("Brightness", 1F, 0F, 1F)
    private val mixerSecondsValue = IntegerValue("Mixer-Seconds", 2, 1, 10)
    private val accuracyValue = IntegerValue("Accuracy", 0, 0, 59)
    private val thicknessValue = FloatValue("Thickness", 1F, 0.1F, 5F)
    private val outLine = BoolValue("Outline", true)
    private lateinit var killAura: KillAura
    private lateinit var speed: Speed
    private lateinit var fly: Fly

    var consts = 0
    var lastDist = 0.0

    override fun onInitialize() {
        killAura=LiquidBounce.moduleManager.getModule(KillAura::class.java) as KillAura
        speed=LiquidBounce.moduleManager.getModule(Speed::class.java) as Speed
        fly=LiquidBounce.moduleManager.getModule(Fly::class.java) as Fly
    }

    fun onMotion(event: MotionEvent) {
        if (thirdPerson.get()) {
            mc.gameSettings.thirdPersonView = if (canStrafe) 2 else 0
        }
    }

    fun onMove(event: MoveEvent) {
        val xDist = event.x
        val zDist = event.z
        lastDist = Math.sqrt(xDist * xDist + zDist * zDist)
    }

    @EventTarget
    fun moveStrafe(event: MoveEvent) {
        onMove(event)

        if (!isVoid(0, 0) && canStrafe) {
            val strafe = RotationUtils.getRotations(killAura.target)
            setSpeed(event, lastDist, strafe.yaw, radius.get(), 1.0)
        }
    }

    val keyMode: Boolean
        get() = when (modeValue.get().toLowerCase()) {
            "jump" -> mc.gameSettings.keyBindJump.isKeyDown
            "none" -> mc.thePlayer.movementInput.moveStrafe != 0f || mc.thePlayer.movementInput.moveForward != 0f
            else -> false
        }

    val canStrafe: Boolean
        get() = (killAura.state && (speed.state || fly.state) && killAura.target != null && !mc.thePlayer.isSneaking
                && keyMode)

    val cansize: Float
        get() = when {
            radiusMode.get().toLowerCase() == "simple" ->
                45f / mc.thePlayer.getDistance(killAura.target!!.posX, mc.thePlayer.posY, killAura.target!!.posZ).toFloat()
            else -> 45f
        }
    val Enemydistance: Double
        get() = mc.thePlayer.getDistance(killAura.target!!.posX, mc.thePlayer.posY, killAura.target!!.posZ)

    val algorithm: Float
        get() = Math.max(Enemydistance - radius.get(), Enemydistance - (Enemydistance - radius.get() / (radius.get() * 2))).toFloat()



    fun setSpeed(
        moveEvent: MoveEvent, moveSpeed: Double, pseudoYaw: Float, pseudoStrafe: Float,
        pseudoForward: Double
    ) {
        var yaw = pseudoYaw
        var forward = pseudoForward
        var strafe = pseudoStrafe
        var strafe2 = 0f

        check()

        when {
            modeValue.get().toLowerCase().equals("jump") ->
                strafe = (pseudoStrafe * 0.98 * consts).toFloat()
            modeValue.get().toLowerCase().equals("none") ->
                strafe = consts.toFloat()
        }

        if (forward != 0.0) {
            if (strafe > 0.0) {
                if (radiusMode.get().toLowerCase() == "trueradius")
                    yaw += (if (forward > 0.0) -cansize else cansize)
                strafe2 += (if (forward > 0.0) -45 / algorithm else 45 / algorithm)
            } else if (strafe < 0.0) {
                if (radiusMode.get().toLowerCase() == "trueradius")
                    yaw += (if (forward > 0.0) cansize else -cansize)
                strafe2 += (if (forward > 0.0) 45 / algorithm else -45 / algorithm)
            }
            strafe = 0.0f
            if (forward > 0.0)
                forward = 1.0
            else if (forward < 0.0)
                forward = -1.0

        }
        if (strafe > 0.0)
            strafe = 1.0f
        else if (strafe < 0.0)
            strafe = -1.0f


        val mx = Math.cos(Math.toRadians(yaw + 90.0 + strafe2))
        val mz = Math.sin(Math.toRadians(yaw + 90.0 + strafe2))
        moveEvent.x = forward * moveSpeed * mx + strafe * moveSpeed * mz
        moveEvent.z = forward * moveSpeed * mz - strafe * moveSpeed * mx
    }

    private fun check() {
        if (mc.thePlayer.isCollidedHorizontally || checkVoid()) {
            if (consts < 2) consts += 1
            else {
                consts = -1
            }
        }
        when (consts) {
            0 -> {
                consts = 1
            }
            2 -> {
                consts = -1
            }
        }
    }

    private fun checkVoid(): Boolean {
        for (x in -1..0) {
            for (z in -1..0) {
                if (isVoid(x, z)) {
                    return true
                }
            }
        }
        return false
    }

    private fun isVoid(X: Int, Z: Int): Boolean {
        val fly = LiquidBounce.moduleManager.getModule(Fly::class.java) as Fly
        if (fly.state) {
            return false
        }
        if (mc.thePlayer.posY < 0.0) {
            return true
        }
        var off = 0
        while (off < mc.thePlayer.posY.toInt() + 2) {
            val bb: AxisAlignedBB = mc.thePlayer.entityBoundingBox.offset(X.toDouble(), (-off).toDouble(), Z.toDouble())
            if (mc.theWorld!!.getCollidingBoundingBoxes(mc.thePlayer as Entity, bb).isEmpty()) {
                off += 2
                continue
            }
            return false
            off += 2
        }
        return true
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        val target = killAura.target
        if (canStrafe && render.get()) {
            target?:return
            GL11.glPushMatrix()
            GL11.glTranslated(
                target.lastTickPosX + (target.posX - target.lastTickPosX) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosX,
                target.lastTickPosY + (target.posY - target.lastTickPosY) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosY,
                target.lastTickPosZ + (target.posZ - target.lastTickPosZ) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosZ
            )
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glDisable(GL11.GL_DEPTH_TEST)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            GL11.glRotatef(90F, 1F, 0F, 0F)

            if (outLine.get()) {
                GL11.glLineWidth(thicknessValue.get() + 1.25F)
                GL11.glColor3f(0F, 0F, 0F)
                GL11.glBegin(GL11.GL_LINE_LOOP)

                for (i in 0..360 step 60 - accuracyValue.get()) { // You can change circle accuracy  (60 - accuracy)
                    GL11.glVertex2f(Math.cos(i * Math.PI / 180.0).toFloat() * radius.get(), (Math.sin(i * Math.PI / 180.0).toFloat() * radius.get()))
                }

                GL11.glEnd()
            }

            val rainbow2 = ColorUtils.LiquidSlowly(System.nanoTime(), 0, saturationValue.get(), brightnessValue.get())
            val sky = RenderUtils.skyRainbow(0, saturationValue.get(), brightnessValue.get())
            val mixer = ColorMixer.getMixedColor(0, mixerSecondsValue.get())
            val fade = ColorUtils.fade(Color(redValue.get(), greenValue.get(), blueValue.get()), 0, 100)

            GL11.glLineWidth(thicknessValue.get())
            GL11.glBegin(GL11.GL_LINE_LOOP)

            for (i in 0..360 step 60 - accuracyValue.get()) { // You can change circle accuracy  (60 - accuracy)
                when (colorType.get()) {
                    "Custom" -> GL11.glColor3f(redValue.get() / 255.0f, greenValue.get() / 255.0f, blueValue.get() / 255.0f)
                    "Rainbow" -> {
                        val rainbow = Color(Color.HSBtoRGB((mc.thePlayer.ticksExisted / 70.0 + sin(i / 50.0 * 1.75)).toFloat() % 1.0f, 0.7f, 1.0f))
                        GL11.glColor3f(rainbow.red / 255.0f, rainbow.green / 255.0f, rainbow.blue / 255.0f)
                    }
                    "Rainbow2" -> GL11.glColor3f(rainbow2!!.red / 255.0f, rainbow2!!.green / 255.0f, rainbow2!!.blue / 255.0f)
                    "Sky" -> GL11.glColor3f(sky.red / 255.0f, sky.green / 255.0f, sky.blue / 255.0f)
                    "Mixer" -> GL11.glColor3f(mixer.red / 255.0f, mixer.green / 255.0f, mixer.blue / 255.0f)
                    else -> GL11.glColor3f(fade.red / 255.0f, fade.green / 255.0f, fade.blue / 255.0f)
                }
                GL11.glVertex2f(Math.cos(i * Math.PI / 180.0).toFloat() * radius.get(), (Math.sin(i * Math.PI / 180.0).toFloat() * radius.get()))
            }

            GL11.glEnd()

            GL11.glDisable(GL11.GL_BLEND)
            GL11.glEnable(GL11.GL_TEXTURE_2D)
            GL11.glEnable(GL11.GL_DEPTH_TEST)
            GL11.glDisable(GL11.GL_LINE_SMOOTH)

            GL11.glPopMatrix()

            GlStateManager.resetColor()
            GL11.glColor4f(1F, 1F, 1F, 1F)
        }
    }
}