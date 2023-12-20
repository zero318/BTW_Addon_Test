
package zero.test.block;
import btw.client.fx.BTWEffectManager;
import btw.util.MiscUtils;
import btw.world.util.BlockPos;
import btw.block.blocks.BuddyBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import java.util.Random;
public class CUDBlock extends BuddyBlock {
    public CUDBlock(int block_id) {
        super(block_id);
        this.setUnlocalizedName("cud_block");
    }
    @Override
    public void onBlockAdded(World world, int X, int Y, int Z) {
    }
    @Override
    public int onPreBlockPlacedByPiston(World world, int X, int Y, int Z, int meta, int direction) {
  return meta;
 }
    @Override
 public boolean triggersBuddy() {
  return true;
 }
    @Override
    public void onNeighborBlockChange(World world, int X, int Y, int Z, int neighbor_id) {
        if (neighbor_id != 1318) {
            int meta = world.getBlockMetadata(X, Y, Z);
            if (!((((meta)&1)!=0))) {
                Block neighborBlock = blocksList[neighbor_id];
                if (
                    neighborBlock != null &&
                    neighborBlock.hasComparatorInputOverride() &&
                    !world.isUpdatePendingThisTickForBlock(X, Y, Z, blockID)
                ) {
                    world.scheduleBlockUpdate(X, Y, Z, blockID, 1);
                }
            }
        }
    }
    public boolean getWeakChanges(World world, int X, int Y, int Z, int neighbor_id) {
        return true;
    }
}
