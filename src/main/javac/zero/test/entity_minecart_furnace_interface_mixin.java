package zero.test;

import net.minecraft.src.*;

#include "feature_flags.h"
#include "util.h"

public interface IEntityMinecartFurnaceMixins {
#if ENABLE_MINECART_OVEN
    public int attemptToAddFuel(ItemStack stack);
#endif
}