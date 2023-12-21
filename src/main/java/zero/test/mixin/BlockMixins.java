package zero.test.mixin;
import net.minecraft.src.Block;
import net.minecraft.src.World;
import org.spongepowered.asm.mixin.Mixin;
import zero.test.IBlockMixins;

@Mixin(Block.class)
public class BlockMixins implements IBlockMixins {
    @Override
    public boolean getWeakChanges(World world, int X, int Y, int Z, int meta) {
        return false;
    }
    @Override
    public boolean caresAboutUpdateDirection() {
        return false;
    }
    @Override
    public boolean isSticky(int X, int Y, int Z, int direction) {
        return false;
    }
    @Override
    public boolean canStickTo(int X, int Y, int Z, int direction, int neighbor_id) {
        return true;
    }
}
