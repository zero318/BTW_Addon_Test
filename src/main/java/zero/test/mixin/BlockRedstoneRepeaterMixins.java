package zero.test.mixin;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Overwrite;
import java.util.Random;
import zero.test.mixin.IBlockRedstoneLogicAccessMixins;
// Block piston reactions
@Mixin(BlockRedstoneRepeater.class)
public abstract class BlockRedstoneRepeaterMixins extends BlockRedstoneLogic {
    public BlockRedstoneRepeaterMixins(int par1, boolean par2) {
        super(par1, par2);
    }
    // Fixes: MC-9194
    // isLocked
    @Overwrite
    public boolean func_94476_e(IBlockAccess blockAccess, int x, int y, int z, int meta) {
        int neighborBlockId;
        int neighborBlockMeta;
        switch ((((meta)&3))) {
            case 0:
            case 2:
                return (
                    BlockRedstoneLogic.isRedstoneRepeaterBlockID(neighborBlockId = blockAccess.getBlockId(x - 1, y, z)) &&
                    ((((neighborBlockMeta = blockAccess.getBlockMetadata(x - 1, y, z)))&3)) == 1 &&
                    ((IBlockRedstoneLogicAccessMixins)Block.blocksList[neighborBlockId]).callFunc_96470_c(neighborBlockMeta)
                ) || (
                    BlockRedstoneLogic.isRedstoneRepeaterBlockID(neighborBlockId = blockAccess.getBlockId(x + 1, y, z)) &&
                    ((((neighborBlockMeta = blockAccess.getBlockMetadata(x + 1, y, z)))&3)) == 3 &&
                    ((IBlockRedstoneLogicAccessMixins)Block.blocksList[neighborBlockId]).callFunc_96470_c(neighborBlockMeta)
                );
            default:
            //case FLAT_DIRECTION_META_EAST:
            //case FLAT_DIRECTION_META_WEST:
                return (
                    BlockRedstoneLogic.isRedstoneRepeaterBlockID(neighborBlockId = blockAccess.getBlockId(x, y, z + 1)) &&
                    ((((neighborBlockMeta = blockAccess.getBlockMetadata(x, y, z + 1)))&3)) == 0 &&
                    ((IBlockRedstoneLogicAccessMixins)Block.blocksList[neighborBlockId]).callFunc_96470_c(neighborBlockMeta)
                ) || (
                    BlockRedstoneLogic.isRedstoneRepeaterBlockID(neighborBlockId = blockAccess.getBlockId(x, y, z - 1)) &&
                    ((((neighborBlockMeta = blockAccess.getBlockMetadata(x, y, z - 1)))&3)) == 2 &&
                    ((IBlockRedstoneLogicAccessMixins)Block.blocksList[neighborBlockId]).callFunc_96470_c(neighborBlockMeta)
                );
        }
    }
    public boolean canRedstoneConnectToSide(IBlockAccess blockAccess, int x, int y, int z, int flatDirection) {
        return ((((flatDirection)^((((blockAccess.getBlockMetadata(x, y, z))&3))))&1)==0);
    }
}
