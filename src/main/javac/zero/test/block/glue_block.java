package zero.test.block;

import net.minecraft.src.*;

import btw.block.BTWBlocks;
import btw.block.blocks.AestheticOpaqueBlock;
import btw.AddonHandler;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import zero.test.sound.ZeroTestSounds;

#include "..\util.h"
#include "..\feature_flags.h"
#include "..\ids.h"

public class GlueBlock extends Block {
    public GlueBlock(int blockId) {
        super(blockId, Material.grass);
        this.setHardness(0.01F);
        this.setResistance(0.0F);
        this.setUnlocalizedName("glue_block");
        this.setShovelsEffectiveOn(true);
        this.setLightOpacity(1);
        this.stepSound = ZeroTestSounds.slime_step_sound;
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }
    
    public int getMobilityFlag() {
        return PISTON_CAN_PUSH;
    }
    
#if DISABLE_SLIME_AND_GLUE_PISTON_SHOVEL
    public boolean canBePistonShoveled(World world, int x, int y, int z) {
		return false;
	}
#endif
    
    public boolean isStickyForBlocks(World world, int x, int y, int z, int direction) {
        return true;
    }
    
    public boolean isStickyForEntitiesWhenMoved(int direction, int meta) {
        return true;
    }
    
    public boolean canBeStuckTo(World world, int x, int y, int z, int direction, int neighborId) {
        return neighborId != SLIME_BLOCK_ID;
    }
    
    
    @Override
    public boolean isOpaqueCube() {
        return false;
    }
    
    // Force enable conductivity
    @Override
    public boolean isNormalCube(IBlockAccess blockAccess, int x, int y, int z) {
        return true;
    }

    @Override
    public boolean hasMortar(IBlockAccess blockAccess, int x, int y, int z) {
        return true;
    }
    
#if ENABLE_MODERN_REDSTONE_WIRE
    public boolean isRedstoneConductor(IBlockAccess blockAccess, int x, int y, int z) {
        return true;
    }
#endif
    
#if ENABLE_SLIME_SUPPORTING_MORTAR_BLOCKS
    public boolean permanentlySupportsMortarBlocks(World world, int x, int y, int z, int direction) {
        return true;
    }
#endif

    @Override
    public void onFallenUpon(World world, int x, int y, int z, Entity entity, float par6) {
        entity.fallDistance = 0.0F;
    }
    
#if ENABLE_PLATFORM_FIXES
    public int getPlatformMobilityFlag(World world, int x, int y, int z) {
        return PLATFORM_MAIN_SUPPORT;
    }
#endif
    
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        double dX = (double)x;
        double dY = (double)y;
        double dZ = (double)z;
        return AxisAlignedBB.getAABBPool().getAABB(dX + 0.0625D, dY + 0.0625D, dZ + 0.0625D, dX + 0.9375D, dY + 0.9375D, dZ + 0.9375D);
    }
    
    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        entity.motionX *= 0.4D;
        entity.motionY *= 0.05D;
        entity.motionZ *= 0.4D;
    }
    
    @Override
    public MapColor getMapColor(int meta) {
        // Kinda orange? Don't feel like defining a custom material for this.
        return MapColor.dirtColor;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public int getRenderBlockPass() {
        return 1;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int neighborX, int neighborY, int neighborZ, int neighborSide) {
        if (blockAccess.getBlockId(neighborX, neighborY, neighborZ) != this.blockID) {
            return super.shouldSideBeRendered(blockAccess, neighborX, neighborY, neighborZ, neighborSide);
        }
        return false;
    }
    
    @Environment(EnvType.CLIENT)
    @Override
    public boolean shouldRenderNeighborFullFaceSide(IBlockAccess blockAccess, int neighborX, int neighborY, int neighborZ, int neighborSide) {
        return true;
    }
    
    // Treat as transparent for AO
    @Environment(EnvType.CLIENT)
    @Override
    public float getAmbientOcclusionLightValue(IBlockAccess blockAccess, int x, int y, int z) {
        return 1.0F;
    }
}