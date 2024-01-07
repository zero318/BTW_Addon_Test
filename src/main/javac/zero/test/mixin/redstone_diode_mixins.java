package zero.test.mixin;

import net.minecraft.src.*;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import zero.test.IBlockRedstoneLogicMixins;
import zero.test.mixin.IBlockRedstoneLogicAccessMixins;

#include "..\func_aliases.h"
#include "..\feature_flags.h"
#include "..\util.h"
    
@Mixin(BlockRedstoneLogic.class)
public abstract class BlockRedstoneLogicMixins extends BlockDirectional implements IBlockRedstoneLogicMixins {
    
    public BlockRedstoneLogicMixins(int block_id, Material material) {
        super(block_id, material);
    }
    
    public boolean isRenderingLogicBase;
    
    public void setRenderingBaseTextures(boolean value) {
        this.isRenderingLogicBase = value;
    }
    public boolean getRenderingBaseTextures() {
        return this.isRenderingLogicBase;
    }
    
    // Hacky fix for rendering a bottom texture
    @Environment(EnvType.CLIENT)
    @Overwrite
    public Icon getIcon(int side, int meta) {
        BlockRedstoneLogic self = (BlockRedstoneLogic)(Object)this;
        if (side == DIRECTION_DOWN && !this.isRenderingLogicBase) {
            return (((IBlockRedstoneLogicAccessMixins)self).getIsRepeaterPowered() ? Block.torchRedstoneActive : Block.torchRedstoneIdle).getBlockTextureFromSide(side);
        }
        return side == DIRECTION_UP ? self.blockIcon : Block.stoneDoubleSlab.getBlockTextureFromSide(1);
    }
    
    // Make sure to cull the bottom
    // texture when it isn't needed
    @Environment(EnvType.CLIENT)
    @Overwrite
    public boolean shouldSideBeRendered(IBlockAccess block_access, int neighborX, int neighborY, int neighborZ, int neighbor_side) {
        switch (neighbor_side) {
            case DIRECTION_DOWN:
                return super.shouldSideBeRendered(block_access, neighborX, neighborY, neighborZ, neighbor_side);
            case DIRECTION_UP:
                return false;
            default:
                return true;
        }
    }
}
