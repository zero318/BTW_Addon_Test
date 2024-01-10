package zero.test.mixin;

import net.minecraft.src.Block;
import net.minecraft.src.World;
import net.minecraft.src.BlockPistonBase;
import net.minecraft.src.*;

import btw.block.blocks.PistonBlockBase;
import btw.block.blocks.PistonBlockMoving;
import btw.item.util.ItemUtils;
import btw.AddonHandler;
import btw.BTWAddon;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

import zero.test.IBlockMixins;
import zero.test.mixin.IPistonBaseAccessMixins;
import zero.test.IWorldMixins;
import zero.test.IBlockEntityPistonMixins;

import java.util.List;
import java.util.ArrayList;

#include "..\func_aliases.h"
#include "..\feature_flags.h"
#include "..\util.h"

#define DIRECTION_META_OFFSET 0
#define STICKY_META_OFFSET 3
#define STICKY_META_BITS 1
#define STICKY_IS_BOOL true

@Mixin(PistonBlockMoving.class)
public class BlockPistonMovingMixins {
#if ENABLE_BETTER_BUDDY_DETECTION
    //@Override
    public boolean triggersBuddy() {
        return false;
    }
#endif
    //@Overwrite
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB maskBox, List list, Entity entity) {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityPiston) {
            ((IBlockEntityPistonMixins)(Object)tileEntity).getCollisionList(maskBox, list);
        }
    }
    
#if ENABLE_MORE_MOVING_BLOCK_HARDPOINTS
    public boolean hasSmallCenterHardPointToFacing(IBlockAccess blockAccess, int x, int y, int z, int direction, boolean ignoreTransparency) {
        TileEntity tileEntity = blockAccess.getBlockTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityPiston) {
            return ((IBlockEntityPistonMixins)(Object)tileEntity).hasSmallCenterHardPointToFacing(x, y, z, direction, ignoreTransparency);
        }
        return false;
    }

    public boolean hasCenterHardPointToFacing(IBlockAccess blockAccess, int x, int y, int z, int direction, boolean ignoreTransparency) {
        TileEntity tileEntity = blockAccess.getBlockTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityPiston) {
            return ((IBlockEntityPistonMixins)(Object)tileEntity).hasCenterHardPointToFacing(x, y, z, direction, ignoreTransparency);
        }
        return false;
    }
#endif
    
    public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int x, int y, int z, int direction, boolean ignoreTransparency) {
        TileEntity tileEntity = blockAccess.getBlockTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityPiston) {
            return ((IBlockEntityPistonMixins)(Object)tileEntity).hasLargeCenterHardPointToFacing(x, y, z, direction, ignoreTransparency);
        }
        return false;
    }
    
    // If the whole point of this is to test the block bounds,
    // maybe overriding to use raw collision isn't a good idea?
    /*
    public MovingObjectPosition collisionRayTraceVsBlockBounds(World world, int x, int y, int z, Vec3 startRay, Vec3 endRay) {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityPiston) {
            List<AxisAlignedBB> collisionList = new ArrayList();
            
            AxisAlignedBB fakeMask = AxisAlignedBB.getAABBPool().getAABB(x - 1.0D, y - 1.0D, z - 1.0D, x + 2.0D, y + 2.0D, z + 2.0D);
            ((IBlockEntityPistonMixins)(Object)tileEntity).getCollisionList(fakeMask, collisionList);
            
            for (AxisAlignedBB collisionBox : collisionList) {
                MovingObjectPosition collisionPoint = collisionBox.calculateIntercept(startRay, endRay);
                if (collisionPoint != null) {
                    collisionPoint.blockX = x;
                    collisionPoint.blockY = y;
                    collisionPoint.blockZ = z;
                    return collisionPoint;
                }
            }
        }
        return null;
    }
    */
    
    // This fixes the selection box of retracting pistons
    // to not derp out and extend behind the base.
    @Overwrite
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(IBlockAccess blockAccess, int x, int y, int z) {
        TileEntity tileEntity = blockAccess.getBlockTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityPiston) {
            return ((IBlockEntityPistonMixins)(Object)tileEntity).getBlockBoundsFromPoolBasedOnState();
        }
        return null;
    }
}