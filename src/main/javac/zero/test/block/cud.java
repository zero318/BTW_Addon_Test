
package zero.test.block;

import btw.client.fx.BTWEffectManager;
import btw.util.MiscUtils;
import btw.world.util.BlockPos;
import btw.block.blocks.BuddyBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

#include "..\util.h"
#include "..\ids.h"

#define POWERED_META_OFFSET 0
#define DIRECTION_META_OFFSET 1

public class CUDBlock extends BuddyBlock {
    public CUDBlock(int block_id) {
        super(block_id);
        this.setUnlocalizedName("cud_block");
    }
    
    @Override
    public void onBlockAdded(World world, int X, int Y, int Z) {
        // HACK: 
        // Java doesn't have super.super, so there's no way of
        // calling Block.onBlockAdded without going through
        // BuddyBlock.onBlockAdded, which schedules an update.
        // So instead we're just assuming that Block.onBlockAdded
        // is still empty and doing nothing.
    }
    
    @Override
    public int onPreBlockPlacedByPiston(World world, int X, int Y, int Z, int meta, int direction) {
		return meta;
	}
    
    @Override
	public boolean triggersBuddy() {
		return true;
	}
    
    @Override
    public void onNeighborBlockChange(World world, int X, int Y, int Z, int neighbor_id) {
    	// IDK why this check works for preventing a crash, but it does
        if (neighbor_id != CUD_BLOCK_ID) {
            int meta = world.getBlockMetadata(X, Y, Z);
            if (!READ_META_FIELD(meta, POWERED)) {
                Block neighborBlock = blocksList[neighbor_id];
                
                if (
                    neighborBlock != null &&
                    neighborBlock.hasComparatorInputOverride() &&
                    !world.isUpdatePendingThisTickForBlock(X, Y, Z, blockID)
                ) {
                    // minimal delay when triggered to avoid notfying neighbors of change in same tick
                    // that they are notifying of the original change. Not doing so causes problems 
                    // with some blocks (like ladders) that haven't finished initializing their state 
                    // on placement when they send out the notification
                    
                    world.scheduleBlockUpdate(X, Y, Z, blockID, 1); 
                }
            }
        }
    }
    
    //@Override
    public boolean getWeakChanges(World world, int X, int Y, int Z, int neighbor_id) {
        return true;
    }
}
