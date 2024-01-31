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

import zero.test.IEntityMixins;

#include "..\feature_flags.h"
#include "..\util.h"

@Mixin(NetClientHandler.class)
public abstract class NetClientHandlerMixins {
#if ENABLE_NOCLIP_COMMAND
#if !ENABLE_NOCLIP_ALT_IMPLEMENTATION
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
        }
    }
#else
    
    @Shadow
    public abstract Entity getEntityByID(int entityId);
    
    @Inject(
        method = "handleGameEvent(Lnet/minecraft/src/Packet70GameEvent;)V",
        at = @At("TAIL")
    )
    public void set_noclip_packet(Packet70GameEvent packet, CallbackInfo info) {
        int eventType;
        switch (eventType = packet.eventType) {
            case 318: // Disable noclip
            case 319: // Enable noclip
                Entity entity = this.getEntityByID(packet.gameMode);
                if (entity != null) {
                    entity.noClip = eventType == 319;
                }
        }
    }
    
#endif
#endif

#if ENABLE_MINECART_LERP_FIXES
    @Overwrite
    public void handleEntity(Packet30Entity packet) {
        NetClientHandler self = (NetClientHandler)(Object)this;
        Entity entity = ((INetClientHandlerAccessMixins)self).callGetEntityByID(packet.entityId);

        if (entity != null) {
            entity.serverPosX += packet.xPosition;
            entity.serverPosY += packet.yPosition;
            entity.serverPosZ += packet.zPosition;
            double x = (double)entity.serverPosX / 32.0D;
            double y = (double)entity.serverPosY / 32.0D;
            double z = (double)entity.serverPosZ / 32.0D;
            float yaw = packet.rotating ? (float)(packet.yaw * 360) / 256.0F : ((IEntityMixins)entity).lerpTargetYaw();
            float pitch = packet.rotating ? (float)(packet.pitch * 360) / 256.0F : ((IEntityMixins)entity).lerpTargetPitch();
            entity.setPositionAndRotation2(x, y, z, yaw, pitch, 3);
        }
    }
#endif
}