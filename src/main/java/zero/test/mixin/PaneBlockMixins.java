package zero.test.mixin;
import net.minecraft.src.*;
import btw.block.blocks.PaneBlock;
import btw.AddonHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
// Block piston reactions
@Mixin(PaneBlock.class)
public abstract class PaneBlockMixins {
    // The original function passed x,y,z to
    // shouldPaneConnectToThisBlockToFacing
    // instead of nextX,nextY,nextZ like
    // walls/fences do.
    @Overwrite
    public static boolean canConnectToBlockToFacing(IBlockAccess blockAccess, int x, int y, int z, int facing) {
        int nextX = x + Facing.offsetsXForSide[facing];
        int nextY = y + Facing.offsetsYForSide[facing];
        int nextZ = z + Facing.offsetsZForSide[facing];
        Block block = Block.blocksList[blockAccess.getBlockId(nextX, nextY, nextZ)];
        if (!((block)==null)) {
            return block.shouldPaneConnectToThisBlockToFacing(blockAccess, nextX, nextY, nextZ, ((facing)^1));
        }
        return false;
    }
}
