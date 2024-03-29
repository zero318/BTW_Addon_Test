package zero.test.mixin;
import net.minecraft.src.*;
import btw.block.tileentity.CookingVesselTileEntity;
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

@Mixin(CookingVesselTileEntity.class)
public interface CookingVesselTileEntityAccessMixins {
    @Accessor
    public boolean getForceValidateOnUpdate();
    @Accessor
    public void setForceValidateOnUpdate(boolean value);
    @Invoker("attemptToEjectStackFromInv")
    public abstract void callAttemptToEjectStackFromInv(int tiltFacing);
}
