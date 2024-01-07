package zero.test.mixin;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
// Block piston reactions
@Mixin(BlockGlowStone.class)
public class BlockGlowStoneMixins {
    public boolean isRedstoneConductor(IBlockAccess block_access, int X, int Y, int Z) {
        return false;
    }
}
