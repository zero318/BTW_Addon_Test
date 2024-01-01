package zero.test.mixin;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Overwrite;
import java.util.Random;
import zero.test.mixin.IBlockRedstoneLogicAccessMixins;
@Mixin(BlockRedstoneRepeater.class)
public abstract class BlockRedstoneRepeaterMixins extends BlockRedstoneLogic {
    BlockRedstoneRepeaterMixins(int par1, boolean par2) {
        super(par1, par2);
    }
    // Fixes: MC-9194
    // isLocked
    @Overwrite
    public boolean func_94476_e(IBlockAccess block_access, int X, int Y, int Z, int meta) {
        int neighbor_block_id;
        int neighbor_block_meta;
        switch ((((meta)&3))) {
            case 0:
            case 2:
                return (
                    BlockRedstoneLogic.isRedstoneRepeaterBlockID(neighbor_block_id = block_access.getBlockId(X - 1, Y, Z)) &&
                    ((((neighbor_block_meta = block_access.getBlockMetadata(X - 1, Y, Z)))&3)) == 1 &&
                    ((IBlockRedstoneLogicAccessMixins)Block.blocksList[neighbor_block_id]).callFunc_96470_c(neighbor_block_meta)
                ) || (
                    BlockRedstoneLogic.isRedstoneRepeaterBlockID(neighbor_block_id = block_access.getBlockId(X + 1, Y, Z)) &&
                    ((((neighbor_block_meta = block_access.getBlockMetadata(X + 1, Y, Z)))&3)) == 3 &&
                    ((IBlockRedstoneLogicAccessMixins)Block.blocksList[neighbor_block_id]).callFunc_96470_c(neighbor_block_meta)
                );
            default:
            //case FLAT_DIRECTION_META_EAST:
            //case FLAT_DIRECTION_META_WEST:
                return (
                    BlockRedstoneLogic.isRedstoneRepeaterBlockID(neighbor_block_id = block_access.getBlockId(X, Y, Z + 1)) &&
                    ((((neighbor_block_meta = block_access.getBlockMetadata(X, Y, Z + 1)))&3)) == 0 &&
                    ((IBlockRedstoneLogicAccessMixins)Block.blocksList[neighbor_block_id]).callFunc_96470_c(neighbor_block_meta)
                ) || (
                    BlockRedstoneLogic.isRedstoneRepeaterBlockID(neighbor_block_id = block_access.getBlockId(X, Y, Z - 1)) &&
                    ((((neighbor_block_meta = block_access.getBlockMetadata(X, Y, Z - 1)))&3)) == 2 &&
                    ((IBlockRedstoneLogicAccessMixins)Block.blocksList[neighbor_block_id]).callFunc_96470_c(neighbor_block_meta)
                );
        }
    }
}
