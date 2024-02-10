package zero.test.mixin;
import net.minecraft.src.*;
import btw.block.blocks.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import java.util.Random;
import zero.test.IBlockMixins;
// Block piston reactions

@Mixin(MortarReceiverStairsBlock.class)
public abstract class MortarReceiverStairsBlockMixins extends FallingStairsBlock {
    public MortarReceiverStairsBlockMixins() {
        super(0, null, 0);
    }
    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        has_adjacent_slime: do {
            int facing = 0;
            do {
                int nextX = x + Facing.offsetsXForSide[facing];
                int nextY = y + Facing.offsetsYForSide[facing];
                int nextZ = z + Facing.offsetsZForSide[facing];
                Block neighborBlock = Block.blocksList[world.getBlockId(nextX, nextY, nextZ)];
                if (
                    !((neighborBlock)==null) &&
                    ((IBlockMixins)neighborBlock).permanentlySupportsMortarBlocks(world, nextX, nextY, nextZ, facing)
                ) {
                    break has_adjacent_slime;
                }
            } while (((++facing)<=5));
            if (checkForFall(world, x, y, z)) {
                return;
            }
        } while(false);
        if (getIsUpsideDown(world, x, y, z)) {
            setIsUpsideDown(world, x, y, z, false);
        }
    }
}
