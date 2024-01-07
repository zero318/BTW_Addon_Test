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
import zero.test.IEntityMixins;
// Block piston reactions

@Mixin(Entity.class)
public class EntityMixins implements IEntityMixins {
    @Inject(
        method = "pushOutOfBlocks(DDD)Z",
        at = @At("HEAD"),
        cancellable = true
    )
    protected void pushOutOfBlocks_cancel_if_noclip(double X, double Y, double Z, CallbackInfoReturnable callback_info) {
        Entity self = (Entity)(Object)this;
        if (self instanceof EntityPlayer && self.noClip) {
            //AddonHandler.logMessage("Player noclip state C: "+self.noClip);
            callback_info.setReturnValue(false);
            callback_info.cancel();
        }
    }
    public long timeOfLastPistonPush;
    public double pistonX;
    public double pistonY;
    public double pistonZ;
    public void moveEntityByPiston(double X, double Y, double Z) {
        Entity self = (Entity)(Object)this;
        if (X * X + Y * Y + Z * Z > 1.0E-7D) {
            long time = self.worldObj.getTotalWorldTime();
            double temp;
            if (time != this.timeOfLastPistonPush) {
                this.timeOfLastPistonPush = time;
                pistonX = pistonY = pistonZ = 0.0D;
            }
            if (X != 0.0D) {
                temp = Math.max(Math.min(X + this.pistonX, 0.51D), -0.51D);
                X = temp - this.pistonX;
                this.pistonX = temp;
                temp = X;
                Y = Z = 0.0D;
            }
            else if (Y != 0.0D) {
                temp = Math.max(Math.min(Y + this.pistonY, 0.51D), -0.51D);
                Y = temp - this.pistonY;
                this.pistonY = temp;
                temp = Y;
                Z = 0.0D;
            }
            else /*if (Z != 0.0D)*/ {
                temp = Math.max(Math.min(Z + this.pistonZ, 0.51D), -0.51D);
                Z = temp - this.pistonZ;
                this.pistonZ = temp;
                temp = Z;
            }
            /*else {
                return;
            }*/
            if (Math.abs(temp) <= 1.0E-5D) {
                return;
            }
        }
        self.moveEntity(X, Y, Z);
    }
}
