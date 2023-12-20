
package zero.test.block;
import btw.client.fx.BTWEffectManager;
import btw.util.MiscUtils;
import btw.world.util.BlockPos;
import btw.block.blocks.BuddyBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import btw.AddonHandler;
import java.util.Random;
public class ObserverBlock extends BuddyBlock {
    public ObserverBlock(int block_id) {
        super(block_id);
        this.setUnlocalizedName("observer");
        this.setTickRandomly(false);
    }
    @Override
    public int tickRate(World world) {
        return 2;
    }
    @Override
 public boolean triggersBuddy() {
  return true;
 }
    public boolean caresAboutUpdateDirection() {
        return true;
    }
    @Override
    public void onBlockAdded(World world, int X, int Y, int Z) {
    }
    @Override
    public void onNeighborBlockChange(World world, int X, int Y, int Z, int neighbor_id) {
        int update_direction = ((neighbor_id)>>>28);
        int meta = world.getBlockMetadata(X, Y, Z);
        if (
            update_direction == (meta | 1) ||
            update_direction == 0xD
        ) {
            if (!((((meta)&1)!=0))) {
                Block neighborBlock = blocksList[neighbor_id & 0xFFF];
                if (
                    neighborBlock != null &&
                    !world.isUpdatePendingThisTickForBlock(X, Y, Z, blockID)
                ) {
                    world.scheduleBlockUpdate(X, Y, Z, blockID, 2);
                }
            }
        }
    }
    @Override
    public void setBlockRedstoneOn(World world, int i, int j, int k, boolean bOn) {
     if (bOn != isRedstoneOn(world, i, j, k) ) {
      int iMetaData = world.getBlockMetadata(i, j, k);
      if ( bOn ) {
       iMetaData = iMetaData | 1;
      }
      else {
       iMetaData = iMetaData & (~1);
      }
         world.setBlockMetadataWithClient( i, j, k, iMetaData );
         int iFacing = this.getFacing(world, i, j, k);
         notifyNeigborsToFacingOfPowerChange(world, i, j, k, iFacing);
         world.markBlockRangeForRenderUpdate( i, j, k, i, j, k );
        }
    }
    @Override
    public int adjustMetadataForPistonMove(int meta) {
  return (((meta)&14));
 }
    @Environment(EnvType.CLIENT)
    private Icon texture_back_off;
    @Environment(EnvType.CLIENT)
    private Icon texture_back_on;
    @Environment(EnvType.CLIENT)
    private Icon texture_front;
    @Environment(EnvType.CLIENT)
    private Icon texture_side;
    @Environment(EnvType.CLIENT)
    private Icon texture_top;
    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
  super.registerIcons(register);
  this.texture_back_off = register.registerIcon("observer_back");
        this.texture_back_on = register.registerIcon("observer_back_on");
        this.texture_front = register.registerIcon("observer_front");
        this.texture_side = register.registerIcon("observer_side");
        this.texture_top = register.registerIcon("observer_top");
    }
    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int side, int meta) {
  if (side == 3) {
   return this.texture_front;
  }
  return blockIcon;
    }
    @Override
    @Environment(EnvType.CLIENT)
    public Icon getBlockTexture(IBlockAccess blockAccess, int X, int Y, int Z, int side) {
     int facing = getFacing(blockAccess, X, Y, Z);
     if (facing == side) {
            return isRedstoneOn(blockAccess, X, Y, Z)
                    ? this.texture_back_on
                    : this.texture_back_off;
     }
        if (facing == (side^1)) {
            return this.texture_front;
        }
        if (((facing)&~1) == 0x0) {
        } else {
        }
     return blockIcon;
    }
}
