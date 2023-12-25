package zero.test;

import net.minecraft.src.Block;
import net.minecraft.src.World;

#include "feature_flags.h"

public interface IBlockMixins {
    
    // Whether or not the block should have onNeighborBlockChange
    // called in response to comparator updates
    default public boolean getWeakChanges(World world, int X, int Y, int Z, int neighbor_id) {
        return false;
    }
    
#if ENABLE_DIRECTIONAL_UPDATES
    
    default public int updateShape(World world, int X, int Y, int Z, int direction, int meta) {
        return meta;
    }
    
    //default public void updateIndirectNeighbourShapes(World world, int X, int Y, int Z) {
    //}
#endif

#if ENABLE_MOVING_BLOCK_CHAINING

    public int getMobilityFlag(World world, int X, int Y, int Z);

    // The direction argument is intended to allow for
    // blocks that are only sticky on specific faces
    default public boolean isStickyForBlocks(World world, int X, int Y, int Z, int direction) {
        return false;
    }
    
    // This is only called after the face shared with the
    // neighbor block is already known to be sticky
    default public boolean canStickTo(World world, int X, int Y, int Z, int direction, int neighbor_id) {
        return true;
    }
    
    // Will entities be yeeted by this block?
    default public boolean isBouncyWhenMoved(int direction, int meta) {
        return false;
    }
    
    // Will entities be moved by this block?
    default public boolean isStickyForEntitiesWhenMoved(int direction, int meta) {
        return false;
    }
#endif
}