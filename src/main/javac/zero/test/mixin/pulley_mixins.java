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

import java.util.Random;

#include "..\feature_flags.h"
#include "..\util.h"

@Mixin(PulleyBlock.class)
public abstract class PulleyBlockMixins {
    
#if ENABLE_LESS_CRAP_BTW_BLOCK_POWERING
    @Redirect(
        method = { "updateTick", "isCurrentStateValid" },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/World;isBlockGettingPowered(III)Z",
            ordinal = 1
        )
    )
    public boolean disable_pulley_quasi(World world, int x, int y, int z) {
        return false;
    }
#endif

#if ENABLE_MORE_COMPARATOR_OUTPUTS
    @Inject(
        method = "breakBlock(Lnet/minecraft/src/World;IIIII)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/BlockContainer;breakBlock(Lnet/minecraft/src/World;IIIII)V"
        )
    )
    public void break_block_comparator_inject(World world, int x, int y, int z, int blockId, int meta, CallbackInfo info) {
        world.func_96440_m(x, y, z, blockId);
    }
#endif
}