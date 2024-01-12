package zero.test.mixin;

import net.minecraft.src.*;

import btw.block.blocks.*;
import btw.AddonHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Random;

import zero.test.IBlockMixins;

#include "../util.h"
#include "../feature_flags.h"
#include "../ids.h"

@Mixin(MortarReceiverBlock.class)
public class MortarReceiverBlockMixins extends FallingFullBlock {
    public MortarReceiverBlockMixins(int id, Material material) {
        super(id, material);
    }
    
#if ENABLE_MOVING_BLOCK_CHAINING && ENABLE_SLIME_SUPPORTING_MORTAR_BLOCKS
    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
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
                return;
            }
        } while (DIRECTION_IS_VALID(++facing));
        checkForFall(world, x, y, z);
    }
#endif
}