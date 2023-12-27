package zero.test.mixin;

import net.minecraft.src.*;

import btw.block.blocks.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Random;

import zero.test.IBlockMixins;

#include "../util.h"
#include "../feature_flags.h"
#include "../ids.h"

@Mixin(MortarReceiverStairsBlock.class)
public class MortarReceiverStairsBlockMixins extends FallingStairsBlock {
    MortarReceiverStairsBlockMixins(int id, Block reference_block, int reference_block_meta) {
        super(id, reference_block, reference_block_meta);
    }
    
#if ENABLE_MOVING_BLOCK_CHAINING && ENABLE_SLIME_SUPPORTING_MORTAR_BLOCKS
    @Override
    public void updateTick(World world, int X, int Y, int Z, Random random) {
        
        goto_block(has_adjacent_slime) {
            int facing = 0;
            do {
                int nextX = X + Facing.offsetsXForSide[facing];
                int nextY = Y + Facing.offsetsYForSide[facing];
                int nextZ = Z + Facing.offsetsZForSide[facing];
                Block neighbor_block = Block.blocksList[world.getBlockId(nextX, nextY, nextZ)];
                if (
                    !BLOCK_IS_AIR(neighbor_block) &&
                    ((IBlockMixins)neighbor_block).permanentlySupportsMortarBlocks(world, nextX, nextY, nextZ, facing)
                ) {
                    goto(has_adjacent_slime);
                }
            } while (++facing < 6);
            if (checkForFall(world, X, Y, Z)) {
                return;
            }
        } goto_target(has_adjacent_slime);
        
        if (getIsUpsideDown(world, X, Y, Z)) {
            setIsUpsideDown(world, X, Y, Z, false);
        }
    }
#endif
}