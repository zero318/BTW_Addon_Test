package zero.test.mixin;
import net.minecraft.src.World;
import net.minecraft.src.BlockComparator;
import org.spongepowered.asm.mixin.Mixin;
@Mixin(BlockComparator.class)
public class ComparatorMixins {
    public boolean getWeakChanges(World world, int X, int Y, int Z, int meta) {
        return true;
    }
}
