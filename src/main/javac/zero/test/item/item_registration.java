package zero.test.item;

import net.minecraft.src.*;

#include "..\feature_flags.h"

public class ZeroTestItems {
#if ENABLE_DEBUG_STICKS
    public static Item debug_stick;
#endif
}