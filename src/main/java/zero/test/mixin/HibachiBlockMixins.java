package zero.test.mixin;
import net.minecraft.src.*;
import btw.block.blocks.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import java.util.Random;
@Mixin(HibachiBlock.class)
public abstract class HibachiBlockMixins {
    // Prevent hibachi getting quasi powered
    @Overwrite
    public boolean isGettingPowered(World world, int x, int y, int z) {
        return world.isBlockGettingPowered(x, y, z);
    }
}
