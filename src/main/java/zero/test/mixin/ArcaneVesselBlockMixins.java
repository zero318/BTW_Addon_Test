package zero.test.mixin;
import net.minecraft.src.*;
import btw.block.blocks.ArcaneVesselBlock;
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

@Mixin(ArcaneVesselBlock.class)
public abstract class ArcaneVesselBlockMixins {
    @Inject(
        method = "breakBlock(Lnet/minecraft/src/World;IIIII)V",
        at = @At(
            value = "INVOKE",
            target = "Lbtw/block/blocks/VesselBlock;breakBlock(Lnet/minecraft/src/World;IIIII)V"
        )
    )
    public void break_block_comparator_inject(World world, int x, int y, int z, int blockId, int meta, CallbackInfo info) {
        world.func_96440_m(x, y, z, blockId);
    }
}