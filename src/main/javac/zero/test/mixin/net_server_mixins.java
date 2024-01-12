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

import java.util.List;
import java.util.ArrayList;

#include "..\feature_flags.h"

@Mixin(NetServerHandler.class)
public class NetServerHandlerMixins {
#if ENABLE_NOCLIP_COMMAND
    @Inject(
        method = "getCollidingBoundingBoxesIgnoreSpecifiedEntities(Lnet/minecraft/src/World;Lnet/minecraft/src/Entity;Lnet/minecraft/src/AxisAlignedBB;)Ljava/util/List;",
        at = @At("HEAD"),
        cancellable = true
    )
    public void getCollidingBoundingBoxesIgnoreSpecifiedEntities_cancel_if_noclip(World world, Entity entity, AxisAlignedBB boundingBox, CallbackInfoReturnable callbackInfo) {
        if (entity instanceof EntityPlayer && entity.noClip) {
            callbackInfo.setReturnValue(new ArrayList());
        }
    }
#endif
}