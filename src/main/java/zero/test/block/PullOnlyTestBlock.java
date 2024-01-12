package zero.test.block;
// Block piston reactions

import net.minecraft.src.*;
public class PullOnlyTestBlock extends Block {
    public PullOnlyTestBlock(int blockId) {
        super(blockId, Material.rock);
        this.setUnlocalizedName("pull_only_test_block");
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }
    @Override
    public boolean canBlockBePulledByPiston(World world, int x, int y, int z, int direction) {
        return true;
    }
    @Override
    public boolean canBlockBePushedByPiston(World world, int x, int y, int z, int direction) {
        return false;
    }
    public boolean canBeStuckTo(World world, int x, int y, int z, int direction, int neighborId) {
        return false;
    }
}
