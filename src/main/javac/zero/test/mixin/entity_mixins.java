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

#include "..\feature_flags.h"

@Mixin(Entity.class)
public class EntityMixins implements IEntityMixins {
#if ENABLE_NOCLIP_COMMAND
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
#endif

    public long timeOfLastPistonPush;
    public double pistonX;
    public double pistonY;
    public double pistonZ;
    
#define HYPER_OPTIMIZE_MATH_CODE 1
    
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
                X = (temp = MATH_CLAMP(X + this.pistonX, -0.51D, 0.51D)) - this.pistonX;
                this.pistonX = temp;
                temp = X;
                Y = Z = 0.0D;
            }
            else if (Y != 0.0D) {
                Y = (temp = MATH_CLAMP(Y + this.pistonY, -0.51D, 0.51D)) - this.pistonY;
                this.pistonY = temp;
                temp = Y;
                Z = 0.0D;
            }
            else {
                Z = (temp = MATH_CLAMP(Z + this.pistonZ, -0.51D, 0.51D)) - this.pistonZ;
                this.pistonZ = temp;
                temp = Z;
            }
            if (Math.abs(temp) <= 1.0E-5D) {
                return;
            }
        }
        self.moveEntity(X, Y, Z);
    }
}