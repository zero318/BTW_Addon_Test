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
public abstract class MetalSpikeBlockMixins extends Block {
    public MetalSpikeBlockMixins() {
        super(0, null);
    }
    @Override
    public boolean canProvidePower() {
        return true;
    }
    public void updateNeighborsInDirection(World world, int x, int y, int z, int direction) {
        x += Facing.offsetsXForSide[direction];
        y += Facing.offsetsYForSide[direction];
        z += Facing.offsetsZForSide[direction];
        MetalSpikeBlock self = (MetalSpikeBlock)(Object)this;
        Block neighborBlock = Block.blocksList[world.getBlockId(x, y, z)];
        if (!((neighborBlock)==null)) {
            neighborBlock.onNeighborBlockChange(world, x, y, z, self.blockID);
        }
        world.notifyBlocksOfNeighborChange(x, y, z, self.blockID, ((direction)^1));
    }
    public void refreshOutputState(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        int direction = (((meta)&7));
        boolean isReceivingPower = ((IWorldMixins)world).getBlockStrongPowerInputExceptFacing(x, y, z, direction) > 0;
        //AddonHandler.logMessage("SpikeUpdate: "+meta+" "+is_receiving_power);
        if (isReceivingPower != ((((meta)>7)))) {
            // 
            world.setBlockMetadataWithNotify(x, y, z, ((meta)^8), 0x02 | 0x80);
            this.updateNeighborsInDirection(world, x, y, z, direction);
        }
    }
    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        this.refreshOutputState(world, x, y, z);
    }
    @Override
    public void breakBlock(World world, int x, int y, int z, int idk, int prevMeta) {
        this.updateNeighborsInDirection(world, x, y, z, (((prevMeta)&7)));
    }
    @Inject(
        method = "onNeighborBlockChange",
        at = @At("TAIL")
    )
    public void onNeighborBlockChange_inject(World world, int x, int y, int z, int neighborId, CallbackInfo info) {
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
        if (world.getBlockId(x, y, z) != 0) {
            this.refreshOutputState(world, x, y, z);
        }
    }
    @Override
    public int isProvidingStrongPower(IBlockAccess blockAccess, int x, int y, int z, int side) {
        // This prevents supplying power to redstone wire
        // via the output block
        if (Block.redstoneWire.canProvidePower()) {
            return isProvidingWeakPower(blockAccess, x, y, z, side);
        }
        return 0;
    }
    @Override
    public int isProvidingWeakPower(IBlockAccess blockAccess, int x, int y, int z, int side) {
        int meta = blockAccess.getBlockMetadata(x, y, z);
        //AddonHandler.logMessage("SpikeUpdate: "+meta+" "+side);
        int direction;
        if (
            ((((meta)>7))) &&
            ((side)^1) == (direction = (((meta)&7)))
        ) {
            // Only check for strong power to prevent getting
            // powered by a weakly powered block
            return ((IWorldMixins)blockAccess).getBlockStrongPowerInputExceptFacing(x, y, z, direction);
        }
        return 0;
    }
    public boolean canRedstoneConnectToSide(IBlockAccess blockAccess, int x, int y, int z, int flatDirection) {
        return true;
    }
}
