/*
 * RinBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/rattermc/rinbounce69
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.block;

import net.ccbluex.liquidbounce.features.module.modules.movement.NoSlow;
import net.ccbluex.liquidbounce.features.module.modules.world.Liquids;
import net.minecraft.block.BlockLiquid;
import net.minecraft.util.Vec3;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SideOnly(Side.CLIENT)
@Mixin(BlockLiquid.class)
public class MixinBlockLiquid {

    @Inject(method = "canCollideCheck", at = @At("HEAD"), cancellable = true)
    private void onCollideCheck(CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        if (Liquids.INSTANCE.handleEvents())
            callbackInfoReturnable.setReturnValue(true);
    }

    @Inject(method = "modifyAcceleration", at = @At("HEAD"), cancellable = true)
    private void onModifyAcceleration(CallbackInfoReturnable<Vec3> callbackInfoReturnable) {
        final NoSlow noSlow = NoSlow.INSTANCE;

        if (noSlow.handleEvents() && noSlow.getLiquidPush()) {
            callbackInfoReturnable.setReturnValue(new Vec3(0, 0, 0));
        }
    }
}