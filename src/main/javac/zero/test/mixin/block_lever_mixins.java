package zero.test.mixin;

import net.minecraft.src.*;

import btw.block.blocks.LeverBlock;
import btw.world.util.WorldUtils;

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

#define LEVER_DIRECTION_META_OFFSET 0
#define LEVER_DIRECTION_META_BITS 3

#define POWERED_META_OFFSET 3

@Mixin(LeverBlock.class)
public abstract class BlockLeverMixins extends Block {
    public BlockLeverMixins() {
        super(0, null);
    }
    
#if ENABLE_BUDDY_BLOCK_IGNORES_POWER_UPDATES
    @Override
    public boolean triggersBuddy() {
        return false;
    }
#endif
    
#if ENABLE_MODERN_SUPPORT_LOGIC == MODERN_SUPPORT_LOGIC_GLOBAL_ALL

    @Override
    public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int direction) {
        return WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(
            world,
            x - Facing.offsetsXForSide[direction],
            y - Facing.offsetsYForSide[direction],
            z - Facing.offsetsZForSide[direction],
            direction,
            true
        );
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return this.canPlaceBlockOnSide(world, x, y, z, DIRECTION_DOWN) ||
               this.canPlaceBlockOnSide(world, x, y, z, DIRECTION_UP) ||
               this.canPlaceBlockOnSide(world, x, y, z, DIRECTION_NORTH) ||
               this.canPlaceBlockOnSide(world, x, y, z, DIRECTION_SOUTH) ||
               this.canPlaceBlockOnSide(world, x, y, z, DIRECTION_WEST) ||
               this.canPlaceBlockOnSide(world, x, y, z, DIRECTION_EAST);
    }
    
    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float clickX, float clickY, float clickZ, int meta) {
        int leverDir = -1;
        if (this.canPlaceBlockOnSide(world, x, y, z, side)) {
            switch (side) {
                case DIRECTION_DOWN:
                    leverDir = 0;
                    break;
                case DIRECTION_UP:
                    leverDir = 5;
                    break;
                default:
                    leverDir = 6 - side;
                    break;
/*
                case DIRECTION_NORTH:
                    leverDir = 4;
                    break;
                case DIRECTION_SOUTH:
                    leverDir = 3;
                    break;
                case DIRECTION_WEST:
                    leverDir = 2;
                    break;
                default:
                    leverDir = 1;
                    break;
*/
            }
        }
        return MERGE_META_FIELD(meta, LEVER_DIRECTION, leverDir);
    }
    
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborId) {
        int meta = world.getBlockMetadata(x, y, z);
        int direction = READ_META_FIELD(meta, LEVER_DIRECTION);
        switch (direction) {
            case 0: case 7:
                direction = DIRECTION_DOWN;
                break;
            case 5: case 6:
                direction = DIRECTION_UP;
                break;
            default:
                direction = 6 - direction;
                break;
        }
        if (!this.canPlaceBlockOnSide(world, x, y, z, direction)) {
            this.dropBlockAsItem(world, x, y, z, meta, 0);
            world.setBlockToAir(x, y, z);
        }
    }
#endif

#if ENABLE_MORE_TURNABLE_BLOCKS
    @Overwrite
    public boolean onRotatedAroundBlockOnTurntableToFacing(World world, int x, int y, int z, int direction) {
        return true;
    }
    
    @Override
    public int rotateMetadataAroundJAxis(int meta, boolean reverse) {
        int direction = READ_META_FIELD(meta, LEVER_DIRECTION);
        switch (direction) {
            case 1: case 2: case 3: case 4:
                direction = 6 - rotateFacingAroundY(6 - direction, reverse);
        }
        return MERGE_META_FIELD(meta, LEVER_DIRECTION, direction);
    }
#endif
}