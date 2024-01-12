package zero.test.mixin;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
// Block piston reactions
@Mixin(BlockGlowStone.class)
public class BlockGlowStoneMixins {
    public boolean isRedstoneConductor(IBlockAccess blockAccess, int x, int y, int z) {
        return false;
    }
}
