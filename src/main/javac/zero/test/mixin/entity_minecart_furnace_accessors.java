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
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

#include "..\feature_flags.h"
#include "..\util.h"
    
@Mixin(EntityMinecartFurnace.class)
public interface IEntityMinecartFurnaceAccessMixins {
#if ENABLE_BLOCK_DISPENSER_VARIANTS
    @Accessor
    public int getFuel();
    @Accessor
    public void setFuel(int value);
#endif
}