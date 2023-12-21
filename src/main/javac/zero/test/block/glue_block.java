package zero.test.block;

#include "..\util.h"
#include "..\ids.h"

import net.minecraft.src.*;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class GlueBlock extends Block {
    public GlueBlock(int block_id) {
        super(block_id, Material.grass);
        this.setUnlocalizedName("glue_block");
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }
    
    
    public int getMobilityFlag() {
        return PISTON_CAN_PUSH;
    }
    
    public boolean isSticky(int X, int Y, int Z, int direction) {
        return true;
    }
    
    public boolean canStickTo(int X, int Y, int Z, int direction, int neighbor_id) {
        return neighbor_id != SLIME_BLOCK_ID;
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