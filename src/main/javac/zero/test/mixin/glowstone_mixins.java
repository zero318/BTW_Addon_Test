package zero.test.mixin;

import net.minecraft.src.*;

import org.spongepowered.asm.mixin.Mixin;

#include "..\feature_flags.h"
#include "..\util.h"

@Mixin(BlockGlowStone.class)
public class BlockGlowStoneMixins {
#if ENABLE_MODERN_REDSTONE_WIRE
    public boolean isRedstoneConductor(IBlockAccess block_access, int X, int Y, int Z) {
        return false;
    }
#endif
}