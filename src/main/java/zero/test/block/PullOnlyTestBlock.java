package zero.test.block;
// Block piston reactions

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
    public boolean canBeStuckTo(World world, int X, int Y, int Z, int direction, int neighbor_id) {
        return false;
    }
}
