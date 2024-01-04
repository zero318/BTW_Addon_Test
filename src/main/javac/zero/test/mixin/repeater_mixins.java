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
    
    BlockRedstoneRepeaterMixins(int par1, boolean par2) {
        super(par1, par2);
    }
    
#if ENABLE_REDSTONE_BUGFIXES

    // Fixes: MC-9194
    // isLocked
    @Overwrite
    public boolean func_94476_e(IBlockAccess block_access, int X, int Y, int Z, int meta) {
        int neighbor_block_id;
        int neighbor_block_meta;
        switch (READ_META_FIELD(meta, FLAT_DIRECTION)) {
            case FLAT_DIRECTION_META_NORTH:
            case FLAT_DIRECTION_META_SOUTH:
                return (
                    BlockRedstoneLogic.isRedstoneRepeaterBlockID(neighbor_block_id = block_access.getBlockId(X - 1, Y, Z)) &&
                    READ_META_FIELD((neighbor_block_meta = block_access.getBlockMetadata(X - 1, Y, Z)), FLAT_DIRECTION) == FLAT_DIRECTION_META_EAST &&
                    ((IBlockRedstoneLogicAccessMixins)Block.blocksList[neighbor_block_id]).callFunc_96470_c(neighbor_block_meta)
                ) || (
                    BlockRedstoneLogic.isRedstoneRepeaterBlockID(neighbor_block_id = block_access.getBlockId(X + 1, Y, Z)) &&
                    READ_META_FIELD((neighbor_block_meta = block_access.getBlockMetadata(X + 1, Y, Z)), FLAT_DIRECTION) == FLAT_DIRECTION_META_WEST &&
                    ((IBlockRedstoneLogicAccessMixins)Block.blocksList[neighbor_block_id]).callFunc_96470_c(neighbor_block_meta)
                );
            default:
            //case FLAT_DIRECTION_META_EAST:
            //case FLAT_DIRECTION_META_WEST:
                return (
                    BlockRedstoneLogic.isRedstoneRepeaterBlockID(neighbor_block_id = block_access.getBlockId(X, Y, Z + 1)) &&
                    READ_META_FIELD((neighbor_block_meta = block_access.getBlockMetadata(X, Y, Z + 1)), FLAT_DIRECTION) == FLAT_DIRECTION_META_NORTH &&
                    ((IBlockRedstoneLogicAccessMixins)Block.blocksList[neighbor_block_id]).callFunc_96470_c(neighbor_block_meta)
                ) || (
                    BlockRedstoneLogic.isRedstoneRepeaterBlockID(neighbor_block_id = block_access.getBlockId(X, Y, Z - 1)) &&
                    READ_META_FIELD((neighbor_block_meta = block_access.getBlockMetadata(X, Y, Z - 1)), FLAT_DIRECTION) == FLAT_DIRECTION_META_SOUTH &&
                    ((IBlockRedstoneLogicAccessMixins)Block.blocksList[neighbor_block_id]).callFunc_96470_c(neighbor_block_meta)
                );
        }
    }
#endif

#if ENABLE_BETTER_REDSTONE_WIRE_CONNECTIONS
    public boolean canRedstoneConnectToSide(IBlockAccess block_access, int X, int Y, int Z, int flat_direction) {
        return FLAT_DIRECTION_AXES_MATCH(flat_direction, READ_META_FIELD(block_access.getBlockMetadata(X, Y, Z), FLAT_DIRECTION));
    }
#endif
}