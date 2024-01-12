package zero.test.mixin;

import net.minecraft.src.*;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import btw.block.blocks.ComparatorBlock;

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

import zero.test.mixin.IBlockComparatorAccessMixins;
import zero.test.mixin.IBlockRedstoneLogicAccessMixins;
import zero.test.IBlockRedstoneLogicMixins;
import zero.test.IRenderBlocksMixins;

@Mixin(ComparatorBlock.class)
public abstract class ComparatorBlockMixins  extends BlockComparator {
    public ComparatorBlockMixins(int id, boolean powered) {
		super(id, powered);
	}
    
    @Environment(EnvType.CLIENT)
    @Inject(
        method = "renderBlock(Lnet/minecraft/src/RenderBlocks;III)Z",
        at = @At("HEAD")
    )
    public void renderBlockComparator_inject(RenderBlocks render, int x, int y, int z, CallbackInfoReturnable info) {
        ((IBlockRedstoneLogicMixins)(ComparatorBlock)(Object)this).setRenderingBaseTextures(false);
    }
    
    @Environment(EnvType.CLIENT)
    @Redirect(
        method = "renderBlock",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/RenderBlocks;renderTorchAtAngle(Lnet/minecraft/src/Block;DDDDDI)V"
        )
    )
    public void renderTorchAtAngle_redirect(RenderBlocks self, Block block, double x, double y, double z, double angleA, double angleB, int meta) {
        ((IRenderBlocksMixins)self).renderTorchForRedstoneLogic(block, x, y, z, meta);
    }
}