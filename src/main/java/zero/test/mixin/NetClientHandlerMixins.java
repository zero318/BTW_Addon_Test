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
// Block piston reactions
@Mixin(NetClientHandler.class)
public abstract class NetClientHandlerMixins {
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
}
