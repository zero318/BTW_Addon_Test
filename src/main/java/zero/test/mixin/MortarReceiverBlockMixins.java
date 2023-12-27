package zero.test.mixin;
import net.minecraft.src.*;
import btw.block.blocks.*;
import btw.AddonHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import java.util.Random;
import zero.test.IBlockMixins;

@Mixin(MortarReceiverBlock.class)
public class MortarReceiverBlockMixins extends FallingFullBlock {
    MortarReceiverBlockMixins(int id, Material material) {
        super(id, material);
    }
    @Override
    public void updateTick(World world, int X, int Y, int Z, Random random) {
        int facing = 0;
        do {
            int nextX = X + Facing.offsetsXForSide[facing];
            int nextY = Y + Facing.offsetsYForSide[facing];
            int nextZ = Z + Facing.offsetsZForSide[facing];
            Block neighbor_block = Block.blocksList[world.getBlockId(nextX, nextY, nextZ)];
            if (
                !((neighbor_block)==null) &&
                ((IBlockMixins)neighbor_block).permanentlySupportsMortarBlocks(world, nextX, nextY, nextZ, facing)
            ) {
                return;
            }
        } while (++facing < 6);
        checkForFall(world, X, Y, Z);
    }
}
