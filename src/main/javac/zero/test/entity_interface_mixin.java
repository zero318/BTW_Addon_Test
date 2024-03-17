package zero.test;

import btw.AddonHandler;

import net.minecraft.src.*;

#include "feature_flags.h"
#include "util.h"

public interface IEntityMixins {
    public void moveEntityByPiston(double x, double y, double z, int direction, boolean isRegularPush);
    
    default public int getPistonMobilityFlags(int direction) {
        return PISTON_CAN_MOVE | PISTON_CAN_BOUNCE | PISTON_CAN_STICK;
    }
    
    default public double getPistonBounceMultiplier(int direction) {
        return 1.0D;
    }
    
#if ENABLE_MINECART_LERP_FIXES
    //public double lerpTargetX();
    //public double lerpTargetY();
    //public double lerpTargetZ();
    public float lerpTargetPitch();
    public float lerpTargetYaw();
#endif
}