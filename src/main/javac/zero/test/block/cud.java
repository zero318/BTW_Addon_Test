
package zero.test.block;

import btw.client.fx.BTWEffectManager;
import btw.util.MiscUtils;
import btw.world.util.BlockPos;
import btw.block.blocks.BuddyBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;

import java.util.Random;

#include "..\feature_flags.h"
#include "..\util.h"
#include "..\ids.h"

#define POWERED_META_OFFSET 0
#define DIRECTION_META_OFFSET 1

public class CUDBlock extends BuddyBlock {
    public CUDBlock(int blockId) {
        super(blockId);
        this.setUnlocalizedName("cud_block");
    }
    
    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        // HACK: 
        // Java doesn't have super.super, so there's no way of
        // calling Block.onBlockAdded without going through
        // BuddyBlock.onBlockAdded, which schedules an update.
        // So instead we're just assuming that Block.onBlockAdded
        // is still empty and doing nothing.
    }
    
    @Override
    public int onPreBlockPlacedByPiston(World world, int x, int y, int z, int meta, int direction) {
        return meta;
    }
    
    @Override
    public boolean triggersBuddy() {
        return true;
    }
    
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborId) {
        // IDK why this check works for preventing a crash, but it does
        if (neighborId != CUD_BLOCK_ID) {
            int meta = world.getBlockMetadata(x, y, z);
            if (!READ_META_FIELD(meta, POWERED)) {
                Block neighbor_block = blocksList[neighborId];
                
                if (
                    !BLOCK_IS_AIR(neighbor_block) &&
                    neighbor_block.hasComparatorInputOverride() &&
                    !world.isUpdatePendingThisTickForBlock(x, y, z, this.blockID)
                ) {
                    // minimal delay when triggered to avoid notfying neighbors of change in same tick
                    // that they are notifying of the original change. Not doing so causes problems 
                    // with some blocks (like ladders) that haven't finished initializing their state 
                    // on placement when they send out the notification
                    
                    world.scheduleBlockUpdate(x, y, z, this.blockID, 1); 
                }
            }
        }
    }
    
    public boolean getWeakChanges(World world, int x, int y, int z, int neighborId) {
        return true;
    }
    
#if ENABLE_BETTER_REDSTONE_WIRE_CONNECTIONS
    public boolean canRedstoneConnectToSide(IBlockAccess blockAccess, int x, int y, int z, int flatDirection) {
        return OPPOSITE_DIRECTION(READ_META_FIELD(blockAccess.getBlockMetadata(x, y, z), DIRECTION)) == Direction.directionToFacing[flatDirection];
    }
#endif
    

    @Environment(EnvType.CLIENT)
    private Icon texture_front;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        super.registerIcons(register);

        this.texture_front = register.registerIcon("cud_block_front");
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int side, int meta) {
        // item render
        return side == 3 ? this.texture_front : this.blockIcon;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getBlockTexture(IBlockAccess blockAccess, int x, int y, int z, int side) {
        
        if (this.isRedstoneOn(blockAccess, x, y, z)) {
            // Use original powered textures
            return super.getBlockTexture(blockAccess, x, y, z, side);
        }
        
        return getFacing(blockAccess, x, y, z) == side
                ? this.texture_front
                : this.blockIcon;
    }  
}
