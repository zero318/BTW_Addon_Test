package zero.test.mixin;

import net.minecraft.src.*;

import java.util.List;

import btw.block.blocks.BlockDispenserBlock;

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

import zero.test.IBlockMixins;

#include "..\func_aliases.h"
#include "..\feature_flags.h"
#include "..\util.h"
    
@Mixin(BlockDispenserBlock.class)
public interface IBlockDispenserBlockAccessMixins {
#if ENABLE_BLOCK_DISPENSER_VARIANTS
    @Environment(EnvType.CLIENT)
    @Accessor
    public Icon[] getIconBySideArray();

    @Invoker("consumeFacingBlock")
    public abstract void callConsumeFacingBlock(World world, int x, int y, int z);
    @Invoker("dispenseBlockOrItem")
    public abstract boolean callDispenseBlockOrItem(World world, int x, int y, int z);
#endif
}