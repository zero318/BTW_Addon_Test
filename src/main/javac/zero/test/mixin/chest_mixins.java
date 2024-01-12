package zero.test.mixin;


import net.minecraft.src.*;

import btw.block.blocks.AestheticOpaqueBlock;
import btw.AddonHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

#include "..\feature_flags.h"
#include "..\util.h"

@Mixin(BlockChest.class)
public class ChestMixins {
    
#if ENABLE_DIRECTIONAL_UPDATES
    @Overwrite
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        int allowed_chests = 2;
        
        int self_id = ((Block)(Object)this).blockID;

        if (world.getBlockId(x - 1, y, z) == self_id) {
            --allowed_chests;
            if (
                world.getBlockId(x - 2, y, z) == self_id ||
                world.getBlockId(x - 1, y, z - 1) == self_id ||
                world.getBlockId(x - 1, y, z + 1) == self_id
            ) {
                return false;
            }
        }

        if (world.getBlockId(x + 1, y, z) == self_id) {
            if (
                --allowed_chests == 0 ||
                world.getBlockId(x + 2, y, z) == self_id ||
                world.getBlockId(x + 1, y, z - 1) == self_id ||
                world.getBlockId(x + 1, y, z + 1) == self_id
            ) {
                return false;
            }
        }

        if (world.getBlockId(x, y, z - 1) == self_id) {
            if (
                --allowed_chests == 0 ||
                world.getBlockId(x, y, z - 2) == self_id ||
                world.getBlockId(x - 1, y, z - 1) == self_id ||
                world.getBlockId(x + 1, y, z - 1) == self_id
            ) {
                return false;
            }
        }

        if (world.getBlockId(x, y, z + 1) == self_id) {
            if (
                --allowed_chests == 0 ||
                world.getBlockId(x, y, z + 2) == self_id ||
                world.getBlockId(x - 1, y, z + 1) == self_id ||
                world.getBlockId(x + 1, y, z + 1) == self_id
            ) {
                return false;
            }
        }
        //AddonHandler.logMessage(""+allowed_chests+" "+Block.chest.blockID+" "+world.getBlockId(x, y, z));

        return true;
    }

    // This breaks the chest if it would
    // form a triple chest
    public int updateShape(World world, int x, int y, int z, int direction, int meta) {
        return ((BlockChest)(Object)this).canPlaceBlockAt(world, x, y, z) ? meta : SHAPE_BREAK_BLOCK;
    }
#endif

#if ENABLE_MOVING_BLOCK_CHAINING
    public boolean isStickyForBlocks(World world, int x, int y, int z, int direction) {
        // Only attach to other chests.
        // Somehow the rendering when splitting double chests
        // got broken and I have no idea how to fix it, so this
        return DIRECTION_IS_HORIZONTAL(direction) && ((BlockChest)(Object)this).blockID == world.getBlockId(x + Facing.offsetsXForSide[direction], y, z + Facing.offsetsZForSide[direction]);
    }
#endif
}