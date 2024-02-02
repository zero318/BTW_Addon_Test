package zero.test.mixin;
import net.minecraft.src.*;
import btw.block.blocks.*;
import btw.AddonHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import java.util.Random;
import zero.test.IBlockMixins;
import zero.test.IWorldMixins;
// Block piston reactions
@Mixin(BlockDispenserBlock.class)
public abstract class BlockDispenserBlockMixins extends BlockContainer {
    public BlockDispenserBlockMixins(int par1, Material par2Material) {
        super(par1, par2Material);
    }
    @Overwrite
    public boolean isReceivingRedstonePower(World world, int x, int y, int z) {
        return ((IWorldMixins)world).getBlockWeakPowerInputExceptFacing(x, y, z, (((world.getBlockMetadata(x, y, z))&7))) != 0 || ((IWorldMixins)world).getBlockWeakPowerInputExceptFacing(x, y + 1, z, 0) != 0;
    }
}
