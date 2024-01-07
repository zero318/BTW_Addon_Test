package zero.test.mixin;
import net.minecraft.src.*;
import btw.block.blocks.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import java.util.Random;
import zero.test.IBlockMixins;
// Block piston reactions

@Mixin(MortarReceiverSlabBlock.class)
public abstract class MortarReceiverSlabBlockMixins extends FallingSlabBlock {
    MortarReceiverSlabBlockMixins(int id, Material material) {
        super(id, material);
    }
    @Override
    public void updateTick(World world, int X, int Y, int Z, Random random) {
        has_adjacent_slime: do {
            int facing = getIsUpsideDown(world, X, Y, Z) ? 1 : 0;
            int nextY = Y + Facing.offsetsXForSide[facing];
            Block neighbor_block = Block.blocksList[world.getBlockId(X, nextY, Z)];
            if (
                !((neighbor_block)==null) &&
                ((IBlockMixins)neighbor_block).permanentlySupportsMortarBlocks(world, X, nextY, Z, facing)
            ) {
                break has_adjacent_slime;
            }
            facing = 2;
            do {
                int nextX = X + Facing.offsetsXForSide[facing];
                //int nextY = Y + Facing.offsetsYForSide[facing];
                int nextZ = Z + Facing.offsetsZForSide[facing];
                neighbor_block = Block.blocksList[world.getBlockId(nextX, Y, nextZ)];
                if (
                    !((neighbor_block)==null) &&
                    ((IBlockMixins)neighbor_block).permanentlySupportsMortarBlocks(world, nextX, Y, nextZ, facing)
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
