package zero.test.block;

import net.minecraft.src.*;

import btw.block.model.BlockModel;
import btw.block.util.RayTraceUtils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.List;

import zero.test.block.model.BufferStopModel;

#include "..\util.h"
#include "..\feature_flags.h"

#define FLAT_DIRECTION_META_OFFSET 0
#define POWERED_META_OFFSET 2

public class BufferStopBlock extends Block {
    public BufferStopBlock(int blockId) {
        super(blockId, Material.circuits);
        this.setHardness(2.0F);
        this.setAxesEffectiveOn();
        this.setStepSound(Block.soundWoodFootstep);
        this.setUnlocalizedName("buffer_stop");
        this.setCreativeTab(CreativeTabs.tabTransport);
    }
    
#if ENABLE_RAIL_BUFFER_STOP

    public static BufferStopModel model;
    public static BlockModel transformedModel;
    
    static {
        transformedModel = model = new BufferStopModel();
    }

    @Override
    public int getMobilityFlag() {
        return PISTON_CAN_PUSH;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }
    
    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }
    
#if ENABLE_MODERN_REDSTONE_WIRE
    public boolean isRedstoneConductor(IBlockAccess blockAccess, int x, int y, int z) {
        return true;
    }
#endif

/*
#if ENABLE_BETTER_REDSTONE_WIRE_CONNECTIONS
    public boolean canRedstoneConnectToSide(IBlockAccess blockAccess, int x, int y, int z, int flatDirection) {
        return true;
    }
#endif
*/
    
    @Override
    public int getRenderType() {
        return 10;
    }
    
    @Override
	public boolean canRotateOnTurntable(IBlockAccess blockAccess, int x, int y, int z) {
		return true;
	}
    
    @Override
    public int rotateMetadataAroundJAxis(int meta, boolean reverse) {
        return MERGE_META_FIELD(meta, FLAT_DIRECTION, meta + (reverse ? 1 : -1) & 3);
    }

/*
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving entity, ItemStack itemStack) {
        world.setBlockMetadataWithNotify(x, y, z, YAW_FLAT_DIRECTION(entity.rotationYaw), UPDATE_CLIENTS);
    }
*/
    @Override
    public int preBlockPlacedBy(World world, int x, int y, int z, int meta, EntityLiving entity) {
        return YAW_FLAT_DIRECTION(entity.rotationYaw);
    }
    
    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB intersectBox, List list, Entity entity) {
        AABBPool pool = AxisAlignedBB.getAABBPool();
        double dX = (double)x;
        double dY = (double)y;
        double dZ = (double)z;
        double dX2 = dX + 1.0D;
        double dZ2 = dZ + 1.0D;
        pool.getAABB(dX, dY, dZ, dX2, dY + 0.5625D, dZ2).addToListIfIntersects(intersectBox, list);
        switch (READ_META_FIELD(world.getBlockMetadata(x, y, z), FLAT_DIRECTION)) {
            case FLAT_DIRECTION_NORTH:
                dZ -= 0.125D;
                dZ2 -= 0.5D;
                break;
            case FLAT_DIRECTION_EAST:
                dX += 0.5D;
                dX2 += 0.125D;
                break;
            case FLAT_DIRECTION_SOUTH:
                dZ += 0.5D;
                dZ2 += 0.125D;
                break;
            default: // FLAT_DIRECTION_WEST
                dX -= 0.125D;
                dX2 -= 0.5D;
                break;
        }
        pool.getAABB(dX, dY + 0.5625D, dZ, dX2, dY + 1.0D, dZ2).addToListIfIntersects(intersectBox, list);
    }
    
    @Override
    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 startRay, Vec3 endRay) {
        RayTraceUtils rayTrace = new RayTraceUtils(world, x, y, z, startRay, endRay);
        
        getTransformedModelForMetadata(model, world.getBlockMetadata(x, y, z)).addToRayTrace(rayTrace);
        
        return rayTrace.getFirstIntersection();
    }

    private BlockModel getTransformedModelForMetadata(BlockModel model, int meta) {
        (transformedModel = model.makeTemporaryCopy()).rotateAroundYToFacing(FLAT_DIRECTION_TO_DIRECTION(READ_META_FIELD(meta, FLAT_DIRECTION)));
        return transformedModel;
    }
    
#define TEXTURE_INDEX_BASE_PLATE 0
#define TEXTURE_INDEX_SIDE_BOARDS 1
#define TEXTURE_INDEX_BUMPERS 2

    @Environment(EnvType.CLIENT)
    private Icon bottomTexture;
    @Environment(EnvType.CLIENT)
    private Icon bumperTexture;

    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        this.blockIcon = Block.planks.getIcon(0, 0);
        this.bottomTexture = Block.stoneDoubleSlab.getBlockTextureFromSide(1);
        this.bumperTexture = Block.cauldron.func_94375_b("cauldron_inner");
    }

#if !ENABLE_TEXTURED_BOX
    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int side, int meta) {
        switch (transformedModel.getActivePrimitiveID()) {
            case TEXTURE_INDEX_BASE_PLATE:
                return this.bottomTexture;
            case TEXTURE_INDEX_SIDE_BOARDS:
                return this.blockIcon;
            default:
                return this.bumperTexture;
        }
    }
#else
    @Environment(EnvType.CLIENT)
    public Icon getIconBySidedIndex(int side, int index) {
        switch (index) {
            case TEXTURE_INDEX_BASE_PLATE:
                return this.bottomTexture;
            case TEXTURE_INDEX_SIDE_BOARDS:
                return this.blockIcon;
            default:
                return this.bumperTexture;
        }
    }
#endif
    
    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderBlocks, int x, int y, int z) {
		//BlockModel transformedModel = ;
		
		return getTransformedModelForMetadata(model, renderBlocks.blockAccess.getBlockMetadata(x, y, z)).renderAsBlock/* WithColorMultiplier */(renderBlocks, this, x, y, z);
    }
    
    /*
    @Environment(EnvType.CLIENT)
    private boolean renderBlockBufferStop(RenderBlocks renderBlocks, int x, int y, int z) {
        
    }
    */
#endif
}