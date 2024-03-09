package zero.test.mixin;
import net.minecraft.src.*;
import btw.block.BTWBlocks;
import btw.block.blocks.PlacedToolBlock;
import btw.block.tileentity.PlacedToolTileEntity;
import btw.item.items.ToolItem;
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

@Mixin(PlacedToolTileEntity.class)
public abstract class PlacedToolTileEntityMixins extends TileEntity {
    @Inject(
        method = "setToolStack(Lnet/minecraft/src/ItemStack;)V",
        at = @At("TAIL")
    )
    public void set_tool_stack_inject(ItemStack stack, CallbackInfo info) {
        this.worldObj.func_96440_m(this.xCoord, this.yCoord, this.zCoord, BTWBlocks.placedTool.blockID);
    }
}
