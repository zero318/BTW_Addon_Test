package zero.test.mixin;

import net.minecraft.src.*;

import btw.block.blocks.BuddyBlock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import zero.test.IBlockMixins;
import zero.test.IBlockEntityPistonMixins;

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
            
            TileEntity tileEntity;
            if (
                (
                    neighborBlock == null ||
#if BETTER_BUDDY_PISTON_FIX_TYPE == BETTER_BUDDY_PISTON_FIX_NONE
                    neighborBlock.triggersBuddy()
#elif BETTER_BUDDY_PISTON_FIX_TYPE == BETTER_BUDDY_PISTON_FIX_A
                    neighborBlock.triggersBuddy() || (
                        neighborId == Block.pistonMoving.blockID &&
                        (tileEntity = world.getBlockTileEntity(x, y, z)) instanceof TileEntityPiston &&
                        !((IBlockEntityPistonMixins)tileEntity).isRetractingBase()
                    )
#elif BETTER_BUDDY_PISTON_FIX_TYPE == BETTER_BUDDY_PISTON_FIX_B
                    ((IBlockMixins)neighborBlock).triggersBuddy(world, x, y, z)
#endif
                ) &&
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