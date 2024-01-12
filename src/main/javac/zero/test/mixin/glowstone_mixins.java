package zero.test.mixin;

import net.minecraft.src.*;

import org.spongepowered.asm.mixin.Mixin;

#include "..\feature_flags.h"
#include "..\util.h"

@Mixin(BlockGlowStone.class)
public class BlockGlowStoneMixins {
#if ENABLE_MODERN_REDSTONE_WIRE
    public boolean isRedstoneConductor(IBlockAccess blockAccess, int x, int y, int z) {
        return false;
    }
#endif
}