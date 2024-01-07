package zero.test.mixin;
import net.minecraft.src.*;
import btw.AddonHandler;
import btw.block.blocks.CompanionCubeBlock;
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
@Mixin(CompanionCubeBlock.class)
public class CompanionCubeBlockMixins {
    // Another case of overriding
    // isNormalBlock that screws with redstone.
    //
    // See the other explanation in the platform mixins.
    public boolean isRedstoneConductor(IBlockAccess block_access, int X, int Y, int Z) {
        return !((CompanionCubeBlock)(Object)this).getIsSlab(block_access, X, Y, Z);
    }
}
