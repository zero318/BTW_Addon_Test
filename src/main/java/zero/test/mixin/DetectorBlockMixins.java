package zero.test.mixin;
import net.minecraft.src.*;
import btw.block.blocks.DetectorBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
// Block piston reactions
@Mixin(DetectorBlock.class)
public abstract class DetectorBlockMixins extends Block {
    public DetectorBlockMixins() {
        super(0, null);
    }
}
