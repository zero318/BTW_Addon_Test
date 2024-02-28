package zero.test;

import btw.AddonHandler;

import net.minecraft.src.Icon;
import net.minecraft.src.Block;
import net.minecraft.src.World;
import net.minecraft.src.IBlockAccess;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;

#include "feature_flags.h"

public interface IBlockMixins {
    
    // Whether or not the block should have onNeighborBlockChange
    // called in response to comparator updates
    default public boolean getWeakChanges(World world, int x, int y, int z, int neighborId) {
        return false;
    }
    
#if ENABLE_DIRECTIONAL_UPDATES
    
    default public int updateShape(World world, int x, int y, int z, int direction, int meta) {
        return meta;
    }
    
    //default public void updateIndirectNeighbourShapes(World world, int x, int y, int z) {
    //}
#endif

#if ENABLE_BETTER_BUDDY_DETECTION
    default public boolean triggersBuddy(World world, int x, int y, int z) {
        return ((Block)(Object)this).triggersBuddy();
    }
#endif

#if ENABLE_MOVING_BLOCK_CHAINING

    public int getMobilityFlag(World world, int x, int y, int z);

    // The direction argument is intended to allow for
    // blocks that are only sticky on specific faces
    default public boolean isStickyForBlocks(World world, int x, int y, int z, int direction) {
        return false;
    }
    
    // This is only called after the face shared with the
    // neighbor block is already known to be sticky
    default public boolean canBeStuckTo(World world, int x, int y, int z, int direction, int neighborId) {
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
    
#if ENABLE_TURNTABLE_SLIME_SUPPORT
    default public boolean canTransmitRotationHorizontallyOnTurntable(World world, int x, int y, int z, int direction) {
        return ((Block)(Object)this).canTransmitRotationHorizontallyOnTurntable(world, x, y, z);
    }
#endif
    
#if ENABLE_SLIME_SUPPORTING_MORTAR_BLOCKS
    default public boolean permanentlySupportsMortarBlocks(World world, int x, int y, int z, int direction) {
        return false;
    }
#endif
#endif

#if ENABLE_MORE_RAIL_PLACEMENTS
    default public boolean canSupportRails(World world, int x, int y, int z) {
        return ((Block)(Object)this).hasLargeCenterHardPointToFacing(world, x, y, z, DIRECTION_UP);
    }
#endif

#if ENABLE_MODERN_REDSTONE_WIRE
    // Default to the old behavior for conductivity testing
    default public boolean isRedstoneConductor(IBlockAccess blockAccess, int x, int y, int z) {
        //return ((Block)(Object)this).isNormalCube(world, x, y, z);
        return blockAccess.isBlockNormalCube(x, y, z);
    }
#endif

#if ENABLE_BETTER_REDSTONE_WIRE_CONNECTIONS
    // Default to the old behavior for dust connections
    default public boolean canRedstoneConnectToSide(IBlockAccess blockAccess, int x, int y, int z, int flatDirection) {
        return ((Block)(Object)this).canProvidePower();
    }
#endif

#if ENABLE_PLATFORM_FIXES
    default public int getPlatformMobilityFlag(World world, int x, int y, int z) {
        return PLATFORM_CANNOT_MOVE;
    }
    
    default public int adjustMetadataForPlatformMove(int meta) {
        return meta;
    }
#endif

#if ENABLE_TEXTURED_BOX
    @Environment(EnvType.CLIENT)
    public Icon getIconBySidedIndex(int side, int index);
#endif

    //public void addCollisionBoxesToListForPiston(World world, int x, int y, int z, int meta, List list);
}