package zero.test.mixin;
import net.minecraft.src.*;
import btw.block.blocks.*;
import btw.AddonHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import java.util.Random;
import zero.test.IBlockMixins;
// Block piston reactions

@Mixin(MortarReceiverBlock.class)
public class MortarReceiverBlockMixins extends FallingFullBlock {
    public MortarReceiverBlockMixins(int id, Material material) {
        super(id, material);
    }
    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
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
                return;
            }
        } while (((++facing)<=5));
        checkForFall(world, x, y, z);
    }
}
