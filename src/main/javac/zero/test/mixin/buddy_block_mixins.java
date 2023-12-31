package zero.test.mixin;

import net.minecraft.src.*;

import btw.block.blocks.BuddyBlock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

#include "..\feature_flags.h"
#include "..\util.h"

#define onNeighborBlockChange(...) method_408(__VA_ARGS__)

#define POWERED_META_OFFSET 0
#define DIRECTION_META_OFFSET 1

@Mixin(BuddyBlock.class)
public class BuddyBlockMixins {
#if ENABLE_BETTER_BUDDY_DETECTION
    @Overwrite(remap=false)
    public void onNeighborBlockChange(World world, int X, int Y, int Z, int neighbor_id) {
        BuddyBlock self = (BuddyBlock)(Object)this;
        if (!self.isRedstoneOn(world, X, Y, Z)) {
            Block neighbor_block = Block.blocksList[neighbor_id];
            
            if (
                (neighbor_block == null || neighbor_block.triggersBuddy()) &&
                !world.isUpdatePendingThisTickForBlock(X, Y, Z, self.blockID)
            ) {
                // minimal delay when triggered to avoid notfying neighbors of change in same tick
                // that they are notifying of the original change. Not doing so causes problems 
                // with some blocks (like ladders) that haven't finished initializing their state 
                // on placement when they send out the notification
                
                world.scheduleBlockUpdate(X, Y, Z, self.blockID, 1); 
            }
        }
    }
#endif

#if ENABLE_BETTER_REDSTONE_WIRE_CONNECTIONS
    public boolean canRedstoneConnectToSide(IBlockAccess block_access, int X, int Y, int Z, int flat_direction) {
        return OPPOSITE_DIRECTION(READ_META_FIELD(block_access.getBlockMetadata(X, Y, Z), DIRECTION)) == Direction.directionToFacing[flat_direction];
    }
#endif
}