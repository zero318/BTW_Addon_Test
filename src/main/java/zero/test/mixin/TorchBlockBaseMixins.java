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
@Mixin(TorchBlockBase.class)
public abstract class TorchBlockBaseMixins {
    @Redirect(
        method = { "canPlaceBlockAt", "onBlockPlaced", "onBlockAdded", "validateState" },
        at = @At(
            value = "INVOKE",
            target = "Lbtw/world/util/WorldUtils;doesBlockHaveCenterHardpointToFacing(Lnet/minecraft/src/IBlockAccess;IIII)Z"
        )
    )
    public boolean doesBlockHaveCenterHardpointToFacing_redirect(IBlockAccess blockAccess, int x, int y, int z, int direction) {
        Block block = Block.blocksList[blockAccess.getBlockId(x, y, z)];
        return !((block)==null) && block.hasCenterHardPointToFacing(blockAccess, x, y, z, direction, true);
    }
}
