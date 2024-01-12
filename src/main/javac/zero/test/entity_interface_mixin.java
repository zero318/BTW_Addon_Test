package zero.test;

import btw.AddonHandler;

import net.minecraft.src.*;

#include "feature_flags.h"
#include "util.h"

public interface IEntityMixins {
    public void moveEntityByPiston(double x, double y, double z);
    
    default public int getPistonMobilityFlags(int direction) {
        return PISTON_CAN_MOVE | PISTON_CAN_BOUNCE | PISTON_CAN_STICK;
    }
    
    default public double getPistonBounceMultiplier(int direction) {
        return 1.0D;
    }
}