#ifndef FEATURE_FLAGS_H
#define FEATURE_FLAGS_H 1

#include "util.h"

MACRO_VOID(/* Add some items to help with testing things */)
#define ENABLE_DEBUG_STICKS 1

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

MACRO_VOID(/* Prevent block dispensers getting powered from their output face */)
#define ENABLE_BETTER_BLOCK_DISPENSER_POWERING 1

MACRO_VOID(/* Make block dispensers prioritize the nearest entity */)
#define ENABLE_BLOCK_DISPENSER_NEAREST_ENTITY_PRIORITY 1

MACRO_VOID(/* Block Breaker and Block Placer */)
#define ENABLE_BLOCK_DISPENSER_VARIANTS 1

MACRO_VOID(
// Changes Buddy Blocks to detect block placement
// and not detect comparators/pistons.
// This gives them much more utility together
// with observers.
)
#define ENABLE_BETTER_BUDDY_DETECTION 1

#define BETTER_BUDDY_PISTON_FIX_NONE 0
#define BETTER_BUDDY_PISTON_FIX_A 1
#define BETTER_BUDDY_PISTON_FIX_B 1

#define BETTER_BUDDY_PISTON_FIX_TYPE BETTER_BUDDY_PISTON_FIX_NONE

MACRO_VOID(
// Make buddy blocks ignore updates from
// buttons, levers, pressure plates, and
// other strong-power components. This
// is to cut down on confusion for them
// triggering buddy blocks through blocks,
// particularly when that block is a piston.
)
#define ENABLE_BUDDY_BLOCK_IGNORES_POWER_UPDATES 1

MACRO_VOID(/*
Done:
MC-8911
MC-9194
MC-10653
MC-12211
MC-63669
MC-195351
MC-2255
MC-8645
MC-9405

TODO:
MC-5951
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

MACRO_VOID(/* Add missing comparator support to BTW blocks */)
#define ENABLE_MORE_COMPARATOR_OUTPUTS 1

MACRO_VOID(/* Add a property for blocks to control where dust points */)
#define ENABLE_BETTER_REDSTONE_WIRE_CONNECTIONS 1

MACRO_VOID(
// Fix MC-2255, MC-8645, and MC-9405
// Generally make dust suck less
)
#define ENABLE_MODERN_REDSTONE_WIRE 1

#define ENABLE_REDSTONE_WIRE_DOT_SHAPE 0

#define MODERN_SUPPORT_LOGIC_DISABLED 0
#define MODERN_SUPPORT_LOGIC_PER_BLOCK 1
#define MODERN_SUPPORT_LOGIC_GLOBAL 2
#define MODERN_SUPPORT_LOGIC_GLOBAL_ALL 3
MACRO_VOID(
// Allow support requiring blocks to care less
// about transparency.
// Tiers:
// 0 Disabled
// 1 Overrides on individual blocks
// 2 Global override for solid tops
// 3 Global override for all hardpoints
)
#define ENABLE_MODERN_SUPPORT_LOGIC MODERN_SUPPORT_LOGIC_GLOBAL_ALL

#define ENABLE_NOCLIP_COMMAND 1

#define ENABLE_NOCLIP_ALT_IMPLEMENTATION 0

#define ENABLE_BLOCK_INTERACTION_DURING_NOCLIP 1

MACRO_VOID(/* Let things sit in cauldrons */)
#define ENABLE_HOLLOW_COLLISION_BOXES 1

MACRO_VOID(/* Allow metal spikes to weakly conduct power to their bases */)
#define ENABLE_CONDUCTIVE_METAL_SPIKES 1

MACRO_VOID(/* Allow platforms to stick to other platforms when moved */)
#define ENABLE_PLATFORMS_WITH_PISTONS 1

MACRO_VOID(/* Tweak the platform code to work properly with lifting blocks */)
#define ENABLE_PLATFORM_FIXES 1

MACRO_VOID(/* Rework the platform code to entirely to integrate slime */)
#define ENABLE_PLATFORM_EXTENSIONS 1

MACRO_VOID(/* Allow anchor blocks to stop a platform  */)
#define ENABLE_ANCHORS_STOPPING_PLATFORMS 1

MACRO_VOID(
// Try to fix platforms by using entity UUIDs
// to record neighbor connections
)
#define ENABLE_PLATFORM_UUID_REWORK 0

MACRO_VOID(/* Relax the hardpoint requirements for fences/panes */)
#define ENABLE_CONNECTED_BLOCK_TWEAKS 1

MACRO_VOID(/* Prevent shoveling slime/glue despite regular shovels being effective */)
#define DISABLE_SLIME_AND_GLUE_PISTON_SHOVEL 0

#define ENABLE_MORE_MOVING_BLOCK_HARDPOINTS 1

MACRO_VOID(/* */)
#define ENABLE_REDSTONE_DUST_ROUTING 0

MACRO_VOID(/*
Done:
MC-10186
MC-14850
MC-51053
MC-163375
MC-200544

Todo:
MC-8265
MC-9157
MC-9551
MC-170907
MC-181760

Investigate:
MC-4057
MC-51632
MC-71779
MC-79528
MC-80895
MC-92484
MC-158169
MC-158363
MC-167889
MC-172407
MC-179971
MC-214442
MC-267151

Well crap:
MC-868
MC-113242
MC-123367
MC-142045
MC-148433
MC-258707

N/A:
MC-51676 (Already fixed?)
MC-113871

Won't fix:
MC-14
MC-957
MC-2714
MC-3430
MC-89221
MC-101818
MC-144321 (Why isn't this invalid?)
MC-191723
MC-201621

*/)
#define ENABLE_MINECART_FIXES 1

MACRO_VOID(/* Prevent minecarts having their clientside hitbox way above the rail */)
#define ENABLE_MINECART_HITBOX_FIXES 1

MACRO_VOID(/* Allow minecarts to visually rotate 360 degrees */)
#define ENABLE_MINECART_FULL_ROTATION 0

MACRO_VOID(/* */)
#define ENABLE_BETTER_MINECART_CLIENT_YAW 0

MACRO_VOID(/* */)
#define ENABLE_VERTICAL_CLIENT_CART_SNAPPING 0

MACRO_VOID(/* */)
#define ENABLE_MINECART_LERP_FIXES 0

MACRO_VOID(/* Make LAN world sharing suck less */)
#define ENABLE_MODERN_PUBLISH_COMMAND 1

MACRO_VOID(/* */)
#define ENABLE_BETTER_HOPPER_BLOCKING 0

MACRO_VOID(/* Reskin furnace minecarts to be ovens */)
#define ENABLE_MINECART_OVEN 1

MACRO_VOID(/* Allow hoppers to add fuel to minecarts */)
#define ENABLE_HOPPERS_FUELING_CARTS 1

MACRO_VOID(/* */)
#define ENABLE_MINECART_BLOCK_DISPENSER 0

#if ENABLE_MINECART_OVEN || ENABLE_MINECART_BLOCK_DISPENSER
MACRO_VOID(/* Restore activator rails */)
#define ENABLE_ACTIVATOR_RAILS 1
#endif

MACRO_VOID(/* Stop the camera spinning wildly in minecarts */)
#define ENABLE_STABLE_MINECART_CAMERA 1

MACRO_VOID(/* Cheap/slow rails. Admittedly not a very original idea. */)
#define ENABLE_WOODEN_RAILS 1

MACRO_VOID(/* Expensive/fast rails */)
#define ENABLE_STEEL_RAILS 1

MACRO_VOID(/* Fixes MC-342, MC-2783, and MC-170907 (all the same bug) */)
#define ENABLE_FIX_FOR_NETWORK_POSITION_DELTAS 1

MACRO_VOID(/* BROKEN */)
#define ENABLE_TEXTURED_BOX 0

MACRO_VOID(/* A block that prevents minecarts bouncing off each other */)
#define ENABLE_RAIL_BUFFER_STOP 1

MACRO_VOID(/* Reduce the cost of a soap block to 4 soap */)
#define ENABLE_CHEAPER_SOAP_BLOCK 1

MACRO_VOID(/* Allow rails to be placed on hoppers */)
#define ENABLE_MORE_RAIL_PLACEMENTS 1

MACRO_VOID(/* Allow buttons to be placed on the top/bottom of blocks */)
#define ENABLE_VERTICAL_BUTTONS 1

MACRO_VOID(/* The worst version of scaffolding you've ever seen */)
#define ENABLE_SCAFFOLDING 1

MACRO_VOID(/* Make detectors only update adjacent blocks */)
#define ENABLE_NORMAL_DETECTOR_POWER_RANGE 1

MACRO_VOID(/* Allow turntables to rotate arbitrary blocks via slime */)
#define ENABLE_TURNTABLE_SLIME_SUPPORT 1

MACRO_VOID(/* Allow more blocks to rotate on turntables like levers, doors, and tools */)
#define ENABLE_MORE_TURNABLE_BLOCKS 1

MACRO_VOID(/* */)
#define ENABLE_MORE_AUTOMATION_RECIPES 1

MACRO_VOID(/* */)
#define ENABLE_AUTOMATIC_COW_MILKING 1

MACRO_VOID(/* */)
#define ENABLE_MIXER_BLOCK 1

MACRO_VOID(/* */)
#define ENABLE_PISTON_TILE_ENTITY_CACHE 0

MACRO_VOID(/* */)
#define ENABLE_PLATFORM_TILE_ENTITY_CACHE 1

MACRO_VOID(/* Use a different random curve for dispenser velocity */)
#define ENABLE_MORE_RELIABLE_RANDOMS 1

MACRO_VOID(/* Reenable droppers but without the ability to insert into containers */)
#define ENABLE_NERFED_DROPPER 1

MACRO_VOID(/* Breaking a moving piston will drop the items from a moved tile entity */)
#define ENABLE_MOVING_PISTON_ITEM_DROPS 1

MACRO_VOID(/* Try to be compatible with the metadata extension addon */)
#define ENABLE_METADATA_EXTENSION_COMPAT 1

MACRO_VOID(/* Try to be compatible with craftguide */)
#define ENABLE_CRAFTGUIDE_COMPAT 1

MACRO_VOID(/* DO NOT ENABLE */)
#define ENABLE_LIGHT_UPDATES_ON_METADATA_CHANGE 0

MACRO_VOID(/* Special light update stick for testing */)
#define ENABLE_LIGHT_STICKS 1

#define ENABLE_DEBUG_STRING_JANK 0

#if ENABLE_DEBUG_STRING_JANK
#define DEBUG_PRINT(...) ZeroUtil.debug_print(__VA_ARGS__)
#else
#define DEBUG_PRINT(...)
#endif

MACRO_VOID(
/// DECO FIXES
)

MACRO_VOID(
// More accurate hitboxes for:
// - Chairs
)
#define ENABLE_BETTER_DECO_HITBOXES 1

#endif