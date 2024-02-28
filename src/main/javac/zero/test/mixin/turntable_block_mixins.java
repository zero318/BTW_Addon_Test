package zero.test.mixin;

import net.minecraft.src.*;

import btw.block.blocks.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

import zero.test.IWorldMixins;

import java.util.Random;

#include "..\feature_flags.h"
#include "..\util.h"

@Mixin(TurntableBlock.class)
public abstract class TurntableBlockMixins {
    
#if ENABLE_LESS_CRAP_BTW_BLOCK_POWERING
    @Redirect(
        method = "updateTick",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/World;isBlockGettingPowered(III)Z"
        )
    )
    public boolean disable_power_through_face(World world, int x, int y, int z) {
        return ((IWorldMixins)world).getBlockWeakPowerInputExceptFacing(x, y, z, DIRECTION_UP) > 0;
    }
#endif
}