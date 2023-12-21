
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
                Block neighbor_block = blocksList[neighbor_id];
                if (
                    !((neighbor_block)==null) &&
                    neighbor_block.hasComparatorInputOverride() &&
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
    @Environment(EnvType.CLIENT)
    private Icon texture_front;
    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons( IconRegister register )
    {
        super.registerIcons( register );
        this.texture_front = register.registerIcon("cud_block_front");
    }
    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int side, int meta) {
        return side == 3 ? this.texture_front : this.blockIcon;
    }
    @Override
    @Environment(EnvType.CLIENT)
    public Icon getBlockTexture(IBlockAccess blockAccess, int X, int Y, int Z, int side) {
        if (this.isRedstoneOn(blockAccess, X, Y, Z)) {
            return super.getBlockTexture(blockAccess, X, Y, Z, side);
        }
        return getFacing(blockAccess, X, Y, Z) == side
                ? this.texture_front
                : this.blockIcon;
    }
}
