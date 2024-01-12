package zero.test.block;

#include "..\util.h"
#include "..\ids.h"

import net.minecraft.src.*;

import btw.AddonHandler;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Random;

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
    
    @Override
    protected boolean canSilkHarvest() {
        return true;
    }
    
    @Override
    public int idDropped(int meta, Random rand, int fortuneModifier) {
        return -1;
    }
    
    public int updateShape(World world, int x, int y, int z, int direction, int meta) {
        int attachedFace = READ_META_FIELD(meta, DIRECTION);
        if (DIRECTION_IS_VALID(attachedFace)) {
            x -= Facing.offsetsXForSide[attachedFace];
            y -= Facing.offsetsYForSide[attachedFace];
            z -= Facing.offsetsZForSide[attachedFace];
            Block attachedBlock = Block.blocksList[world.getBlockId(x, y, z)];
            if (
                BLOCK_IS_AIR(attachedBlock) ||
                !attachedBlock.hasLargeCenterHardPointToFacing(world, x, y, z, OPPOSITE_DIRECTION(attachedFace))
            ) {
                return -1;
            }
        }
        return meta;
    }
    
    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        int direction = 0;
        do {
            int testX = x - Facing.offsetsXForSide[direction];
            int testY = y - Facing.offsetsYForSide[direction];
            int testZ = z - Facing.offsetsZForSide[direction];
            Block neighborBlock = Block.blocksList[world.getBlockId(testX, testY, testZ)];
            if (
                !BLOCK_IS_AIR(neighborBlock) &&
                neighborBlock.hasLargeCenterHardPointToFacing(world, testX, testY, testZ, OPPOSITE_DIRECTION(direction))
            ) {
                return true;
            }
        } while (DIRECTION_IS_VALID(++direction));
        return false;
    }
    
    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta) {
        x -= Facing.offsetsXForSide[side];
        y -= Facing.offsetsYForSide[side];
        z -= Facing.offsetsZForSide[side];
        Block neighborBlock = Block.blocksList[world.getBlockId(x, y, z)];
        if (
            !BLOCK_IS_AIR(neighborBlock) &&
            neighborBlock.hasLargeCenterHardPointToFacing(world, x, y, z, OPPOSITE_DIRECTION(side))
        ) {
            return side;
        }
        return -1;
    }
    
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
    }
    
    @Override
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(IBlockAccess blockAccess, int x, int y, int z) {
        switch (READ_META_FIELD(blockAccess.getBlockMetadata(x, y, z), DIRECTION)) {
            case DIRECTION_DOWN:
                return AxisAlignedBB.getAABBPool().getAABB(0.125D, 0.75D, 0.125D, 0.875D, 1.0D, 0.875D);
            case DIRECTION_UP:
                return AxisAlignedBB.getAABBPool().getAABB(0.125D, 0.0D, 0.125D, 0.875D, 0.25D, 0.875D);
            case DIRECTION_NORTH:
                return AxisAlignedBB.getAABBPool().getAABB(0.0D, 0.25D, 0.3125D, 1.0D, 0.75D, 1.0D);
            case DIRECTION_SOUTH:
                return AxisAlignedBB.getAABBPool().getAABB(0.0D, 0.25D, 0.0D, 1.0D, 0.75D, 0.6875D);
            case DIRECTION_WEST:
                return AxisAlignedBB.getAABBPool().getAABB(0.3125D, 0.25D, 0.0D, 1.0D, 0.75D, 1.0D);
            default:
                return AxisAlignedBB.getAABBPool().getAABB(0.0D, 0.25D, 0.0D, 0.6875D, 0.75D, 1.0D);
        }
    }
}