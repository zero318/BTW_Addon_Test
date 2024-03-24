package zero.test;

import net.minecraft.src.*;

import btw.block.BTWBlocks;
import btw.block.blocks.PistonBlockBase;
import btw.block.blocks.PistonBlockMoving;
import btw.item.util.ItemUtils;
import btw.world.util.WorldUtils;
import btw.AddonHandler;
import btw.BTWAddon;

import zero.test.IBlockMixins;
import zero.test.mixin.IPistonBaseAccessMixins;
import zero.test.IWorldMixins;
import zero.test.IBlockEntityPistonMixins;
import zero.test.ZeroUtil;
import zero.test.ZeroCompatUtil;

#include "feature_flags.h"
#include "util.h"

#if ENABLE_METADATA_EXTENSION_COMPAT
#define blockstate_t long
#define TURNTABLE_BLOCK_STATE_PACK(...) BLOCK_STATE_PACK_LONG(__VA_ARGS__)
#define TURNTABLE_BLOCK_STATE_UNPACK(...) BLOCK_STATE_UNPACK_LONG(__VA_ARGS__)
#define TURNTABLE_BLOCK_STATE_EXTRACT_ID(...) BLOCK_STATE_LONG_EXTRACT_ID(__VA_ARGS__)
#define TURNTABLE_BLOCK_STATE_EXTRACT_META(...) BLOCK_STATE_LONG_EXTRACT_META(__VA_ARGS__)
#define TURNTABLE_SET_BLOCK(world, x, y, z, blockId, meta, extMeta, flags) ZeroCompatUtil.setBlockWithExtra(world, x, y, z, blockId, meta, extMeta, flags)
#else
#define blockstate_t int
#define TURNTABLE_BLOCK_STATE_PACK(...) BLOCK_STATE_PACK(__VA_ARGS__)
#define TURNTABLE_BLOCK_STATE_UNPACK(...) BLOCK_STATE_UNPACK(__VA_ARGS__)
#define TURNTABLE_BLOCK_STATE_EXTRACT_ID(...) BLOCK_STATE_EXTRACT_ID(__VA_ARGS__)
#define TURNTABLE_BLOCK_STATE_EXTRACT_META(...) BLOCK_STATE_EXTRACT_META(__VA_ARGS__)
#define TURNTABLE_SET_BLOCK(world, x, y, z, blockId, meta, extMeta, flags) world.setBlock(x, y, z, blockId, meta, flags)
#endif

public class TurntableResolver {
#if ENABLE_TURNTABLE_SLIME_SUPPORT

#pragma push_macro("TURNTABLE_HEIGHT_LIMIT")
#undef TURNTABLE_HEIGHT_LIMIT
    private static final int TURNTABLE_HEIGHT_LIMIT =
#pragma pop_macro("TURNTABLE_HEIGHT_LIMIT")
    TURNTABLE_HEIGHT_LIMIT;
#undef TURNTABLE_HEIGHT_LIMIT

    // Backwards compatibility hack for existing builds
    private static final int FORCE_ROTATE_HEIGHT = 2;

    private static final int PILLAR_LIST_LENGTH = TURNTABLE_HEIGHT_LIMIT;
    private static final int ATTACHMENT_LIST_LENGTH = PILLAR_LIST_LENGTH * 4;
    
    private static final int PILLAR_LIST_START_INDEX = 0;
    private static final int ATTACHMENT_LIST_START_INDEX = PILLAR_LIST_START_INDEX + PILLAR_LIST_LENGTH;
    
    private static final int PILLAR_ID_LIST_LENGTH = PILLAR_LIST_LENGTH;
    private static final int ATTACHMENT_STATE_LIST_LENGTH = ATTACHMENT_LIST_LENGTH;
    private static final int SPIN_OFFSET_LIST_LENGTH = ATTACHMENT_LIST_LENGTH;
    
    private static final int PILLAR_ID_LIST_START_INDEX = 0;
    private static final int ATTACHMENT_STATE_LIST_START_INDEX = PILLAR_ID_LIST_START_INDEX + PILLAR_ID_LIST_LENGTH;
    private static final int SPIN_OFFSET_LIST_START_INDEX = ATTACHMENT_STATE_LIST_START_INDEX + ATTACHMENT_STATE_LIST_LENGTH;
    
    private static final int SPIN_OFFSET_LIST_OFFSET = ATTACHMENT_STATE_LIST_LENGTH;
    
    private static final byte[] spin_offsets = new byte[] {
        -1, -1, // SOUTH_EAST_OFFSETS (North clockwise, West  counterclockwise) 2, 4  (0, 3)
         1, -1, // SOUTH_WEST_OFFSETS (East  clockwise, North counterclockwise) 5, 2  (1, 0)
         1,  1, // NORTH_WEST_OFFSETS (South clockwise, East  counterclockwise) 3, 5  (2, 1)
        -1,  1, // NORTH_EAST_OFFSETS (West  clockwise, South counterclockwise) 4, 3  (3, 2)
        -1, -1, // SOUTH_EAST_OFFSETS (North clockwise, West  counterclockwise) 2, 4  (0, 3) COPIED INDICES
    };
    
    private final long[] pillar_blocks = new long[PILLAR_LIST_LENGTH + ATTACHMENT_LIST_LENGTH];
    private final blockstate_t[] data_list = new blockstate_t[PILLAR_ID_LIST_LENGTH + ATTACHMENT_STATE_LIST_LENGTH + SPIN_OFFSET_LIST_LENGTH];
    private final TileEntity[] tile_entity_list = new TileEntity[ATTACHMENT_STATE_LIST_LENGTH];
    
    private int max_height;
    
    private int pillar_index_global;
    private int attachment_index_global;
    
    private boolean addAttachmentBlock(World world, int x, int y, int z, int direction, boolean sticky) {
        int blockId = world.getBlockId(x, y, z);
        
        if (sticky) {
            Block block = Block.blocksList[blockId];
            
            int facing = 2;
            do {
                if (
                    facing != direction &&
                    ((IBlockMixins)block).isStickyForBlocks(world, x, y, z, facing)
                ) {
                    int nextX = x + Facing.offsetsXForSide[facing];
                    int nextY = y + Facing.offsetsYForSide[facing];
                    int nextZ = z + Facing.offsetsZForSide[facing];
                    Block neighborBlock = Block.blocksList[world.getBlockId(nextX, nextY, nextZ)];
                    
                    if (
                        !BLOCK_IS_AIR(neighborBlock) &&
                        neighborBlock.canBlockBePulledByPiston(world, nextX, nextY, nextZ, DIRECTION_NONE) &&
                        ((IBlockMixins)neighborBlock).canBeStuckTo(world, nextX, nextY, nextZ, facing, blockId)
                    ) {
                        return false;
                    }
                }
            } while (DIRECTION_IS_VALID(++facing));
        }
        
        pillar_blocks[attachment_index_global] = BLOCK_POS_PACK(x, y, z);
        
        // getNewMetadataRotatedAroundBlockOnTurntableToFacing
        // is an awful function and this is simpler without it.
        data_list[attachment_index_global] = TURNTABLE_BLOCK_STATE_PACK(
            blockId,
            world.getBlockMetadata(x, y, z),
            ZeroCompatUtil.getBlockExtMetadata(world, x, y, z)
        );
        
        data_list[attachment_index_global + SPIN_OFFSET_LIST_OFFSET] = direction;
        ++attachment_index_global;
        
        return true;
    }
    
    private boolean addPillarBlock(World world, int x, int y, int z) {
        int blockId = world.getBlockId(x, y, z);
        Block block = Block.blocksList[blockId];
        if (
            !BLOCK_IS_AIR(block) &&
            block.canRotateOnTurntable(world, x, y, z)
        ) {
            Block neighborBlock;
            
            int direction = 2;
            do {
                int nextX = x + Facing.offsetsXForSide[direction];
                int nextZ = z + Facing.offsetsZForSide[direction];
                
                neighborBlock = Block.blocksList[world.getBlockId(nextX, y, nextZ)];
                
                if (!BLOCK_IS_AIR(neighborBlock)) {
                    boolean sticky = ((IBlockMixins)block).isStickyForBlocks(world, x, y, z, direction) &&
                                     neighborBlock.canBlockBePulledByPiston(world, nextX, y, nextZ, DIRECTION_NONE) &&
                                     ((IBlockMixins)neighborBlock).canBeStuckTo(world, nextX, y, nextZ, direction, blockId);
                    if (
                        (
                            sticky ||
                            (
                                ((IBlockMixins)block).canTransmitRotationHorizontallyOnTurntable(world, x, y, z, direction) &&
                                neighborBlock.canRotateAroundBlockOnTurntableToFacing(world, nextX, y, nextZ, OPPOSITE_DIRECTION(direction))
                            )
                        ) &&
                        neighborBlock.onRotatedAroundBlockOnTurntableToFacing(world, nextX, y, nextZ, OPPOSITE_DIRECTION(direction)) &&
                        // Check failure conditions
                        !this.addAttachmentBlock(world, nextX, y, nextZ, OPPOSITE_DIRECTION(direction), sticky)
                    ) {
                        return false;
                    }
                }
            } while (DIRECTION_IS_VALID(++direction));
            
            pillar_blocks[pillar_index_global] = BLOCK_POS_PACK(x, y, z);
            data_list[pillar_index_global] = blockId;
            ++pillar_index_global;
            
            int nextY = y + 1;
            
            neighborBlock = Block.blocksList[world.getBlockId(x, nextY, z)];
            
            if (
                !BLOCK_IS_AIR(neighborBlock) &&
                (
                    ((IBlockMixins)block).isStickyForBlocks(world, x, y, z, DIRECTION_UP)
                        // If the block is sticky, make sure that it only
                        // turns things it'll actually stick to. Currently
                        // assuming that all sticky blocks can transmit vertically.
                        ? (
                            // Just like in the piston resolver, this filters
                            // out blocks that break when pushed.
                            neighborBlock.canBlockBePulledByPiston(world, x, nextY, z, DIRECTION_NONE) &&
                            ((IBlockMixins)neighborBlock).canBeStuckTo(world, x, nextY, z, DIRECTION_UP, blockId)
                        )
                        // Otherwise most blocks are allowed to rotate if they're
                        // within the forced rotation height.
                        : (
                            pillar_index_global < FORCE_ROTATE_HEIGHT &&
                            block.canTransmitRotationVerticallyOnTurntable(world, x, y, z) &&
                            // Just make sure not to rotate slime in this case
                            !((IBlockMixins)neighborBlock).isStickyForBlocks(world, x, nextY, z, DIRECTION_DOWN)
                        )
                ) &&
                // Check the failure conditions
                (
                    nextY == max_height ||
                    !this.addPillarBlock(world, x, nextY, z)
                )
            ) {
                return false;
            }
        }
        return true;
    }
    
    public int turnBlocks(World world, int x, int y, int z, boolean reverse, int crafting_counter) {
        
        pillar_index_global = PILLAR_LIST_START_INDEX;
        attachment_index_global = ATTACHMENT_LIST_START_INDEX;
        
        max_height = y + TURNTABLE_HEIGHT_LIMIT;
        
        ++y;
        
        if (this.addPillarBlock(world, x, y, z)) {
            
            long packedPos;
            int blockId;
            int blockMeta;
#if ENABLE_METADATA_EXTENSION_COMPAT
            int blockExtMeta;
#endif
            blockstate_t blockState;
            int tile_entity_index = 0;
            
            for (int i = attachment_index_global; --i >= ATTACHMENT_LIST_START_INDEX;) {
                packedPos = pillar_blocks[i];
                BLOCK_POS_UNPACK(packedPos, x, y, z);
                
                blockId = TURNTABLE_BLOCK_STATE_EXTRACT_ID(data_list[i]);
                Block block = Block.blocksList[blockId];
                if (block.hasTileEntity()) {
                    tile_entity_list[tile_entity_index++] = world.getBlockTileEntity(x, y, z);
                    world.removeBlockTileEntity(x, y, z);
                }
                
                world.setBlock(x, y, z, 0, 0, UPDATE_NEIGHBORS | UPDATE_CLIENTS);
                if (block.hasComparatorInputOverride()) {
                    world.func_96440_m(x, y, z, blockId);
                }
            }
            
            for (int i = pillar_index_global; --i >= PILLAR_LIST_START_INDEX;) {
                packedPos = pillar_blocks[i];
                crafting_counter = Block.blocksList[(int)data_list[i]].rotateOnTurntable(world, BLOCK_POS_UNPACK_ARGS(packedPos), reverse, crafting_counter);
            }
            
            tile_entity_index = 0;
            
            for (int i = attachment_index_global; --i >= ATTACHMENT_LIST_START_INDEX;) {
                packedPos = pillar_blocks[i];
                BLOCK_POS_UNPACK(packedPos, x, y, z);
                
                blockState = data_list[i];
                TURNTABLE_BLOCK_STATE_UNPACK(blockState, blockId, blockMeta, blockExtMeta);
                
                Block block = Block.blocksList[blockId];
                
                int spin_index = DIRECTION_TO_FLAT_DIRECTION((int)data_list[i + SPIN_OFFSET_LIST_OFFSET]);
                if (reverse) {
                    ++spin_index;
                }
                spin_index += spin_index;
                
                int nextX = x + spin_offsets[spin_index];
                int nextZ = z + spin_offsets[spin_index + 1];
                
                TileEntity tile_entity = null;
                if (block.hasTileEntity()) {
                    tile_entity = tile_entity_list[tile_entity_index];
                    tile_entity_list[tile_entity_index++] = null;
                }
                boolean breakBlock = false;
                Block neighborBlock = Block.blocksList[world.getBlockId(nextX, y, nextZ)];
                
                if (
                    BLOCK_IS_AIR(neighborBlock) ||
                    neighborBlock.blockMaterial.isReplaceable() ||
                    (breakBlock = neighborBlock.getMobilityFlag() == PISTON_CAN_BREAK)
                ) {
                    if (breakBlock) {
                        neighborBlock.onBrokenByPistonPush(world, nextX, y, nextZ, block.adjustMetadataForPistonMove(world.getBlockMetadata(nextX, y, nextZ)));
                    }
                    if (tile_entity != null) {
                        tile_entity.validate();
                        world.setBlockTileEntity(nextX, y, nextZ, tile_entity);
                    }
#if ENABLE_DIRECTIONAL_UPDATES
                    int newMeta = ((IWorldMixins)world).updateFromNeighborShapes(nextX, y, nextZ, blockId, blockMeta);
                    if (newMeta >= 0) {
                        // Fortunately no blocks have important calls in
                        // getNewMetadataRotatedAroundBlockOnTurntableToFacing
                        // that can't be handled by a normal metadata rotation
                        // and most of the side effects of rotateAroundJAxis
                        // are already handled by setting the block itself.
                        // Major exception is mechanical power not snapping axles.
                        TURNTABLE_SET_BLOCK(world, nextX, y, nextZ, blockId, block.rotateMetadataAroundJAxis(newMeta, BOOL_INVERT(reverse)), blockExtMeta, UPDATE_NEIGHBORS | UPDATE_CLIENTS);
                    }
                    else {
                        TURNTABLE_SET_BLOCK(world, nextX, y, nextZ, blockId, blockMeta, blockExtMeta, UPDATE_INVISIBLE | UPDATE_KNOWN_SHAPE | UPDATE_MOVE_BY_PISTON | UPDATE_SUPPRESS_LIGHT);
                        world.destroyBlock(nextX, y, nextZ, true);
                    }
#else
                    TURNTABLE_SET_BLOCK(world, nextX, y, nextZ, blockId, block.rotateMetadataAroundJAxis(blockMeta, BOOL_INVERT(reverse)), blockExtMeta, UPDATE_NEIGHBORS | UPDATE_CLIENTS);
#endif
                }
                else {
                    if (tile_entity != null) {
                        ZeroUtil.break_tile_entity(world, x, y, z, tile_entity);
                    }
                    // This seems like it could be an issue.
                    // Maybe destroy the block instead?
                    block.dropBlockAsItem(world, x, y, z, blockId, blockMeta);
                }
            }
        }
        return crafting_counter;
    }

#endif
}