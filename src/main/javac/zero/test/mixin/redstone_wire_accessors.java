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
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.gen.Accessor;

import zero.test.IBlockMixins;

#include "..\func_aliases.h"
#include "..\feature_flags.h"
#include "..\util.h"

@Mixin(BlockRedstoneWire.class)
public interface IRedstoneWireAccessMixins {
#if ENABLE_MODERN_REDSTONE_WIRE
    //@Invoker("updateAndPropagateCurrentStrength")
    //public abstract void callUpdateAndPropagateCurrentStrength(World world, int x, int y, int z);
    
    @Environment(EnvType.CLIENT)
    @Accessor
    public Icon getField_94413_c();
    @Environment(EnvType.CLIENT)
    @Accessor
    public Icon getField_94410_cO();
    @Environment(EnvType.CLIENT)
    @Accessor
    public Icon getField_94411_cP();
    @Environment(EnvType.CLIENT)
    @Accessor
    public Icon getField_94412_cQ();
#endif
}