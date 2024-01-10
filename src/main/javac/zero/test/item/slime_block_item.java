package zero.test.item;

import net.minecraft.src.*;

import btw.item.items.PlaceAsBlockItem;
import btw.AddonHandler;

import zero.test.block.SlimeBlock;

import zero.test.IEntityMixins;

import java.util.List;

#include "..\feature_flags.h"
#include "..\util.h"

public class SlimeBlockItem extends ItemBlock {
    public SlimeBlockItem(int block_id) {
        super(block_id);
    }
    
    @Override
	public boolean onItemUsedByBlockDispenser(ItemStack stack, World world, int x, int y, int z, int direction) {
        if (super.onItemUsedByBlockDispenser(stack, world, x, y, z, direction)) {
            x += Facing.offsetsXForSide[direction];
            y += Facing.offsetsYForSide[direction];
            z += Facing.offsetsZForSide[direction];
            
            AxisAlignedBB bounceMask = AxisAlignedBB.getAABBPool().getAABB(x, y, z, x + 1.0D, y + 1.0D, z + 1.0D);
            
            List<Entity> entityList = world.getEntitiesWithinAABBExcludingEntity((Entity)null, bounceMask);
            for (Entity entity : entityList) {
                if (
                    PISTON_CAN_BOUNCE_ENTITY(((IEntityMixins)entity).getPistonMobilityFlags(direction)) &&
                    entity.getVisualBoundingBox() != null
                ) {
                    double entityBounceMultiplier = ((IEntityMixins)entity).getPistonBounceMultiplier(direction);
                    switch (DIRECTION_AXIS(direction)) {
                        case AXIS_X: {
                            double tempMotion = entity.motionX;
                            double dX = (double)Facing.offsetsXForSide[direction];
                            if (Math.abs(tempMotion) < Math.abs(dX)) {
                                tempMotion = dX;
                            }
                            entity.motionX = tempMotion * entityBounceMultiplier;
                            break;
                        }
                        case AXIS_Y: {
                            double tempMotion = entity.motionY;
                            double dY = (double)Facing.offsetsYForSide[direction];
                            if (Math.abs(tempMotion) < Math.abs(dY)) {
                                tempMotion = dY;
                            }
                            entity.motionY = tempMotion * entityBounceMultiplier;
                            break;
                        }
                        default: {
                            double tempMotion = entity.motionZ;
                            double dZ = (double)Facing.offsetsZForSide[direction];
                            if (Math.abs(tempMotion) < Math.abs(dZ)) {
                                tempMotion = dZ;
                            }
                            entity.motionZ = tempMotion * entityBounceMultiplier;
                            break;
                        }
                    }
                }
                // Hopefully this just takes care of 
                entity.moveEntity(0.0D, 0.0D, 0.0D);
            }
            AddonHandler.logMessage("bounce");
            return true;
        }
        return false;
    }
}