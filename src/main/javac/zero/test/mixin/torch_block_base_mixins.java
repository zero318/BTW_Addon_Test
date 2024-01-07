package zero.test.mixin;

import net.minecraft.src.*;

import btw.AddonHandler;
import btw.block.blocks.TorchBlockBase;

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

@Mixin(TorchBlockBase.class)
public abstract class TorchBlockBaseMixins {
#if ENABLE_MODERN_SUPPORT_LOGIC == MODERN_SUPPORT_LOGIC_GLOBAL_ALL
    @Redirect(
        method = { "canPlaceBlockAt", "onBlockPlaced", "onBlockAdded", "validateState" },
        at = @At(
            value = "INVOKE",
            target = "Lbtw/world/util/WorldUtils;doesBlockHaveCenterHardpointToFacing(Lnet/minecraft/src/IBlockAccess;IIII)Z"
        )
    )
    public boolean redirect_doesBlockHaveCenterHardpointToFacing(IBlockAccess block_access, int X, int Y, int Z, int direction) {
        Block block = Block.blocksList[block_access.getBlockId(X, Y, Z)];
        return !BLOCK_IS_AIR(block) && block.hasCenterHardPointToFacing(block_access, X, Y, Z, OPPOSITE_DIRECTION(direction), true);
    }
#endif
}