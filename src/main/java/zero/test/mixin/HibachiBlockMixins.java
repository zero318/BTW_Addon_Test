package zero.test.mixin;
import net.minecraft.src.*;
import btw.block.blocks.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import java.util.Random;
@Mixin(HibachiBlock.class)
public class HibachiBlockMixins {
    // Prevent hibachi getting quasi powered
    @Overwrite
    private boolean isGettingPowered(World world, int X, int Y, int Z) {
        return world.isBlockGettingPowered(X, Y, Z);
    }
}
