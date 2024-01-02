
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
    public void onNeighborBlockChange(World world, int X, int Y, int Z, int neighbor_id) {
    }
    
    public int updateShape(World world, int X, int Y, int Z, int direction, int meta) {
        int update_direction = READ_META_FIELD(meta, DIRECTION);
        if (update_direction == OPPOSITE_DIRECTION(direction)) {
            if (!world.isUpdateScheduledForBlock(X, Y, Z, this.blockID)) {
                world.scheduleBlockUpdate(X, Y, Z, this.blockID, 2);
            }
        } //else {
            //AddonHandler.logMessage("Observer fail "+OPPOSITE_DIRECTION(direction)+" != "+update_direction);
        //}
        return meta;
    }
    
    @Override
    public void updateTick(World world, int X, int Y, int Z, Random random) {
        int meta = world.getBlockMetadata(X, Y, Z);
        
        if (READ_META_FIELD(meta, POWERED)) {
            world.setBlockMetadataWithClient(X, Y, Z, MERGE_META_FIELD(meta, POWERED, false));
        } else {
            world.setBlockMetadataWithClient(X, Y, Z, MERGE_META_FIELD(meta, POWERED, true));
            world.scheduleBlockUpdate(X, Y, Z, this.blockID, 2); 
        }
        
        //notifyNeigborsToFacingOfPowerChange(world, X, Y, Z, READ_META_FIELD(meta, DIRECTION));
        
        int direction = READ_META_FIELD(meta, DIRECTION);
        
        X += Facing.offsetsXForSide[direction];
        Y += Facing.offsetsYForSide[direction];
        Z += Facing.offsetsZForSide[direction];
        
        int neighbor_id = world.getBlockId(X, Y, Z);
        Block neighbor_block = Block.blocksList[neighbor_id];
        if (!BLOCK_IS_AIR(neighbor_block)) {
            neighbor_block.onNeighborBlockChange(world, X, Y, Z, this.blockID);
        }
        world.notifyBlocksOfNeighborChange(X, Y, Z, this.blockID, OPPOSITE_DIRECTION(direction));
    }
    
    // This just gets rid of the clicking sound
    @Override
    public void setBlockRedstoneOn(World world, int X, int Y, int Z, boolean is_activated) {
        if (is_activated != isRedstoneOn(world, X, Y, Z)) {
            int meta = world.getBlockMetadata(X, Y, Z);
            
            if (is_activated) {
                meta |= 1;
            }
            else {
                meta &= ~1;
            }
            
            world.setBlockMetadataWithClient(X, Y, Z, meta);
            
            // only notify on the output side to prevent weird shit like doors auto-closing when the block
            // goes off
            
            int direction = this.getFacing(world, X, Y, Z);
            
            notifyNeigborsToFacingOfPowerChange(world, X, Y, Z, direction);
            
            // the following forces a re-render (for texture change)
            
            world.markBlockRangeForRenderUpdate(X, Y, Z, X, Y, Z);    	
        }
    }
    
    /*@Override
    public int onPreBlockPlacedByPiston(World world, int X, int Y, int Z, int meta, int direction) {
        return meta;
    }*/
    
    // Maybe this will prevent it getting stuck on when moved?
    @Override
    public int adjustMetadataForPistonMove(int meta) {
        return MERGE_META_FIELD(meta, POWERED, false);
    }
    
    @Environment(EnvType.CLIENT)
    protected Icon texture_back_off;
    @Environment(EnvType.CLIENT)
    protected Icon texture_back_on;
    @Environment(EnvType.CLIENT)
    protected Icon texture_front;
    @Environment(EnvType.CLIENT)
    protected Icon texture_side;
    @Environment(EnvType.CLIENT)
    protected Icon texture_top;
    
    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        super.registerIcons(register);

        this.texture_back_off = register.registerIcon("observer_back");
        this.texture_back_on = register.registerIcon("observer_back_on");
        this.texture_front = register.registerIcon("observer_front");
        this.texture_side = register.registerIcon("observer_side");
        this.texture_top = register.registerIcon("observer_top");
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int side, int meta) {
        // Why are these different from the regular block texture directions?
        switch (side) {
            case DIRECTION_DOWN:
            case DIRECTION_UP:
                return this.texture_top;
            case DIRECTION_NORTH:
                return this.texture_back_off;
            case DIRECTION_SOUTH:
                return this.texture_front;
            default:
                return this.texture_side;
        }
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Icon getBlockTexture(IBlockAccess block_access, int X, int Y, int Z, int side) {
        int meta = block_access.getBlockMetadata(X, Y, Z);
        int facing = READ_META_FIELD(meta, DIRECTION);
        
        if (facing == side) {
            return READ_META_FIELD(meta, POWERED)
                    ? this.texture_back_on
                    : this.texture_back_off;
        }
        if (facing == OPPOSITE_DIRECTION(side)) {
            return this.texture_front;
        }
        
        if (DIRECTION_AXIS(facing) != AXIS_Y) {
            return DIRECTION_AXIS(side) == AXIS_Y ? this.texture_top : this.texture_side;
        }
        // When facing up, the non-arrow side should be on E/W
        return DIRECTION_AXIS(side) == AXIS_X ? this.texture_top : this.texture_side;
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks render, int X, int Y, int Z) {
        // IDK why these rotation values work
        switch (READ_META_FIELD(render.blockAccess.getBlockMetadata(X, Y, Z), DIRECTION)) {
            case DIRECTION_DOWN:
                render.setUVRotateEast(3);
                render.setUVRotateWest(3);
                render.setUVRotateNorth(1);
                render.setUVRotateSouth(2);
                render.setUVRotateTop(3);
                render.setUVRotateBottom(3);
                break;
            case DIRECTION_UP:
                render.setUVRotateNorth(2);
                render.setUVRotateSouth(1);
                break;
            case DIRECTION_NORTH:
                break;
            case DIRECTION_SOUTH:
                render.setUVRotateTop(3);
                render.setUVRotateBottom(3);
                break;
            case DIRECTION_WEST:
                render.setUVRotateTop(2);
                render.setUVRotateBottom(1);
                break;
            default:
                render.setUVRotateTop(1);
                render.setUVRotateBottom(2);
                break;
        }
        
        render.setRenderBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        boolean ret = render.renderStandardBlock(this, X, Y, Z);
        render.clearUVRotation();
        return ret;
    }
}