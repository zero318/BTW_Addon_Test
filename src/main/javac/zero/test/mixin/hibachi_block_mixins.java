package zero.test.mixin;

import net.minecraft.src.*;

import btw.block.blocks.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Random;

#include "..\feature_flags.h"
#include "..\util.h"

@Mixin(HibachiBlock.class)
public class HibachiBlockMixins {
#if ENABLE_LESS_CRAP_BTW_BLOCK_POWERING
    // Prevent hibachi getting quasi powered
    @Overwrite
    private boolean isGettingPowered(World world, int X, int Y, int Z) {
        return world.isBlockGettingPowered(X, Y, Z);
    }
#endif
}