package zero.test.mixin;
import net.minecraft.src.*;
import btw.AddonHandler;
import btw.block.blocks.MetalSpikeBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import zero.test.IWorldMixins;
import java.util.Random;
@Mixin(MetalSpikeBlock.class)
public class MetalSpikeBlockMixins {
    public boolean canProvidePower() {
        return true;
    }
    public void updateNeighborsInDirection(World world, int X, int Y, int Z, int direction) {
        X += Facing.offsetsXForSide[direction];
        Y += Facing.offsetsYForSide[direction];
        Z += Facing.offsetsZForSide[direction];
        MetalSpikeBlock self = (MetalSpikeBlock)(Object)this;
        Block neighbor_block = Block.blocksList[world.getBlockId(X, Y, Z)];
        if (!((neighbor_block)==null)) {
            neighbor_block.onNeighborBlockChange(world, X, Y, Z, self.blockID);
        }
        world.notifyBlocksOfNeighborChange(X, Y, Z, self.blockID, ((direction)^1));
    }
    public void refreshOutputState(World world, int X, int Y, int Z) {
        int meta = world.getBlockMetadata(X, Y, Z);
        int direction = (((meta)&7));
        boolean is_receiving_power = ((IWorldMixins)world).getBlockStrongPowerInputExceptFacing(X, Y, Z, direction) > 0;
        //AddonHandler.logMessage("SpikeUpdate: "+meta+" "+is_receiving_power);
        if (is_receiving_power != ((((meta)>7)))) {
            // 
            world.setBlockMetadataWithNotify(X, Y, Z, meta ^ 8, 0x02 | 0x80);
            this.updateNeighborsInDirection(world, X, Y, Z, direction);
        }
    }
    /*
    @Inject(
        method = "onBlockAdded",
        at = @At("TAIL")
    )
    */
    public void onBlockAdded(World world, int X, int Y, int Z) {
        this.refreshOutputState(world, X, Y, Z);
    }
    /*
    @Inject(
        method = "breakBlock",
        at = @At("TAIL")
    )
    */
    public void breakBlock(World world, int X, int Y, int Z, int idk, int prev_meta) {
        this.updateNeighborsInDirection(world, X, Y, Z, (((prev_meta)&7)));
    }
    @Inject(
        method = "onNeighborBlockChange",
        at = @At("TAIL")
    )
    public void onNeighborBlockChange_inject(World world, int X, int Y, int Z, int neighbor_id, CallbackInfo info) {
        // The block can already be set to air
        // when updated normally since this is
        // only added to the tail of the function
        // and is run after support checks, so
        // make sure that hasn't happened before
        // doing anything with power states.
        //
        // TODO: Do indirect neighbors still need
        // to be notified, or would removing the
        // block already take care of that?
        if (world.getBlockId(X, Y, Z) != 0) {
            this.refreshOutputState(world, X, Y, Z);
        }
    }
    public int isProvidingStrongPower(IBlockAccess block_access, int X, int Y, int Z, int side) {
        // This prevents supplying power to redstone wire
        // via the output block
        if (Block.redstoneWire.canProvidePower()) {
            return isProvidingWeakPower(block_access, X, Y, Z, side);
        }
        return 0;
    }
    public int isProvidingWeakPower(IBlockAccess block_access, int X, int Y, int Z, int side) {
        int meta = block_access.getBlockMetadata(X, Y, Z);
        //AddonHandler.logMessage("SpikeUpdate: "+meta+" "+side);
        int direction;
        if (
            ((((meta)>7))) &&
            ((side)^1) == (direction = (((meta)&7)))
        ) {
            // Only check for strong power to prevent getting
            // powered by a weakly powered block
            return ((IWorldMixins)block_access).getBlockStrongPowerInputExceptFacing(X, Y, Z, direction);
        }
        return 0;
    }
    public boolean canRedstoneConnectToSide(IBlockAccess block_access, int X, int Y, int Z, int flat_direction) {
        return true;
    }
}
