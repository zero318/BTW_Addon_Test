package zero.test.block;

import net.minecraft.src.*;

import btw.block.model.BlockModel;
import btw.block.util.RayTraceUtils;
import btw.AddonHandler;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Random;
import java.util.List;

import zero.test.block.model.ScaffoldingModel;
import zero.test.block.model.ScaffoldingBottomModel;

#include "..\util.h"
#include "..\feature_flags.h"

#define DISTANCE_META_BITS 3
#define DISTANCE_META_OFFSET 0

#define HAS_BOTTOM_META_BITS 1
#define HAS_BOTTOM_META_OFFSET 3
#define HAS_BOTTOM_META_IS_BOOL true

public class ScaffoldingBlock extends Block {
    
    public ScaffoldingBlock(int blockId) {
        super(blockId, Material.circuits);
        this.setUnlocalizedName("scaffolding");
        this.setCreativeTab(CreativeTabs.tabTransport);
    }
    
#if ENABLE_SCAFFOLDING
    
    public static BlockModel normal_model = (BlockModel)new ScaffoldingModel();
    public static BlockModel bottom_model = (BlockModel)new ScaffoldingBottomModel();
    
    @Override
    public boolean isBlockClimbable(World world, int x, int y, int z) {
        return true;
    }
    
    @Override
    public boolean isOpaqueCube() {
        return false;
    }
    
    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }
    
    @Override
    public int getMobilityFlag() {
        return PISTON_CAN_BREAK;
    }
    
    @Override
    public boolean canMobsSpawnOn(World world, int x, int y, int z) {
        return false;
    }
    
    @Override
    public boolean isFallingBlock()
    {
        return true;
    }
    
    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        if (!world.isRemote) {
            world.scheduleBlockUpdate(x, y, z, this.blockID, 1);
        }
    }
    
    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float clickX, float clickY, float clickZ, int meta) {
        int distance = this.calculateDistance(world, x, y, z);
        return distance | (this.isBottom(world, x, y, z, distance) ? 8 : 0);
    }
    
    @Override
    public boolean canBlockStay(World world, int x, int y, int z) {
        return READ_META_FIELD(world.getBlockMetadata(x, y, z), DISTANCE) < 7;
    }
    
    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        int distance = this.calculateDistance(world, x, y, z);
        if (distance == 7) {
            if (READ_META_FIELD(world.getBlockMetadata(x, y, z), DISTANCE) == 7) {
                this.checkForFall(world, x, y, z);
            } else {
                world.destroyBlock(x, y, z, true);
            }
        } else {
            world.setBlockMetadataWithNotify(x, y, z, distance | (this.isBottom(world, x, y, z, distance) ? 8 : 0), UPDATE_NEIGHBORS | UPDATE_CLIENTS);
        }
    }
    
    public int updateShape(World world, int x, int y, int z, int direction, int meta) {
        if (!world.isRemote) {
            world.scheduleBlockUpdate(x, y, z, this.blockID, 1);
        }
        return meta;
    }
    
    @Override
    public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int x, int y, int z, int facing, boolean ignoreTransparency) {
        return facing == DIRECTION_UP ||
               (
                   facing == DIRECTION_DOWN &&
                   READ_META_FIELD(blockAccess.getBlockMetadata(x, y, z), HAS_BOTTOM)
               );
    }
    
    private boolean isBottom(World world, int x, int y, int z, int distance) {
        return distance > 0 && world.getBlockId(x, y - 1, z) != this.blockID;
    }
    
    private int calculateDistance(World world, int x, int y, int z) {
        int blockId = world.getBlockId(x, y - 1, z);
        
        int distance = 7;
        if (blockId == this.blockID) {
            distance = READ_META_FIELD(world.getBlockMetadata(x, y - 1, z), DISTANCE);
        } else {
            Block block = Block.blocksList[blockId];
            if (
                !BLOCK_IS_AIR(block) &&
                block.hasLargeCenterHardPointToFacing(world, x, y, z, DIRECTION_UP, true)
            ) {
                return 0;
            }
        }
        int direction = 2;
        do {
            int nextX = x + Facing.offsetsXForSide[direction];
            int nextZ = z + Facing.offsetsZForSide[direction];
            
            if (world.getBlockId(nextX, y, nextZ) == this.blockID) {
                distance = Math.min(READ_META_FIELD(world.getBlockMetadata(nextX, y, nextZ), DISTANCE) + 1, distance);
            }
        } while (distance != 1 && DIRECTION_IS_VALID(++direction));
        return distance;
    }
    
    @Override
    public boolean canRotateOnTurntable(IBlockAccess blockAccess, int x, int y, int z) {
        return true;
    }
    
    @Override
    public boolean canTransmitRotationVerticallyOnTurntable(IBlockAccess blockAccess, int x, int y, int z) {
        return true;
    }
    
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
    }
    
    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB intersectBox, List list, Entity entity) {
        if (entity != null) {
            AABBPool pool = AxisAlignedBB.getAABBPool();
            
            double dX = (double)x;
            double dY = (double)y;
            double dZ = (double)z;
            if (
                entity.boundingBox.minY >= dY + 1.0D &&
                !entity.isSneaking()
            ) {
                pool.getAABB(dX, dY, dZ, dX + 1.0D, dY + 1.0D, dZ + 1.0D).addToListIfIntersects(intersectBox, list);
            }
            else if (
                // Metadata checks DISTANCE >= 1 && HAS_BOTTOM
                world.getBlockMetadata(x, y, z) >= 9 &&
                entity.boundingBox.minY >= dY + 0.125D
            ) {
                pool.getAABB(dX, dY, dZ, dX + 1.0D, dY + 0.125D, dZ + 1.0D).addToListIfIntersects(intersectBox, list);
            }
        }
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderBlocks, int x, int y, int z) {
        return (READ_META_FIELD(renderBlocks.blockAccess.getBlockMetadata(x, y, z), HAS_BOTTOM) ? bottom_model : normal_model).renderAsBlockWithColorMultiplier(renderBlocks, this, x, y, z);
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderBlocks, int damage, float brightness) {
        normal_model.renderAsItemBlock(renderBlocks, this, damage);
    }
    
    /*
    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        // TEMPORARY HACK TO GET RID OF WARNING IN LOG
        this.blockIcon = null;
    }
    */
#endif
}