package zero.test.mixin;
import net.minecraft.src.*;
import btw.block.blocks.BasketBlock;
import btw.block.blocks.WickerBasketBlock;
import btw.block.tileentity.WickerBasketTileEntity;
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
// Block piston reactions

@Mixin(WickerBasketBlock.class)
public abstract class WickerBasketBlockMixins extends BasketBlock {
    public WickerBasketBlockMixins() {
        super(0);
    }
    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }
    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int flatDirection) {
        TileEntity tileEntity;
        if ((tileEntity = world.getBlockTileEntity(x, y, z)) instanceof WickerBasketTileEntity) {
            ItemStack stack;
            if ((stack = ((WickerBasketTileEntity)tileEntity).getStorageStack()) != null) {
                return MathHelper.floor_float(((float)stack.stackSize / (float)stack.getMaxStackSize()) * 14.0F) + 1;
            }
        }
        return 0;
    }
}
