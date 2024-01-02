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

@Mixin(EntityPlayer.class)
public class EntityPlayerMixins {
#if ENABLE_NOCLIP_COMMAND
    @Inject(
        method = "isEntityInsideOpaqueBlock()Z",
        at = @At("HEAD"),
        cancellable = true
    )
    protected void isEntityInsideOpaqueBlock_cancel_if_noclip(CallbackInfoReturnable callback_info) {
        if (((EntityPlayer)(Object)this).noClip) {
            callback_info.setReturnValue(false);
        }
    }
#endif
}