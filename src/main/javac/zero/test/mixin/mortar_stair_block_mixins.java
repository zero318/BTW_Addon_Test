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
    public MortarReceiverStairsBlockMixins(int id, Block referenceBlock, int referenceBlockMeta) {
        super(id, referenceBlock, referenceBlockMeta);
    }
    
#if ENABLE_MOVING_BLOCK_CHAINING && ENABLE_SLIME_SUPPORTING_MORTAR_BLOCKS
    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        
        goto_block(has_adjacent_slime) {
            int facing = 0;
            do {
                int nextX = x + Facing.offsetsXForSide[facing];
                int nextY = y + Facing.offsetsYForSide[facing];
                int nextZ = z + Facing.offsetsZForSide[facing];
                Block neighborBlock = Block.blocksList[world.getBlockId(nextX, nextY, nextZ)];
                if (
                    !BLOCK_IS_AIR(neighborBlock) &&
                    ((IBlockMixins)neighborBlock).permanentlySupportsMortarBlocks(world, nextX, nextY, nextZ, facing)
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