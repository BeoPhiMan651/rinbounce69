/*
 * RinBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/rattermc/rinbounce69
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.vulcan

import net.ccbluex.liquidbounce.features.module.modules.movement.speedmodes.SpeedMode
import net.ccbluex.liquidbounce.utils.extensions.isInLiquid
import net.ccbluex.liquidbounce.utils.extensions.isMoving
import net.ccbluex.liquidbounce.utils.extensions.tryJump
import net.ccbluex.liquidbounce.utils.movement.MovementUtils.strafe

object VulcanLowHop : SpeedMode("VulcanLowHop") {
    override fun onUpdate() {
        val player = mc.thePlayer ?: return
        if (player.isInLiquid || player.isInWeb || player.isOnLadder) return

        if (player.isMoving) {
            if (!player.onGround && player.fallDistance > 1.1) {
                mc.timer.timerSpeed = 1f
                player.motionY = -0.25
                return
            }

            if (player.onGround) {
                player.tryJump()
                strafe(0.4815f)
                mc.timer.timerSpeed = 1.263f
            } else if (player.ticksExisted % 4 == 0) {
                if (player.ticksExisted % 3 == 0) {
                    player.motionY = -0.01 / player.motionY
                } else {
                    player.motionY = -player.motionY / player.posY
                }
                mc.timer.timerSpeed = 0.8985f
            }

        } else {
            mc.timer.timerSpeed = 1f
        }
    }
}