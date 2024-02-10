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
    public MortarReceiverSlabBlockMixins() {
        super(0, null);
    }
    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        has_adjacent_slime: do {
            int facing = getIsUpsideDown(world, x, y, z) ? 1 : 0;
            int nextY = y + Facing.offsetsXForSide[facing];
            Block neighborBlock = Block.blocksList[world.getBlockId(x, nextY, z)];
            if (
                !((neighborBlock)==null) &&
                ((IBlockMixins)neighborBlock).permanentlySupportsMortarBlocks(world, x, nextY, z, facing)
            ) {
                break has_adjacent_slime;
            }
            facing = 2;
            do {
                int nextX = x + Facing.offsetsXForSide[facing];
                int nextZ = z + Facing.offsetsZForSide[facing];
                neighborBlock = Block.blocksList[world.getBlockId(nextX, y, nextZ)];
                if (
                    !((neighborBlock)==null) &&
                    ((IBlockMixins)neighborBlock).permanentlySupportsMortarBlocks(world, nextX, y, nextZ, facing)
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
