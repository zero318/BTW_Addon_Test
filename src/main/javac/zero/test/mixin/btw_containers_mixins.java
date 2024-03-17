package zero.test.mixin;

import net.minecraft.src.*;

import btw.inventory.BTWContainers;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

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

import zero.test.block.block_entity.MixerBlockEntity;
import zero.test.gui.MixerGui;

#include "..\util.h"
#include "..\feature_flags.h"
#include "..\ids.h"

@Mixin(BTWContainers.class)
public abstract class BTWContainersMixins {
#if ENABLE_MIXER_BLOCK
    @Inject(
        method = "getAssociatedGui",
        at = @At("TAIL"),
        cancellable = true
    )
    private static void getAssociatedGui_custom_ids(EntityClientPlayerMP player, int containerId, CallbackInfoReturnable callbackInfo) {
        if (containerId == MIXER_CONTAINER_ID) {
            callbackInfo.setReturnValue(new MixerGui(player.inventory, new MixerBlockEntity()));
        }
    }
#endif
}
