package zero.test.mixin;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import zero.test.IBlockMixins;
import zero.test.IWorldMixins;
@Mixin(Block.class)
public class BlockMixins implements IBlockMixins {
    @Overwrite
    public boolean hasLargeCenterHardPointToFacing(IBlockAccess block_access, int X, int Y, int Z, int direction, boolean ignore_transparency) {
        // Skip the extra block lookup from chaining through world
        // since the block is already known.
  return ((Block)(Object)this).isNormalCube(block_access, X, Y, Z);
 }
/*
    @Overwrite
    public boolean isNormalCube(IBlockAccess block_access, int X, int Y, int Z) {
        return ((Block)(Object)this).renderAsNormalBlock();
    }
    
    @Overwrite
    public static boolean isNormalCube(int block_id) {
		Block block = Block.blocksList[block_id];
        return !BLOCK_IS_AIR(block) && block.renderAsNormalBlock();
	}
*/
    // Extra variant of getMobilityFlag that allows
    // changing the result based on metadata.
    @Override
    public int getMobilityFlag(World world, int X, int Y, int Z) {
        return ((Block)(Object)this).getMobilityFlag();
    }
    @Overwrite
    public boolean rotateAroundJAxis(World world, int X, int Y, int Z, boolean reverse) {
        int prev_meta = world.getBlockMetadata(X, Y, Z);
        Block self = (Block)(Object)this;
        int new_meta = self.rotateMetadataAroundJAxis(prev_meta, reverse);
        if (prev_meta != new_meta) {
            new_meta = ((IWorldMixins)world).updateFromNeighborShapes(X, Y, Z, self.blockID, new_meta);
            world.setBlockMetadataWithNotify(X, Y, Z, new_meta, 0x01 | 0x02);
            return true;
        }
        return false;
    }
}
