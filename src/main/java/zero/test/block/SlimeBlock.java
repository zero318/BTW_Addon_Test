package zero.test.block;

import net.minecraft.src.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
public class SlimeBlock extends Block {
    public SlimeBlock(int block_id) {
        super(block_id, Material.grass);
        this.setUnlocalizedName("slime_block");
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }
    public int getMobilityFlag() {
        return 0;
    }
    public boolean isSticky(int X, int Y, int Z, int direction) {
        return true;
    }
    public boolean canStickTo(int X, int Y, int Z, int direction, int neighbor_id) {
        return neighbor_id != 1321;
    }
    @Override
    public boolean isNormalCube(IBlockAccess blockAccess, int X, int Y, int Z) {
        return true;
    }
    @Environment(EnvType.CLIENT)
    @Override
    public int getRenderBlockPass() {
        return 1;
    }
    @Environment(EnvType.CLIENT)
    @Override
    public boolean shouldRenderNeighborFullFaceSide(IBlockAccess blockAccess, int X, int Y, int Z, int neighbor_side) {
        return true;
    }
}
