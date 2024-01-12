package zero.test;

import net.minecraft.src.Block;
import net.minecraft.src.World;

import zero.test.IBlockMixins;

#include "feature_flags.h"
#include "util.h"

public interface IWorldMixins {
    
    //public boolean get_is_handling_piston_move();
    
#if ENABLE_DIRECTIONAL_UPDATES
    public void updateNeighbourShapes(int x, int y, int z, int flags);
    
    public int updateFromNeighborShapes(int x, int y, int z, int blockId, int blockMeta);
#endif

    public int getBlockStrongPowerInputExceptFacing(int x, int y, int z, int facing);
    public int getBlockWeakPowerInputExceptFacing(int x, int y, int z, int facing);

#if ENABLE_MODERN_REDSTONE_WIRE
    default public boolean isBlockRedstoneConductor(int x, int y, int z) {
        World self = (World)(Object)this;
        Block block = Block.blocksList[self.getBlockId(x, y, z)];
        return !BLOCK_IS_AIR(block) && ((IBlockMixins)block).isRedstoneConductor(self, x, y, z);
    }
#endif
}