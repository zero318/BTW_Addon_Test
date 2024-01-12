package zero.test.mixin;

import net.minecraft.src.*;

import btw.block.blocks.*;
import btw.AddonHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Random;
import java.util.List;

import zero.test.IBlockMixins;
import zero.test.IEntityMixins;

#include "../util.h"
#include "../feature_flags.h"
#include "../ids.h"

#define addCollisionBoxesToList(...) method_413(__VA_ARGS__)

@Mixin(CisternBlock.class)
public class CisternBlockMixins {
#if ENABLE_HOLLOW_COLLISION_BOXES
    
    @Overwrite(remap=false)
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB intersectBox, List list, Entity entity) {
    	AABBPool pool = AxisAlignedBB.getAABBPool();
        double dX = (double)x;
        double dY = (double)y;
        double dZ = (double)z;
        double dX2 = dX + 1.0D;
        double dY2 = dY + 1.0D;
        double dZ2 = dZ + 1.0D;
        pool.getAABB(dX, dY, dZ, dX2, dY + 0.3125D, dZ2).addToListIfIntersects(intersectBox, list);
        if (
            entity == null ||
            ((IEntityMixins)entity).getPistonDirection() != DIRECTION_WEST
        ) {
            pool.getAABB(dX, dY, dZ, dX + 0.125D, dY2, dZ2).addToListIfIntersects(intersectBox, list);
        }
        if (
            entity == null ||
            ((IEntityMixins)entity).getPistonDirection() != DIRECTION_NORTH
        ) {
            pool.getAABB(dX, dY, dZ, dX2, dY2, dZ + 0.125D).addToListIfIntersects(intersectBox, list);
        }
        if (
            entity == null ||
            ((IEntityMixins)entity).getPistonDirection() != DIRECTION_EAST
        ) {
            pool.getAABB(dX + 0.875D, dY, dZ, dX2, dY2, dZ2).addToListIfIntersects(intersectBox, list);
        }
        if (
            entity == null ||
            ((IEntityMixins)entity).getPistonDirection() != DIRECTION_SOUTH
        ) {
            pool.getAABB(dX, dY, dZ + 0.875D, dX2, dY2, dZ2).addToListIfIntersects(intersectBox, list);
        }
    }
#endif
}