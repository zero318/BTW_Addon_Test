package zero.test.mixin;

import net.minecraft.src.*;

import btw.entity.mob.CowEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

#include "..\util.h"
#include "..\feature_flags.h"

@Mixin(CowEntity.class)
public interface ICowEntityAccessMixins {
#if ENABLE_DEBUG_STICKS
    @Invoker("setGotMilk")
    public abstract void callSetGotMilk(boolean value);
#endif
}