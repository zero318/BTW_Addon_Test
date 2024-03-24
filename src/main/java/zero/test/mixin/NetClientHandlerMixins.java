package zero.test.mixin;
import net.minecraft.src.*;
import btw.AddonHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import zero.test.IEntityMixins;
import zero.test.IMovingPlatformEntityMixins;
@Mixin(NetClientHandler.class)
public abstract class NetClientHandlerMixins {
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
/*
    @Redirect(
        method = "handleVehicleSpawn",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/EntityList;createEntityOfType(Ljava/lang/Class;[Ljava/lang/Object;)Lnet/minecraft/src/Entity;",
            ordinal = 20
        )
    )
    public static Entity createMovingPlatform_redirect(Class entityClass, Object[] parameters) {
        Entity entity = EntityList.createEntityOfType(entityClass, parameters);
    }
*/
    private static int platform_block_state;
    @Inject(
        method = "handleVehicleSpawn(Lnet/minecraft/src/Packet23VehicleSpawn;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/EntityList;createEntityOfType(Ljava/lang/Class;[Ljava/lang/Object;)Lnet/minecraft/src/Entity;",
            ordinal = 20
        )
    )
    public void createMovingPlatform_inject(Packet23VehicleSpawn vehicle_packet, CallbackInfo info) {
        platform_block_state = vehicle_packet.throwerEntityId;
        vehicle_packet.throwerEntityId = 0;
    }
    @ModifyVariable(
        method = "handleVehicleSpawn(Lnet/minecraft/src/Packet23VehicleSpawn;)V",
        name = "var8",
        at = @At(
            value = "STORE",
            ordinal = 22
        )
    )
    public Object createMovingPlatform_variable_modify(Object object) {
        if (platform_block_state != 0) {
            int blockId;
            int blockMeta;
            {(blockId)=((int)(platform_block_state)&0xFFFF);(blockMeta)=((int)(platform_block_state)>>>16);};
            platform_block_state = 0;
            ((IMovingPlatformEntityMixins)object).setBlockId(blockId);
            ((IMovingPlatformEntityMixins)object).setBlockMeta(blockMeta);
        }
        return object;
    }
}
