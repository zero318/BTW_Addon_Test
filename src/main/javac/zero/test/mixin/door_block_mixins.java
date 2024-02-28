package zero.test.mixin;

import net.minecraft.src.*;

import btw.world.util.WorldUtils;
import btw.block.blocks.DoorBlock;

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

#define DOOR_DIRECTION_META_OFFSET 0
#define DOOR_DIRECTION_META_BITS 2

#define DOOR_DIRECTION_META_EAST 0
#define DOOR_DIRECTION_META_SOUTH 1
#define DOOR_DIRECTION_META_WEST 2
#define DOOR_DIRECTION_META_NORTH 3

#define OPENED_META_OFFSET 2
#define OPENED_META_BITS 1
#define OPENED_META_IS_BOOL true

#define DOOR_IS_TOP_META_OFFSET 3
#define DOOR_IS_TOP_META_BITS 1
#define DOOR_IS_TOP_META_IS_BOOL true

// This doesn't work, but consistency is cool
#define FLIPPED_HINGE_META_OFFSET 4
#define FLIPPED_HINGE_META_BITS 1
#define FLIPPED_HINGE_META_IS_BOOL true

@Mixin(DoorBlock.class)
public abstract class DoorBlockMixins extends BlockDoor {
    
    public DoorBlockMixins() {
        super(0, null);
    }
    
    @Override
    public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int x, int y, int z, int facing, boolean ignoreTransparency) {
        if (ignoreTransparency) {
            int meta = this.getFullMetadata(blockAccess, x, y, z);
            
            int openDiff = 0;
            if (READ_META_FIELD(meta, OPENED)) {
                openDiff = meta <= 15 ? 1 : -1;
            }
            switch (READ_META_FIELD(meta + openDiff, DOOR_DIRECTION)) {
                case DOOR_DIRECTION_META_EAST:
                    return facing == DIRECTION_WEST;
                case DOOR_DIRECTION_META_SOUTH:
                    return facing == DIRECTION_NORTH;
                case DOOR_DIRECTION_META_WEST:
                    return facing == DIRECTION_EAST;
                default:
                    return facing == DIRECTION_SOUTH;
            }
            
        }
        return false;
    }
    
#if ENABLE_MORE_TURNABLE_BLOCKS && ENABLE_MOVING_BLOCK_CHAINING
    @Override
    public boolean canRotateOnTurntable(IBlockAccess blockAccess, int x, int y, int z) {
        return true;
    }
    
    @Override
    public int rotateMetadataAroundJAxis(int meta, boolean reverse) {
        if (!READ_META_FIELD(meta, DOOR_IS_TOP)) {
            return MERGE_META_FIELD(meta, DOOR_DIRECTION, meta + (reverse ? -1 : 1) & 3);
        }
        return meta;
    }
    
    public boolean isStickyForBlocks(World world, int x, int y, int z, int direction) {
        int meta = world.getBlockMetadata(x, y, z);
        return direction == (READ_META_FIELD(meta, DOOR_IS_TOP) ? DIRECTION_DOWN : DIRECTION_UP);
    }
#endif
}