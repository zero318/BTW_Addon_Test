package zero.test.mixin;

import net.minecraft.src.*;

import btw.block.blocks.RedstoneRepeaterBlock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

#include "..\util.h"
#include "..\feature_flags.h"

@Mixin(RedstoneRepeaterBlock.class)
public abstract class RedstoneRepeaterBlockMixins extends BlockRedstoneRepeater {
    
    public RedstoneRepeaterBlockMixins(int blockId, boolean powered) {
        super(blockId, powered);
    }
    
#if ENABLE_REDSTONE_BUGFIXES
    @Inject(
        method = "rotateAroundJAxis(Lnet/minecraft/src/World;IIIZ)Z",
        at = @At(
            value = "RETURN",
            ordinal = 0
        )
    )
    public void rotateAroundJAxis_inject(World world, int x, int y, int z, boolean reverse, CallbackInfoReturnable info) {
        this.func_94483_i_(world, x, y, z);
    }
#endif
}