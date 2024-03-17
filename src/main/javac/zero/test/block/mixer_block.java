package zero.test.block;

import net.minecraft.src.*;

import btw.block.BTWBlocks;
import btw.block.blocks.VesselBlock;
import btw.block.blocks.CookingVesselBlock;
import btw.block.blocks.CauldronBlock;
import btw.block.blocks.AxleBlock;
import btw.block.tileentity.CauldronTileEntity;
import btw.block.tileentity.CookingVesselTileEntity;
import btw.block.model.BlockModel;
import btw.block.util.RayTraceUtils;
import btw.block.util.MechPowerUtils;
import btw.client.render.util.RenderUtils;
import btw.inventory.BTWContainers;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.Random;

import zero.test.block.model.MixerBladesModel;
import zero.test.block.block_entity.MixerBlockEntity;

#include "..\util.h"
#include "..\feature_flags.h"
#include "..\ids.h"

#define TILT_DIRECTION_META_BITS 2
#define TILT_DIRECTION_META_OFFSET 0

#define TILT_DIRECTION_META_NORTH 0
#define TILT_DIRECTION_META_SOUTH 1
#define TILT_DIRECTION_META_WEST 2
#define TILT_DIRECTION_META_EAST 3

#define POWERED_META_OFFSET 2

#define SPINNING_META_BITS 1
#define SPINNING_META_IS_BOOL true
#define SPINNING_META_OFFSET 3

#define SPAWN_PARTICLES 0

public class MixerBlock
#if ENABLE_MIXER_BLOCK
extends CookingVesselBlock
#endif
{
#if ENABLE_MIXER_BLOCK
    public MixerBlock(int blockId) {
        super(blockId, Material.iron);
        
        // Copied from cauldron
        this.setHardness(3.5f);
        this.setResistance(10.0f);
        
        this.setStepSound(Block.soundMetalFootstep);
        this.setUnlocalizedName("mixer_block");
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }
    
    @Override
    public void updateTick(World world, int x, int y, int z, Random rand) {
        super.updateTick(world, x, y, z, rand);
        
        int meta = world.getBlockMetadata(x, y, z);
        
        if (
            (
                !READ_META_FIELD(meta, POWERED) && (
                    MechPowerUtils.isBlockPoweredByAxleToSide(world, x, y, z, DIRECTION_DOWN) ||
                    MechPowerUtils.isBlockPoweredByAxleToSide(world, x, y, z, DIRECTION_UP)
                )
            ) != READ_META_FIELD(meta, SPINNING)
        ) {
            world.setBlockMetadataWithNotify(x, y, z, TOGGLE_META_FIELD(meta, SPINNING), UPDATE_NEIGHBORS | UPDATE_CLIENTS);
        }
        if (!world.isRemote) {
            TileEntity tileEntity;
            if ((tileEntity = world.getBlockTileEntity(x, y, z)) instanceof MixerBlockEntity) {
                ((MixerBlockEntity)tileEntity).validateFireUnderType();
            }
        }
    }
    
    @Override
    public void overpower(World world, int x, int y, int z) {
        // This function can't tell which direction the axle
        // is coming from to properly break the block only
        // when it's the top/bottom, so screw it we'll just
        // make it run faster
        TileEntity tileEntity;
        if ((tileEntity = world.getBlockTileEntity(x, y, z)) instanceof MixerBlockEntity) {
            ((MixerBlockEntity)tileEntity).overpower();
        }
    }
    
    @Override
    protected void validateFireUnderState(World world, int x, int y, int z) {
        // Unused
    }
    
    @Override
    public boolean canInputAxlePowerToFacing(World world, int x, int y, int z, int facing) {
        // All six sides are inputs, but the
        // top/bottom do different things
        // than the side inputs.
        return true;
    }
    
    public boolean getSpinningState(World world, int x, int y, int z) {
        return READ_META_FIELD(world.getBlockMetadata(x, y, z), SPINNING);
    }
    
    @Override
    public TileEntity createNewTileEntity(World world) {
        return new MixerBlockEntity();
    }
    
    @Override
    protected int getContainerID() {
        return MIXER_CONTAINER_ID;
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
#if SPAWN_PARTICLES
        if (READ_META_FIELD(world.getBlockMetadata(x, y, z), SPINNING)) {
            
        }
#endif
    }
    
    @Environment(EnvType.CLIENT)
    public static MixerBladesModel model;
    @Environment(EnvType.CLIENT)
    public static BlockModel transformedModel;
    
    static {
        transformedModel = model = new MixerBladesModel();
    }
    
#define TEXTURE_INDEX_BLADE_1 0
#define TEXTURE_INDEX_BLADE_2 1
#define TEXTURE_INDEX_AXLE 2
#define TEXTURE_INDEX_BAR 3

    @Environment(EnvType.CLIENT)
    public boolean isSpinning;
    
    @Environment(EnvType.CLIENT)
    public Icon blade_texture1;
    @Environment(EnvType.CLIENT)
    public Icon blade_texture2;
    @Environment(EnvType.CLIENT)
    public Icon blade_texture1_spin;
    @Environment(EnvType.CLIENT)
    public Icon blade_texture2_spin;
    
    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        this.iconInteriorBySideArray[0] = this.iconWideBandBySideArray[0] = this.iconCenterColumnBySideArray[0] = register.registerIcon("mixer_bottom");

        this.iconInteriorBySideArray[1] = this.iconCenterColumnBySideArray[1] = register.registerIcon("mixer_top");
        //this.iconInteriorBySideArray[1] = this.iconCenterColumnBySideArray[1] = register.registerIcon("fcBlockCauldron_top");
        this.iconWideBandBySideArray[1] = register.registerIcon("fcBlockCauldronWideBand_top");

        this.blockIcon = // for hit effects
        this.iconInteriorBySideArray[2] = this.iconWideBandBySideArray[2] = this.iconCenterColumnBySideArray[2] =
        this.iconInteriorBySideArray[3] = this.iconWideBandBySideArray[3] = this.iconCenterColumnBySideArray[3] =
        this.iconInteriorBySideArray[4] = this.iconWideBandBySideArray[4] = this.iconCenterColumnBySideArray[4] =
        this.iconInteriorBySideArray[5] = this.iconWideBandBySideArray[5] = this.iconCenterColumnBySideArray[5] = register.registerIcon("fcBlockCauldron_side");
        
        this.blade_texture1 = register.registerIcon("mixer_blades1");
        this.blade_texture2 = register.registerIcon("mixer_blades2");
        this.blade_texture1_spin = register.registerIcon("mixer_blades1_spin");
        this.blade_texture2_spin = register.registerIcon("mixer_blades2_spin");
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int side, int meta) {
        switch (transformedModel.getActivePrimitiveID()) {
            case TEXTURE_INDEX_AXLE:
                return DIRECTION_IS_VERTICAL(side)
                        ? BTWBlocks.axle.blockIcon
                        : this.isSpinning
                            ? ((AxleBlock)BTWBlocks.axle).iconSideOn
                            : ((AxleBlock)BTWBlocks.axle).iconSide;
            case TEXTURE_INDEX_BAR:
                return Block.fenceIron.blockIcon;
            default:
                return super.getIcon(side, meta);
        }
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIconByIndex(int index) {
        switch (index) {
            case TEXTURE_INDEX_BLADE_1:
                return this.isSpinning ? this.blade_texture1_spin : this.blade_texture1;
            case TEXTURE_INDEX_BLADE_2:
                return this.isSpinning ? this.blade_texture2_spin : this.blade_texture2;
            default:
                // Just don't get here plz
                return this.blockIcon;
        }
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    public Icon getBlockTexture(IBlockAccess blockAccess, int x, int y, int z, int side) {
        int meta = blockAccess.getBlockMetadata(x, y, z);
        
        if (
            transformedModel.getActivePrimitiveID() < 0 &&
            READ_META_FIELD(meta, POWERED)
        ) {
            int tilt = READ_META_FIELD(meta, TILT_DIRECTION) + 2;
            if (side == tilt) {
                side = DIRECTION_UP;
            }
            else if (side == OPPOSITE_DIRECTION(tilt)) {
                side = DIRECTION_DOWN;
            }
            else {
                side = DIRECTION_NORTH;
            }
        }
        
        return this.getIcon(side, meta);
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderBlocks, int x, int y, int z) {
        super.renderBlock(renderBlocks, x, y, z);
        
        transformedModel = model.makeTemporaryCopy();
        int meta = renderBlocks.blockAccess.getBlockMetadata(x, y, z);
        if (READ_META_FIELD(meta, POWERED)) {
            transformedModel.tiltToFacingAlongY(READ_META_FIELD(meta, TILT_DIRECTION) + 2);
        }
        this.isSpinning = READ_META_FIELD(meta, SPINNING);
        transformedModel.renderAsBlockWithColorMultiplier(renderBlocks, this, x, y, z);
        
        return true;
    }
    
    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderBlocks, int damage, float brightness) {
        super.renderBlockAsItem(renderBlocks, damage, brightness);
        
        this.isSpinning = false;
        (transformedModel = model.makeTemporaryCopy()).renderAsItemBlock(renderBlocks, this, damage);
        
    }
#endif
}