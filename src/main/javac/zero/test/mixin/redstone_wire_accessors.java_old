package zero.test.mixin;

import net.minecraft.src.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

import zero.test.IBlockMixins;

#include "..\func_aliases.h"
#include "..\feature_flags.h"
#include "..\util.h"

@Mixin(BlockRedstoneWire.class)
public interface IRedstoneWireAccessMixins {
    @Invoker("updateAndPropagateCurrentStrength")
    public abstract void callUpdateAndPropagateCurrentStrength(World world, int X, int Y, int Z);
}