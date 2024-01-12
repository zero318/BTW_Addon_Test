package zero.test.block;
import net.minecraft.src.*;
import btw.block.blocks.*;
public class IronTrapDoor extends TrapDoorBlock {
    public IronTrapDoor(int blockId) {
        super(blockId);
        this.blockMaterial = Material.iron;
        this.setAxesEffectiveOn(false);
        this.setPicksEffectiveOn(true);
  this.setStepSound(soundMetalFootstep);
  this.setUnlocalizedName("iron_trapdoor");
        //disable_stats();
    }
    @Override
 public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
  return false;
 }
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborId) {
        if (!world.isRemote) {
            boolean receivingPower = world.isBlockIndirectlyGettingPowered(x, y, z);
            if (receivingPower || neighborId > 0 && Block.blocksList[neighborId].canProvidePower()) {
                this.onPoweredBlockChange(world, x, y, z, receivingPower);
            }
        }
    }
    @Override
    public void onPoweredBlockChange(World world, int x, int y, int z, boolean receivingPower) {
        int meta = world.getBlockMetadata(x, y, z);
        boolean isPowered = (meta & 4) > 0;
        if (isPowered != receivingPower) {
            world.setBlockMetadataWithNotify(x, y, z, meta ^ 4, 2);
            world.playAuxSFXAtEntity((EntityPlayer)null, 1003, x, y, z, 0);
        }
    }
    @Override
 public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side) {
  return true;
 }
 @Override
 public boolean isBreakableBarricade(IBlockAccess blockAccess, int x, int y, int z) {
  return false;
 }
    @Override
    public boolean isBreakableBarricadeOpen(IBlockAccess blockAccess, int x, int y, int z) {
  return false;
 }
}
