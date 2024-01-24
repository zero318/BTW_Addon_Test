package zero.test;

import net.minecraft.src.*;

#include "feature_flags.h"
#include "util.h"

public interface IBaseRailBlockMixins {
    default public double getRailMaxSpeedFactor() {
        return 1.0D;
    }
}