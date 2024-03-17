package zero.test.mixin;

import net.minecraft.src.*;

import btw.entity.mob.SpiderEntity;

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

@Mixin(SpiderEntity.class)
public interface ISpiderEntityAccessMixins {
#if ENABLE_DEBUG_STICKS
    @Accessor
    public int getTimeToNextWeb();
    @Accessor
    public void setTimeToNextWeb(int value);
#endif
}