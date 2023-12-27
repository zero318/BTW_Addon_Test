package zero.test.mixin;

import net.minecraft.src.Block;
import net.minecraft.src.World;
import net.minecraft.src.BlockPistonBase;
import net.minecraft.src.*;

import btw.block.blocks.PistonBlockBase;
import btw.block.blocks.PistonBlockMoving;
import btw.item.util.ItemUtils;
import btw.AddonHandler;
import btw.BTWAddon;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

import zero.test.IBlockMixins;
import zero.test.mixin.IPistonBaseAccessMixins;
import zero.test.IWorldMixins;
import zero.test.IBlockEntityPistonMixins;

#include "..\func_aliases.h"
#include "..\feature_flags.h"
#include "..\util.h"

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

@Mixin(PistonBlockBase.class)
public abstract class PistonMixins MACRO_IF(MACRO_IS_1(ENABLE_MOVING_BLOCK_CHAINING), extends BlockPistonBase) {
    
    
    public boolean hasLargeCenterHardPointToFacing(IBlockAccess block_access, int X, int Y, int Z, int direction, boolean ignore_transparency) {
        int meta = block_access.getBlockMetadata(X, Y, Z);
        return !READ_META_FIELD(meta, EXTENDED) || OPPOSITE_DIRECTION(direction) == READ_META_FIELD(meta, DIRECTION);
    }
    
#if ENABLE_MOVING_BLOCK_CHAINING

    public PistonMixins(int block_id, boolean is_sticky) {
        super(block_id, is_sticky);
    }
    
    @Shadow
    protected abstract int getPistonShovelEjectionDirection(World world, int X, int Y, int Z, int direction);
    
    @Shadow
    protected abstract void onShovelEjectIntoBlock(World world, int X, int Y, int Z);

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
    private static final long[] pushed_blocks = new long[PUSH_LIST_LENGTH + DESTROY_LIST_LENGTH + SHOVEL_EJECT_LIST_LENGTH + SHOVEL_LIST_LENGTH];
    private static final int[] data_list = new int[PUSH_BLOCK_STATE_LIST_LENGTH + DESTROY_BLOCK_STATE_LIST_LENGTH + SHOVEL_BLOCK_STATE_LIST_LENGTH + SHOVEL_DIRECTION_LIST_LENGTH];

    // Position of the piston 
    private static long piston_position;
    
    // Becase screw java and by value semantics
    private static int push_index_global;
    private static int destroy_index_global;
    private static int shovel_index_global;
    
    // This is only called in situations when the
    // current block is known to not be air
    //
    // Returns false if the block chain is invalid,
    // which cancels all movement.
    // Returns true otherwise.
    protected boolean add_branch(World world, int X, int Y, int Z, int direction) {
        // This could be pulled from the static lists if an index was passed in
        int block_id = world.getBlockId(X, Y, Z);
        Block block = Block.blocksList[block_id];
        PISTON_DEBUG("Branching Block ("+X+" "+Y+" "+Z+")"+direction);
        
        int facing = 0;
        do {
            //PISTON_DEBUG("Facing/Direction ("+facing+"/"+direction+") ("+DIRECTION_AXIS(facing)+"/"+DIRECTION_AXIS(direction)+")");
            if (DIRECTION_AXIS(facing) != DIRECTION_AXIS(direction)) {
                //PISTON_DEBUG("Adding branch "+facing);
                if (((IBlockMixins)block).isStickyForBlocks(world, X, Y, Z, facing)) {
                    int nextX = X + Facing.offsetsXForSide[facing];
                    int nextY = Y + Facing.offsetsYForSide[facing];
                    int nextZ = Z + Facing.offsetsZForSide[facing];
                    Block neighbor_block = Block.blocksList[world.getBlockId(nextX, nextY, nextZ)];
                    if (
                        !BLOCK_IS_AIR(neighbor_block) &&
                        ((IBlockMixins)neighbor_block).canBeStuckTo(world, nextX, nextY, nextZ, facing, block_id) &&
                        !this.add_moved_block(world, nextX, nextY, nextZ, direction)
                    ) {
                        return false;
                    }
                }
            }
        } while (++facing < 6);
        return true;
    }

    // TODO: Make this less naive
    protected int gcd(int A, int B) {
        return B == 0 ? A : gcd(B, A % B);
    }
    
    // Returns false if the block chain is invalid,
    // which cancels all movement.
    // Returns true otherwise.
    protected boolean add_moved_block(World world, int X, int Y, int Z, int direction) {
        // IDK where vanilla checks for valid block positions, but the old
        // 1.5 logic did a Y check here so I copied it.
        if (!IS_VALID_BLOCK_Y_POS(Y)) {
            return false;
        }
        
        int block_id = world.getBlockId(X, Y, Z);
        Block block = Block.blocksList[block_id];
        long packed_pos;
        if (
            BLOCK_IS_AIR(block) ||
            (packed_pos = BLOCK_POS_PACK(X, Y, Z)) == piston_position ||
            (
                // Compatibility shim:
                // Since canBlockBePulledByPiston is used to prevent
                // destroyable blocks from getting pushed by adjacent
                // sticky blocks, blocks that can't be pulled need
                // to be detected separately. Vanilla solves this by
                // having a more complicated implementation of pushing
                // logic.
                ((IBlockMixins)block).getMobilityFlag(world, X, Y, Z) != PISTON_CAN_PUSH_ONLY &&
                !block.canBlockBePulledByPiston(world, X, Y, Z, direction)
            )
        ) {
            return true;
        }
        PISTON_DEBUG("SearchForExistingPush ("+X+" "+Y+" "+Z+")");
        
        
        // Copy the global push index into a local
        // to avoid mutating it until the movement
        // is confirmed
        int push_index = push_index_global;
        
        // This replaces toPush.contains
        for (int i = push_index; --i >= PUSH_LIST_START_INDEX;) {
            if (pushed_blocks[i] == packed_pos) {
                return true;
            }
        }
        // START SHOVEL CODE
        // Don't push blocks that're already
        // getting shoveled. This helps prevent
        // duping blocks when slime tries
        // to push a block in front of a shovel.
        for (int i = shovel_index_global + SHOVEL_LIST_OFFSET; --i >= SHOVEL_LIST_START_INDEX;) {
            if (pushed_blocks[i] == packed_pos) {
                return true;
            }
        }
        // END SHOVEL CODE
        
        int push_write_index = push_index;
        
        // Leave the starting position in X,Y,Z
        // Copy to next variables for mutation
        int nextX = X;
        int nextY = Y;
        int nextZ = Z;
        long next_packed_pos = packed_pos;
        int next_block_id = block_id;
        Block next_block = block;
        
        // This loop handles pulling blocks.
        loop {
            PISTON_DEBUG("StickySearch ("+nextX+" "+nextY+" "+nextZ+")");
            
            // Check the push limit
            if (push_write_index == (PUSH_LIST_START_INDEX + PUSH_LIST_LENGTH)) {
                PISTON_DEBUG("Push failed limit reached A");
                return false;
            }
            ++push_write_index;
            
            if (!((IBlockMixins)next_block).isStickyForBlocks(world, nextX, nextY, nextZ, direction)) {
                PISTON_DEBUG("IsSticky false");
                break;
            }
            PISTON_DEBUG("IsSticky true");
            // Index in opposite direction
            int tempX = nextX - Facing.offsetsXForSide[direction];
            int tempY = nextY - Facing.offsetsYForSide[direction];
            int tempZ = nextZ - Facing.offsetsZForSide[direction];
            long temp_packed_pos = BLOCK_POS_PACK(tempX, tempY, tempZ);
            if (temp_packed_pos == piston_position) {
                break;
            }
            block_id = world.getBlockId(tempX, tempY, tempZ);
            next_block = Block.blocksList[block_id];
            if (
                BLOCK_IS_AIR(next_block) ||
                !((IBlockMixins)next_block).canBeStuckTo(world, tempX, tempY, tempZ, direction, next_block_id) ||
                !next_block.canBlockBePulledByPiston(world, tempX, tempY, tempZ, direction)
            ) {
                break;
            }
            nextX = tempX;
            nextY = tempY;
            nextZ = tempZ;
            next_block_id = block_id;
            next_packed_pos = temp_packed_pos;
        }
        
        int push_count = push_write_index - push_index;
        // This loop adds the current block and any
        // pulled blocks to the push list.
        // Regardless of what the previous loop does,
        // nextX,nextY,nextZ will contain the position
        // of the furthest away block being moved,
        // next_packed_pos will contain that same position,
        // and next_block_id will contain the ID of the
        // block at that position.
        // This is potentially the starting block.
        do {
            // Index back towards the start
            PISTON_DEBUG("AddPush ("+nextX+" "+nextY+" "+nextZ+")");
            data_list[push_index] = BLOCK_STATE_PACK(next_block_id, world.getBlockMetadata(nextX, nextY, nextZ));
            pushed_blocks[push_index++] = next_packed_pos;
            nextX += Facing.offsetsXForSide[direction];
            nextY += Facing.offsetsYForSide[direction];
            nextZ += Facing.offsetsZForSide[direction];
            next_packed_pos = BLOCK_POS_PACK(nextX, nextY, nextZ);
            next_block_id = world.getBlockId(nextX, nextY, nextZ);
        } while (push_index != push_write_index);
        
        PISTON_DEBUG("PushCount "+push_count);
        
        // This loop handles pushing blocks.
        // X,Y,Z still contain the initial
        // movement position on entry, packed_pos
        // still contains the initial position,
        // block contains the block at that position,
        // nextX,nextY,nextZ contain the position
        // of the first block to check for pushing,
        // next_packed_pos contains that same position,
        // and next_block_id contains the id of the
        // first block to check for pushing.
        loop {
            push_index_global = push_index;
            PISTON_DEBUG("New push index "+push_index_global);
            PISTON_DEBUG("ParsePush ("+nextX+" "+nextY+" "+nextZ+")");
            
            // This loop must index forwards
            for (int i = PUSH_LIST_START_INDEX; i < push_index; ++i) {
                // Loop will never continue when this is true
                if (pushed_blocks[i] == next_packed_pos) {
                    // This branch handles two
                    // sticky blocks trying to push
                    // the same block at once
                    PISTON_DEBUG("Hacky swap code");
                    //PISTON_DEBUG("Swap stats "+"n3="+push_count+" n4="+i);
                    
                    //PISTON_DEBUG(""+" "+pushed_blocks[PUSH_LIST_START_INDEX]+" "+pushed_blocks[PUSH_LIST_START_INDEX+1]+" "+pushed_blocks[PUSH_LIST_START_INDEX+2]+" "+pushed_blocks[PUSH_LIST_START_INDEX+3]+" "+pushed_blocks[PUSH_LIST_START_INDEX+4]+" "+pushed_blocks[PUSH_LIST_START_INDEX+5]+" "+pushed_blocks[PUSH_LIST_START_INDEX+6]+" "+pushed_blocks[PUSH_LIST_START_INDEX+7]+" "+pushed_blocks[PUSH_LIST_START_INDEX+8]+" "+pushed_blocks[PUSH_LIST_START_INDEX+9]+" "+pushed_blocks[PUSH_LIST_START_INDEX+10]+" "+pushed_blocks[PUSH_LIST_START_INDEX+11]);
                    
                    // WARNING:
                    // This section requires
                    // the push list to be first
                    
                    // Hacky in place array right rotation
                    // Based on the first suitable thing
                    // I found on Google: https://www.codewhoop.com/array/rotation-in-place.html
                    
                    int swap_max = push_index - gcd(push_index -= i, push_count);
                    push_write_index = push_count - i;
                    
                    for (int j = i; j < swap_max; ++j) {
                        int k = j;
                        block_id = data_list[j];
                        packed_pos = pushed_blocks[j];
                        loop {
                            int d = i + (k + push_write_index) % push_index;
                            if (d == j) {
                                break;
                            }
                            data_list[k] = data_list[d];
                            pushed_blocks[k] = pushed_blocks[d];
                            k = d;
                        }
                        data_list[k] = block_id;
                        pushed_blocks[k] = packed_pos;
                    }
                    i += push_count;
                    
                    //PISTON_DEBUG(""+" "+pushed_blocks[PUSH_LIST_START_INDEX]+" "+pushed_blocks[PUSH_LIST_START_INDEX+1]+" "+pushed_blocks[PUSH_LIST_START_INDEX+2]+" "+pushed_blocks[PUSH_LIST_START_INDEX+3]+" "+pushed_blocks[PUSH_LIST_START_INDEX+4]+" "+pushed_blocks[PUSH_LIST_START_INDEX+5]+" "+pushed_blocks[PUSH_LIST_START_INDEX+6]+" "+pushed_blocks[PUSH_LIST_START_INDEX+7]+" "+pushed_blocks[PUSH_LIST_START_INDEX+8]+" "+pushed_blocks[PUSH_LIST_START_INDEX+9]+" "+pushed_blocks[PUSH_LIST_START_INDEX+10]+" "+pushed_blocks[PUSH_LIST_START_INDEX+11]);
                    
                    for (int j = PUSH_LIST_START_INDEX; j < i; ++j) {
                        packed_pos = pushed_blocks[j];
                        BLOCK_POS_UNPACK(packed_pos, X, Y, Z);
                        
                        // Vanilla checks stickiness as part of this statement,
                        // but supporting directional stickiness required
                        // moving the check into add_branch
                        if (
                            // Air can't be added to the push list,
                            // so no need to check for that here
                            !this.add_branch(world, X, Y, Z, direction)
                        ) {
                            PISTON_DEBUG("Push failed move block branching ("+X+" "+Y+" "+Z+")");
                            return false;
                        }
                    }
                    return true;
                    // unreachable
                }
                // this is reachable, part of above for loop
                // when the if is false
            }
            next_block = Block.blocksList[next_block_id];
            if (BLOCK_IS_AIR(next_block)) {
                return true;
            }
            if (
                next_packed_pos == piston_position ||
                !next_block.canBlockBePushedByPiston(world, nextX, nextY, nextZ, direction)
            ) {
                PISTON_DEBUG("Push failed move block IDK ("+nextX+" "+nextY+" "+nextZ+")");
                return false;
            }
            int next_block_meta = world.getBlockMetadata(nextX, nextY, nextZ);
            if (next_block.getMobilityFlag() == PISTON_CAN_BREAK) {
                data_list[destroy_index_global] = BLOCK_STATE_PACK(next_block_id, next_block_meta);
                pushed_blocks[destroy_index_global++] = next_packed_pos;
                return true;
            }
            // START SHOVEL CODE
            if (next_block.canBePistonShoveled(world, nextX, nextY, nextZ)) {
                // If nextX,nextY,nextZ are a block that can be shoveled,
                // then X,Y,Z/block will contain the shovel itself
                int eject_direction = block.getPistonShovelEjectDirection(world, X, Y, Z, direction);
                if (eject_direction >= 0) {
                    // Set X,Y,Z to the position of the ejection destination
                    X = nextX + Facing.offsetsXForSide[eject_direction];
                    Y = nextY + Facing.offsetsYForSide[eject_direction];
                    Z = nextZ + Facing.offsetsZForSide[eject_direction];
                    int eject_destination_block_id = world.getBlockId(X, Y, Z);
                    block = Block.blocksList[eject_destination_block_id];
                    if (
                        BLOCK_IS_AIR(block) ||
                        eject_destination_block_id == Block.pistonMoving.blockID ||
                        block.getMobilityFlag() == PISTON_CAN_BREAK
                    ) {
                        packed_pos = BLOCK_POS_PACK(X, Y, Z);
                        goto_block(block_is_shoveled) {
                            // Make sure the ejection block isn't already being used
                            for (int i = shovel_index_global; --i >= SHOVEL_EJECT_LIST_START_INDEX;) {
                                if (pushed_blocks[i] == packed_pos) {
                                    goto(block_is_shoveled);
                                }
                            }
                            data_list[shovel_index_global] = BLOCK_STATE_PACK(next_block_id, next_block_meta);
                            data_list[shovel_index_global + SHOVEL_DIRECTION_LIST_OFFSET] = eject_direction;
                            
                            pushed_blocks[shovel_index_global] = packed_pos;
                            pushed_blocks[shovel_index_global + SHOVEL_LIST_OFFSET] = next_packed_pos;
                            ++shovel_index_global;
                            return true;
                        } goto_target(block_is_shoveled);
                    }
                }
            }
            // END SHOVEL CODE
            
            // Check the push limit
            if (push_index == (PUSH_LIST_START_INDEX + PUSH_LIST_LENGTH)) {
                PISTON_DEBUG("Push failed limit reached B");
                return false;
            }
            data_list[push_index] = BLOCK_STATE_PACK(next_block_id, next_block_meta);
            pushed_blocks[push_index++] = next_packed_pos;
            ++push_count;
            // Set X,Y,Z to the position of the currently
            // pushed block, since it's now the previous
            X = nextX;
            Y = nextY;
            Z = nextZ;
            packed_pos = next_packed_pos;
            block = next_block;
            // Index nextX,nextY,nextZ to the position of
            // the next potential pushed block
            nextX += Facing.offsetsXForSide[direction];
            nextY += Facing.offsetsYForSide[direction];
            nextZ += Facing.offsetsZForSide[direction];
            next_packed_pos = BLOCK_POS_PACK(nextX, nextY, nextZ);
            next_block_id = world.getBlockId(nextX, nextY, nextZ);
        }
        // unreachable
    }
    
    protected boolean resolve(World world, int X, int Y, int Z, int direction, boolean is_extending) {
        
        int block_id = world.getBlockId(X, Y, Z);
        Block block = Block.blocksList[block_id];
        if (BLOCK_IS_AIR(block)) {
            return true;
        }
        long packed_pos = BLOCK_POS_PACK(X,Y,Z);
        if (
            ((IBlockMixins)block).getMobilityFlag(world, X, Y, Z) != PISTON_CAN_PUSH_ONLY &&
            !block.canBlockBePulledByPiston(world, X, Y, Z, direction)
        ) {
            if (
                is_extending &&
                block.getMobilityFlag() == PISTON_CAN_BREAK
            ) {
                // Add destroy
                PISTON_DEBUG("Resolve destroy ("+X+" "+Y+" "+Z+")");
                data_list[destroy_index_global] = BLOCK_STATE_PACK(block_id, world.getBlockMetadata(X, Y, Z));
                pushed_blocks[destroy_index_global++] = packed_pos;
                return true;
            }
            PISTON_DEBUG("Push failed immobile "+block_id+"("+X+" "+Y+" "+Z+")");
            return false;
        }
        PISTON_DEBUG("Resolve add blocks ("+X+" "+Y+" "+Z+")");
        if (!this.add_moved_block(world, X, Y, Z, direction)) {
            PISTON_DEBUG("Push failed move block");
            return false;
        }
        
        // This loop uses the global index to end iteration,
        // which can be mutated by add_branch. This is an
        // intentional side effect.
        for (int i = PUSH_LIST_START_INDEX; i < push_index_global; ++i) {
            // Set X,Y,Z to the position of the next block in push list
            packed_pos = pushed_blocks[i];
            
            // Air will never be added to the push list,
            // so no checks are done for that.
            if (
                // Vanilla checks stickiness as part of this statement,
                // but supporting directional stickiness required
                // moving the check into add_branch
                !this.add_branch(world, BLOCK_POS_UNPACK_ARGS(packed_pos), direction)
            ) {
                PISTON_DEBUG("Push failed branching");
                return false;
            }
        }
        return true;
    }
    
    public boolean moveBlocks(World world, int X, int Y, int Z, int direction, boolean is_extending) {
        
        // Set initial global values
        piston_position = BLOCK_POS_PACK(X,Y,Z);
        push_index_global = PUSH_LIST_START_INDEX;
        destroy_index_global = DESTROY_LIST_START_INDEX;
        shovel_index_global = SHOVEL_EJECT_LIST_START_INDEX;
        
        X += Facing.offsetsXForSide[direction];
        Y += Facing.offsetsYForSide[direction];
        Z += Facing.offsetsZForSide[direction];
        
        // Save the position of the piston head
        int headX = X;
        int headY = Y;
        int headZ = Z;
        
        if (!is_extending) {
            if (world.getBlockId(X, Y, Z) == Block.pistonExtension.blockID) {
                world.setBlock(X, Y, Z, 0, 0, UPDATE_SUPPRESS_DROPS);
            }
            X += Facing.offsetsXForSide[direction];
            Y += Facing.offsetsYForSide[direction];
            Z += Facing.offsetsZForSide[direction];
            direction = OPPOSITE_DIRECTION(direction);
        }
        if (!this.resolve(world, X, Y, Z, direction, is_extending)) {
            return false;
        }
        
        long packed_pos;
        int block_id;
        int block_meta;
        int block_state;
        Block block;
        
        int i = destroy_index_global;
        PISTON_DEBUG("DestroyIndex "+i);
        while (--i >= DESTROY_LIST_START_INDEX) {
            // Set X,Y,Z to position of block in destroy list
            packed_pos = pushed_blocks[i];
            BLOCK_POS_UNPACK(packed_pos, X, Y, Z);
            
            // Get data about destroyed block
            block_state = data_list[i];
            BLOCK_STATE_UNPACK(block_state, block_id, block_meta);
            block = Block.blocksList[block_id];
            block_meta = block.adjustMetadataForPistonMove(block_meta);
            
            PISTON_DEBUG("Destroy "+block_id+"."+block_meta+"("+X+" "+Y+" "+Z+")");
            
            block.onBrokenByPistonPush(world, X, Y, Z, block_meta);
            world.setBlock(X, Y, Z, 0, 0, UPDATE_IMMEDIATE | UPDATE_KNOWN_SHAPE);
        }
        
        i = push_index_global;
        PISTON_DEBUG("PushIndex "+i);
        
        while (--i >= PUSH_LIST_START_INDEX) {
            // Set X,Y,Z to position of block in push list
            packed_pos = pushed_blocks[i];
            BLOCK_POS_UNPACK(packed_pos, X, Y, Z);
            
            // Get data about pushed block
            block_state = data_list[i];
            BLOCK_STATE_UNPACK(block_state, block_id, block_meta);
            block = Block.blocksList[block_id];
            block_meta = block.adjustMetadataForPistonMove(block_meta);
            
            PISTON_DEBUG("Push "+block_id+"."+block_meta+"("+X+" "+Y+" "+Z+")");
            
            NBTTagCompound tile_entity_data = getBlockTileEntityData(world, X, Y, Z);
            world.removeBlockTileEntity(X, Y, Z);
            
            packed_pos = BLOCK_POS_PACK(X + Facing.offsetsXForSide[direction], Y + Facing.offsetsYForSide[direction], Z + Facing.offsetsZForSide[direction]);
            
            goto_block(coord_will_move) {
                //for (int j = 0; j < i; ++j) {
                for (int j = i; --j > PUSH_LIST_START_INDEX;) {
                    if (pushed_blocks[j] == packed_pos) {
                        goto(coord_will_move);
                    }
                }
                // This is to make sure that blocks being pulled leave air
                // behind when another block isn't moving into that space.
                // This replaces the hashmap from vanilla
                world.setBlock(X, Y, Z, 0, 0, UPDATE_CLIENTS | UPDATE_SUPPRESS_LIGHT);
            } goto_target(coord_will_move);
            
            // Offset X,Y,Z to the destination position
            X += Facing.offsetsXForSide[direction];
            Y += Facing.offsetsYForSide[direction];
            Z += Facing.offsetsZForSide[direction];
            world.setBlock(X, Y, Z, Block.pistonMoving.blockID, block_meta, UPDATE_IMMEDIATE | UPDATE_SUPPRESS_DROPS | UPDATE_MOVE_BY_PISTON);
            world.setBlockTileEntity(X, Y, Z, BlockPistonMoving.getTileEntity(block_id, block_meta, direction, true, false));
            if (tile_entity_data != null) {
                ((TileEntityPiston)world.getBlockTileEntity(X, Y, Z)).storeTileEntity(tile_entity_data);
            }
        }
        
        // Place a moving piston entity for the head
        if (is_extending) {
            block_meta = direction | (this.isSticky ? 8 : 0);
            PISTON_DEBUG("PistonHead "+Block.pistonMoving.blockID+"."+block_meta+"("+headX+" "+headY+" "+headZ+")");
            world.setBlock(headX, headY, headZ, Block.pistonMoving.blockID, block_meta, UPDATE_IMMEDIATE | UPDATE_SUPPRESS_DROPS | UPDATE_MOVE_BY_PISTON);
			world.setBlockTileEntity(headX, headY, headZ, BlockPistonMoving.getTileEntity(Block.pistonExtension.blockID, block_meta, direction, true, false));
        }
        
        // START SHOVEL CODE
        i = shovel_index_global;
        while (--i >= SHOVEL_EJECT_LIST_START_INDEX) {
            // Set X,Y,Z to position of ejection destination block in shovel list
            packed_pos = pushed_blocks[i];
            BLOCK_POS_UNPACK(packed_pos, X, Y, Z);
            
            block_state = data_list[i];
            BLOCK_STATE_UNPACK(block_state, block_id, block_meta);
            block = Block.blocksList[block_id];
            block_meta =  block.adjustMetadataForPistonMove(block_meta);
            int eject_direction = data_list[i + SHOVEL_DIRECTION_LIST_OFFSET];
            
            if (
                BLOCK_IS_AIR(block) ||
                block.getMobilityFlag() == PISTON_CAN_BREAK
            ) {
                onShovelEjectIntoBlock(world, X, Y, Z);
                world.setBlock(X, Y, Z, Block.pistonMoving.blockID, block_meta, UPDATE_INVISIBLE);
                world.setBlockTileEntity(X, Y, Z, PistonBlockMoving.getShoveledTileEntity(block_id, block_meta, eject_direction));
            } else if (!world.isRemote) {
                block = Block.blocksList[block_id]; // Get shoveled block
                if (!BLOCK_IS_AIR(block)) {
                    // BUG: Some blocks don't seem to be returning the correct
                    // items when called like this, particularly packed earth.
                    int item_id = block.idDropped(block_meta, world.rand, 0);
                    if (item_id != 0) {
                        ItemUtils.ejectStackFromBlockTowardsFacing(
                            world,
                            // X,Y,Z contain the position of the ejection
                            // target and not the block being shoveled,
                            // so index backwards to spawn the item at
                            // the correct location
                            X - Facing.offsetsXForSide[eject_direction],
                            Y - Facing.offsetsYForSide[eject_direction],
                            Z - Facing.offsetsZForSide[eject_direction],
                            new ItemStack(item_id, block.quantityDropped(world.rand), block.damageDropped(block_meta)),
                            eject_direction
                        );
                    }
                }
            }
        }
        // END SHOVEL CODE
        
        i = destroy_index_global;
        while (--i >= DESTROY_LIST_START_INDEX) {
            // Set X,Y,Z to position of block in destroy list
            packed_pos = pushed_blocks[i];
            world.notifyBlocksOfNeighborChange(BLOCK_POS_UNPACK_ARGS(packed_pos), data_list[i]);
        }
        i = push_index_global;
        while (--i >= PUSH_LIST_START_INDEX) {
            // Set X,Y,Z to position of block in push list
            packed_pos = pushed_blocks[i];
            world.notifyBlocksOfNeighborChange(BLOCK_POS_UNPACK_ARGS(packed_pos), data_list[i] & 0xFFFF);
        }
        if (is_extending) {
            world.notifyBlocksOfNeighborChange(headX, headY, headZ, Block.pistonExtension.blockID);
        }
        return true;
    }
    
    @Overwrite
    public boolean tryExtend(World world, int X, int Y, int Z, int direction) {
        return this.moveBlocks(world, X, Y, Z, direction, true);
    }
    
    @Overwrite
    public boolean canExtend(World world, int X, int Y, int Z, int direction) {
        piston_position = BLOCK_POS_PACK(X,Y,Z);
        push_index_global = PUSH_LIST_START_INDEX;
        destroy_index_global = DESTROY_LIST_START_INDEX;
        shovel_index_global = SHOVEL_EJECT_LIST_START_INDEX;
        
        X += Facing.offsetsXForSide[direction];
        Y += Facing.offsetsYForSide[direction];
        Z += Facing.offsetsZForSide[direction];
        
        return this.resolve(world, X, Y, Z, direction, true);
    }
    
    private static final int PISTON_EVENT_EXTENDING = 0;
    private static final int PISTON_EVENT_RETRACTING_NORMAL = 1;
    private static final int PISTON_EVENT_RETRACTING_ZERO_TICK = 2;
    
    //@Override
    protected void updatePistonState(World world, int X, int Y, int Z) {
        if (!world.isRemote) {
            int meta = world.getBlockMetadata(X, Y, Z);
            int direction = READ_META_FIELD(meta, DIRECTION);
            
            // Why does the 1.5 code check for 7?
            if (direction != 7) {
                boolean is_powered = ((IPistonBaseAccessMixins)(Object)this).callIsIndirectlyPowered(world, X, Y, Z, direction);
                if (is_powered != READ_META_FIELD(meta, EXTENDED)) {
                    if (is_powered) {
                        // Yes, it does matter. Without this, the standard
                        // redstone block engine doesn't work.
                        // TODO: Why?
                        if (this.canExtend(world, X, Y, Z, direction)) {
                            world.addBlockEvent(X, Y, Z, this.blockID, PISTON_EVENT_EXTENDING, direction);
                        }
                    } else {
                        int nextX = Facing.offsetsXForSide[direction];
                        int nextY = Facing.offsetsYForSide[direction];
                        int nextZ = Facing.offsetsZForSide[direction];
                        nextX += nextX + X;
                        nextY += nextY + Y;
                        nextZ += nextZ + Z;
                        int next_block_id = world.getBlockId(nextX, nextY, nextZ);
                        TileEntity tile_entity;
                        world.addBlockEvent(
                            X, Y, Z, this.blockID,
                            (
                                next_block_id == Block.pistonMoving.blockID &&
                                (tile_entity = world.getBlockTileEntity(nextX, nextY, nextZ)) instanceof TileEntityPiston &&
                                ((TileEntityPiston)tile_entity).getPistonOrientation() == direction &&
                                ((TileEntityPiston)tile_entity).isExtending() &&
                                (
                                    ((TileEntityPiston)tile_entity).getProgress(0.0f) < 0.5f ||
                                    world.getTotalWorldTime() == ((IBlockEntityPistonMixins)tile_entity).getLastTicked()
                                    // something about "handling tick"?
                                )
                            ) ? PISTON_EVENT_RETRACTING_ZERO_TICK : PISTON_EVENT_RETRACTING_NORMAL,
                            direction
                        );
                    }
                }
            }
        }
    }
    
    //@Override
    public boolean onBlockEventReceived(World world, int X, int Y, int Z, int event_type, int direction) {
        if (!world.isRemote) {
            boolean is_powered = ((IPistonBaseAccessMixins)(Object)this).callIsIndirectlyPowered(world, X, Y, Z, direction);
            if (
                is_powered &&
                (
                    event_type == PISTON_EVENT_RETRACTING_NORMAL ||
                    event_type == PISTON_EVENT_RETRACTING_ZERO_TICK
                )
            ) {
                world.setBlockMetadataWithNotify(X, Y, Z, MERGE_META_FIELD(direction, EXTENDED, true), UPDATE_CLIENTS);
                return false;
            }
            if (!is_powered && event_type == PISTON_EVENT_EXTENDING) {
                return false;
            }
        }
        PISTON_DEBUG("===== PistonCoords ("+X+" "+Y+" "+Z+")");
        switch (event_type) {
            default:
                return true;
            case PISTON_EVENT_EXTENDING: // Extend
                PISTON_DEBUG("Case PISTON_EVENT_EXTENDING");
                if (!this.moveBlocks(world, X, Y, Z, direction, true)) {
                    return false;
                }
                world.setBlockMetadataWithNotify(X, Y, Z, MERGE_META_FIELD(direction, EXTENDED, true), UPDATE_NEIGHBORS | UPDATE_CLIENTS | UPDATE_SUPPRESS_DROPS | UPDATE_MOVE_BY_PISTON);
                world.playSoundEffect((double)X + 0.5D, (double)Y + 0.5D, (double)Z + 0.5D, "tile.piston.out", 0.5F, world.rand.nextFloat() * 0.25F + 0.6F);
                return true;
            case PISTON_EVENT_RETRACTING_NORMAL: case PISTON_EVENT_RETRACTING_ZERO_TICK: // Retract
                PISTON_DEBUG("Case PISTON_EVENT_RETRACTING "+event_type);
                int nextX = X + Facing.offsetsXForSide[direction];
                int nextY = Y + Facing.offsetsYForSide[direction];
                int nextZ = Z + Facing.offsetsZForSide[direction];
                TileEntity tile_entity = world.getBlockTileEntity(nextX, nextY, nextZ);
                if (tile_entity instanceof TileEntityPiston) {
                    ((TileEntityPiston)tile_entity).clearPistonTileEntity();
                }
                world.setBlock(X, Y, Z, Block.pistonMoving.blockID, direction, UPDATE_SUPPRESS_DROPS);
                world.setBlockTileEntity(X, Y, Z, BlockPistonMoving.getTileEntity(this.blockID, direction, direction, false, true));
                if (this.isSticky) {
                    int currentX = nextX + Facing.offsetsXForSide[direction];
                    int currentY = nextY + Facing.offsetsYForSide[direction];
                    int currentZ = nextZ + Facing.offsetsZForSide[direction];
                    int next_block_id = world.getBlockId(currentX, currentY, currentZ);
                    goto_block(block_set_from_moving_piston) {
                        if (next_block_id == Block.pistonMoving.blockID) {
                            TileEntity next_tile_entity = world.getBlockTileEntity(currentX, currentY, currentZ);
                            if (next_tile_entity instanceof TileEntityPiston) {
                                TileEntityPiston piston_tile_entity = (TileEntityPiston)next_tile_entity;
                                if (
                                    piston_tile_entity.getPistonOrientation() == direction &&
                                    piston_tile_entity.isExtending()
                                ) {
                                    piston_tile_entity.clearPistonTileEntity();
                                    goto(block_set_from_moving_piston);
                                }
                            }
                        }
                        if (event_type == PISTON_EVENT_RETRACTING_NORMAL) {
                            Block next_block = Block.blocksList[next_block_id];
                            if (
                                !BLOCK_IS_AIR(next_block) &&
                                next_block.canBlockBePulledByPiston(world, currentX, currentY, currentZ, Block.getOppositeFacing(direction))
                                // What does this block do in modern vanilla?
                                /*&& (
                                    next_block_id == Block.pistonBase.blockID ||
                                    next_block_id == Block.pistonStickyBase.blockID ||
                                    block.getMobilityFlag() == PISTON_CAN_PUSH
                                )*/
                            ) {
                                this.moveBlocks(world, X, Y, Z, direction, false);
                            } else {
                                world.setBlockToAir(nextX, nextY, nextZ);
                            }
                        }
                    } goto_target(block_set_from_moving_piston);
                } else {
                    world.setBlockToAir(nextX, nextY, nextZ);
                }
                world.playSoundEffect((double)X + 0.5D, (double)Y + 0.5D, (double)Z + 0.5D, "tile.piston.in", 0.5F, world.rand.nextFloat() * 0.15F + 0.6F);
                return true;
        }
    }
#endif
}