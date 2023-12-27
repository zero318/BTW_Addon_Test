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

@Mixin(MortarReceiverSlabBlock.class)
public abstract class MortarReceiverSlabBlockMixins extends FallingSlabBlock {
    MortarReceiverSlabBlockMixins(int id, Material material) {
        super(id, material);
    }
    
#if ENABLE_MOVING_BLOCK_CHAINING && ENABLE_SLIME_SUPPORTING_MORTAR_BLOCKS
    @Override
    public void updateTick(World world, int X, int Y, int Z, Random random) {
        
        goto_block(has_adjacent_slime) {
            int facing = getIsUpsideDown(world, X, Y, Z) ? 1 : 0;
            int nextY = Y + Facing.offsetsXForSide[facing];
            Block neighbor_block = Block.blocksList[world.getBlockId(X, nextY, Z)];
            if (
                !BLOCK_IS_AIR(neighbor_block) &&
                ((IBlockMixins)neighbor_block).permanentlySupportsMortarBlocks(world, X, nextY, Z, facing)
            ) {
                goto(has_adjacent_slime);
            }
            facing = 2;
            do {
                int nextX = X + Facing.offsetsXForSide[facing];
                //int nextY = Y + Facing.offsetsYForSide[facing];
                int nextZ = Z + Facing.offsetsZForSide[facing];
                neighbor_block = Block.blocksList[world.getBlockId(nextX, Y, nextZ)];
                if (
                    !BLOCK_IS_AIR(neighbor_block) &&
                    ((IBlockMixins)neighbor_block).permanentlySupportsMortarBlocks(world, nextX, Y, nextZ, facing)
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