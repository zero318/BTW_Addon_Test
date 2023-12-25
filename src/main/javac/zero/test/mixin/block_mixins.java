package zero.test.mixin;

import net.minecraft.src.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import zero.test.IBlockMixins;
import zero.test.IWorldMixins;

#include "..\feature_flags.h"
#include "..\util.h"

@Mixin(Block.class)
public class BlockMixins implements IBlockMixins {
    @Overwrite
    public boolean hasLargeCenterHardPointToFacing(IBlockAccess block_access, int X, int Y, int Z, int direction, boolean ignore_transparency) {
		return ((Block)(Object)this).isNormalCube(block_access, X, Y, Z);
	}
    
    @Override
    public int getMobilityFlag(World world, int X, int Y, int Z) {
        return ((Block)(Object)this).getMobilityFlag();
    }
}