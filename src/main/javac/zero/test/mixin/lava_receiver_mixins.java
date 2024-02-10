package zero.test.mixin;

import net.minecraft.src.*;

import btw.block.blocks.*;
import btw.client.fx.BTWEffectManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;

import zero.test.IBlockMixins;

#include "../util.h"
#include "../feature_flags.h"
#include "../ids.h"

@Mixin(LavaReceiverBlock.class)
public abstract class LavaReceiverBlockMixins extends MortarReceiverBlock {
    public LavaReceiverBlockMixins() {
        super(0, null);
    }
    
#if ENABLE_MOVING_BLOCK_CHAINING && ENABLE_SLIME_SUPPORTING_MORTAR_BLOCKS
    @Shadow
    protected abstract boolean getHasLavaInCracks(IBlockAccess blockAccess, int x, int y, int z);
    
    @Shadow
    protected abstract void setHasLavaInCracks(World blockAccess, int x, int y, int z, boolean hasLava);
    
    @Shadow
    protected abstract boolean hasLavaAbove(IBlockAccess blockAccess, int x, int y, int z);
    
    @Shadow
    protected abstract boolean hasWaterAbove(IBlockAccess blockAccess, int x, int y, int z);
    
    @Shadow
    public abstract int getStrata(IBlockAccess blockAccess, int x, int y, int z);


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
        
        if (getHasLavaInCracks(world, x, y, z)) {
            if (hasWaterAbove(world, x, y, z)) {
                world.playAuxSFX(BTWEffectManager.FIRE_FIZZ_EFFECT_ID, x, y, z, 0);
                
                world.setBlockAndMetadataWithNotify(x, y, z, Block.stone.blockID, getStrata(world, x, y, z));
                
                return;
            }
        }
        else if (hasLavaAbove(world, x, y, z)) {
            setHasLavaInCracks(world, x, y, z, true);
        }
    }
#endif
}