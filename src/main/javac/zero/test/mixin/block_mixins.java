package zero.test.mixin;

import net.minecraft.src.Block;
import net.minecraft.src.World;

import org.spongepowered.asm.mixin.Mixin;

import zero.test.IBlockMixins;

#include "..\feature_flags.h"

@Mixin(Block.class)
public class BlockMixins implements IBlockMixins {
    @Override
    public boolean getWeakChanges(World world, int X, int Y, int Z, int meta) {
        return false;
    }
    
#if ENABLE_DIRECTIONAL_UPDATES
    @Override
    public boolean caresAboutUpdateDirection() {
        return false;
    }
#endif

#if ENABLE_MOVING_BLOCK_CHAINING
    @Override
    public boolean isSticky(int X, int Y, int Z, int direction) {
        return false;
    }
#endif
}