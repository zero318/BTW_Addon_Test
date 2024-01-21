package zero.test;

import net.minecraft.src.*;

#include "feature_flags.h"
#include "util.h"

public interface IBaseRailBlockMixins {
#if ENABLE_WOODEN_RAILS
    default public double getRailMaxSpeedFactor() {
        return 1.0D;
    }
#endif
}