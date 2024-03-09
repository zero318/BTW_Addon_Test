package zero.test.mixin;

import net.minecraft.src.*;

import btw.block.blocks.CookingVesselBlock;

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

#include "..\util.h"
#include "..\feature_flags.h"

@Mixin(CookingVesselBlock.class)
public abstract class CookingVesselBlockMixins {
#if ENABLE_REDSTONE_BUGFIXES
    @Inject(
        method = "breakBlock(Lnet/minecraft/src/World;IIIII)V",
        at = @At(
            value = "INVOKE",
            target = "Lbtw/block/blocks/VesselBlock;breakBlock(Lnet/minecraft/src/World;IIIII)V"
        )
    )
    public void break_block_comparator_inject(World world, int x, int y, int z, int blockId, int meta, CallbackInfo info) {
        world.func_96440_m(x, y, z, blockId);
    }
#endif
}