package zero.test.mixin;
import net.minecraft.src.*;
import btw.block.blocks.BasketBlock;
import btw.block.blocks.HamperBlock;
import btw.block.tileentity.HamperTileEntity;
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

@Mixin(HamperBlock.class)
public abstract class HamperBlockMixins extends BasketBlock {
    public HamperBlockMixins() {
        super(0);
    }
    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }
    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int flatDirection) {
        TileEntity tileEntity;
        if ((tileEntity = world.getBlockTileEntity(x, y, z)) instanceof IInventory) {
            return Container.calcRedstoneFromInventory((IInventory)tileEntity);
        }
        return 0;
    }
}
