package zero.test.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
    @Environment(EnvType.CLIENT)
    protected Icon texture_top;
    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        super.registerIcons(register);
        this.texture_top = register.registerIcon("hardpoint_top");
    }
    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int side, int meta) {
        return ((side)>=2) ? this.blockIcon : this.texture_top;
    }
}
