package zero.test.block;
import net.minecraft.src.*;
import btw.block.blocks.*;
public class IronTrapDoor extends TrapDoorBlock {
    public IronTrapDoor(int block_id) {
        super(block_id);
        this.blockMaterial = Material.iron;
        this.setAxesEffectiveOn(false);
        this.setPicksEffectiveOn(true);
  this.setStepSound(soundMetalFootstep);
  this.setUnlocalizedName("iron_trapdoor");
    }
    @Override
 public boolean onBlockActivated(World world, int X, int Y, int Z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
  return false;
 }
    @Override
    public void onNeighborBlockChange(World world, int X, int Y, int Z, int neighbor_id) {
        if (!world.isRemote) {
            boolean receiving_power = world.isBlockIndirectlyGettingPowered(X, Y, Z);
            if (receiving_power || neighbor_id > 0 && Block.blocksList[neighbor_id].canProvidePower()) {
                this.onPoweredBlockChange(world, X, Y, Z, receiving_power);
            }
        }
    }
    @Override
    public void onPoweredBlockChange(World world, int X, int Y, int Z, boolean receiving_power) {
        int meta = world.getBlockMetadata(X, Y, Z);
        boolean is_powered = (meta & 4) > 0;
        if (is_powered != receiving_power) {
            world.setBlockMetadataWithNotify(X, Y, Z, meta ^ 4, 2);
            world.playAuxSFXAtEntity((EntityPlayer)null, 1003, X, Y, Z, 0);
        }
    }
    @Override
 public boolean canPlaceBlockOnSide(World world, int X, int Y, int Z, int side) {
  return true;
 }
 @Override
 public boolean isBreakableBarricade(IBlockAccess block_access, int X, int Y, int Z) {
  return false;
 }
    @Override
    public boolean isBreakableBarricadeOpen(IBlockAccess block_access, int X, int Y, int Z) {
  return false;
 }
}
