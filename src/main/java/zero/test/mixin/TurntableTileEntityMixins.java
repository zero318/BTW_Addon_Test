package zero.test.mixin;
import net.minecraft.src.*;
import btw.block.BTWBlocks;
import btw.block.blocks.TurntableBlock;
import btw.block.tileentity.TurntableTileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import zero.test.TurntableResolver;
// Block piston reactions
@Mixin(TurntableTileEntity.class)
public abstract class TurntableTileEntityMixins extends TileEntity {
    private static final TurntableResolver server_resolver = new TurntableResolver();
    private static final TurntableResolver client_resolver = new TurntableResolver();
    @Overwrite(remap=false)
    public void rotateTurntable() {
        TurntableTileEntity self = (TurntableTileEntity)(Object)this;
        boolean reverse = ((TurntableBlock)BTWBlocks.turntable).isBlockRedstoneOn(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
        int craftingCounter = (!this.worldObj.isRemote ? server_resolver : client_resolver).turnBlocks(this.worldObj, this.xCoord, this.yCoord, this.zCoord, reverse, self.craftingRotationCount);
        self.craftingRotationCount = craftingCounter > self.craftingRotationCount ? craftingCounter : 0;
        this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, BTWBlocks.turntable.blockID);
    }
}
