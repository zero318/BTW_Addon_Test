package zero.test.mixin;

import net.minecraft.src.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import zero.test.IWorldMixins;

#include "..\feature_flags.h"
#include "..\util.h"

@Mixin(EntityBat.class)
public abstract class EntityBatMixins {
#if ENABLE_MODERN_REDSTONE_WIRE
    @Redirect(
        method = "updateAITasks()V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/World;isBlockNormalCube(III)Z"
        )
    )
    private boolean isBlockNormalCube_redirect(World world, int x, int y, int z) {
        return ((IWorldMixins)world).isBlockRedstoneConductor(x, y, z);
    }
#endif
}