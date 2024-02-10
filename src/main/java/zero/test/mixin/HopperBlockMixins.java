package zero.test.mixin;
import net.minecraft.src.*;
import btw.AddonHandler;
import btw.block.BTWBlocks;
import btw.block.blocks.HopperBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import zero.test.IWorldMixins;
// Block piston reactions

@Mixin(HopperBlock.class)
public abstract class HopperBlockMixins extends BlockContainer {
    public HopperBlockMixins() {
        super(0, null);
    }
    public boolean canSupportRails(World world, int x, int y, int z) {
        return true;
    }
}
