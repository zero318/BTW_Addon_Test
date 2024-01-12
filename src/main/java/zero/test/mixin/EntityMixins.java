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
    protected void pushOutOfBlocks_cancel_if_noclip(double x, double y, double z, CallbackInfoReturnable callbackInfo) {
        Entity self = (Entity)(Object)this;
        if (self instanceof EntityPlayer && self.noClip) {
            callbackInfo.setReturnValue(false);
        }
    }
    public long timeOfLastPistonPush;
    public double pistonX;
    public double pistonY;
    public double pistonZ;
    public int pistonDirection = -1;
    public int getPistonDirection() {
        return pistonDirection;
    }
    public void moveEntityByPiston(double x, double y, double z) {
        Entity self = (Entity)(Object)this;
        if (x * x + y * y + z * z > 1.0E-7D) {
            long time = self.worldObj.getTotalWorldTime();
            if (time != this.timeOfLastPistonPush) {
                this.timeOfLastPistonPush = time;
                pistonX = pistonY = pistonZ = 0.0D;
            }
            double temp;
            if (x != 0.0D) {
                this.pistonDirection = x < 0.0D ? 4 : 5;
                x = (temp = (Math.max(Math.min((x + this.pistonX),(0.51D)),(-0.51D)))) - this.pistonX;
                this.pistonX = temp;
                temp = x;
                y = z = 0.0D;
            }
            else if (y != 0.0D) {
                this.pistonDirection = y < 0.0D ? 0 : 1;
                y = (temp = (Math.max(Math.min((y + this.pistonY),(0.51D)),(-0.51D)))) - this.pistonY;
                this.pistonY = temp;
                temp = y;
                z = 0.0D;
            }
            else {
                this.pistonDirection = z < 0.0D ? 2 : 3;
                z = (temp = (Math.max(Math.min((z + this.pistonZ),(0.51D)),(-0.51D)))) - this.pistonZ;
                this.pistonZ = temp;
                temp = z;
            }
            if (Math.abs(temp) <= 1.0E-5D) {
                this.pistonDirection = -1;
                return;
            }
        }
        self.moveEntity(x, y, z);
        this.pistonDirection = -1;
    }
}
