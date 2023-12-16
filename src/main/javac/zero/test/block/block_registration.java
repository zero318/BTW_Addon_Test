package zero.test.block;

import net.minecraft.src.Block;

import zero.test.block.CUDBlock;

#include "..\ids.h"
#include "..\feature_flags.h"

public class ZeroTestBlocks {
    public static Block cud_block;// = new CUDBlock(CUD_BLOCK_ID);
#if ENABLE_DIRECTIONAL_UPDATES
    public static Block observer_block;
#endif
}