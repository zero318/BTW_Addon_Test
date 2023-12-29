package zero.test;

import net.minecraft.src.Block;
import net.minecraft.src.World;

#include "feature_flags.h"

public interface IWorldMixins {
    
    //public boolean get_is_handling_piston_move();
    
#if ENABLE_DIRECTIONAL_UPDATES
    public void updateNeighbourShapes(int X, int Y, int Z, int flags);
    
    public int updateFromNeighborShapes(int X, int Y, int Z, int block_id, int block_meta);
#endif
}