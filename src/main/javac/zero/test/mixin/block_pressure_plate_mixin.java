package zero.test.mixin;

import net.minecraft.src.*;

import btw.world.util.WorldUtils;
import btw.block.blocks.ButtonBlock;

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

@Mixin(BlockPressurePlate.class)
public abstract class BlockPressurePlateMixins extends Block {
    public BlockPressurePlateMixins(int blockId, Material material) {
        super(blockId, material);
    }
#if ENABLE_BUDDY_BLOCK_IGNORES_POWER_UPDATES
    @Override
    public boolean triggersBuddy() {
        return false;
    }
#endif
}