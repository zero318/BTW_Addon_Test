#ifndef FEATURE_FLAGS_H
#define FEATURE_FLAGS_H 1

#include "util.h"

MACRO_VOID(/* Vanilla observers and shape updates */)
#define ENABLE_DIRECTIONAL_UPDATES 1

MACRO_VOID(/* Slime blocks */)
#define ENABLE_MOVING_BLOCK_CHAINING 1

MACRO_VOID(/* Push only and dead coral fans */)
#define ENABLE_PISTON_TEST_BLOCKS 1

MACRO_VOID(
// Allow slime to keep loose blocks
// suspended in midair as if they
// had mortar applied
)
#define ENABLE_SLIME_SUPPORTING_MORTAR_BLOCKS 1

MACRO_VOID(/* Fix how most BTW blocks receive power */)
#define ENABLE_LESS_CRAP_BTW_BLOCK_POWERING 1

MACRO_VOID(/* Allow block dispensers to respond to short pulses */)
#define ENABLE_LESS_CRAP_BLOCK_DISPENSER 1

MACRO_VOID(/* Block Breaker and Block Placer */)
#define ENABLE_BLOCK_DISPENSER_VARIANTS 1

MACRO_VOID(
// Changes Buddy Blocks to detect block placement
// and not detect comparators/pistons.
// This gives them much more utility together
// with observers.
)
#define ENABLE_BETTER_BUDDY_DETECTION 1

MACRO_VOID(/*
Done:
MC-8911
MC-9194
MC-10653
MC-12211
MC-63669
MC-195351

TODO:
MC-2255
MC-5951
MC-8645
MC-9405
MC-11109 (Specific to BTW blocks)
MC-11434
MC-94566
MC-120986
MC-182820
MC-230883

Investigate:
MC-711
MC-2340 (Might be fixed already?)
MC-11165
MC-81098
MC-169919
MC-197590

Well crap:
MC-11193
MC-172213

N/A (yet):
MC-54711

Won't fix:
MC-3703 (Breaks dust BUDs)
MC-5726 (Feature)
MC-8328 (Feature)
MC-8340 (Breaks common CUDs)
MC-9955
MC-64394
MC-157644
MC-200887

*/)
#define ENABLE_REDSTONE_BUGFIXES 1

MACRO_VOID(
// Remove the tile entity from comparators on the
// client since it never worked right to begin with
)
#define ENABLE_COMPARATOR_CLIENT_SIDE_ENTITY_REMOVAL 1

MACRO_VOID(/* Add a property for blocks to control where dust points */)
#define ENABLE_BETTER_REDSTONE_WIRE_CONNECTIONS 1

MACRO_VOID(
// Fix MC-2255, MC-8645, and MC-9405
// Generally make dust suck less
)
#define ENABLE_MODERN_REDSTONE_WIRE 1

#define ENABLE_REDSTONE_WIRE_DOT_SHAPE 0

#define ENABLE_MODERN_SUPPORT_LOGIC 0

#define ENABLE_NOCLIP_COMMAND 1

#define ENABLE_HOLLOW_COLLISION_BOXES 1

MACRO_VOID(
// Allow metal spikes to weakly conduct power to their bases
// TODO: Think of a way to prevent feedback loops...
)
#define ENABLE_CONDUCTIVE_METAL_SPIKES 0

#endif