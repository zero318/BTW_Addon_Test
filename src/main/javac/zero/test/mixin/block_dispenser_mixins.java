package zero.test.mixin;

import net.minecraft.src.*;

import btw.block.blocks.*;
import btw.AddonHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Random;

import zero.test.IBlockMixins;
import zero.test.IWorldMixins;

#include "../util.h"
#include "../feature_flags.h"
#include "../ids.h"

#define DIRECTION_META_OFFSET 0
#define POWERED_META_OFFSET 3

@Mixin(BlockDispenserBlock.class)
public abstract class BlockDispenserBlockMixins extends BlockContainer {
    
    public BlockDispenserBlockMixins(int par1, Material par2Material) {
        super(par1, par2Material);
    }
    
#if ENABLE_BETTER_BLOCK_DISPENSER_POWERING
    @Overwrite
    public boolean isReceivingRedstonePower(World world, int x, int y, int z) {
#if ENABLE_LESS_CRAP_BTW_BLOCK_POWERING
        return ((IWorldMixins)world).getBlockWeakPowerInputExceptFacing(x, y, z, READ_META_FIELD(world.getBlockMetadata(x, y, z), DIRECTION)) != 0 || ((IWorldMixins)world).getBlockWeakPowerInputExceptFacing(x, y + 1, z, DIRECTION_DOWN) != 0;
#else
        return ((IWorldMixins)world).getBlockStrongPowerInputExceptFacing(x, y, z, READ_META_FIELD(world.getBlockMetadata(x, y, z), DIRECTION)) != 0 || ((IWorldMixins)world).getBlockStrongPowerInputExceptFacing(x, y + 1, z, DIRECTION_DOWN) != 0;
#endif    
    }
#endif
}