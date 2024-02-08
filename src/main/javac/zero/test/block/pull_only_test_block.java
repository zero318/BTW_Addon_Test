package zero.test.block;

#include "..\util.h"
#include "..\feature_flags.h"
#include "..\ids.h"

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
    
#if ENABLE_MOVING_BLOCK_CHAINING
    public boolean canBeStuckTo(World world, int x, int y, int z, int direction, int neighborId) {
        return false;
    }
#endif

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
        return DIRECTION_IS_HORIZONTAL(side) ? this.blockIcon : this.texture_top;
    }
}