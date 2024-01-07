package zero.test.mixin;
import net.minecraft.src.*;
import java.util.List;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
// Block piston reactions
//#define getInputSignal(...) func_94482_f(__VA_ARGS__)
@Mixin(RenderBlocks.class)
public interface IRenderBlocksAccessMixins {
    @Accessor
    public Icon getOverrideBlockTexture();
}
