package zero.test.block;

#include "..\util.h"
#include "..\ids.h"

import net.minecraft.src.*;

import btw.block.BTWBlocks;
import btw.block.blocks.AestheticOpaqueBlock;

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
    
    public boolean isStickyForBlocks(World world, int X, int Y, int Z, int direction) {
        return true;
    }
    
    public boolean isStickyForEntitiesWhenMoved(int direction, int meta) {
        return true;
    }
    
    public boolean canStickTo(World world, int X, int Y, int Z, int direction, int neighbor_id) {
        if (neighbor_id == SLIME_BLOCK_ID) {
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