package zero.test.mixin;

import net.minecraft.src.*;

import btw.AddonHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import zero.test.IWorldMixins;
import zero.test.IBlockBaseRailLogicMixins;
import zero.test.mixin.IBlockBaseRailLogicAccessMixins;
import zero.test.IBaseRailBlockMixins;

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
public abstract class BlockRailBaseMixins extends Block implements IBaseRailBlockMixins {
    
    public BlockRailBaseMixins() {
        super(0, null);
    }
    
    /*
    @Inject(
        method = "breakBlock(Lnet/minecraft/src/World;IIIII)V",
        at = @At("HEAD"),
        cancellable = true
    )
    public void break_block_new(World world, int x, int y, int z, int par5, int meta, CallbackInfo callbackInfo) {
        if (((IWorldMixins)world).get_is_handling_piston_move()) {
            //AddonHandler.logMessage("Cancel break rail");
            callbackInfo.cancel();
        }
        //AddonHandler.logMessage("Break rail");
    }
    */
    
    // Make rail placement match the direction players are facing
    @Override
    public int preBlockPlacedBy(World world, int x, int y, int z, int meta, EntityLiving entityLiving) {
        return /*entityLiving instanceof EntityPlayer &&*/
               AXIS_Z != DIRECTION_AXIS(YAW_DIRECTION(entityLiving.rotationYaw))
                ? RAIL_EAST_WEST
                : RAIL_NORTH_SOUTH;
    }
    
#if ENABLE_PLATFORM_FIXES
    public int getPlatformMobilityFlag(World world, int x, int y, int z) {
        return PLATFORM_CAN_LIFT;
    }
    
    public int adjustMetadataForPlatformMove(int meta) {
        return ((BlockRailBase)(Object)this).isPowered() ? MERGE_META_FIELD(meta, POWERED, false) : meta;
    }
#endif

#if ENABLE_MORE_RAIL_PLACEMENTS
    @Redirect(
        method = { "canPlaceBlockAt(Lnet/minecraft/src/World;III)Z", "onNeighborBlockChange(Lnet/minecraft/src/World;IIII)V" },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/World;doesBlockHaveSolidTopSurface(III)Z"
        )
    )
    public boolean doesBlockHaveSolidTopSurface_redirect(World world, int x, int y, int z) {
        return ((IWorldMixins)world).doesBlockSupportRails(x, y, z);
    }
#endif
}