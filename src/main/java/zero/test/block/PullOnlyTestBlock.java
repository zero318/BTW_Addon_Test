package zero.test.block;

import net.minecraft.src.*;
public class PullOnlyTestBlock extends Block {
    public PullOnlyTestBlock(int block_id) {
        super(block_id, Material.rock);
        this.setUnlocalizedName("pull_only_test_block");
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }
    @Override
    public boolean canBlockBePulledByPiston(World world, int X, int Y, int Z, int direction) {
        return true;
    }
    @Override
    public boolean canBlockBePushedByPiston(World world, int X, int Y, int Z, int direction) {
        return false;
    }
}
