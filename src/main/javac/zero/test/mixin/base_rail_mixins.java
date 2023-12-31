package zero.test.mixin;

import net.minecraft.src.*;

import btw.AddonHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import zero.test.IWorldMixins;
import zero.test.IBlockBaseRailLogicMixins;
import zero.test.mixin.IBlockBaseRailLogicAccessMixins;

#include "..\feature_flags.h"
#include "..\util.h"

#define RAIL_INVALID -1
#define RAIL_NORTH_SOUTH 0
#define RAIL_EAST_WEST 1
#define RAIL_ASCENDING_EAST 2
#define RAIL_ASCENDING_WEST 3
#define RAIL_ASCENDING_NORTH 4
#define RAIL_ASCENDING_SOUTH 5
#define RAIL_SOUTH_EAST 6
#define RAIL_SOUTH_WEST 7
#define RAIL_NORTH_WEST 8
#define RAIL_NORTH_EAST 9

#define POWERED_META_OFFSET 3

@Mixin(BlockRailBase.class)
public class BlockRailBaseMixins {
    
    /*
    @Inject(
        method = "breakBlock(Lnet/minecraft/src/World;IIIII)V",
        at = @At("HEAD"),
        cancellable = true
    )
    public void break_block_new(World world, int X, int Y, int Z, int par5, int meta, CallbackInfo callback_info) {
        if (((IWorldMixins)world).get_is_handling_piston_move()) {
            //AddonHandler.logMessage("Cancel break rail");
            callback_info.cancel();
        }
        //AddonHandler.logMessage("Break rail");
    }
    */
    
    //@Override
    public int preBlockPlacedBy(World world, int X, int Y, int Z, int meta, EntityLiving entity_living) {
        return entity_living instanceof EntityPlayer &&
               AXIS_Z != DIRECTION_AXIS(Direction.directionToFacing[(int)Math.floor(entity_living.rotationYaw / 90.0 + 0.5) & 3])
                ? RAIL_EAST_WEST
                : RAIL_NORTH_SOUTH;
    }
    
#if ENABLE_PLATFORM_FIXES
    public int getPlatformMobilityFlag(World world, int X, int Y, int Z) {
        return PLATFORM_CAN_LIFT;
    }
    
    public int adjustMetadataForPlatformMove(int meta) {
        return ((BlockRailBase)(Object)this).isPowered() ? MERGE_META_FIELD(meta, POWERED, false) : meta;
    }
#endif
}