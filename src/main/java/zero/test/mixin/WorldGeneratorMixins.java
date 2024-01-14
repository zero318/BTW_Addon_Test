package zero.test.mixin;
import net.minecraft.src.*;
import btw.AddonHandler;
import btw.block.blocks.TorchBlockBase;
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
// Block piston reactions
@Mixin(WorldGenerator.class)
public abstract class WorldGeneratorMixins {
    @Redirect(
        method = "setBlockAndMetadata(Lnet/minecraft/src/World;IIIII)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/World;setBlock(IIIIII)Z",
            ordinal = 1
        )
    )
    public boolean setBlock_redirect(World world, int x, int y, int z, int blockId, int meta, int flags) {
        return world.setBlock(x, y, z, blockId, meta, flags | 0x10);
    }
}
