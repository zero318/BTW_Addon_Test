package zero.test.mixin.deco;
import net.minecraft.src.*;
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
import org.spongepowered.asm.mixin.gen.Accessor;
import deco.block.blocks.TerracottaBlock;
import deco.block.blocks.GlazedTerracottaBlock;

@Mixin(GlazedTerracottaBlock.class)
public abstract class GlazedTerracottaBlockMixins /*extends TerracottaBlock*/ {
    /*
    public GlazedTerracottaBlockMixins() {
        super(0, null);
    }
    */
    //@Override
    public boolean canBlockBePulledByPiston(World world, int x, int y, int z, int direction) {
        return false;
    }
    public boolean canBeStuckTo(World world, int x, int y, int z, int direction, int neighborId) {
        return false;
    }
}
