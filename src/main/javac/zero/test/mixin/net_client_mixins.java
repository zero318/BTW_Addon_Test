package zero.test.mixin;

import net.minecraft.src.*;

import btw.AddonHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

#include "..\feature_flags.h"

@Mixin(NetClientHandler.class)
public class NetClientHandlerMixins {
#if ENABLE_NOCLIP_COMMAND
    @Inject(
        method = "handleGameEvent(Lnet/minecraft/src/Packet70GameEvent;)V",
        at = @At("TAIL"),
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    public void set_noclip_packet(Packet70GameEvent packet, CallbackInfo info, EntityClientPlayerMP player, int event_type, int event_arg) {
        if (event_type == 318) {
            switch (event_arg) {
                case 0: // Disable noclip
                    player.noClip = false;
                    break;
                case 1: // Enable noclip
                    player.noClip = true;
                    break;
            }
            //AddonHandler.logMessage("Player noclip state B: "+player.noClip);
        }
    }
#endif
}