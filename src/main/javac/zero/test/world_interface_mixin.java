package zero.test;

import net.minecraft.src.Block;
import net.minecraft.src.World;

import zero.test.IBlockMixins;

#include "feature_flags.h"
#include "util.h"

public interface IWorldMixins {
    
    //public boolean get_is_handling_piston_move();
    
#if ENABLE_DIRECTIONAL_UPDATES
    public void updateNeighbourShapes(int X, int Y, int Z, int flags);
    
    public int updateFromNeighborShapes(int X, int Y, int Z, int block_id, int block_meta);
#endif

#if ENABLE_MODERN_REDSTONE_WIRE
    default public boolean isBlockRedstoneConductor(int X, int Y, int Z) {
        World self = (World)(Object)this;
        Block block = Block.blocksList[self.getBlockId(X, Y, Z)];
        return !BLOCK_IS_AIR(block) && ((IBlockMixins)block).isRedstoneConductor(self, X, Y, Z);
    }
#endif
}