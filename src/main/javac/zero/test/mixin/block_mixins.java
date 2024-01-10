package zero.test.mixin;

import net.minecraft.src.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import zero.test.IBlockMixins;
import zero.test.IWorldMixins;

import java.util.List;

#include "..\feature_flags.h"
#include "..\util.h"

@Mixin(Block.class)
public class BlockMixins implements IBlockMixins {

    @Overwrite
    public boolean hasLargeCenterHardPointToFacing(IBlockAccess block_access, int X, int Y, int Z, int direction, boolean ignore_transparency) {
        // Skip the extra block lookup from chaining through world
        // since the block is already known.
		return ((Block)(Object)this).isNormalCube(block_access, X, Y, Z);
	}
    
#if ENABLE_MODERN_SUPPORT_LOGIC == MODERN_SUPPORT_LOGIC_GLOBAL_ALL
    /*
        THESE IGNORE THE WorldUtils VERSIONS OF HARDPOINT CHECKS
        
        This was done deliberately because calls to the WorldUtils functions
        only seem to happen in cases where checking for transparency makes sense
        or an explicit true value is passed.
    */

    /*
        Effects:
    */
    @Overwrite
    public boolean hasSmallCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing) {
        return ((Block)(Object)this).hasSmallCenterHardPointToFacing(blockAccess, i, j, k, iFacing, true);
    }
    
    /*
        Effects:
    */
    @Overwrite
    public boolean hasCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing) {
		return ((Block)(Object)this).hasCenterHardPointToFacing(blockAccess, i, j, k, iFacing, true);
	}
    
    /*
        Effects:
        - Most block placement works on transparent blocks (maybe including fire? Might disable that)
        - Wall logic doesn't ignore transparent blocks
        - doesBlockHaveSolidTopSurface allows transparent blocks
         - Water makes drip particles when above transparent blocks
         - Leaves don't make drip particles in the rain when above transparent blocks
         - Dismounting an entity considers transparent blocks as valid
         - Entities following the player can teleport onto transparent blocks
         - Players can respawn from beacons on transparent blocks
         - Iron Golems can spawn on transparent blocks (assuming BTW didn't screw with the code elsewhere)
         - World bonus chests can spawn on transparent blocks
         - Ash can be deposited on transparent blocks
         
        Rendering bugs to fix:
        - Redstone dust
        - Repeaters
        - Comparators
    */
    @Overwrite
    public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int i, int j, int k, int iFacing) {
		return ((Block)(Object)this).hasLargeCenterHardPointToFacing(blockAccess, i, j, k, iFacing, true);
	}
    
    /*
        Effects:
        - Prevent above changes from impacting grass
    */
    @Overwrite
    public boolean getCanGrassGrowUnderBlock(World world, int i, int j, int k, boolean bGrassOnHalfSlab) {
		return bGrassOnHalfSlab || !((Block)(Object)this).hasLargeCenterHardPointToFacing(world, i, j, k, 0, false);
	}
    
    /*
        Check for effects:
        - isSnowCoveringTopSurface
        - hasFallingBlockRestingOn
    */
#endif
    
    // Extra variant of getMobilityFlag that allows
    // changing the result based on metadata.
    @Override
    public int getMobilityFlag(World world, int X, int Y, int Z) {
        return ((Block)(Object)this).getMobilityFlag();
    }
    
    /*
    public void addCollisionBoxesToListForPiston(TileEntityPiston pistonEntity, List list) {
        double x = (double)pistonEntity.xCoord;
        double y = (double)pistonEntity.yCoord;
        double z = (double)pistonEntity.zCoord;
        
        AxisAlignedBB fakeMask = AxisAlignedBB.getAABBPool().getAABB(x - 1.0D, y - 1.0D, z - 1.0D, x + 2.0D, y + 2.0D, z + 2.0D);
        
        int prevMeta = pistonEntity.worldObj.getBlockMetadata(pistonEntity.xCoord, pistonEntity.yCoord, pistonEntity.zCoord);
        pistonEntity.worldObj.setBlockMetadataWithNotify(x, y, z, pistonEntity.getBlockMetadata(), UPDATE_INVISIBLE | UPDATE_KNOWN_SHAPE | UPDATE_SUPPRESS_LIGHT);
        ((Block)(Object)this).addCollisionBoxesToList(pistonEntity.worldObj, x, y, z, fakeMask, list, (Entity)null);
        pistonEntity.worldObj.setBlockMetadataWithNotify(x, y, z, prevMeta, UPDATE_INVISIBLE | UPDATE_KNOWN_SHAPE | UPDATE_SUPPRESS_LIGHT);
    }
    */
    
#if ENABLE_DIRECTIONAL_UPDATES
    @Overwrite
    public boolean rotateAroundJAxis(World world, int X, int Y, int Z, boolean reverse) {
        int prev_meta = world.getBlockMetadata(X, Y, Z);
        
        Block self = (Block)(Object)this;

        int new_meta = self.rotateMetadataAroundJAxis(prev_meta, reverse);

        if (prev_meta != new_meta) {
            new_meta = ((IWorldMixins)world).updateFromNeighborShapes(X, Y, Z, self.blockID, new_meta);
            
            world.setBlockMetadataWithNotify(X, Y, Z, new_meta, UPDATE_NEIGHBORS | UPDATE_CLIENTS);

            return true;
        }

        return false;
    }
#endif

#if ENABLE_CONNECTED_BLOCK_TWEAKS
    // Reduced hardpoint requirement from large to medium.
    // This allows fences to stay connected to extended piston sides.
    @Overwrite
    public boolean shouldFenceConnectToThisBlockToFacing(IBlockAccess blockAccess, int x, int y, int z, int facing) {
        Block self = (Block)(Object)this;
		return self.isNormalCube(blockAccess, x, y, z) || self.isFence(blockAccess.getBlockMetadata(x, y, z)) || self.hasCenterHardPointToFacing(blockAccess, x, y, z, facing, true);
	}
	// Reduced hardpoint requirement from large to small.
    // This allows panes to stay connected to extended piston sides.
    @Overwrite
	public boolean shouldPaneConnectToThisBlockToFacing(IBlockAccess blockAccess, int x, int y, int z, int facing) {
        Block self = (Block)(Object)this;
		return self.isNormalCube(blockAccess, x, y, z) || self.hasSmallCenterHardPointToFacing(blockAccess, x, y, z, facing, true);
	}
#endif
}