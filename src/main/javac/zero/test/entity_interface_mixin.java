package zero.test;

import btw.AddonHandler;

import net.minecraft.src.*;

#include "feature_flags.h"

public interface IEntityMixins {
    public void moveEntityByPiston(double X, double Y, double Z);
}