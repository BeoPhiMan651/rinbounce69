
package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.features.module.Category
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.modules.render.Animations.animations
import net.ccbluex.liquidbounce.features.module.modules.render.Animations.defaultAnimation
import net.ccbluex.liquidbounce.features.module.modules.render.Animations.delay
import net.ccbluex.liquidbounce.features.module.modules.render.Animations.itemRotate
import net.ccbluex.liquidbounce.features.module.modules.render.Animations.itemRotateSpeed
import net.ccbluex.liquidbounce.features.module.modules.render.Animations.itemRotationMode
import net.ccbluex.liquidbounce.utils.client.MinecraftInstance
import net.ccbluex.liquidbounce.utils.timing.MSTimer
import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.client.renderer.GlStateManager.*
import net.minecraft.util.MathHelper
import org.lwjgl.opengl.GL11.glTranslated
import org.lwjgl.opengl.GL11.glTranslatef

/**
 * Animations module
 *
 * This module affects the blocking animation. It allows the user to customize the animation.
 * If you are looking forward to contribute to this module, please name your animation with a reasonable name. Do not name them after clients or yourself.
 * Please credit from where you got the animation from and make sure they are willing to contribute.
 * If they are not willing to contribute, please do not add the animation to this module.
 *
 * If you are looking for the animation classes, please look at the [Animation] class. It allows you to create your own animation.
 * After making your animation class, please add it to the [animations] array. It should automatically be added to the list and show up in the GUI.
 *
 * By default, the module uses the [OneSevenAnimation] animation. If you want to change the default animation, please change the [defaultAnimation] variable.
 * Default animations are even used when the module is disabled.
 *
 * If another variables from the renderItemInFirstPerson method are needed, please let me know or pass them by yourself.
 *
 * @author CCBlueX
 */
object Animations : Module("Animations", Category.RENDER, gameDetecting = false) {

    // Default animation
    val defaultAnimation = OneSevenAnimation()

    private val animations = arrayOf(
        OneSevenAnimation(),
        OldPushdownAnimation(),
        NewPushdownAnimation(),
        OldAnimation(),
        HeliumAnimation(),
        ArgonAnimation(),
        CesiumAnimation(),
        SulfurAnimation(),
        SmoothFloatAnimation(),
        ReverseAnimation(),
        FluxAnimation(),
        ETBAnimation()
    )

    private val animationMode by choices("Mode", animations.map { it.name }.toTypedArray(), "Pushdown")
    val oddSwing by boolean("OddSwing", false)
    val swingSpeed by int("SwingSpeed", 15, 0..20)

    val handItemScale by float("ItemScale", 0f, -5f..5f)
    val handX by float("X", 0f, -5f..5f)
    val handY by float("Y", 0f, -5f..5f)
    val handPosX by float("PositionRotationX", 0f, -50f..50f)
    val handPosY by float("PositionRotationY", 0f, -50f..50f)
    val handPosZ by float("PositionRotationZ", 0f, -50f..50f)


    var itemRotate by boolean("ItemRotate", false)
    val itemRotationMode by choices("ItemRotateMode", arrayOf("None", "Straight", "Forward", "Nano", "Uh"), "None") { itemRotate }
    val itemRotateSpeed by float("RotateSpeed", 8f, 1f.. 15f)  { itemRotate }

    var delay = 0f

    fun getAnimation() = animations.firstOrNull { it.name == animationMode }

}

/**
 * Item Render Rotation
 *
 * This class allows you to rotate item animation.
 *
 * @author Zywl
 */
fun itemRenderRotate() {
    val rotationTimer = MSTimer()

    if (itemRotationMode == "none") {
        itemRotate = false
        return
    }

    when (itemRotationMode.lowercase()) {
        "straight" -> rotate(delay, 0.0f, 1.0f, 0.0f)
        "forward" -> rotate(delay, 1.0f, 1.0f, 0.0f)
        "nano" -> rotate(delay, 0.0f, 0.0f, 0.0f)
        "uh" -> rotate(delay, 1.0f, 0.0f, 1.0f)
    }

    if (rotationTimer.hasTimePassed(1L)) {
        delay++
        delay += itemRotateSpeed
        rotationTimer.reset()
    }

    if (delay > 360.0f) {
        delay = 0.0f
    }
}

/**
 * Sword Animation
 *
 * This class allows you to create your own animation.
 * It transforms the item in the hand and the known functions from Mojang are directly accessible as well.
 *
 * @author CCBlueX
 */
abstract class Animation(val name: String) : MinecraftInstance {
    abstract fun transform(f1: Float, f: Float, clientPlayer: AbstractClientPlayer)

    /**
     * Transforms the block in the hand
     *
     * @author Mojang
     */
    protected fun doBlockTransformations() {
        translate(-0.5f, 0.2f, 0f)
        rotate(30f, 0f, 1f, 0f)
        rotate(-80f, 1f, 0f, 0f)
        rotate(60f, 0f, 1f, 0f)
        if (itemRotate) {
            itemRenderRotate()
        }
    }

    /**
     * Transforms the item in the hand
     *
     * @author Mojang
     */
    protected fun transformFirstPersonItem(equipProgress: Float, swingProgress: Float) {
        translate(0.56f, -0.52f, -0.71999997f)
        translate(0f, equipProgress * -0.6f, 0f)
        rotate(45f, 0f, 1f, 0f)
        val f = MathHelper.sin(swingProgress * swingProgress * 3.1415927f)
        val f1 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * 3.1415927f)
        rotate(f * -20f, 0f, 1f, 0f)
        rotate(f1 * -20f, 0f, 0f, 1f)
        rotate(f1 * -80f, 1f, 0f, 0f)
        scale(0.4f, 0.4f, 0.4f)
        if (itemRotate) {
            itemRenderRotate()
        }
    }

}

/**
 * OneSeven animation (default). Similar to the 1.7 blocking animation.
 *
 * @author CCBlueX
 */
class OneSevenAnimation : Animation("OneSeven") {
    override fun transform(f1: Float, f: Float, clientPlayer: AbstractClientPlayer) {
        transformFirstPersonItem(f, f1)
        doBlockTransformations()
        translate(-0.5f, 0.2f, 0f)
        if (itemRotate) {
            itemRenderRotate()
        }
    }

}

class OldAnimation : Animation("Old") {
    override fun transform(f1: Float, f: Float, clientPlayer: AbstractClientPlayer) {
        transformFirstPersonItem(f, f1)
        doBlockTransformations()
        if (itemRotate) {
            itemRenderRotate()
        }
    }
}

/**
 * Pushdown animation
 */
class OldPushdownAnimation : Animation("Pushdown") {

    /**
     * @author CzechHek. Taken from Animations script.
     */
    override fun transform(f1: Float, f: Float, clientPlayer: AbstractClientPlayer) {
        translate(0.56, -0.52, -0.5)
        translate(0.0, -f.toDouble() * 0.3, 0.0)
        rotate(45.5f, 0f, 1f, 0f)
        val var3 = MathHelper.sin(0f)
        val var4 = MathHelper.sin(0f)
        rotate((var3 * -20f), 0f, 1f, 0f)
        rotate((var4 * -20f), 0f, 0f, 1f)
        rotate((var4 * -80f), 1f, 0f, 0f)
        scale(0.32, 0.32, 0.32)
        val var15 = MathHelper.sin((MathHelper.sqrt_float(f1) * 3.1415927f))
        rotate((-var15 * 125 / 1.75f), 3.95f, 0.35f, 8f)
        rotate(-var15 * 35, 0f, (var15 / 100f), -10f)
        translate(-1.0, 0.6, -0.0)
        rotate(30f, 0f, 1f, 0f)
        rotate(-80f, 1f, 0f, 0f)
        rotate(60f, 0f, 1f, 0f)
        glTranslated(1.05, 0.35, 0.4)
        glTranslatef(-1f, 0f, 0f)
        if (itemRotate) {
            itemRenderRotate()
        }
    }

}

/**
 * New Pushdown animation.
 * @author EclipsesDev
 *
 */
class NewPushdownAnimation : Animation("NewPushdown") {

    override fun transform(f1: Float, f: Float, clientPlayer: AbstractClientPlayer) {
        val x = Animations.handPosX - 0.08
        val y = Animations.handPosY + 0.12
        val z = Animations.handPosZ.toDouble()
        translate(x, y, z)

        val var9 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927f)
        translate(0.0, 0.0, 0.0)

        transformFirstPersonItem(f / 1.4f, 0.0f)

        rotate(-var9 * 65.0f / 2.0f, var9 / 2.0f, 1.0f, 4.0f)
        rotate(-var9 * 60.0f, 1.0f, var9 / 3.0f, -0.0f)
        doBlockTransformations()

        scale(1.0, 1.0, 1.0)
    }

}

/**
 * Helium animation.
 * @author 182exe
 */
class HeliumAnimation : Animation("Helium") {
    override fun transform(f1: Float, f: Float, clientPlayer: AbstractClientPlayer) {
        transformFirstPersonItem(f, 0.0f)
        val c0 = MathHelper.sin(f1 * f * 3.1415927f)
        val c1 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927f)
        rotate(-c1 * 55.0f, 30.0f, c0 / 5.0f, 0.0f)
        doBlockTransformations()
        if (itemRotate) {
            itemRenderRotate()
        }
    }
}

/**
 * Argon animation.
 * @author 182exe
 */
class ArgonAnimation : Animation("Argon") {
    override fun transform(f1: Float, f: Float, clientPlayer: AbstractClientPlayer) {
        transformFirstPersonItem(f / 2.5f, f1)
        val c2 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927f)
        val c3 = MathHelper.cos(MathHelper.sqrt_float(f) * 3.1415927f)
        rotate(c3 * 50.0f / 10.0f, -c2, -0.0f, 100.0f)
        rotate(c2 * 50.0f, 200.0f, -c2 / 2.0f, -0.0f)
        translate(0.0, 0.3, 0.0)
        doBlockTransformations()
        if (itemRotate) {
            itemRenderRotate()
        }
    }
}

/**
 * Cesium animation.
 * @author 182exe
 */
class CesiumAnimation : Animation("Cesium") {
    override fun transform(f1: Float, f: Float, clientPlayer: AbstractClientPlayer) {
        val c4 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927f)
        transformFirstPersonItem(f, 0.0f)
        rotate(-c4 * 10.0f / 20.0f, c4 / 2.0f, 0.0f, 4.0f)
        rotate(-c4 * 30.0f, 0.0f, c4 / 3.0f, 0.0f)
        rotate(-c4 * 10.0f, 1.0f, c4/10.0f, 0.0f)
        translate(0.0, 0.2, 0.0)
        if (itemRotate) {
            itemRenderRotate()
        }
    }
}

/**
 * Sulfur animation.
 * @author 182exe
 */
class SulfurAnimation : Animation("Sulfur") {
    override fun transform(f1: Float, f: Float, clientPlayer: AbstractClientPlayer) {
        val c5 = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927f)
        val c6 = MathHelper.cos(MathHelper.sqrt_float(f1) * 3.1415927f)
        transformFirstPersonItem(f, 0.0f)
        rotate(-c5 * 30.0f, c5 / 10.0f, c6 / 10.0f, 0.0f)
        translate(c5 / 1.5, 0.2, 0.0)
        doBlockTransformations()
        if (itemRotate) {
            itemRenderRotate()
        }
    }
}

/**
 * SmoothFloat animation.
 * @author MinusBounce
 */
class SmoothFloatAnimation : Animation("SmoothFloat") {
    override fun transform(f1: Float, f: Float, clientPlayer: AbstractClientPlayer) {
        val smoothSpeed = itemRotateSpeed * 0.7f
        val progress = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927f)
        
        transformFirstPersonItem(f / 3f, 0f)
        
        rotate(progress * 20f / smoothSpeed, 1f, -0.5f, 0.1f)
        rotate(progress * 40f, 0.2f, 0.5f, 0.1f)
        rotate(-progress * 20f, 1f, -0.3f, 0.7f)
        
        translate(0.1f, -0.1f, -0.2f)
        doBlockTransformations()
        
        rotate(progress * 20f, 0f, 1f, 0f)
        
        if (itemRotate) {
            itemRenderRotate()
        }
    }
}

/**
 * Reverse animation.
 * @author MinusBounce
 */
class ReverseAnimation : Animation("Reverse") {
    override fun transform(f1: Float, f: Float, clientPlayer: AbstractClientPlayer) {
        val progress = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927f)
        
        transformFirstPersonItem(f, 0f)
        translate(0.0f, 0.3f, -0.4f)
        rotate(progress * -30f, 1f, 0f, 2f)
        rotate(progress * -20f, 0f, 1f, 0f)
        rotate(-progress * 20f, 0f, 0f, 1f)
        
        scale(0.4f, 0.4f, 0.4f)
        doBlockTransformations()
        
        if (itemRotate) {
            itemRenderRotate()
        }
    }
}

/**
 * ETB animation.
 * @author MinusBounce
 */
class ETBAnimation : Animation("ETB") {
    override fun transform(f1: Float, f: Float, clientPlayer: AbstractClientPlayer) {
        val progress = MathHelper.sin(MathHelper.sqrt_float(f) * 3.1415927f)
        val progress2 = MathHelper.sin(f * f * 3.1415927f)

        transformFirstPersonItem(f1 * -0.6f, 0f)
        translate(0.56f, -0.52f, -0.71999997f)
        
        rotate(45.0f, 0.0f, 1.0f, 0.0f)
        rotate(progress2 * -34.0f, 0.0f, 1.0f, 0.2f)
        rotate(progress * -20.7f, 0.2f, 0.1f, 1.0f)
        rotate(progress * -68.6f, 1.3f, 0.1f, 0.2f)
        
        scale(0.4f, 0.4f, 0.4f)
        doBlockTransformations()
        
        if (itemRotate) {
            itemRenderRotate()
        }
    }
}

/**
 * Flux animation.
 * @author MinusBounce
 */
class FluxAnimation : Animation("Flux") {
    override fun transform(f1: Float, f: Float, clientPlayer: AbstractClientPlayer) {
        val progress = MathHelper.sin(MathHelper.sqrt_float(f1) * 3.1415927f)
        
        transformFirstPersonItem(f, 0f)
        translate(0.1f, 0.2f, 0.1f)
        
        rotate(-progress * 40f, 1f, -0.2f, 0.1f)
        rotate(progress * 20f, 0f, 1f, 0f)
        rotate(-progress * 20f, 0f, 0f, 0.5f)
        
        translate(0f, -0.3f, 0f)
        scale(0.4f, 0.4f, 0.4f)
        doBlockTransformations()
        
        if (itemRotate) {
            itemRenderRotate()
        }
    }
}