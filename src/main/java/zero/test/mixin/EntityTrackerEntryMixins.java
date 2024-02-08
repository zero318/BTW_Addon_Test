package zero.test.mixin;
import net.minecraft.src.*;
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
import org.objectweb.asm.Opcodes;
//import zero.test.mixin.IDataWatcherAccessMixins;
// Block piston reactions
@Mixin(EntityTrackerEntry.class)
public abstract class EntityTrackerEntryMixins {
    @Shadow
    private double posX;
    @Shadow
    private double posY;
    @Shadow
    private double posZ;
    @Shadow
    private int ticksSinceLastForcedTeleport;
    /*
    @Redirect(
        method = "getPacketForThisEntity()Lnet/minecraft/src/Packet;",
        at = @At(
            value = "NEW",
            target = "net/minecraft/src/Packet23VehicleSpawn"
        )
    )
    private Packet force_entity_sync_A(Entity entity, int idk1) {
        Packet23VehicleSpawn packet = new Packet23VehicleSpawn(entity, idk1);
    }
    */
    /*
    @Inject(
        method = "sendLocationToAllClients(Ljava/util/List;)V",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/src/EntityTrackerEntry;isDataInitialized:Z",
            opcode = Opcodes.PUTFIELD
        )
    )
    */
    /*
    @Inject(
        method = "sendLocationToAllClients(Ljava/util/List;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/EntityTrackerEntry;sendEventsToPlayers(Ljava/util/List;)V"
        )
    )
    private void force_entity_sync_C(CallbackInfo info) {
        this.ticksSinceLastForcedTeleport = 400;
    }
    */
    @Inject(
        method = "tryStartWachingThis(Lnet/minecraft/src/EntityPlayerMP;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/EntityTrackerEntry;getPacketForThisEntity()Lnet/minecraft/src/Packet;"
        )
    )
    private void force_entity_sync_D(CallbackInfo info) {
        this.ticksSinceLastForcedTeleport = 400;
        //EntityTrackerEntry self = (EntityTrackerEntry)(Object)this;
        //self.lastScaledXPosition = MathHelper.floor_double(this.posX = self.myEntity.posX);
        //self.lastScaledYPosition = MathHelper.floor_double(this.posY = self.myEntity.posY);
        //self.lastScaledZPosition = MathHelper.floor_double(this.posZ = self.myEntity.posZ);
        //EntityTrackerEntry self = (EntityTrackerEntry)(Object)this;
        //((IDataWatcherAccessMixins)self.myEntity.getDataWatcher()).setObjectChanged(true);
    }
}
