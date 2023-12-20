package zero.test.mixin;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import zero.test.IWorldMixins;
@Mixin(TileEntityPiston.class)
public class BlockEntityPistonMixins {
    @Redirect(
  method = "restoreStoredBlock()V",
  at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/src/World;notifyBlockOfNeighborChange(IIII)V"
  )
 )
 public void force_update_observers(World world, int X, int Y, int Z, int neighbor_id) {
        ((IWorldMixins)world).forceNotifyBlockOfNeighborChange(X, Y, Z, neighbor_id);
    }
}
