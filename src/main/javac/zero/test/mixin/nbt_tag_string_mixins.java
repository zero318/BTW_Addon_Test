package zero.test.mixin;

import net.minecraft.src.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.gen.Accessor;

import zero.test.INBTBaseMixins;

#include "..\util.h"
#include "..\feature_flags.h"

@Mixin(NBTTagString.class)
public abstract class NBTTagStringMixins implements INBTBaseMixins {
    @Override
    public void toSNBT(StringBuilder str) {
        str.append(((NBTTagString)(Object)this).data);
    }
}