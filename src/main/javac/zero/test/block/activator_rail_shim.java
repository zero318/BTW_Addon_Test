package zero.test.block;

import net.minecraft.src.*;

#include "..\feature_flags.h"
#include "..\util.h"

public class ActivatorRailShim
#if ENABLE_ACTIVATOR_RAILS
extends BlockRailPowered
#endif
{
#if ENABLE_ACTIVATOR_RAILS
    public ActivatorRailShim(int blockId) {
        super(blockId);
    }
#endif
}