package zero.test.block;
import net.minecraft.src.*;
import btw.block.BTWBlocks;
import btw.block.blocks.BlockDispenserBlock;
import btw.block.tileentity.dispenser.BlockDispenserTileEntity;
import btw.AddonHandler;
import btw.util.MiscUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import zero.test.mixin.IBlockDispenserBlockAccessMixins;
import java.util.Random;
// Block piston reactions
public class BlockPlacer extends BlockDispenserBlock {
    public BlockPlacer(int block_id) {
        super(block_id);
        setTickRandomly(false);
        setUnlocalizedName("block_placer");
    }
    @Override
    public void onBlockAdded(World world, int X, int Y, int Z) {
    }
    @Override
 public void onBlockPlacedBy(World world, int X, int Y, int Z, EntityLiving entity, ItemStack stack) {
  setFacing(world, X, Y, Z, MiscUtils.convertPlacingEntityOrientationToBlockFacingReversed(entity));
 }
    @Override
    public int idDropped(int i, Random random, int fortune_modifier) {
        return this.blockID;
    }
    @Override
    public void onNeighborBlockChange(World world, int X, int Y, int Z, int neighbor_id) {
        boolean receiving_power = world.isBlockIndirectlyGettingPowered(X, Y, Z) || world.isBlockIndirectlyGettingPowered(X, Y + 1, Z);
        int meta = world.getBlockMetadata(X, Y, Z);
        boolean is_powered = ((((meta)>7)));
        if (receiving_power != is_powered) {
            if (!is_powered) {
                world.scheduleBlockUpdate(X, Y, Z, this.blockID, this.tickRate(world));
            }
            world.setBlockMetadataWithNotify(X, Y, Z, meta ^ 8, 0x04);
        }
    }
    // This matches what the base block does
    @Override
    public void randomUpdateTick(World world, int X, int Y, int Z, Random random) {
        updateTick(world, X, Y, Z, random);
    }
    @Override
    public void updateTick(World world, int X, int Y, int Z, Random random) {
        ((IBlockDispenserBlockAccessMixins)this).callDispenseBlockOrItem(world, X, Y, Z);
    }
    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        super.registerIcons(register);
        Icon[] icon_array = ((IBlockDispenserBlockAccessMixins)this).getIconBySideArray();
        Icon side_icon = register.registerIcon("place_block_side");
        icon_array[2] = side_icon;
        icon_array[3] = side_icon;
        icon_array[4] = side_icon;
        icon_array[5] = side_icon;
    }
}
