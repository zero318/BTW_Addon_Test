package zero.test.mixin;
import net.minecraft.src.*;
import btw.block.blocks.AestheticOpaqueBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BlockChest.class)
public class ChestMixins {
    public int updateShape(World world, int X, int Y, int Z, int direction, int meta) {
        return ((BlockChest)(Object)this).canPlaceBlockAt(world, X, Y, Z) ? meta : -1;
    }
}
