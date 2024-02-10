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
    public MortarReceiverSlabBlockMixins() {
        super(0, null);
    }
    
#if ENABLE_MOVING_BLOCK_CHAINING && ENABLE_SLIME_SUPPORTING_MORTAR_BLOCKS
    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        
        goto_block(has_adjacent_slime) {
            int facing = getIsUpsideDown(world, x, y, z) ? DIRECTION_UP : DIRECTION_DOWN;
            int nextY = y + Facing.offsetsXForSide[facing];
            Block neighborBlock = Block.blocksList[world.getBlockId(x, nextY, z)];
            if (
                !BLOCK_IS_AIR(neighborBlock) &&
                ((IBlockMixins)neighborBlock).permanentlySupportsMortarBlocks(world, x, nextY, z, facing)
            ) {
                goto(has_adjacent_slime);
            }
            facing = 2;
            do {
                int nextX = x + Facing.offsetsXForSide[facing];
                int nextZ = z + Facing.offsetsZForSide[facing];
                neighborBlock = Block.blocksList[world.getBlockId(nextX, y, nextZ)];
                if (
                    !BLOCK_IS_AIR(neighborBlock) &&
                    ((IBlockMixins)neighborBlock).permanentlySupportsMortarBlocks(world, nextX, y, nextZ, facing)
                ) {
                    goto(has_adjacent_slime);
                }
            } while (DIRECTION_IS_VALID(++facing));
            if (checkForFall(world, x, y, z)) {
                return;
            }
        } goto_target(has_adjacent_slime);
        
        if (getIsUpsideDown(world, x, y, z)) {
            setIsUpsideDown(world, x, y, z, false);
        }
    }
#endif
}