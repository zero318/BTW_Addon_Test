package zero.test.mixin;

import net.minecraft.src.*;

import btw.world.util.WorldUtils;
import btw.block.blocks.TrapDoorBlock;

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

#define TRAPDOOR_DIRECTION_META_OFFSET 0
#define TRAPDOOR_DIRECTION_META_BITS 2

@Mixin(TrapDoorBlock.class)
public abstract class TrapDoorBlockMixins extends BlockTrapDoor {
    public TrapDoorBlockMixins() {
        super(0, null);
    }
    
#if ENABLE_MORE_TURNABLE_BLOCKS
    @Override
    public boolean canRotateOnTurntable(IBlockAccess blockAccess, int x, int y, int z) {
        return true;
    }
    
    @Override
    public int rotateMetadataAroundJAxis(int meta, boolean reverse) {
        ;return MERGE_META_FIELD(meta, TRAPDOOR_DIRECTION, rotateFacingAroundY(READ_META_FIELD(meta, TRAPDOOR_DIRECTION) + 2, BOOL_INVERT(reverse)) - 2);
    }
#endif
}