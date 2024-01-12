package zero.test.mixin;

import net.minecraft.src.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Random;

import zero.test.mixin.IBlockRedstoneLogicAccessMixins;

#include "..\feature_flags.h"
#include "..\util.h"

#define FLAT_DIRECTION_META_OFFSET 0

@Mixin(BlockRedstoneRepeater.class)
public abstract class BlockRedstoneRepeaterMixins extends BlockRedstoneLogic {
    
    public BlockRedstoneRepeaterMixins(int par1, boolean par2) {
        super(par1, par2);
    }
    
#if ENABLE_REDSTONE_BUGFIXES

    // Fixes: MC-9194
    // isLocked
    @Overwrite
    public boolean func_94476_e(IBlockAccess blockAccess, int x, int y, int z, int meta) {
        int neighborBlockId;
        int neighborBlockMeta;
        switch (READ_META_FIELD(meta, FLAT_DIRECTION)) {
            case FLAT_DIRECTION_META_NORTH:
            case FLAT_DIRECTION_META_SOUTH:
                return (
                    BlockRedstoneLogic.isRedstoneRepeaterBlockID(neighborBlockId = blockAccess.getBlockId(x - 1, y, z)) &&
                    READ_META_FIELD((neighborBlockMeta = blockAccess.getBlockMetadata(x - 1, y, z)), FLAT_DIRECTION) == FLAT_DIRECTION_META_EAST &&
                    ((IBlockRedstoneLogicAccessMixins)Block.blocksList[neighborBlockId]).callFunc_96470_c(neighborBlockMeta)
                ) || (
                    BlockRedstoneLogic.isRedstoneRepeaterBlockID(neighborBlockId = blockAccess.getBlockId(x + 1, y, z)) &&
                    READ_META_FIELD((neighborBlockMeta = blockAccess.getBlockMetadata(x + 1, y, z)), FLAT_DIRECTION) == FLAT_DIRECTION_META_WEST &&
                    ((IBlockRedstoneLogicAccessMixins)Block.blocksList[neighborBlockId]).callFunc_96470_c(neighborBlockMeta)
                );
            default:
            //case FLAT_DIRECTION_META_EAST:
            //case FLAT_DIRECTION_META_WEST:
                return (
                    BlockRedstoneLogic.isRedstoneRepeaterBlockID(neighborBlockId = blockAccess.getBlockId(x, y, z + 1)) &&
                    READ_META_FIELD((neighborBlockMeta = blockAccess.getBlockMetadata(x, y, z + 1)), FLAT_DIRECTION) == FLAT_DIRECTION_META_NORTH &&
                    ((IBlockRedstoneLogicAccessMixins)Block.blocksList[neighborBlockId]).callFunc_96470_c(neighborBlockMeta)
                ) || (
                    BlockRedstoneLogic.isRedstoneRepeaterBlockID(neighborBlockId = blockAccess.getBlockId(x, y, z - 1)) &&
                    READ_META_FIELD((neighborBlockMeta = blockAccess.getBlockMetadata(x, y, z - 1)), FLAT_DIRECTION) == FLAT_DIRECTION_META_SOUTH &&
                    ((IBlockRedstoneLogicAccessMixins)Block.blocksList[neighborBlockId]).callFunc_96470_c(neighborBlockMeta)
                );
        }
    }
#endif

#if ENABLE_BETTER_REDSTONE_WIRE_CONNECTIONS
    public boolean canRedstoneConnectToSide(IBlockAccess blockAccess, int x, int y, int z, int flatDirection) {
        return FLAT_DIRECTION_AXES_MATCH(flatDirection, READ_META_FIELD(blockAccess.getBlockMetadata(x, y, z), FLAT_DIRECTION));
    }
#endif
}