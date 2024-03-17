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
    public static Block iron_trapdoor;
#if ENABLE_PISTON_TEST_BLOCKS
    public static Block pull_only_test_block;
    public static Block dead_coral_fan;
#endif
#if ENABLE_BLOCK_DISPENSER_VARIANTS
    public static Block block_breaker;
    public static Block block_placer;
#endif
#if ENABLE_WOODEN_RAILS
    public static Block wooden_rail;
#endif
#if ENABLE_STEEL_RAILS
    public static Block steel_rail;
#endif
#if ENABLE_RAIL_BUFFER_STOP
    public static Block buffer_stop;
#endif
#if ENABLE_SCAFFOLDING
    public static Block scaffolding;
#endif
#if ENABLE_MIXER_BLOCK
    public static Block mixer_block;
#endif
}