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
    LavaReceiverBlockMixins(int id, Material material) {
        super(id, material);
    }
    @Shadow
    protected abstract boolean getHasLavaInCracks(IBlockAccess block_access, int X, int Y, int Z);
    @Shadow
    protected abstract void setHasLavaInCracks(World block_access, int X, int Y, int Z, boolean has_lava);
    @Shadow
    protected abstract boolean hasLavaAbove(IBlockAccess block_access, int X, int Y, int Z);
    @Shadow
    protected abstract boolean hasWaterAbove(IBlockAccess block_access, int X, int Y, int Z);
    @Shadow
    public abstract int getStrata(IBlockAccess block_access, int X, int Y, int Z);
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
        if (getHasLavaInCracks(world, X, Y, Z)) {
            if (hasWaterAbove(world, X, Y, Z)) {
                world.playAuxSFX(BTWEffectManager.FIRE_FIZZ_EFFECT_ID, X, Y, Z, 0);
                world.setBlockAndMetadataWithNotify(X, Y, Z, Block.stone.blockID, getStrata(world, X, Y, Z));
                return;
            }
        }
        else if (hasLavaAbove(world, X, Y, Z)) {
            setHasLavaInCracks(world, X, Y, Z, true);
        }
    }
}
