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
public abstract class EntityMixins implements IEntityMixins {

    public long timeOfLastPistonPush;
    public double pistonX;
    public double pistonY;
    public double pistonZ;
    
    public void moveEntityByPiston(double x, double y, double z, int direction, boolean isRegularPush) {
        Entity self = (Entity)(Object)this;
        if (x * x + y * y + z * z > 1.0E-7D) {
            long time = self.worldObj.getTotalWorldTime();
            if (time != this.timeOfLastPistonPush) {
                this.timeOfLastPistonPush = time;
                pistonX = pistonY = pistonZ = 0.0D;
            }
            double temp;
            if (x != 0.0D) {
                x = (temp = MATH_CLAMP(x + this.pistonX, -0.51D, 0.51D)) - this.pistonX;
                this.pistonX = temp;
                temp = x;
                y = z = 0.0D;
            }
            else if (y != 0.0D) {
                y = (temp = MATH_CLAMP(y + this.pistonY, -0.51D, 0.51D)) - this.pistonY;
                this.pistonY = temp;
                temp = y;
                z = 0.0D;
            }
            else {
                z = (temp = MATH_CLAMP(z + this.pistonZ, -0.51D, 0.51D)) - this.pistonZ;
                this.pistonZ = temp;
                temp = z;
            }
            if (Math.abs(temp) <= 1.0E-5D) {
                return;
            }
        }
        self.moveEntity(x, y, z);
    }
    
#if ENABLE_MINECART_LERP_FIXES
/*
    public double lerpTargetX() {
        return ((Entity)(Object)this).posX;
    }
    public double lerpTargetY() {
        return ((Entity)(Object)this).posY;
    }
    public double lerpTargetZ() {
        return ((Entity)(Object)this).posZ;
    }
*/
    public float lerpTargetPitch() {
        return ((Entity)(Object)this).rotationPitch;
    }
    public float lerpTargetYaw() {
        return ((Entity)(Object)this).rotationYaw;
    }
#endif
}