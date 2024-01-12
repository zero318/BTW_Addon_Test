package zero.test.mixin;
import net.minecraft.src.*;
import btw.block.blocks.*;
import btw.client.fx.BTWEffectManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import java.util.Random;
import zero.test.IBlockMixins;
// Block piston reactions

@Mixin(LavaReceiverBlock.class)
public abstract class LavaReceiverBlockMixins extends MortarReceiverBlock {
    public LavaReceiverBlockMixins(int id, Material material) {
        super(id, material);
    }
    @Shadow
    protected abstract boolean getHasLavaInCracks(IBlockAccess blockAccess, int x, int y, int z);
    @Shadow
    protected abstract void setHasLavaInCracks(World blockAccess, int x, int y, int z, boolean hasLava);
    @Shadow
    protected abstract boolean hasLavaAbove(IBlockAccess blockAccess, int x, int y, int z);
    @Shadow
    protected abstract boolean hasWaterAbove(IBlockAccess blockAccess, int x, int y, int z);
    @Shadow
    public abstract int getStrata(IBlockAccess blockAccess, int x, int y, int z);
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
        if (getHasLavaInCracks(world, x, y, z)) {
            if (hasWaterAbove(world, x, y, z)) {
                world.playAuxSFX(BTWEffectManager.FIRE_FIZZ_EFFECT_ID, x, y, z, 0);
                world.setBlockAndMetadataWithNotify(x, y, z, Block.stone.blockID, getStrata(world, x, y, z));
                return;
            }
        }
        else if (hasLavaAbove(world, x, y, z)) {
            setHasLavaInCracks(world, x, y, z, true);
        }
    }
}
