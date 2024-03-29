package zero.test;

import net.minecraft.src.*;

import btw.block.BTWBlocks;
import btw.block.blocks.PistonBlockBase;
import btw.block.blocks.PistonBlockMoving;
import btw.item.util.ItemUtils;
import btw.AddonHandler;
import btw.BTWAddon;

import zero.test.IBlockMixins;
import zero.test.mixin.IPistonBaseAccessMixins;
import zero.test.IWorldMixins;
import zero.test.IBlockEntityPistonMixins;
import zero.test.ZeroUtil;
import zero.test.ZeroCompatUtil;

#include "func_aliases.h"
#include "feature_flags.h"
#include "util.h"

#define DIRECTION_META_OFFSET 0
#define EXTENDED_META_OFFSET 3
#define EXTENDED_META_BITS 1
#define EXTENDED_META_IS_BOOL true

#define PISTON_PRINT_DEBUGGING 0

#if PISTON_PRINT_DEBUGGING
#define PISTON_DEBUG(...) if (!world.isRemote) AddonHandler.logMessage(__VA_ARGS__)
#else
#define PISTON_DEBUG(...)
#endif

#define SHOVEL_PRINT_DEBUGGING 0

#if SHOVEL_PRINT_DEBUGGING
#define SHOVEL_DEBUG(...) if (!world.isRemote) AddonHandler.logMessage(__VA_ARGS__)
#else
#define SHOVEL_DEBUG(...)
#endif

#if ENABLE_METADATA_EXTENSION_COMPAT
#define blockstate_t long
#define PISTON_BLOCK_STATE_PACK(...) BLOCK_STATE_PACK_LONG(__VA_ARGS__)
#define PISTON_BLOCK_STATE_UNPACK(...) BLOCK_STATE_UNPACK_LONG(__VA_ARGS__)
#define PISTON_BLOCK_STATE_EXTRACT_ID(...) BLOCK_STATE_LONG_EXTRACT_ID(__VA_ARGS__)
#define PISTON_BLOCK_STATE_EXTRACT_META(...) BLOCK_STATE_LONG_EXTRACT_META(__VA_ARGS__)
#define PISTON_SET_BLOCK(world, x, y, z, blockId, meta, extMeta, flags) ZeroCompatUtil.setBlockWithExtra(world, x, y, z, blockId, meta, extMeta, flags)
#define PISTON_GET_TILE_ENTITY(blockId, meta, extMeta, direction, isExtending, isBase) ZeroCompatUtil.getPistonTileEntity(blockId, meta, extMeta, direction, isExtending, isBase)
#define PISTON_GET_SHOVEL_TILE_ENTITY(blockId, meta, extMeta, direction) ZeroCompatUtil.getShoveledTileEntity(blockId, meta, extMeta, direction)
#else
#define blockstate_t int
#define PISTON_BLOCK_STATE_PACK(...) BLOCK_STATE_PACK(__VA_ARGS__)
#define PISTON_BLOCK_STATE_UNPACK(...) BLOCK_STATE_UNPACK(__VA_ARGS__)
#define PISTON_BLOCK_STATE_EXTRACT_ID(...) BLOCK_STATE_EXTRACT_ID(__VA_ARGS__)
#define PISTON_BLOCK_STATE_EXTRACT_META(...) BLOCK_STATE_EXTRACT_META(__VA_ARGS__)
#define PISTON_SET_BLOCK(world, x, y, z, blockId, meta, extMeta, flags) world.setBlock(x, y, z, blockId, meta, flags)
#define PISTON_GET_TILE_ENTITY(blockId, meta, extMeta, direction, isExtending, isBase) BlockPistonMoving.getTileEntity(blockId, meta, direction, isExtending, isBase)
#define PISTON_GET_SHOVEL_TILE_ENTITY(blockId, meta, extMeta, direction) PistonBlockMoving.getShoveledTileEntity(blockId, meta, direction)
#endif

public class PistonResolver {
#pragma push_macro("PISTON_PUSH_LIMIT")
#undef PISTON_PUSH_LIMIT
    private static final int PISTON_PUSH_LIMIT =
#pragma pop_macro("PISTON_PUSH_LIMIT")
    PISTON_PUSH_LIMIT;
#undef PISTON_PUSH_LIMIT

    /*

        Push list:
            Contains XYZ coordinates of blocks being moved
        
        Destroy list:
            Contains XYZ coordinates of blocks being destroyed
        
        Shovel eject list:
            Contains XYZ coordinates being ejected into by shovels
        
        Shovel list:
            Contains XYZ coordinates blocks being shoveled.
            Indexed separately from the eject list since
            multiple blocks can be ejected towards the same place
        
        Push block state list:
            Contains the block IDs/meta of blocks being moved.
            Indices correspond to the push list.
            Exists so that neighbor updates can still
            recieve correct IDs after moving pistons
            have been created.
        
        Destroy block state list:
            Contains the block IDs/meta of blocks being destroyed.
            Indices correspond to the destroy list.
            Exists so that neighbor updates can still
            recieve correct IDs after moving pistons
            have been created.
        
        Shovel block state list:
            Contains the block IDs/meta of blocks being shoveled.
            Indices correspond to the shovel list.
        
        Shovel direction list:
            Contains the ejection directions of blocks being shoveled.
            Indices correspond to the shovel list.
    
    */
    
    private static final int PUSH_LIST_LENGTH = PISTON_PUSH_LIMIT;
    private static final int DESTROY_LIST_LENGTH = PUSH_LIST_LENGTH;
    private static final int SHOVEL_EJECT_LIST_LENGTH = PUSH_LIST_LENGTH;
    private static final int SHOVEL_LIST_LENGTH = PUSH_LIST_LENGTH;

    private static final int PUSH_LIST_START_INDEX = 0;
    private static final int DESTROY_LIST_START_INDEX = PUSH_LIST_START_INDEX + PUSH_LIST_LENGTH;
    private static final int SHOVEL_EJECT_LIST_START_INDEX = DESTROY_LIST_START_INDEX + DESTROY_LIST_LENGTH;
    private static final int SHOVEL_LIST_START_INDEX = SHOVEL_EJECT_LIST_START_INDEX + SHOVEL_EJECT_LIST_LENGTH;
    
    private static final int SHOVEL_LIST_OFFSET = SHOVEL_EJECT_LIST_LENGTH;
    
    private static final int PUSH_BLOCK_STATE_LIST_LENGTH = PUSH_LIST_LENGTH;
    private static final int DESTROY_BLOCK_STATE_LIST_LENGTH = DESTROY_LIST_LENGTH;
    private static final int SHOVEL_BLOCK_STATE_LIST_LENGTH = SHOVEL_LIST_LENGTH;
    private static final int SHOVEL_DIRECTION_LIST_LENGTH = SHOVEL_LIST_LENGTH;
    
    private static final int PUSH_BLOCK_STATE_LIST_START_INDEX = 0;
    private static final int DESTROY_BLOCK_STATE_LIST_START_INDEX = PUSH_BLOCK_STATE_LIST_START_INDEX + PUSH_BLOCK_STATE_LIST_LENGTH;
    private static final int SHOVEL_BLOCK_STATE_LIST_START_INDEX = DESTROY_BLOCK_STATE_LIST_START_INDEX + DESTROY_BLOCK_STATE_LIST_LENGTH;
    private static final int SHOVEL_DIRECTION_LIST_START_INDEX = SHOVEL_BLOCK_STATE_LIST_START_INDEX + SHOVEL_BLOCK_STATE_LIST_LENGTH;
    
    private static final int SHOVEL_BLOCK_STATE_LIST_OFFSET = 0;
    private static final int SHOVEL_DIRECTION_LIST_OFFSET = SHOVEL_BLOCK_STATE_LIST_LENGTH;

    
    // The destroy list can't be longer than the push list, right?
    // TODO: TEST THIS ASSUMPTION
    private final long[] pushed_blocks = new long[PUSH_LIST_LENGTH + DESTROY_LIST_LENGTH + SHOVEL_EJECT_LIST_LENGTH + SHOVEL_LIST_LENGTH];
    private final blockstate_t[] data_list = new blockstate_t[PUSH_BLOCK_STATE_LIST_LENGTH + DESTROY_BLOCK_STATE_LIST_LENGTH + SHOVEL_BLOCK_STATE_LIST_LENGTH + SHOVEL_DIRECTION_LIST_LENGTH];
    
    // Position of the piston 
    private long piston_position;
    
    // Becase screw java and by value semantics
    private int push_index_global;
    private int destroy_index_global;
    private int shovel_index_global;
    
    // This is only called in situations when the
    // current block is known to not be air
    //
    // Returns false if the block chain is invalid,
    // which cancels all movement.
    // Returns true otherwise.
    private boolean addBranch(World world, int x, int y, int z, int direction) {
        // This could be pulled from the static lists if an index was passed in
        int blockId = world.getBlockId(x, y, z);
        Block block = Block.blocksList[blockId];
        PISTON_DEBUG("Branching Block ("+x+" "+y+" "+z+")"+direction);
        
        int facing = 0;
        do {
            if (
                DIRECTION_AXIS(facing) != DIRECTION_AXIS(direction) &&
                ((IBlockMixins)block).isStickyForBlocks(world, x, y, z, facing)
            ) {
                int nextX = x + Facing.offsetsXForSide[facing];
                int nextY = y + Facing.offsetsYForSide[facing];
                int nextZ = z + Facing.offsetsZForSide[facing];
                Block neighborBlock = Block.blocksList[world.getBlockId(nextX, nextY, nextZ)];
                if (
                    !BLOCK_IS_AIR(neighborBlock) &&
                    ((IBlockMixins)neighborBlock).canBeStuckTo(world, nextX, nextY, nextZ, facing, blockId) &&
                    !this.addMovedBlock(world, nextX, nextY, nextZ, direction)
                ) {
                    return false;
                }
            }
        } while (DIRECTION_IS_VALID(++facing));
        return true;
    }
    
    // TODO: Make this less naive
    private static int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }
    
    // Returns false if the block chain is invalid,
    // which cancels all movement.
    // Returns true otherwise.
    private boolean addMovedBlock(World world, int x, int y, int z, int direction) {
        // IDK where vanilla checks for valid block positions, but the old
        // 1.5 logic did a Y check here so I copied it.
        if (!IS_VALID_BLOCK_Y_POS(y)) {
            return false;
        }
        
        int blockId = world.getBlockId(x, y, z);
        Block block = Block.blocksList[blockId];
        long packedPos;
        if (
            BLOCK_IS_AIR(block) ||
            (packedPos = BLOCK_POS_PACK(x, y, z)) == piston_position ||
            (
                // Compatibility shim:
                // Since canBlockBePulledByPiston is used to prevent
                // destroyable blocks from getting pushed by adjacent
                // sticky blocks, blocks that can't be pulled need
                // to be detected separately. Vanilla solves this by
                // having a more complicated implementation of pushing
                // logic.
                ((IBlockMixins)block).getMobilityFlag(world, x, y, z) != PISTON_CAN_PUSH_ONLY &&
                !block.canBlockBePulledByPiston(world, x, y, z, direction)
            )
        ) {
            return true;
        }
        PISTON_DEBUG("SearchForExistingPush ("+x+" "+y+" "+z+")");
        
        
        // Copy the global push index into a local
        // to avoid mutating it until the movement
        // is confirmed
        int pushIndex = push_index_global;
        
        // This replaces toPush.contains
        for (int i = pushIndex; --i >= PUSH_LIST_START_INDEX;) {
            if (pushed_blocks[i] == packedPos) {
                return true;
            }
        }
        
        // START SHOVEL CODE
        // Don't push blocks that're already
        // getting shoveled. This helps prevent
        // duping blocks when slime tries
        // to push a block in front of a shovel.
        for (int i = shovel_index_global + SHOVEL_LIST_OFFSET; --i >= SHOVEL_LIST_START_INDEX;) {
            if (pushed_blocks[i] == packedPos) {
                return true;
            }
        }
        // END SHOVEL CODE
        
        int pushWriteIndex = pushIndex;
        
        // Leave the starting position in x,y,z
        // Copy to next variables for mutation
        int nextX = x;
        int nextY = y;
        int nextZ = z;
        long nextPackedPos = packedPos;
        int nextBlockId = blockId;
        Block nextBlock = block;
        
        // This loop handles pulling blocks.
        loop {
            PISTON_DEBUG("StickySearch ("+nextX+" "+nextY+" "+nextZ+")");
            
            // Check the push limit
            if (pushWriteIndex == (PUSH_LIST_START_INDEX + PUSH_LIST_LENGTH)) {
                PISTON_DEBUG("Push failed limit reached A");
                return false;
            }
            ++pushWriteIndex;
            
            if (!((IBlockMixins)nextBlock).isStickyForBlocks(world, nextX, nextY, nextZ, OPPOSITE_DIRECTION(direction))) {
                PISTON_DEBUG("IsSticky false");
                break;
            }
            
            PISTON_DEBUG("IsSticky true");
            // Index in opposite direction
            int tempX = nextX - Facing.offsetsXForSide[direction];
            int tempY = nextY - Facing.offsetsYForSide[direction];
            int tempZ = nextZ - Facing.offsetsZForSide[direction];
            long tempPackedPos = BLOCK_POS_PACK(tempX, tempY, tempZ);
            if (tempPackedPos == piston_position) {
                break;
            }
            
            blockId = world.getBlockId(tempX, tempY, tempZ);
            nextBlock = Block.blocksList[blockId];
            if (
                BLOCK_IS_AIR(nextBlock) ||
                !((IBlockMixins)nextBlock).canBeStuckTo(world, tempX, tempY, tempZ, direction, nextBlockId) ||
                !nextBlock.canBlockBePulledByPiston(world, tempX, tempY, tempZ, direction)
            ) {
                break;
            }
            
            nextX = tempX;
            nextY = tempY;
            nextZ = tempZ;
            nextBlockId = blockId;
            nextPackedPos = tempPackedPos;
        }
        
        int pushCount = pushWriteIndex - pushIndex;
        // This loop adds the current block and any
        // pulled blocks to the push list.
        // Regardless of what the previous loop does,
        // nextX,nextY,nextZ will contain the position
        // of the furthest away block being moved,
        // nextPackedPos will contain that same position,
        // and nextBlockId will contain the ID of the
        // block at that position.
        // This is potentially the starting block.
        do {
            // Index back towards the start
            PISTON_DEBUG("AddPush ("+nextX+" "+nextY+" "+nextZ+")");
            data_list[pushIndex] = PISTON_BLOCK_STATE_PACK(
                nextBlockId,
                world.getBlockMetadata(nextX, nextY, nextZ),
                ZeroCompatUtil.getBlockExtMetadata(world, nextX, nextY, nextZ)
            );
            pushed_blocks[pushIndex++] = nextPackedPos;
            nextX += Facing.offsetsXForSide[direction];
            nextY += Facing.offsetsYForSide[direction];
            nextZ += Facing.offsetsZForSide[direction];
            nextPackedPos = BLOCK_POS_PACK(nextX, nextY, nextZ);
            nextBlockId = world.getBlockId(nextX, nextY, nextZ);
        } while (pushIndex != pushWriteIndex);
        
        PISTON_DEBUG("PushCount "+pushCount);
        
        // This loop handles pushing blocks.
        // x,y,z still contain the initial
        // movement position on entry, packedPos
        // still contains the initial position,
        // block contains the block at that position,
        // nextX,nextY,nextZ contain the position
        // of the first block to check for pushing,
        // nextPackedPos contains that same position,
        // and nextBlockId contains the id of the
        // first block to check for pushing.
        loop {
            push_index_global = pushIndex;
            PISTON_DEBUG("New push index "+push_index_global);
            PISTON_DEBUG("ParsePush ("+nextX+" "+nextY+" "+nextZ+")");
            
            // This loop must index forwards
            for (int i = PUSH_LIST_START_INDEX; i < pushIndex; ++i) {
                // Loop will never continue when this is true
                if (pushed_blocks[i] == nextPackedPos) {
                    // This branch handles two
                    // sticky blocks trying to push
                    // the same block at once
                    PISTON_DEBUG("Hacky swap code");
                    //PISTON_DEBUG("Swap stats "+"n3="+pushCount+" n4="+i);
                    
                    //PISTON_DEBUG(""+" "+pushed_blocks[PUSH_LIST_START_INDEX]+" "+pushed_blocks[PUSH_LIST_START_INDEX+1]+" "+pushed_blocks[PUSH_LIST_START_INDEX+2]+" "+pushed_blocks[PUSH_LIST_START_INDEX+3]+" "+pushed_blocks[PUSH_LIST_START_INDEX+4]+" "+pushed_blocks[PUSH_LIST_START_INDEX+5]+" "+pushed_blocks[PUSH_LIST_START_INDEX+6]+" "+pushed_blocks[PUSH_LIST_START_INDEX+7]+" "+pushed_blocks[PUSH_LIST_START_INDEX+8]+" "+pushed_blocks[PUSH_LIST_START_INDEX+9]+" "+pushed_blocks[PUSH_LIST_START_INDEX+10]+" "+pushed_blocks[PUSH_LIST_START_INDEX+11]);
                    
                    // WARNING:
                    // This section requires
                    // the push list to be first
                    
                    // Hacky in place array right rotation
                    // Based on the first suitable thing
                    // I found on Google: https://www.codewhoop.com/array/rotation-in-place.html
                    
                    int swapMax = pushIndex - gcd(pushIndex -= i, pushCount);
                    pushWriteIndex = pushCount - i;
                    
#if ENABLE_METADATA_EXTENSION_COMPAT
#define tempState nextPackedPos
#else
#define tempState blockId
#endif
                    
                    for (int j = i; j < swapMax; ++j) {
                        int k = j;
                        tempState = data_list[j];
                        packedPos = pushed_blocks[j];
                        loop {
                            int d = i + (k + pushWriteIndex) % pushIndex;
                            if (d == j) {
                                break;
                            }
                            data_list[k] = data_list[d];
                            pushed_blocks[k] = pushed_blocks[d];
                            k = d;
                        }
                        data_list[k] = tempState;
                        pushed_blocks[k] = packedPos;
                    }
                    i += pushCount;
                    
#undef tempState
                    
                    //PISTON_DEBUG(""+" "+pushed_blocks[PUSH_LIST_START_INDEX]+" "+pushed_blocks[PUSH_LIST_START_INDEX+1]+" "+pushed_blocks[PUSH_LIST_START_INDEX+2]+" "+pushed_blocks[PUSH_LIST_START_INDEX+3]+" "+pushed_blocks[PUSH_LIST_START_INDEX+4]+" "+pushed_blocks[PUSH_LIST_START_INDEX+5]+" "+pushed_blocks[PUSH_LIST_START_INDEX+6]+" "+pushed_blocks[PUSH_LIST_START_INDEX+7]+" "+pushed_blocks[PUSH_LIST_START_INDEX+8]+" "+pushed_blocks[PUSH_LIST_START_INDEX+9]+" "+pushed_blocks[PUSH_LIST_START_INDEX+10]+" "+pushed_blocks[PUSH_LIST_START_INDEX+11]);
                    
                    for (int j = PUSH_LIST_START_INDEX; j < i; ++j) {
                        packedPos = pushed_blocks[j];
                        BLOCK_POS_UNPACK(packedPos, x, y, z);
                        
                        // Vanilla checks stickiness as part of this statement,
                        // but supporting directional stickiness required
                        // moving the check into add_branch
                        if (
                            // Air can't be added to the push list,
                            // so no need to check for that here
                            !this.addBranch(world, x, y, z, direction)
                        ) {
                            PISTON_DEBUG("Push failed move block branching ("+x+" "+y+" "+z+")");
                            return false;
                        }
                    }
                    return true;
                    // unreachable
                }
                // this is reachable, part of above for loop
                // when the if is false
            }
            nextBlock = Block.blocksList[nextBlockId];
            
            if (BLOCK_IS_AIR(nextBlock)) {
                return true;
            }
            
            if (
                nextPackedPos == piston_position ||
                !nextBlock.canBlockBePushedByPiston(world, nextX, nextY, nextZ, direction)
            ) {
                PISTON_DEBUG("Push failed move block IDK ("+nextX+" "+nextY+" "+nextZ+")");
                return false;
            }
            
            int nextBlockMeta = world.getBlockMetadata(nextX, nextY, nextZ);
#if ENABLE_METADATA_EXTENSION_COMPAT
            int nextBlockExtMeta = ZeroCompatUtil.getBlockExtMetadata(world, nextX, nextY, nextZ);
#endif
            if (nextBlock.getMobilityFlag() == PISTON_CAN_BREAK) {
                data_list[destroy_index_global] = PISTON_BLOCK_STATE_PACK(nextBlockId, nextBlockMeta, nextBlockExtMeta);
                pushed_blocks[destroy_index_global++] = nextPackedPos;
                return true;
            }
            
            // START SHOVEL CODE
            if (nextBlock.canBePistonShoveled(world, nextX, nextY, nextZ)) {
                // If nextX,nextY,nextZ are a block that can be shoveled,
                // then x,y,z/block will contain the shovel itself
                int ejectDirection = block.getPistonShovelEjectDirection(world, x, y, z, direction);
                if (ejectDirection >= 0) {
                    // Set x,y,z to the position of the ejection destination
                    x = nextX + Facing.offsetsXForSide[ejectDirection];
                    y = nextY + Facing.offsetsYForSide[ejectDirection];
                    z = nextZ + Facing.offsetsZForSide[ejectDirection];
                    int ejectDestinationBlockId = world.getBlockId(x, y, z);
                    block = Block.blocksList[ejectDestinationBlockId];
                    if (
                        BLOCK_IS_AIR(block) ||
                        ejectDestinationBlockId == Block.pistonMoving.blockID ||
                        block.getMobilityFlag() == PISTON_CAN_BREAK
                    ) {
                        packedPos = BLOCK_POS_PACK(x, y, z);
                        // Make sure the ejection block isn't already being used
                        int i = shovel_index_global;
                        do {
                            if (--i < SHOVEL_EJECT_LIST_START_INDEX) {
                                data_list[shovel_index_global] = PISTON_BLOCK_STATE_PACK(nextBlockId, nextBlockMeta, nextBlockExtMeta);
                                data_list[shovel_index_global + SHOVEL_DIRECTION_LIST_OFFSET] = ejectDirection;
                                pushed_blocks[shovel_index_global] = packedPos;
                                pushed_blocks[shovel_index_global + SHOVEL_LIST_OFFSET] = nextPackedPos;
                                ++shovel_index_global;
                                return true;
                            }
                        } while (pushed_blocks[i] != packedPos);
                    }
                }
            }
            // END SHOVEL CODE
            
            // Check the push limit
            if (pushIndex == (PUSH_LIST_START_INDEX + PUSH_LIST_LENGTH)) {
                PISTON_DEBUG("Push failed limit reached B");
                return false;
            }
            
            data_list[pushIndex] = PISTON_BLOCK_STATE_PACK(nextBlockId, nextBlockMeta, nextBlockExtMeta);
            pushed_blocks[pushIndex++] = nextPackedPos;
            ++pushCount;
            // Set x,y,z to the position of the currently
            // pushed block, since it's now the previous
            x = nextX;
            y = nextY;
            z = nextZ;
            packedPos = nextPackedPos;
            block = nextBlock;
            // Index nextX,nextY,nextZ to the position of
            // the next potential pushed block
            nextX += Facing.offsetsXForSide[direction];
            nextY += Facing.offsetsYForSide[direction];
            nextZ += Facing.offsetsZForSide[direction];
            nextPackedPos = BLOCK_POS_PACK(nextX, nextY, nextZ);
            nextBlockId = world.getBlockId(nextX, nextY, nextZ);
        }
        // unreachable
    }
    
    private boolean resolve(World world, int x, int y, int z, int direction, boolean isExtending) {
        
        int blockId = world.getBlockId(x, y, z);
        Block block = Block.blocksList[blockId];
        if (BLOCK_IS_AIR(block)) {
            return true;
        }
        
        long packedPos = BLOCK_POS_PACK(x, y, z);
        if (
            ((IBlockMixins)block).getMobilityFlag(world, x, y, z) != PISTON_CAN_PUSH_ONLY &&
            !block.canBlockBePulledByPiston(world, x, y, z, direction)
        ) {
            if (
                isExtending &&
                block.getMobilityFlag() == PISTON_CAN_BREAK
            ) {
                // Add destroy
                PISTON_DEBUG("Resolve destroy ("+x+" "+y+" "+z+")");
                data_list[destroy_index_global] = PISTON_BLOCK_STATE_PACK(
                    blockId,
                    world.getBlockMetadata(x, y, z),
                    ZeroCompatUtil.getBlockExtMetadata(world, x, y, z)
                );
                pushed_blocks[destroy_index_global++] = packedPos;
                return true;
            }
            PISTON_DEBUG("Push failed immobile "+blockId+"("+x+" "+y+" "+z+")");
            return false;
        }
        
        PISTON_DEBUG("Resolve add blocks ("+x+" "+y+" "+z+")");
        if (!this.addMovedBlock(world, x, y, z, direction)) {
            PISTON_DEBUG("Push failed move block");
            return false;
        }
        
        // This loop uses the global index to end iteration,
        // which can be mutated by add_branch. This is an
        // intentional side effect.
        for (int i = PUSH_LIST_START_INDEX; i < push_index_global; ++i) {
            // Set x,y,z to the position of the next block in push list
            packedPos = pushed_blocks[i];
            
            // Air will never be added to the push list,
            // so no checks are done for that.
            if (
                // Vanilla checks stickiness as part of this statement,
                // but supporting directional stickiness required
                // moving the check into add_branch
                !this.addBranch(world, BLOCK_POS_UNPACK_ARGS(packedPos), direction)
            ) {
                PISTON_DEBUG("Push failed branching");
                return false;
            }
        }
        return true;
    }
    
    public boolean moveBlocks(World world, int x, int y, int z, int direction, boolean isExtending, boolean isSticky) {
        
        // Set initial global values
        piston_position = BLOCK_POS_PACK(x, y, z);
        push_index_global = PUSH_LIST_START_INDEX;
        destroy_index_global = DESTROY_LIST_START_INDEX;
        shovel_index_global = SHOVEL_EJECT_LIST_START_INDEX;
        
        x += Facing.offsetsXForSide[direction];
        y += Facing.offsetsYForSide[direction];
        z += Facing.offsetsZForSide[direction];
        
        // Save the position of the piston head
        int headX = x;
        int headY = y;
        int headZ = z;
        
        if (!isExtending) {
            if (world.getBlockId(x, y, z) == Block.pistonExtension.blockID) {
                world.setBlock(x, y, z, 0, 0, UPDATE_SUPPRESS_DROPS | UPDATE_SUPPRESS_LIGHT);
            }
            x += Facing.offsetsXForSide[direction];
            y += Facing.offsetsYForSide[direction];
            z += Facing.offsetsZForSide[direction];
            direction = OPPOSITE_DIRECTION(direction);
        }
        
        if (!this.resolve(world, x, y, z, direction, isExtending)) {
            return false;
        }
        
        long packedPos;
        int blockId;
        int blockMeta;
#if ENABLE_METADATA_EXTENSION_COMPAT
        int blockExtMeta;
#endif
        blockstate_t blockState;
        Block block;
        
        PISTON_DEBUG("DestroyIndex "+destroy_index_global);
        for (int i = destroy_index_global; --i >= DESTROY_LIST_START_INDEX;) {
            // Set x,y,z to position of block in destroy list
            packedPos = pushed_blocks[i];
            BLOCK_POS_UNPACK(packedPos, x, y, z);
            
            // Get data about destroyed block
            blockState = data_list[i];
            // This one doesn't care about extMeta
            BLOCK_STATE_UNPACK(blockState, blockId, blockMeta);
            block = Block.blocksList[blockId];
            blockMeta = block.adjustMetadataForPistonMove(blockMeta);
            
            PISTON_DEBUG("Destroy "+blockId+"."+blockMeta+"("+x+" "+y+" "+z+")");
            
            block.onBrokenByPistonPush(world, x, y, z, blockMeta);
            world.setBlock(x, y, z, 0, 0, UPDATE_IMMEDIATE | UPDATE_KNOWN_SHAPE);
        }
        
        PISTON_DEBUG("PushIndex "+push_index_global);
        for (int i = push_index_global; --i >= PUSH_LIST_START_INDEX;) {
            // Set x,y,z to position of block in push list
            packedPos = pushed_blocks[i];
            BLOCK_POS_UNPACK(packedPos, x, y, z);
            
            // Get data about pushed block
            blockState = data_list[i];
            PISTON_BLOCK_STATE_UNPACK(blockState, blockId, blockMeta, blockExtMeta);
            block = Block.blocksList[blockId];
            blockMeta = block.adjustMetadataForPistonMove(blockMeta);
            
            PISTON_DEBUG("Push "+blockId+"."+blockMeta+"("+x+" "+y+" "+z+")");
            
            TileEntity prevTileEntity = world.getBlockTileEntity(x, y, z);
            world.removeBlockTileEntity(x, y, z);
            
            packedPos = BLOCK_POS_PACK(x + Facing.offsetsXForSide[direction], y + Facing.offsetsYForSide[direction], z + Facing.offsetsZForSide[direction]);
            
            int j = i;
            do {
                if (--j <= PUSH_LIST_START_INDEX) {
                    // This is to make sure that blocks being pulled leave air
                    // behind when another block isn't moving into that space.
                    // This replaces the hashmap from vanilla
                    world.setBlock(x, y, z, 0, 0, UPDATE_CLIENTS);
                    break;
                }
            } while (pushed_blocks[j] != packedPos);
            
            // Offset x,y,z to the destination position
            x += Facing.offsetsXForSide[direction];
            y += Facing.offsetsYForSide[direction];
            z += Facing.offsetsZForSide[direction];
            PISTON_SET_BLOCK(world, x, y, z, Block.pistonMoving.blockID, blockMeta, blockExtMeta, UPDATE_IMMEDIATE | UPDATE_SUPPRESS_DROPS | UPDATE_MOVE_BY_PISTON);
            TileEntity tileEntity = PISTON_GET_TILE_ENTITY(blockId, blockMeta, blockExtMeta, direction, true, false);
            if (prevTileEntity != null) {
                ((IBlockEntityPistonMixins)tileEntity).storeTileEntity(prevTileEntity);
            }
            world.setBlockTileEntity(x, y, z, tileEntity);
        }
        
        // Place a moving piston entity for the head
        if (isExtending) {
            blockMeta = direction | (isSticky ? 8 : 0);
            PISTON_DEBUG("PistonHead "+Block.pistonMoving.blockID+"."+blockMeta+"("+headX+" "+headY+" "+headZ+")");
            world.setBlock(headX, headY, headZ, Block.pistonMoving.blockID, blockMeta, UPDATE_IMMEDIATE | UPDATE_SUPPRESS_DROPS | UPDATE_MOVE_BY_PISTON);
            world.setBlockTileEntity(headX, headY, headZ, PISTON_GET_TILE_ENTITY(Block.pistonExtension.blockID, blockMeta, 0, direction, true, true));
        }
        
        // START SHOVEL CODE
        for (int i = shovel_index_global; --i >= SHOVEL_EJECT_LIST_START_INDEX;) {
            // Set x,y,z to position of ejection destination block in shovel list
            packedPos = pushed_blocks[i];
            BLOCK_POS_UNPACK(packedPos, x, y, z);
            Block targetBlock = Block.blocksList[world.getBlockId(x, y, z)];
            
            blockState = data_list[i];
            PISTON_BLOCK_STATE_UNPACK(blockState, blockId, blockMeta, blockExtMeta);
            block = Block.blocksList[blockId];
            blockMeta =  block.adjustMetadataForPistonMove(blockMeta);
            int ejectDirection = (int)data_list[i + SHOVEL_DIRECTION_LIST_OFFSET];
            
            boolean breakTarget = false;
            if (
                BLOCK_IS_AIR(targetBlock) ||
                (breakTarget = targetBlock.getMobilityFlag() == PISTON_CAN_BREAK)
            ) {
                SHOVEL_DEBUG("Shovel path A");
                if (breakTarget) {
                    targetBlock.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
                }
                PISTON_SET_BLOCK(world, x, y, z, Block.pistonMoving.blockID, blockMeta, blockExtMeta, UPDATE_IMMEDIATE | UPDATE_SUPPRESS_DROPS | UPDATE_MOVE_BY_PISTON);
                world.setBlockTileEntity(x, y, z, PISTON_GET_SHOVEL_TILE_ENTITY(blockId, blockMeta, blockExtMeta, ejectDirection));
            }
            else if (
                !world.isRemote &&
                !BLOCK_IS_AIR(block)
            ) {
                SHOVEL_DEBUG("Shovel path B");
                int itemId = block.idDropped(blockMeta, world.rand, 0);
                if (itemId != 0) {
                    ItemUtils.ejectStackFromBlockTowardsFacing(
                        world,
                        // x,y,z contain the position of the ejection
                        // target and not the block being shoveled,
                        // so index backwards to spawn the item at
                        // the correct location
                        x - Facing.offsetsXForSide[ejectDirection],
                        y - Facing.offsetsYForSide[ejectDirection],
                        z - Facing.offsetsZForSide[ejectDirection],
                        new ItemStack(itemId, block.quantityDropped(world.rand), block.damageDropped(blockMeta)),
                        ejectDirection
                    );
                }
            }
            
            // For some reason this is required to keep
            // certain 0-tick shovels working. IDK why.
            world.notifyBlocksOfNeighborChange(
                x - Facing.offsetsXForSide[ejectDirection],
                y - Facing.offsetsYForSide[ejectDirection],
                z - Facing.offsetsZForSide[ejectDirection],
                BTWBlocks.pistonShovel.blockID
            );
        }
        // END SHOVEL CODE
        
        for (int i = destroy_index_global; --i >= DESTROY_LIST_START_INDEX;) {
            // Set x,y,z to position of block in destroy list
            packedPos = pushed_blocks[i];
            //world.notifyBlocksOfNeighborChange(BLOCK_POS_UNPACK_ARGS(packedPos), PISTON_BLOCK_STATE_EXTRACT_ID(data_list[i]));
            blockId = PISTON_BLOCK_STATE_EXTRACT_ID(data_list[i]);
            ((IWorldMixins)world).notifyBlockChangeAndComparators(BLOCK_POS_UNPACK_ARGS(packedPos), blockId, blockId);
        }
        
        for (int i = push_index_global; --i >= PUSH_LIST_START_INDEX;) {
            // Set x,y,z to position of block in push list
            packedPos = pushed_blocks[i];
            //world.notifyBlocksOfNeighborChange(BLOCK_POS_UNPACK_ARGS(packedPos), PISTON_BLOCK_STATE_EXTRACT_ID(data_list[i]));
            blockId = PISTON_BLOCK_STATE_EXTRACT_ID(data_list[i]);
            ((IWorldMixins)world).notifyBlockChangeAndComparators(BLOCK_POS_UNPACK_ARGS(packedPos), blockId, blockId);
        }
        
        // START SHOVEL CODE
        for (int i = shovel_index_global; --i >= SHOVEL_EJECT_LIST_START_INDEX;) {
            // Set x,y,z to position of block in shovel eject list
            packedPos = pushed_blocks[i];
            world.notifyBlocksOfNeighborChange(BLOCK_POS_UNPACK_ARGS(packedPos), PISTON_BLOCK_STATE_EXTRACT_ID(data_list[i]));
        }
        // END SHOVEL CODE
        
        if (isExtending) {
            world.notifyBlocksOfNeighborChange(headX, headY, headZ, Block.pistonExtension.blockID);
        }
        
        return true;
    }
    
    public boolean canExtend(World world, int x, int y, int z, int direction) {
        piston_position = BLOCK_POS_PACK(x, y, z);
        push_index_global = PUSH_LIST_START_INDEX;
        destroy_index_global = DESTROY_LIST_START_INDEX;
        shovel_index_global = SHOVEL_EJECT_LIST_START_INDEX;
        
        x += Facing.offsetsXForSide[direction];
        y += Facing.offsetsYForSide[direction];
        z += Facing.offsetsZForSide[direction];
        
        return this.resolve(world, x, y, z, direction, true);
    }
}