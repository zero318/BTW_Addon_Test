package zero.test.block;

import net.minecraft.src.Block;

#include "..\ids.h"
#include "..\feature_flags.h"

public class ZeroTestBlocks {
    public static Block cud_block;
#if ENABLE_DIRECTIONAL_UPDATES
    public static Block observer_block;
#endif
#if ENABLE_MOVING_BLOCK_CHAINING
    public static Block slime_block;
    public static Block glue_block;
#endif
}