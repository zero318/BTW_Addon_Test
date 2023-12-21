package zero.test;

import net.minecraft.src.Block;
import net.minecraft.src.World;

#include "feature_flags.h"

public interface IBlockMixins {
    
    // Whether or not the block should have onNeighborBlockChange
    // called in response to comparator updates
    public boolean getWeakChanges(World world, int X, int Y, int Z, int neighbor_id);
    
#if ENABLE_DIRECTIONAL_UPDATES
    // Whether or not onNeighborBlockChange will receive the
    // update direction int the upper bits of neighbor_id
    public boolean caresAboutUpdateDirection();
#endif

#if ENABLE_MOVING_BLOCK_CHAINING
    // The direction argument is intended to allow for
    // blocks that are only sticky on specific faces
    public boolean isSticky(int X, int Y, int Z, int direction);
    
    // This is only called after the face shared with the
    // neighbor block is already known to be sticky
    public boolean canStickTo(int X, int Y, int Z, int direction, int neighbor_id);
#endif
}