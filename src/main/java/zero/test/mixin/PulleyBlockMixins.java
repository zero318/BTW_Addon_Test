package zero.test.mixin;
import net.minecraft.src.*;
import btw.block.blocks.*;
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
import java.util.Random;
// Block piston reactions
@Mixin(PulleyBlock.class)
public abstract class PulleyBlockMixins {
    @Redirect(
        method = { "updateTick", "isCurrentStateValid" },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/World;isBlockGettingPowered(III)Z",
            ordinal = 1
        )
    )
    public boolean disable_pulley_quasi(World world, int x, int y, int z) {
        return false;
    }
}
