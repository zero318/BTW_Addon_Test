package zero.test.block;

import net.minecraft.src.*;
import btw.block.BTWBlocks;
import btw.block.blocks.AestheticOpaqueBlock;
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
    public boolean isStickyForBlocks(World world, int X, int Y, int Z, int direction) {
        return true;
    }
    public boolean isBouncyWhenMoved(int direction, int meta) {
        return true;
    }
    public boolean canStickTo(World world, int X, int Y, int Z, int direction, int neighbor_id) {
        if (neighbor_id == 1321) {
            return false;
        }
        if (neighbor_id == BTWBlocks.aestheticOpaque.blockID) {
            return world.getBlockMetadata(X, Y, Z) != AestheticOpaqueBlock.SUBTYPE_SOAP;
        }
        return true;
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
