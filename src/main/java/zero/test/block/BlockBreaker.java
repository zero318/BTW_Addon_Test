package zero.test.block;
import net.minecraft.src.*;
import btw.block.BTWBlocks;
import btw.block.blocks.BlockDispenserBlock;
import btw.AddonHandler;
import btw.util.MiscUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import zero.test.mixin.IBlockDispenserBlockAccessMixins;
import java.util.Random;
// Block piston reactions
public class BlockBreaker extends BlockDispenserBlock {
    public BlockBreaker(int blockId) {
        super(blockId);
        setTickRandomly(false);
        setUnlocalizedName("block_breaker");
    }
    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
    }
    @Override
 public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving entity, ItemStack stack) {
  setFacing(world, x, y, z, MiscUtils.convertPlacingEntityOrientationToBlockFacingReversed(entity));
 }
    @Override
    public int idDropped(int i, Random random, int fortune_modifier) {
        return this.blockID;
    }
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborId) {
        boolean receivingPower = world.isBlockIndirectlyGettingPowered(x, y, z) || world.isBlockIndirectlyGettingPowered(x, y + 1, z);
        int meta = world.getBlockMetadata(x, y, z);
        boolean isPowered = ((((meta)>7)));
        if (receivingPower != isPowered) {
            if (!isPowered) {
                world.scheduleBlockUpdate(x, y, z, this.blockID, this.tickRate(world));
            }
            world.setBlockMetadataWithNotify(x, y, z, ((meta)^8), 0x04);
        }
    }
    // This matches what the base block does
    @Override
    public void randomUpdateTick(World world, int x, int y, int z, Random random) {
        updateTick(world, x, y, z, random);
    }
    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        ((IBlockDispenserBlockAccessMixins)this).callConsumeFacingBlock(world, x, y, z);
    }
    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        super.registerIcons(register);
        Icon[] icon_array = ((IBlockDispenserBlockAccessMixins)this).getIconBySideArray();
        Icon side_icon = register.registerIcon("pickaxe_block_side");
        icon_array[2] = side_icon;
        icon_array[3] = side_icon;
        icon_array[4] = side_icon;
        icon_array[5] = side_icon;
    }
}
