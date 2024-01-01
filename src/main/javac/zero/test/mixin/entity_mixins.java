package zero.test.mixin;

import net.minecraft.src.*;

import btw.AddonHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

#include "..\feature_flags.h"

@Mixin(Entity.class)
public class EntityMixins {
#if ENABLE_NOCLIP_COMMAND && 0
    @Inject(
        method = "pushOutOfBlocks(DDD)Z",
        at = @At("HEAD"),
        cancellable = true
    )
    protected void pushOutOfBlocks_cancel_if_noclip(double X, double Y, double Z, CallbackInfoReturnable callback_info) {
        Entity self = (Entity)(Object)this;
        if (self instanceof EntityPlayer && self.noClip) {
            //AddonHandler.logMessage("Player noclip state C: "+self.noClip);
            callback_info.setReturnValue(false);
            callback_info.cancel();
        }
    }
#endif
}