package zero.test.mixin;
import net.minecraft.src.*;
import btw.block.blocks.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import java.util.Random;
import zero.test.IBlockMixins;

@Mixin(MortarReceiverStairsBlock.class)
public class MortarReceiverStairsBlockMixins extends FallingStairsBlock {
    MortarReceiverStairsBlockMixins(int id, Block reference_block, int reference_block_meta) {
        super(id, reference_block, reference_block_meta);
    }
    @Override
    public void updateTick(World world, int X, int Y, int Z, Random random) {
        has_adjacent_slime: do {
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
                    break has_adjacent_slime;
                }
            } while (++facing < 6);
            if (checkForFall(world, X, Y, Z)) {
                return;
            }
        } while(false);
        if (getIsUpsideDown(world, X, Y, Z)) {
            setIsUpsideDown(world, X, Y, Z, false);
        }
    }
}
