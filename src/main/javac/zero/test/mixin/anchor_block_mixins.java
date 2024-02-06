package zero.test.mixin;

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
import zero.test.mixin.IAnchorBlockAccessMixins;

import java.util.Random;

#include "..\feature_flags.h"
#include "..\util.h"

#define PLATFORM_LIFT_DEBUGGING 0

#if PLATFORM_LIFT_DEBUGGING
#define PLATFORM_LIFT_DEBUG(...) if (!world.isRemote) AddonHandler.logMessage(__VA_ARGS__)
#else
#define PLATFORM_LIFT_DEBUG(...)
#endif

@Mixin(AnchorBlock.class)
public abstract class AnchorBlockMixins extends Block {
    public AnchorBlockMixins(int blockId, Material material) {
        super(blockId, material);
    }
    
#if ENABLE_PLATFORM_EXTENSIONS

    @Shadow
    public abstract void convertAnchorToEntity(World world, int i, int j, int k, PulleyTileEntity attachedTileEntityPulley, int iMovementDirection);

#pragma push_macro("PLATFORM_LIFT_LIMIT")
#undef PLATFORM_LIFT_LIMIT
    private static final int PLATFORM_LIFT_LIMIT =
#pragma pop_macro("PLATFORM_LIFT_LIMIT")
    PLATFORM_LIFT_LIMIT;
#undef PLATFORM_LIFT_LIMIT

    private static final int PLATFORM_LIST_LENGTH = PLATFORM_LIFT_LIMIT;
    // IDK how to properly calculate this, so just add a lot of extra space?
    private static final int LIFT_LIST_LENGTH = PLATFORM_LIFT_LIMIT * 5;
    
    private static final int PLATFORM_LIST_START_INDEX = 0;
    private static final int LIFT_LIST_START_INDEX = PLATFORM_LIST_START_INDEX + PLATFORM_LIST_LENGTH;
    
    private static final int PLATFORM_STATE_LIST_LENGTH = PLATFORM_LIST_LENGTH;
    private static final int LIFT_STATE_LIST_LENGTH = LIFT_LIST_LENGTH;
    
    private static final int PLATFORM_STATE_LIST_START_INDEX = 0;
    private static final int LIFT_STATE_LIST_START_INDEX = PLATFORM_STATE_LIST_START_INDEX + PLATFORM_STATE_LIST_LENGTH;
    
    private static final long[] platform_blocks = new long[PLATFORM_LIST_LENGTH + LIFT_LIST_LENGTH];
    private static final int[] data_list = new int[PLATFORM_STATE_LIST_LENGTH + LIFT_STATE_LIST_LENGTH];
    
    private static long anchor_position;
    
    private static int platform_index_global;
    private static int lift_index_global;
    
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
        data_list[platform_index_global] = BLOCK_STATE_PACK(blockId, world.getBlockMetadata(x, y, z));
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
                                    data_list[lift_index_global] = BLOCK_STATE_PACK(neighborId, world.getBlockMetadata(nextX, nextY, nextZ));
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

    @Overwrite(remap=false)
    public void convertConnectedPlatformsToEntities(World world, int x, int y, int z, MovingAnchorEntity associatedAnchorEntity) {
        PLATFORM_LIFT_DEBUG("Start anchor");
        
        anchor_position = BLOCK_POS_PACK(x, y, z);
        platform_index_global = PLATFORM_LIST_START_INDEX;
        lift_index_global = LIFT_LIST_START_INDEX;
        
        --y;
        
        if (this.addPlatformBlock(world, x, y, z, associatedAnchorEntity.motionY < 0.0D)) {
            long packedPos;
            
            PLATFORM_LIFT_DEBUG("PlatformIndex "+platform_index_global);
            for (int i = platform_index_global; --i >= PLATFORM_LIST_START_INDEX;) {
                packedPos = platform_blocks[i];
                BLOCK_POS_UNPACK(packedPos, x, y, z);
                
                MovingPlatformEntity moving_entity = (MovingPlatformEntity)EntityList.createEntityOfType(
                    MovingPlatformEntity.class, world,
                    (float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F, 
                    associatedAnchorEntity
                );
                
                int blockId;
                int blockMeta;
                int blockState = data_list[i];
                BLOCK_STATE_UNPACK(blockState, blockId, blockMeta);
                
                ((IMovingPlatformEntityMixins)moving_entity).setBlockId(blockId);
                ((IMovingPlatformEntityMixins)moving_entity).setBlockMeta(blockMeta);
                
                world.spawnEntityInWorld(moving_entity);
                
                world.setBlock(x, y, z, 0, 0, UPDATE_CLIENTS | UPDATE_IMMEDIATE | UPDATE_SUPPRESS_DROPS | UPDATE_MOVE_BY_PISTON);
            }
            PLATFORM_LIFT_DEBUG("LiftIndex "+lift_index_global);
            for (int i = lift_index_global; --i >= LIFT_LIST_START_INDEX;) {
                packedPos = platform_blocks[i];
                BLOCK_POS_UNPACK(packedPos, x, y, z);
                
                int blockId;
                int blockMeta;
                int blockState = data_list[i];
                BLOCK_STATE_UNPACK(blockState, blockId, blockMeta);
                Block block = Block.blocksList[blockId];
                
                BlockLiftedByPlatformEntity lifted_entity = (BlockLiftedByPlatformEntity)EntityList.createEntityOfType(
                    BlockLiftedByPlatformEntity.class, world,
                    (double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D
                );
                lifted_entity.setBlockID(blockId);
                lifted_entity.setBlockMetadata(((IBlockMixins)block).adjustMetadataForPlatformMove(blockMeta));
                world.spawnEntityInWorld(lifted_entity);
                world.setBlock(x, y, z, 0, 0, UPDATE_CLIENTS | UPDATE_IMMEDIATE | UPDATE_SUPPRESS_DROPS | UPDATE_MOVE_BY_PISTON);
            }
            
            for (int i = lift_index_global; --i >= LIFT_LIST_START_INDEX;) {
                // Set x,y,z to position of block in lift list
                packedPos = platform_blocks[i];
                world.notifyBlocksOfNeighborChange(BLOCK_POS_UNPACK_ARGS(packedPos), BLOCK_STATE_EXTRACT_ID(data_list[i]));
                //world.setBlock(BLOCK_POS_UNPACK_ARGS(packedPos), 0, 0, UPDATE_NEIGHBORS | UPDATE_CLIENTS | UPDATE_IMMEDIATE | UPDATE_SUPPRESS_DROPS | UPDATE_MOVE_BY_PISTON);
            }
            for (int i = platform_index_global; --i >= PLATFORM_LIST_START_INDEX;) {
                // Set x,y,z to position of block in platform list
                packedPos = platform_blocks[i];
                world.notifyBlocksOfNeighborChange(BLOCK_POS_UNPACK_ARGS(packedPos), BLOCK_STATE_EXTRACT_ID(data_list[i]));
                //world.setBlock(BLOCK_POS_UNPACK_ARGS(packedPos), 0, 0, UPDATE_NEIGHBORS | UPDATE_CLIENTS | UPDATE_IMMEDIATE | UPDATE_SUPPRESS_DROPS | UPDATE_MOVE_BY_PISTON);
            }
        }
        /*
    	--y;
        
        Block target_block = Block.blocksList[world.getBlockId(x, y, z)];
        if (
            !BLOCK_IS_AIR(target_block) &&
            ((IBlockMixins)target_block).getPlatformMobilityFlag(world, x, y, z) == PLATFORM_MAIN_SUPPORT
        ) {
            ((PlatformBlock)BTWBlocks.platform).covertToEntitiesFromThisPlatform(
				world,
                x, y, z,
                associatedAnchorEntity
            );
        }
        */
    }
    
    @Overwrite(remap=false)
    public boolean notifyAnchorBlockOfAttachedPulleyStateChange(PulleyTileEntity tileEntityPulley, World world, int x, int y, int z){
		int iMovementDirection = 0;
		
		if (tileEntityPulley.isRaising()) {
			if (world.getBlockId(x, y + 1, z) == BTWBlocks.ropeBlock.blockID) {
				iMovementDirection = 1;
			}
		}
		else if (tileEntityPulley.isLowering()) {
            Block block = Block.blocksList[world.getBlockId(x, y - 1, z)];
            if (
                BLOCK_IS_AIR(block) ||
                ((IBlockMixins)block).getPlatformMobilityFlag(world, x, y - 1, z) == PLATFORM_MAIN_SUPPORT
            ) {
                iMovementDirection = -1;
            }
		}
		
		if (iMovementDirection != 0) {
			this.callConvertAnchorToEntity(world, x, y, z, tileEntityPulley, iMovementDirection);
			
			return true;
		}
		return false;
	}
#endif
}