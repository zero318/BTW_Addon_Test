package zero.test;

import net.minecraft.src.*;

import btw.AddonHandler;
import btw.block.BTWBlocks;
import btw.block.blocks.PlatformBlock;
import btw.block.blocks.AnchorBlock;
import btw.entity.mechanical.platform.BlockLiftedByPlatformEntity;
import btw.entity.mechanical.platform.MovingAnchorEntity;
import btw.entity.mechanical.platform.MovingPlatformEntity;
import btw.block.tileentity.PulleyTileEntity;
import btw.item.util.ItemUtils;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

import zero.test.IWorldMixins;
import zero.test.IBlockMixins;
import zero.test.IMovingPlatformEntityMixins;
//import zero.test.mixin.IAnchorBlockAccessMixins;
import zero.test.ZeroUtil;
import zero.test.ZeroMetaUtil;

import java.util.Random;

#include "feature_flags.h"
#include "util.h"
#include "ids.h"

#define PLATFORM_LIFT_DEBUGGING 0

#if PLATFORM_LIFT_DEBUGGING
#define PLATFORM_LIFT_DEBUG(...) if (!world.isRemote) AddonHandler.logMessage(__VA_ARGS__)
#else
#define PLATFORM_LIFT_DEBUG(...)
#endif

#define ENABLE_PLATFORM_MAX_DISTANCE 1

// I mean... they're already solid, right?
#define USE_PLATFORM_ENTITY_FOR_ALL_BLOCKS 1

#if ENABLE_METADATA_EXTENSION_COMPAT
#define blockstate_t long
#define PLATFORM_BLOCK_STATE_PACK(...) BLOCK_STATE_PACK_LONG(__VA_ARGS__)
#define PLATFORM_BLOCK_STATE_UNPACK(...) BLOCK_STATE_UNPACK_LONG(__VA_ARGS__)
#define PLATFORM_BLOCK_STATE_EXTRACT_ID(...) BLOCK_STATE_LONG_EXTRACT_ID(__VA_ARGS__)
#define PLATFORM_BLOCK_STATE_EXTRACT_META(...) BLOCK_STATE_LONG_EXTRACT_META(__VA_ARGS__)
#define PLATFORM_SET_BLOCK(world, x, y, z, blockId, meta, extMeta, flags) ZeroMetaUtil.setBlockWithExtra(world, x, y, z, blockId, meta, extMeta, flags)
#else
#define blockstate_t int
#define PLATFORM_BLOCK_STATE_PACK(...) BLOCK_STATE_PACK(__VA_ARGS__)
#define PLATFORM_BLOCK_STATE_UNPACK(...) BLOCK_STATE_UNPACK(__VA_ARGS__)
#define PLATFORM_BLOCK_STATE_EXTRACT_ID(...) BLOCK_STATE_EXTRACT_ID(__VA_ARGS__)
#define PLATFORM_BLOCK_STATE_EXTRACT_META(...) BLOCK_STATE_EXTRACT_META(__VA_ARGS__)
#define PLATFORM_SET_BLOCK(world, x, y, z, blockId, meta, extMeta, flags) world.setBlock(x, y, z, blockId, meta, flags)
#endif

public class PlatformResolver {
    
#if ENABLE_PLATFORM_EXTENSIONS
    
#pragma push_macro("PLATFORM_LIFT_LIMIT")
#undef PLATFORM_LIFT_LIMIT
    private static final int PLATFORM_LIFT_LIMIT =
#pragma pop_macro("PLATFORM_LIFT_LIMIT")
    PLATFORM_LIFT_LIMIT;
#undef PLATFORM_LIFT_LIMIT

#if ENABLE_PLATFORM_MAX_DISTANCE
    // 7 just feels right
    private static final int PLATFORM_MAX_DISTANCE = 7;
#endif

    private static final int PLATFORM_LIST_LENGTH = PLATFORM_LIFT_LIMIT;
    // IDK how to properly calculate this, so just add a lot of extra space?
    private static final int LIFT_LIST_LENGTH = PLATFORM_LIFT_LIMIT * 5;
    
    private static final int PLATFORM_LIST_START_INDEX = 0;
    private static final int LIFT_LIST_START_INDEX = PLATFORM_LIST_START_INDEX + PLATFORM_LIST_LENGTH;
    
    private static final int PLATFORM_STATE_LIST_LENGTH = PLATFORM_LIST_LENGTH;
    private static final int LIFT_STATE_LIST_LENGTH = LIFT_LIST_LENGTH;
    
    private static final int PLATFORM_STATE_LIST_START_INDEX = 0;
    private static final int LIFT_STATE_LIST_START_INDEX = PLATFORM_STATE_LIST_START_INDEX + PLATFORM_STATE_LIST_LENGTH;
    
    private final long[] platform_blocks = new long[PLATFORM_LIST_LENGTH + LIFT_LIST_LENGTH];
    private final blockstate_t[] data_list = new blockstate_t[PLATFORM_STATE_LIST_LENGTH + LIFT_STATE_LIST_LENGTH];
    
    private long anchor_position;
#if ENABLE_PLATFORM_MAX_DISTANCE
    private int source_x;
    private int source_y;
    private int source_z;
#endif
    
    private int platform_index_global;
    private int lift_index_global;
    
    protected boolean addPlatformBlock(World world, int x, int y, int z, boolean isExtending) {
        int blockId = world.getBlockId(x, y, z);
        Block block = Block.blocksList[blockId];
        long packedPos;
        if (
            BLOCK_IS_AIR(block) ||
            (packedPos = BLOCK_POS_PACK(x, y, z)) == anchor_position ||
            ((IBlockMixins)block).getPlatformMobilityFlag(world, x, y, z) != PLATFORM_MAIN_SUPPORT
        ) {
            PLATFORM_LIFT_DEBUG("WTF");
            return true;
        }
        
        PLATFORM_LIFT_DEBUG("SearchForExistingPlatform ("+x+" "+y+" "+z+")");
        
        for (int i = platform_index_global; --i >= PLATFORM_LIST_START_INDEX;) {
            if (platform_blocks[i] == packedPos) {
                return true;
            }
        }
        
        if (platform_index_global == (PLATFORM_LIST_START_INDEX + PLATFORM_LIST_LENGTH)) {
            PLATFORM_LIFT_DEBUG("Platform failed limit reached A");
            return false;
        }
        
        platform_blocks[platform_index_global] = packedPos;
        data_list[platform_index_global] = PLATFORM_BLOCK_STATE_PACK(
            blockId,
            world.getBlockMetadata(x, y, z),
            ZeroMetaUtil.getBlockExtMetadata(world, x, y, z)
        );
        ++platform_index_global;
        
        int move_direction = isExtending ? DIRECTION_DOWN : DIRECTION_UP;
        int facing = 0;
        do {
            int nextX = x + Facing.offsetsXForSide[facing];
            int nextY = y + Facing.offsetsYForSide[facing];
            int nextZ = z + Facing.offsetsZForSide[facing];
            int neighborId = world.getBlockId(nextX, nextY, nextZ);
            Block neighborBlock = Block.blocksList[neighborId];
            if (!BLOCK_IS_AIR(neighborBlock)) {
                packedPos = BLOCK_POS_PACK(nextX, nextY, nextZ);
                switch (((IBlockMixins)neighborBlock).getPlatformMobilityFlag(world, nextX, nextY, nextZ)) {
                    case PLATFORM_MAIN_SUPPORT:
                        PLATFORM_LIFT_DEBUG("Add main support ("+nextX+" "+nextY+" "+nextZ+")");
#if ENABLE_PLATFORM_MAX_DISTANCE
                        int diffX, diffY, diffZ;
                        if ((diffX = nextX - source_x) < 0) diffX = -diffX;
                        if ((diffY = nextY - source_y) < 0) diffY = -diffY;
                        if ((diffZ = nextZ - source_z) < 0) diffZ = -diffZ;
                        if (diffX + diffY + diffZ >= PLATFORM_MAX_DISTANCE) {
                            return false;
                        }
#endif
                        if (!this.addPlatformBlock(world, nextX, nextY, nextZ, isExtending)) {
                            return false;
                        }
                        break;
                    case PLATFORM_CANNOT_MOVE:
                        PLATFORM_LIFT_DEBUG("Default immobile block ("+nextX+" "+nextY+" "+nextZ+")");
                        if (
                            packedPos == anchor_position ||
                            !(
                                ((IBlockMixins)block).isStickyForBlocks(world, x, y, z, facing) &&
                                ((IBlockMixins)neighborBlock).canBeStuckTo(world, nextX, nextY, nextZ, facing, blockId) &&
                                !(
                                    ((IBlockMixins)neighborBlock).getMobilityFlag(world, nextX, nextY, nextZ) != PISTON_CAN_PUSH_ONLY &&
                                    !neighborBlock.canBlockBePulledByPiston(world, nextX, nextY, nextZ, move_direction)
                                ) &&
                                (
                                    facing >= 2 ||
                                    (
                                        facing == move_direction
                                            ? neighborBlock.canBlockBePushedByPiston(world, nextX, nextY, nextZ, facing)
                                            : neighborBlock.canBlockBePulledByPiston(world, nextX, nextY, nextZ, OPPOSITE_DIRECTION(facing))
                                    )
                                )
                            )
                        ) {
                            break;
                        }
                    case PLATFORM_CAN_LIFT: {
                        PLATFORM_LIFT_DEBUG("Liftable block ("+nextX+" "+nextY+" "+nextZ+")");
                        if (
                            facing == DIRECTION_UP ||
                            ((IBlockMixins)block).isStickyForBlocks(world, x, y, z, facing)
                        ) {
                            int i = lift_index_global;
                            do {
                                if (--i < LIFT_LIST_START_INDEX) {
                                    platform_blocks[lift_index_global] = packedPos;
                                    data_list[lift_index_global] = PLATFORM_BLOCK_STATE_PACK(
                                        neighborId,
                                        world.getBlockMetadata(nextX, nextY, nextZ),
                                        ZeroMetaUtil.getBlockExtMetadata(world, nextX, nextY, nextZ)
                                    );
                                    ++lift_index_global;
                                    break;
                                }
                            } while (platform_blocks[i] != packedPos);
                        }
                        break;
                    }
                }
            }
        } while (DIRECTION_IS_VALID(++facing));
        return true;
    }
    
    
    public void liftBlocks(World world, int x, int y, int z, MovingAnchorEntity associatedAnchorEntity) {
        PLATFORM_LIFT_DEBUG("Start anchor");
        
#if ENABLE_PLATFORM_MAX_DISTANCE
        anchor_position = BLOCK_POS_PACK(source_x = x, y, source_z = z);
#else
        anchor_position = BLOCK_POS_PACK(x, y, z);
#endif
        platform_index_global = PLATFORM_LIST_START_INDEX;
        lift_index_global = LIFT_LIST_START_INDEX;
        
#if ENABLE_PLATFORM_MAX_DISTANCE
        source_y = --y;
#else
        --y;
#endif
        
        if (this.addPlatformBlock(world, x, y, z, associatedAnchorEntity.motionY < 0.0D)) {
            long packedPos;
            
            PLATFORM_LIFT_DEBUG("LiftIndex "+lift_index_global);
            for (int i = lift_index_global; --i >= LIFT_LIST_START_INDEX;) {
                packedPos = platform_blocks[i];
                BLOCK_POS_UNPACK(packedPos, x, y, z);
                
                int blockId;
                int blockMeta;
#if ENABLE_METADATA_EXTENSION_COMPAT
                int blockExtMeta;
#endif
                blockstate_t blockState = data_list[i];
                PLATFORM_BLOCK_STATE_UNPACK(blockState, blockId, blockMeta, blockExtMeta);
                Block block = Block.blocksList[blockId];
                
                blockMeta = ((IBlockMixins)block).adjustMetadataForPlatformMove(blockMeta);
                
#if USE_PLATFORM_ENTITY_FOR_ALL_BLOCKS
                if (
                    ((IBlockMixins)block).getMobilityFlag(world, x, y, z) != PISTON_CAN_BREAK &&
                    // Rails just don't render correctly as a platform block
                    !(block instanceof BlockRailBase)
                ) {
                    MovingPlatformEntity lifted_entity = (MovingPlatformEntity)EntityList.createEntityOfType(
                        MovingPlatformEntity.class, world,
                        (double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D,
                        associatedAnchorEntity
                    );
                    
                    ((IMovingPlatformEntityMixins)lifted_entity).setBlockId(blockId);
                    ((IMovingPlatformEntityMixins)lifted_entity).setBlockMeta(blockMeta);
#if ENABLE_METADATA_EXTENSION_COMPAT
                    ZeroMetaUtil.addExtMetaToMovingPlatformEntity(lifted_entity, blockExtMeta);
#endif
                    
                    TileEntity tileEntity;
                    if ((tileEntity = world.getBlockTileEntity(x, y, z)) != null) {
                        world.removeBlockTileEntity(x, y, z);
                        ((IMovingPlatformEntityMixins)lifted_entity).storeTileEntity(tileEntity);
                    }
                    world.spawnEntityInWorld(lifted_entity);
                }
                else {
#endif
                    BlockLiftedByPlatformEntity lifted_entity = (BlockLiftedByPlatformEntity)EntityList.createEntityOfType(
                        BlockLiftedByPlatformEntity.class, world,
                        (double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D
                    );
                    lifted_entity.setBlockID(blockId);
                    lifted_entity.setBlockMetadata(blockMeta);
#if ENABLE_METADATA_EXTENSION_COMPAT
                    ZeroMetaUtil.addExtMetaToLiftedBlockEntity(lifted_entity, blockExtMeta);
#endif
                    world.spawnEntityInWorld(lifted_entity);
#if USE_PLATFORM_ENTITY_FOR_ALL_BLOCKS
                }
#endif
                
                world.setBlock(x, y, z, 0, 0, UPDATE_CLIENTS | UPDATE_IMMEDIATE | UPDATE_SUPPRESS_DROPS | UPDATE_MOVE_BY_PISTON);
            }
            
            PLATFORM_LIFT_DEBUG("PlatformIndex "+platform_index_global);
            for (int i = platform_index_global; --i >= PLATFORM_LIST_START_INDEX;) {
                packedPos = platform_blocks[i];
                BLOCK_POS_UNPACK(packedPos, x, y, z);
                
                MovingPlatformEntity moving_entity = (MovingPlatformEntity)EntityList.createEntityOfType(
                    MovingPlatformEntity.class, world,
                    (double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D,
                    associatedAnchorEntity
                );
                
                int blockId;
                int blockMeta;
#if ENABLE_METADATA_EXTENSION_COMPAT
                int blockExtMeta;
#endif
                blockstate_t blockState = data_list[i];
                PLATFORM_BLOCK_STATE_UNPACK(blockState, blockId, blockMeta, blockExtMeta);
                
                ((IMovingPlatformEntityMixins)moving_entity).setBlockId(blockId);
                ((IMovingPlatformEntityMixins)moving_entity).setBlockMeta(blockMeta);
#if ENABLE_METADATA_EXTENSION_COMPAT
                ZeroMetaUtil.addExtMetaToMovingPlatformEntity(moving_entity, blockExtMeta);
#endif

                // Blocks in this list shouldn't need to check tile entities, right?
                
                world.spawnEntityInWorld(moving_entity);
                
                world.setBlock(x, y, z, 0, 0, UPDATE_CLIENTS | UPDATE_IMMEDIATE | UPDATE_SUPPRESS_DROPS | UPDATE_MOVE_BY_PISTON);
            }
            
            for (int i = lift_index_global; --i >= LIFT_LIST_START_INDEX;) {
                // Set x,y,z to position of block in lift list
                packedPos = platform_blocks[i];
                //world.notifyBlocksOfNeighborChange(BLOCK_POS_UNPACK_ARGS(packedPos), PLATFORM_BLOCK_STATE_EXTRACT_ID(data_list[i]));
                //world.setBlock(BLOCK_POS_UNPACK_ARGS(packedPos), 0, 0, UPDATE_NEIGHBORS | UPDATE_CLIENTS | UPDATE_IMMEDIATE | UPDATE_SUPPRESS_DROPS | UPDATE_MOVE_BY_PISTON);
                int blockId = PLATFORM_BLOCK_STATE_EXTRACT_ID(data_list[i]);
                ((IWorldMixins)world).notifyBlockChangeAndComparators(BLOCK_POS_UNPACK_ARGS(packedPos), blockId, blockId);
            }
            for (int i = platform_index_global; --i >= PLATFORM_LIST_START_INDEX;) {
                // Set x,y,z to position of block in platform list
                packedPos = platform_blocks[i];
                //world.notifyBlocksOfNeighborChange(BLOCK_POS_UNPACK_ARGS(packedPos), PLATFORM_BLOCK_STATE_EXTRACT_ID(data_list[i]));
                //world.setBlock(BLOCK_POS_UNPACK_ARGS(packedPos), 0, 0, UPDATE_NEIGHBORS | UPDATE_CLIENTS | UPDATE_IMMEDIATE | UPDATE_SUPPRESS_DROPS | UPDATE_MOVE_BY_PISTON);
                int blockId = PLATFORM_BLOCK_STATE_EXTRACT_ID(data_list[i]);
                ((IWorldMixins)world).notifyBlockChangeAndComparators(BLOCK_POS_UNPACK_ARGS(packedPos), blockId, blockId);
            }
        }
    }
#endif
};