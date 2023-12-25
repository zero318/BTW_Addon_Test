package zero.test.mixin;


import net.minecraft.src.*;

import btw.block.blocks.AestheticOpaqueBlock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

#include "..\feature_flags.h"
#include "..\util.h"

@Mixin(AestheticOpaqueBlock.class)
public class SoapMixins {
    
#if ENABLE_MOVING_BLOCK_CHAINING
    
    public int getMobilityFlag(World world, int X, int Y, int Z) {
        return world.getBlockMetadata(X, Y, Z) == AestheticOpaqueBlock.SUBTYPE_SOAP ? PISTON_CAN_PUSH_ONLY : ((Block)(Object)this).getMobilityFlag();
    }

    public boolean canBlockBePulledByPiston(World world, int X, int Y, int Z, int direction) {
        return world.getBlockMetadata(X, Y, Z) != AestheticOpaqueBlock.SUBTYPE_SOAP;
    }
#endif
}