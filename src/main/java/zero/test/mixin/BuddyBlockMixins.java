package zero.test.mixin;
import net.minecraft.src.*;
import btw.block.blocks.BuddyBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
// Block piston reactions
@Mixin(BuddyBlock.class)
public class BuddyBlockMixins {
    @Overwrite(remap=false)
    public void method_408(World world, int x, int y, int z, int neighborId) {
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
    public boolean canRedstoneConnectToSide(IBlockAccess blockAccess, int x, int y, int z, int flatDirection) {
        return (((((blockAccess.getBlockMetadata(x, y, z))>>>1)))^1) == Direction.directionToFacing[flatDirection];
    }
}
