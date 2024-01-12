package zero.test.mixin;


import net.minecraft.src.*;

import btw.block.blocks.AestheticOpaqueBlock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

#include "..\feature_flags.h"
#include "..\util.h"

@Mixin(AestheticOpaqueBlock.class)
public class SoapMixins extends Block {
    
    public SoapMixins(int par1, Material par2) {
        super(par1, par2);
    }
    
#if ENABLE_MOVING_BLOCK_CHAINING
    
    public int getMobilityFlag(World world, int x, int y, int z) {
        return world.getBlockMetadata(x, y, z) == AestheticOpaqueBlock.SUBTYPE_SOAP ? PISTON_CAN_PUSH_ONLY : ((Block)(Object)this).getMobilityFlag();
    }

    @Override
    public boolean canBlockBePulledByPiston(World world, int x, int y, int z, int direction) {
        return world.getBlockMetadata(x, y, z) != AestheticOpaqueBlock.SUBTYPE_SOAP;
    }
    
    public boolean canBeStuckTo(World world, int x, int y, int z, int direction, int neighborId) {
        return false;
    }
#endif
}