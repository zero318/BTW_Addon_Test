package zero.test.mixin;

import net.minecraft.src.*;

import btw.block.blocks.*;
import btw.AddonHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Random;

import zero.test.IBlockMixins;

#include "../util.h"
#include "../feature_flags.h"
#include "../ids.h"

@Mixin(BlockDispenserBlock.class)
public class BlockDispenserBlockMixins {
#if ENABLE_LESS_CRAP_BLOCK_DISPENSER && 0
    @Overwrite
    public boolean validateBlockDispenser(World world, int x, int y, int z) {
        return true;
    }
    @Overwrite
	public boolean isCurrentStateValid(World world, int x, int y, int z) {
        return true;
    }
#endif
}