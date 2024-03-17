package zero.test;

import btw.AddonHandler;

import net.minecraft.src.*;

#include "feature_flags.h"
#include "util.h"

public interface ICowMixins {
#if ENABLE_AUTOMATIC_COW_MILKING
    public abstract void pistonMilk();
#endif
}