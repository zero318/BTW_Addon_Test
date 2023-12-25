package zero.test.block;

#include "..\util.h"
#include "..\ids.h"

import net.minecraft.src.*;

import btw.AddonHandler;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

#define DIRECTION_META_OFFSET 0

public class DeadCoralFan extends Block {
    public DeadCoralFan(int block_id) {
        super(block_id, Material.rock);
        this.setUnlocalizedName("dead_coral_fan");
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }
    
    public boolean isOpaqueCube() {
        return false;
    }
    
    public boolean renderAsNormalBlock() {
        return false;
    }
    
    public int updateShape(World world, int X, int Y, int Z, int direction, int meta) {
        AddonHandler.logMessage("CORAL META "+meta);
        int attached_face = READ_META_FIELD(meta, DIRECTION);
        if (attached_face != 7) {
            X -= Facing.offsetsXForSide[attached_face];
            Y -= Facing.offsetsYForSide[attached_face];
            Z -= Facing.offsetsZForSide[attached_face];
            int attached_block_id = world.getBlockId(X, Y, Z);
            Block attached_block = Block.blocksList[attached_block_id];
            if (
                !BLOCK_IS_AIR(attached_block) &&
                !attached_block.hasLargeCenterHardPointToFacing(world, X, Y, Z, OPPOSITE_DIRECTION(attached_face))
            ) {
                return -1;
            }
        }
        return meta;
    }
    
    @Override
    public int onBlockPlaced(World world, int X, int Y, int Z, int side, float hitX, float hitY, float hitZ, int meta) {
        int ret = -1;
        int neighbor_id;
        
        //AddonHandler.logMessage("CORAL SIDE "+side);
        //AddonHandler.logMessage("CORAL META "+meta);
        
        
        X -= Facing.offsetsXForSide[side];
        Y -= Facing.offsetsYForSide[side];
        Z -= Facing.offsetsZForSide[side];
        Block neighbor_block = Block.blocksList[world.getBlockId(X, Y, Z)];
        if (
            !BLOCK_IS_AIR(neighbor_block) &&
            neighbor_block.hasLargeCenterHardPointToFacing(world, X, Y, Z, OPPOSITE_DIRECTION(side))
        ) {
            return side;
        }
        return -1;
    }
    
    @Environment(EnvType.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int X, int Y, int Z) {
        this.setBlockBoundsBasedOnState(world, X, Y, Z);
        return super.getSelectedBoundingBoxFromPool(world, X, Y, Z);
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int X, int Y, int Z) {
        this.setBlockBoundsBasedOnState(world, X, Y, Z);
        return super.getCollisionBoundingBoxFromPool(world, X, Y, Z);
    }
    
    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess block_access, int X, int Y, int Z) {
        this.setBlockBoundsForBlockRender(block_access.getBlockMetadata(X, Y, Z));
    }
    
    public void setBlockBoundsForBlockRender(int meta) {
        switch (READ_META_FIELD(meta, DIRECTION)) {
            case DIRECTION_DOWN:
                this.setBlockBounds(2.0f, 0.0f, 2.0f, 14.0f, 4.0f, 14.0f);
                return;
            case DIRECTION_UP:
                this.setBlockBounds(2.0f, 12.0f, 2.0f, 14.0f, 16.0f, 14.0f);
                return;
            case DIRECTION_NORTH:
                this.setBlockBounds(0.0f, 4.0f, 5.0f, 16.0f, 12.0f, 16.0f);
                return;
            case DIRECTION_SOUTH:
                this.setBlockBounds(0.0f, 4.0f, 0.0f, 16.0f, 12.0f, 11.0f);
                return;
            case DIRECTION_WEST:
                this.setBlockBounds(5.0f, 4.0f, 0.0f, 16.0f, 12.0f, 16.0f);
                return;
            default:
                this.setBlockBounds(0.0f, 4.0f, 0.0f, 11.0f, 12.0f, 16.0f);
                return;
        }
    }
}