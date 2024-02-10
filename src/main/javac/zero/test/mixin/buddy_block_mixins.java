package zero.test.mixin;

import net.minecraft.src.*;

import btw.block.blocks.BuddyBlock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

#include "..\feature_flags.h"
#include "..\util.h"

#define POWERED_META_OFFSET 0
#define DIRECTION_META_OFFSET 1

@Mixin(BuddyBlock.class)
public abstract class BuddyBlockMixins extends Block {
    
    public BuddyBlockMixins() {
        super(0, null);
    }
    
#if ENABLE_BETTER_BUDDY_DETECTION
    @Overwrite
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborId) {
        BuddyBlock self = (BuddyBlock)(Object)this;
        if (!self.isRedstoneOn(world, x, y, z)) {
            Block neighborBlock = Block.blocksList[neighborId];
            
            if (
                (neighborBlock == null || neighborBlock.triggersBuddy()) &&
                !world.isUpdatePendingThisTickForBlock(x, y, z, self.blockID)
            ) {
                // minimal delay when triggered to avoid notfying neighbors of change in same tick
                // that they are notifying of the original change. Not doing so causes problems 
                // with some blocks (like ladders) that haven't finished initializing their state 
                // on placement when they send out the notification
                
                world.scheduleBlockUpdate(x, y, z, self.blockID, 1); 
            }
        }
    }
#endif

#if ENABLE_BETTER_REDSTONE_WIRE_CONNECTIONS
    public boolean canRedstoneConnectToSide(IBlockAccess blockAccess, int x, int y, int z, int flatDirection) {
        return OPPOSITE_DIRECTION(READ_META_FIELD(blockAccess.getBlockMetadata(x, y, z), DIRECTION)) == Direction.directionToFacing[flatDirection];
    }
#endif
}