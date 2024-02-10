package zero.test.mixin;

import net.minecraft.src.*;

import btw.AddonHandler;
import btw.block.blocks.CompanionCubeBlock;

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

@Mixin(CompanionCubeBlock.class)
public abstract class CompanionCubeBlockMixins {
#if ENABLE_MODERN_REDSTONE_WIRE
    // Another case of overriding
    // isNormalBlock that screws with redstone.
    //
    // See the other explanation in the platform mixins.
    public boolean isRedstoneConductor(IBlockAccess blockAccess, int x, int y, int z) {
        return BOOL_INVERT(((CompanionCubeBlock)(Object)this).getIsSlab(blockAccess, x, y, z));
    }
#endif
}