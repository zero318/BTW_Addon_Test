
package zero.test.block;

import btw.client.fx.BTWEffectManager;
import btw.util.MiscUtils;
import btw.world.util.BlockPos;
import btw.block.blocks.BuddyBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import btw.AddonHandler;

import java.util.Random;

#include "..\util.h"
#include "..\ids.h"

#define POWERED_META_OFFSET 0
#define DIRECTION_META_OFFSET 1

public class ObserverBlock extends BuddyBlock {
    public ObserverBlock(int block_id) {
        super(block_id);
        this.setUnlocalizedName("observer");
        this.setTickRandomly(false);
    }
    
    @Override
    public int tickRate(World world) {
        return 2;
    }
    
    @Override
	public boolean triggersBuddy() {
		return true;
	}
    
    public boolean caresAboutUpdateDirection() {
        return true;
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
    
    // Power: Down  0
    //        Up    1
    //        North 6
    //        South 4
    //        West  10
    //        East  8
    
    @Override
    public void onNeighborBlockChange(World world, int X, int Y, int Z, int neighbor_id) {
        int update_direction = neighbor_id >>> UPDATE_DIRECTION_OFFSET;
        int meta = world.getBlockMetadata(X, Y, Z);
        if (update_direction == (meta | 1)) {
            if (!READ_META_FIELD(meta, POWERED)) {
                Block neighborBlock = blocksList[neighbor_id & BLOCK_ID_MASK];
                if (neighborBlock != null && !world.isUpdatePendingThisTickForBlock(X, Y, Z, blockID)) {
                    world.scheduleBlockUpdate(X, Y, Z, blockID, 2); 
                }
            }
        }
    }
    
    // This just gets rid of the clicking sound
    @Override
    public void setBlockRedstoneOn(World world, int i, int j, int k, boolean bOn) {
    	if (bOn != isRedstoneOn(world, i, j, k) ) {
	    	int iMetaData = world.getBlockMetadata(i, j, k);
	    	
	    	if ( bOn ) {
	    		iMetaData = iMetaData | 1;
	    	}
	    	else {
	    		iMetaData = iMetaData & (~1);
	    	}
	    	
	        world.setBlockMetadataWithClient( i, j, k, iMetaData );
	        
	        // only notify on the output side to prevent weird shit like doors auto-closing when the block
	        // goes off
	        
	        int iFacing = this.getFacing(world, i, j, k);
	        
	        notifyNeigborsToFacingOfPowerChange(world, i, j, k, iFacing);
	        
	        // the following forces a re-render (for texture change)
	        
	        world.markBlockRangeForRenderUpdate( i, j, k, i, j, k );    	
        }
    }
}