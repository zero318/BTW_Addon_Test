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
// Block piston reactions
public class MixerBlock
extends CookingVesselBlock
{
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
                !((((meta)&4)!=0)) && (
                    MechPowerUtils.isBlockPoweredByAxleToSide(world, x, y, z, 0) ||
                    MechPowerUtils.isBlockPoweredByAxleToSide(world, x, y, z, 1)
                )
            ) != ((((meta)>7)))
        ) {
            world.setBlockMetadataWithNotify(x, y, z, ((meta)^8), 0x01 | 0x02);
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
        return ((((world.getBlockMetadata(x, y, z))>7)));
    }
    @Override
    public TileEntity createNewTileEntity(World world) {
        return new MixerBlockEntity();
    }
    @Override
    protected int getContainerID() {
        return 318;
    }
    @Override
    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random random) {
    }
    @Environment(EnvType.CLIENT)
    public static MixerBladesModel model;
    @Environment(EnvType.CLIENT)
    public static BlockModel transformedModel;
    static {
        transformedModel = model = new MixerBladesModel();
    }
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
            case 2:
                return ((side)<2)
                        ? BTWBlocks.axle.blockIcon
                        : this.isSpinning
                            ? ((AxleBlock)BTWBlocks.axle).iconSideOn
                            : ((AxleBlock)BTWBlocks.axle).iconSide;
            case 3:
                return Block.fenceIron.blockIcon;
            default:
                return super.getIcon(side, meta);
        }
    }
    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIconByIndex(int index) {
        switch (index) {
            case 0:
                return this.isSpinning ? this.blade_texture1_spin : this.blade_texture1;
            case 1:
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
            ((((meta)&4)!=0))
        ) {
            int tilt = (((meta)&3)) + 2;
            if (side == tilt) {
                side = 1;
            }
            else if (side == ((tilt)^1)) {
                side = 0;
            }
            else {
                side = 2;
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
        if (((((meta)&4)!=0))) {
            transformedModel.tiltToFacingAlongY((((meta)&3)) + 2);
        }
        this.isSpinning = ((((meta)>7)));
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
}
