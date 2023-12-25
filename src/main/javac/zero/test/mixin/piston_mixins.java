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

#define PISTON_PRINT_DEBUGGING 1

#if PISTON_PRINT_DEBUGGING
#define PISTON_DEBUG(...) AddonHandler.logMessage(__VA_ARGS__)
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
    
    @Overwrite
    public boolean canExtend(World world, int X, int Y, int Z, int direction) {
        int pushes_remaining = PISTON_PUSH_LIMIT;
        do {
            X += Facing.offsetsXForSide[direction];
            Y += Facing.offsetsYForSide[direction];
            Z += Facing.offsetsZForSide[direction];
            if (!IS_VALID_BLOCK_Y_POS(Y)) {
                return false;
            }
            Block next_block = Block.blocksList[world.getBlockId(X, Y, Z)];
            if (next_block == null) {
                return true;
            }
            if (!next_block.canBlockBePushedByPiston(world, X, Y, Z, direction)) {
                return false;
            }
            if (
                next_block.getMobilityFlag() == PISTON_CAN_BREAK ||
                getPistonShovelEjectionDirection(world, X, Y, Z, direction) >= 0
            ) {
                return true;
            }
        } while (--pushes_remaining >= 0);
        return false;
    }
    
    @Overwrite
    public boolean tryExtend(World world, int X, int Y, int Z, int direction) {
        int nextX = X;
        int nextY = Y;
        int nextZ = Z;
        
        int tempX;
        int tempY;
        int tempZ;
        
        
        goto_block(invalid_block) {
            int pushes_remaining = PISTON_PUSH_LIMIT;
            do {
                nextX += Facing.offsetsXForSide[direction];
                nextY += Facing.offsetsYForSide[direction];
                nextZ += Facing.offsetsZForSide[direction];
                if (!IS_VALID_BLOCK_Y_POS(nextY)) {
                    return false;
                }
                int next_block_id = world.getBlockId(nextX, nextY, nextZ);
                Block next_block = Block.blocksList[next_block_id];
                if (next_block == null) {
                    goto(invalid_block);
                }
                if (!next_block.canBlockBePushedByPiston(world, nextX, nextY, nextZ, direction)) {
                    return false;
                }
                if (next_block.getMobilityFlag() == PISTON_CAN_BREAK) {
                    next_block.onBrokenByPistonPush(world, nextX, nextY, nextZ, world.getBlockMetadata(nextX, nextY, nextZ));
                    break;
                }
                int shovel_direction = getPistonShovelEjectionDirection(world, nextX, nextY, nextZ, direction);
                if (shovel_direction >= 0) {
                    int shoveling_meta = next_block.adjustMetadataForPistonMove(world.getBlockMetadata(nextX, nextY, nextZ));
                    tempX = nextX + Facing.offsetsXForSide[shovel_direction];
                    tempY = nextY + Facing.offsetsYForSide[shovel_direction];
                    tempZ = nextZ + Facing.offsetsZForSide[shovel_direction];
                    
                    onShovelEjectIntoBlock(world, tempX, tempY, tempZ);
                    
                    world.setBlock(tempX, tempY, tempZ, Block.pistonMoving.blockID, shoveling_meta, UPDATE_INVISIBLE);
                    
                    world.setBlockTileEntity(tempX, tempY, tempZ, PistonBlockMoving.getShoveledTileEntity(next_block_id, shoveling_meta, shovel_direction));
                    break;
                }
            } while (--pushes_remaining >= 0);
            
            world.setBlockToAir(nextX, nextY, nextZ);
        } goto_target(invalid_block);
        
        int currentX = nextX;
        int currentY = nextY;
        int currentZ = nextZ;
        
        int[] block_list = new int[13];
        int block_index = 0;
        do {
            tempX = currentX - Facing.offsetsXForSide[direction];
            tempY = currentY - Facing.offsetsYForSide[direction];
            tempZ = currentZ - Facing.offsetsZForSide[direction];
            int moving_block_id = world.getBlockId(tempX, tempY, tempZ);
            NBTTagCompound block_entity_nbt = PistonBlockBase.getBlockTileEntityData(world, tempX, tempY, tempZ);
            world.removeBlockTileEntity(tempX, tempY, tempZ);
            if (
                moving_block_id == ((PistonBlockBase)(Object)this).blockID &&
                tempX == X && tempY == Y && tempZ == Z
            ) {
                world.setBlock(currentX, currentY, currentZ, Block.pistonMoving.blockID, direction | (this.isSticky ? 8 : 0), UPDATE_INVISIBLE);
                world.setBlockTileEntity(currentX, currentY, currentZ, BlockPistonMoving.getTileEntity(Block.pistonExtension.blockID, direction | (this.isSticky ? 8 : 0), direction, true, false));
            } else {
                int moving_meta = world.getBlockMetadata(tempX, tempY, tempZ);
                Block moving_block = Block.blocksList[moving_block_id];
                if (moving_block != null) {
                    moving_meta = moving_block.adjustMetadataForPistonMove(moving_meta);
                }
                world.setBlock(currentX, currentY, currentZ, Block.pistonMoving.blockID, moving_meta, UPDATE_INVISIBLE);
                world.setBlockTileEntity(currentX, currentY, currentZ, BlockPistonMoving.getTileEntity(moving_block_id, moving_meta, direction, true, false));
                if (block_entity_nbt != null) {
                    ((TileEntityPiston)world.getBlockTileEntity(currentX, currentY, currentZ)).storeTileEntity(block_entity_nbt);
                }
            }
            block_list[block_index++] = moving_block_id;
            
            currentX = tempX;
            currentY = tempY;
            currentZ = tempZ;
        } while (currentX != X || currentY != Y || currentZ != Z);
        
        block_index = 0;
        do {
            nextX -= Facing.offsetsXForSide[direction];
            nextY -= Facing.offsetsYForSide[direction];
            nextZ -= Facing.offsetsZForSide[direction];
            world.notifyBlocksOfNeighborChange(nextX, nextY, nextZ, block_list[block_index++]);
        } while (nextX != X || nextY != Y || nextZ != Z);
        return true;
    }

#pragma push_macro("PISTON_PUSH_LIMIT")
#undef PISTON_PUSH_LIMIT
    private static final int PISTON_PUSH_LIMIT =
#pragma pop_macro("PISTON_PUSH_LIMIT")
    PISTON_PUSH_LIMIT;
#undef PISTON_PUSH_LIMIT

    /*
        Shovel list:
            Contains XYZ coordinates being ejected into by shovels

        Push list:
            Contains XYZ coordinates of blocks being moved

        Destroy list:
            Contains XYZ coordinates of blocks being destroyed
        
        Shovel ID list:
            Contains the block IDs of blocks being shoveled.
            Indices correspond to the shovel list.
                        
        Push block ID list:
            Contains the block IDs of blocks being moved.
            Indices correspond to the push list.
            Exists so that neighbor updates can still
            recieve correct IDs after moving pistons
            have been created.
                            
        Destroy block ID list:
            Contains the block IDs of blocks being destroyed.
            Indices correspond to the destroy list.
            Exists so that neighbor updates can still
            recieve correct IDs after moving pistons
            have been created.
            
        Shovel direction list:
            Contains the ejection directions of blocks being shoveled.
            Indices correspond to the shovel list.
            
        Shovel meta list:
            Contains the metadata values of blocks being shoveled.
            Indices correspond to the shovel list.
    
    */
    
    private static final int PUSH_LIST_LENGTH = PISTON_PUSH_LIMIT;
    private static final int SHOVEL_LIST_LENGTH = PUSH_LIST_LENGTH;
    private static final int DESTROY_LIST_LENGTH = PUSH_LIST_LENGTH;

    private static final int SHOVEL_LIST_START_INDEX = 0;
    private static final int PUSH_LIST_START_INDEX = SHOVEL_LIST_START_INDEX + SHOVEL_LIST_LENGTH;
    private static final int DESTROY_LIST_START_INDEX = PUSH_LIST_START_INDEX + PUSH_LIST_LENGTH;
    
    private static final int SHOVEL_BLOCK_ID_LIST_LENGTH = SHOVEL_LIST_LENGTH;
    private static final int PUSH_BLOCK_ID_LIST_LENGTH = PUSH_LIST_LENGTH;
    private static final int DESTROY_BLOCK_ID_LIST_LENGTH = DESTROY_LIST_LENGTH;
    private static final int SHOVEL_DIRECTION_LIST_LENGTH = SHOVEL_LIST_LENGTH;
    private static final int SHOVEL_BLOCK_META_LIST_LENGTH = SHOVEL_LIST_LENGTH;
    
    private static final int SHOVEL_BLOCK_ID_LIST_START_INDEX = 0;
    private static final int PUSH_BLOCK_ID_LIST_START_INDEX = SHOVEL_BLOCK_ID_LIST_START_INDEX + SHOVEL_BLOCK_ID_LIST_LENGTH;
    private static final int DESTROY_BLOCK_ID_LIST_START_INDEX = PUSH_BLOCK_ID_LIST_START_INDEX + PUSH_BLOCK_ID_LIST_LENGTH;
    private static final int SHOVEL_DIRECTION_LIST_START_INDEX = DESTROY_BLOCK_ID_LIST_START_INDEX + DESTROY_BLOCK_ID_LIST_LENGTH;
    private static final int SHOVEL_BLOCK_META_LIST_START_INDEX = SHOVEL_DIRECTION_LIST_START_INDEX + SHOVEL_DIRECTION_LIST_LENGTH;

    
    // The destroy list can't be longer than the push list, right?
    // TODO: TEST THIS ASSUMPTION
    private static long[] pushed_blocks = new long[SHOVEL_LIST_LENGTH + PUSH_LIST_LENGTH + DESTROY_LIST_LENGTH];
    private static int[] data_list = new int[SHOVEL_BLOCK_ID_LIST_LENGTH + PUSH_BLOCK_ID_LIST_LENGTH + DESTROY_BLOCK_ID_LIST_LENGTH + SHOVEL_DIRECTION_LIST_LENGTH + SHOVEL_BLOCK_META_LIST_LENGTH];

    // Position of the piston 
    private static long piston_position;
    
    // Becase screw java and by value semantics
    private static int push_index_global;
    private static int shovel_index_global;
    private static int destroy_index_global;
    
    // This is only called in situations when the
    // current block is known to not be air
    //
    // Returns false if the block chain is invalid,
    // which cancels all movement.
    // Returns true otherwise.
    protected boolean add_branch(World world, int X, int Y, int Z, int direction) {
        int block_id = world.getBlockId(X, Y, Z);
        Block block = Block.blocksList[block_id];
        PISTON_DEBUG("Branching Block ("+X+" "+Y+" "+Z+")"+direction);
        
        for (int facing = 0; facing < 6; ++facing) {
            PISTON_DEBUG("Facing/Direction ("+facing+"/"+direction+") ("+DIRECTION_AXIS(facing)+"/"+DIRECTION_AXIS(direction)+")");
            if (DIRECTION_AXIS(facing) != DIRECTION_AXIS(direction)) {
                PISTON_DEBUG("Adding branch "+facing);
                if (((IBlockMixins)block).isStickyForBlocks(world, X, Y, Z, facing)) {
                    int nextX = X + Facing.offsetsXForSide[facing];
                    int nextY = Y + Facing.offsetsYForSide[facing];
                    int nextZ = Z + Facing.offsetsZForSide[facing];
                    Block neighbor_block = Block.blocksList[world.getBlockId(nextX, nextY, nextZ)];
                    if (
                        !BLOCK_IS_AIR(neighbor_block) &&
                        ((IBlockMixins)neighbor_block).canStickTo(world, nextX, nextY, nextZ, facing, block_id) &&
                        !this.add_moved_block(world, nextX, nextY, nextZ, direction)
                    ) {
                        return false;
                    }
                }
            }
        }
        return true;
    }


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
                // logic, but 
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
        for (int i = PUSH_LIST_START_INDEX; i < push_index; ++i) {
            if (pushed_blocks[i] == packed_pos) {
                return true;
            }
        }
        
        int push_write_index = push_index;
        
        // Leave the starting position in X,Y,Z
        // Copy to next variables for mutation
        int nextX = X;
        int nextY = Y;
        int nextZ = Z;
        int next_block_id = block_id;
        Block next_block = block;
        
        // This loop handles pulling blocks.
        loop {
            PISTON_DEBUG("StickySearch ("+nextX+" "+nextY+" "+nextZ+")");
            
            // Check the push limit
            if (++push_write_index == (PUSH_LIST_START_INDEX + PUSH_LIST_LENGTH)) {
                PISTON_DEBUG("Push failed limit reached A");
                return false;
            }
            
            if (!((IBlockMixins)next_block).isStickyForBlocks(world, nextX, nextY, nextZ, direction)) {
                PISTON_DEBUG("IsSticky false");
                break;
            }
            PISTON_DEBUG("IsSticky true");
            // Index in opposite direction
            int tempX = nextX - Facing.offsetsXForSide[direction];
            int tempY = nextY - Facing.offsetsYForSide[direction];
            int tempZ = nextZ - Facing.offsetsZForSide[direction];
            if (BLOCK_POS_PACK(tempX, tempY, tempZ) == piston_position) {
                break;
            }
            block_id = world.getBlockId(tempX, tempY, tempZ);
            next_block = Block.blocksList[block_id];
            if (
                BLOCK_IS_AIR(next_block) ||
                !((IBlockMixins)next_block).canStickTo(world, tempX, tempY, tempZ, direction, next_block_id) ||
                !next_block.canBlockBePulledByPiston(world, tempX, tempY, tempZ, direction)
            ) {
                break;
            }
            nextX = tempX;
            nextY = tempY;
            nextZ = tempZ;
            next_block_id = block_id;
        }
        
        int push_count = push_write_index - push_index;
        // This loop adds the current block and any
        // pulled blocks to the push list.
        // Regardless of what the previous loop does,
        // nextX,nextY,nextZ will contain the position
        // of the furthest away block being moved, which
        // is potentially the starting block.
        do {
            // Index back towards the start
            PISTON_DEBUG("AddPush ("+nextX+" "+nextY+" "+nextZ+")");
            data_list[push_index] = next_block_id;
            pushed_blocks[push_index++] = BLOCK_POS_PACK(nextX, nextY, nextZ);
            nextX += Facing.offsetsXForSide[direction];
            nextY += Facing.offsetsYForSide[direction];
            nextZ += Facing.offsetsZForSide[direction];
            next_block_id = world.getBlockId(nextX, nextY, nextZ);
        } while (push_index != push_write_index);
        
        // This loop handles pushing blocks.
        // X,Y,Z still contain the initial
        // movement position on entry, block
        // contains the block at that position,
        // nextX,nextY,nextZ contain the position
        // of the first block to check for pushing,
        // and next_block_id contains the id of the
        // first block to check for pushing.
        loop {
            push_index_global = push_index;
            PISTON_DEBUG("New push index "+push_index_global);
            PISTON_DEBUG("ParsePush ("+nextX+" "+nextY+" "+nextZ+")");
            packed_pos = BLOCK_POS_PACK(nextX, nextY, nextZ);
            
            for (int i = PUSH_LIST_START_INDEX; i < push_index; ++i) {
                // Loop will never continue when this is true
                if (pushed_blocks[i] == packed_pos) {
                    // Does this even run? It looks like dead code...
                    // CRAP IT'S NOT DEAD CODE
                    // AND IT'S BROKEN
                    PISTON_DEBUG("Hacky swap code");
                    
                    // Hacky in place array rotation
                    // Based on the first suitable thing
                    // I found on Google: https://www.codewhoop.com/array/rotation-in-place.html
                    int swap_max = gcd(push_index - PUSH_LIST_START_INDEX, push_count) + i;
                    for (int j = i; j < swap_max; ++j) {
                        int k = j;
                        block_id = data_list[j];
                        packed_pos = pushed_blocks[j];
                        loop {
                            int d = (k + push_count) % push_index;
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
                    
                    for (int j = PUSH_LIST_START_INDEX; j < i + push_count; ++j) {
                        packed_pos = pushed_blocks[j];
                        BLOCK_POS_UNPACK(packed_pos, X, Y, Z);
                        
                        block_id = data_list[j];
                        block = Block.blocksList[block_id];
                        // Vanilla checks stickiness as part of this statement,
                        // but supporting directional stickiness required
                        // moving the check into add_branch
                        if (
                            !BLOCK_IS_AIR(block) &&
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
                packed_pos == piston_position ||
                !next_block.canBlockBePushedByPiston(world, nextX, nextY, nextZ, direction)
            ) {
                PISTON_DEBUG("Push failed move block IDK ("+nextX+" "+nextY+" "+nextZ+")");
                return false;
            }
            if (next_block.getMobilityFlag() == PISTON_CAN_BREAK) {
                data_list[destroy_index_global] = next_block_id;
                pushed_blocks[destroy_index_global++] = packed_pos;
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
                            int i = shovel_index_global;
                            while (--i >= SHOVEL_LIST_START_INDEX) {
                                if (pushed_blocks[i] == packed_pos) {
                                    goto(block_is_shoveled);
                                }
                            }
                            data_list[shovel_index_global + SHOVEL_BLOCK_ID_LIST_START_INDEX] = next_block_id;
                            data_list[shovel_index_global + SHOVEL_DIRECTION_LIST_START_INDEX] = eject_direction;
                            data_list[shovel_index_global + SHOVEL_BLOCK_META_LIST_START_INDEX] = next_block.adjustMetadataForPistonMove(world.getBlockMetadata(nextX, nextY, nextZ));
                            
                            // This prevents duping shoveled blocks.
                            // TODO: Should this be moved into the lists somehow to
                            // prevent update order weirdness?
                            world.setBlock(nextX, nextY, nextZ, 0, 0, UPDATE_INVISIBLE);
                            
                            pushed_blocks[shovel_index_global++] = packed_pos;
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
            data_list[push_index] = next_block_id;
            pushed_blocks[push_index++] = packed_pos;
            ++push_count;
            // Set X,Y,Z to the position of the currently
            // pushed block, since it's now the previous
            X = nextX;
            Y = nextY;
            Z = nextZ;
            // Index nextX,nextY,nextZ to the position of
            // the next potential pushed block
            nextX += Facing.offsetsXForSide[direction];
            nextY += Facing.offsetsYForSide[direction];
            nextZ += Facing.offsetsZForSide[direction];
            block = next_block;
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
                data_list[destroy_index_global] = block_id;
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
            BLOCK_POS_UNPACK(packed_pos, X, Y, Z);
            
            block_id = data_list[i];
            block = Block.blocksList[block_id];
            if (
                //!BLOCK_IS_AIR(block) &&
                // Vanilla checks stickiness as part of this statement,
                // but supporting directional stickiness required
                // moving the check into add_branch
                !this.add_branch(world, X, Y, Z, direction)
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
        shovel_index_global = SHOVEL_LIST_START_INDEX;
        push_index_global = PUSH_LIST_START_INDEX;
        destroy_index_global = DESTROY_LIST_START_INDEX;
        
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
        Block block;
        
        int i = destroy_index_global;
        PISTON_DEBUG("DestroyIndex "+i);
        while (--i >= DESTROY_LIST_START_INDEX) {
            // Set X,Y,Z to position of block in destroy list
            packed_pos = pushed_blocks[i];
            BLOCK_POS_UNPACK(packed_pos, X, Y, Z);
            
            // Get data about destroyed block
            block_id = data_list[i];
            block = Block.blocksList[block_id];
            block_meta = world.getBlockMetadata(X, Y, Z);
            
            PISTON_DEBUG("Destroy "+block_id+"."+block_meta+"("+X+" "+Y+" "+Z+")");
            
            block.onBrokenByPistonPush(world, X, Y, Z, block_meta);
            //world.setBlockToAir(X, Y, Z);
            world.setBlock(X, Y, Z, 0, 0, UPDATE_IMMEDIATE | UPDATE_KNOWN_SHAPE);
        }
        
        i = push_index_global;
        PISTON_DEBUG("PushIndex "+i);
        
        while (--i >= PUSH_LIST_START_INDEX) {
            // Set X,Y,Z to position of block in push list
            packed_pos = pushed_blocks[i];
            BLOCK_POS_UNPACK(packed_pos, X, Y, Z);
            
            // Get data about pushed block
            block_id = data_list[i];
            block = Block.blocksList[block_id];
            block_meta = world.getBlockMetadata(X, Y, Z);
            
            PISTON_DEBUG("Push "+block_id+"."+block_meta+"("+X+" "+Y+" "+Z+")");
            
            NBTTagCompound tile_entity_data = getBlockTileEntityData(world, X, Y, Z);
            world.removeBlockTileEntity(X, Y, Z);
            
            packed_pos = BLOCK_POS_PACK(X + Facing.offsetsXForSide[direction], Y + Facing.offsetsYForSide[direction], Z + Facing.offsetsZForSide[direction]);
            
            goto_block(coord_will_move) {
                for (int j = 0; j < i; ++j) {
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
            //if (!BLOCK_IS_AIR(block)) {
                block_meta = block.adjustMetadataForPistonMove(block_meta);
            //}
            //world.setBlock(X, Y, Z, Block.pistonMoving.blockID, block_meta, UPDATE_INVISIBLE);
            world.setBlock(X, Y, Z, Block.pistonMoving.blockID, block_meta, UPDATE_IMMEDIATE | UPDATE_SUPPRESS_DROPS | UPDATE_MOVE_BY_PISTON);
            world.setBlockTileEntity(X, Y, Z, BlockPistonMoving.getTileEntity(block_id, block_meta, direction, true, false));
            if (tile_entity_data != null) {
                ((TileEntityPiston)world.getBlockTileEntity(X, Y, Z)).storeTileEntity(tile_entity_data);
            }
        }
        
        // Place a moving piston entity for the head
        if (is_extending) {
            PISTON_DEBUG("PistonHead "+Block.pistonMoving.blockID+"."+(direction | (this.isSticky ? 8 : 0))+"("+headX+" "+headY+" "+headZ+")");
            //world.setBlock(headX, headY, headZ, Block.pistonMoving.blockID, direction | (this.isSticky ? 8 : 0), UPDATE_INVISIBLE);
            world.setBlock(headX, headY, headZ, Block.pistonMoving.blockID, direction | (this.isSticky ? 8 : 0), UPDATE_IMMEDIATE | UPDATE_SUPPRESS_DROPS | UPDATE_MOVE_BY_PISTON);
			world.setBlockTileEntity(headX, headY, headZ, BlockPistonMoving.getTileEntity(Block.pistonExtension.blockID, direction | (this.isSticky ? 8 : 0), direction, true, false));
        }
        
        // START SHOVEL CODE
        i = shovel_index_global;
        while (--i >= SHOVEL_LIST_START_INDEX) {
            // Set X,Y,Z to position of ejection destination block in shovel list
            packed_pos = pushed_blocks[i];
            BLOCK_POS_UNPACK(packed_pos, X, Y, Z);
            
            block_id = world.getBlockId(X, Y, Z);
            block = Block.blocksList[block_id]; // Get ejection block
            block_id = data_list[i + SHOVEL_BLOCK_ID_LIST_START_INDEX];
            int eject_direction = data_list[i + SHOVEL_DIRECTION_LIST_START_INDEX];
            block_meta = data_list[i + SHOVEL_BLOCK_META_LIST_START_INDEX];
            
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
            world.notifyBlocksOfNeighborChange(BLOCK_POS_UNPACK_ARGS(packed_pos), data_list[i]);
        }
        if (is_extending) {
            world.notifyBlocksOfNeighborChange(headX, headY, headZ, Block.pistonExtension.blockID);
        }
        return true;
    }
    
    private static final int PISTON_EVENT_EXTENDING = 0;
    private static final int PISTON_EVENT_RETRACTING = 1;
    private static final int PISTON_EVENT_IDK = 2;
    
    //@Override
    protected void updatePistonState(World world, int X, int Y, int Z) {
        int meta = world.getBlockMetadata(X, Y, Z);
        int direction = READ_META_FIELD(meta, DIRECTION);
        
        // Why does the 1.5 code check for 7?
        if (direction != 7) {
            boolean is_powered = ((IPistonBaseAccessMixins)(Object)this).callIsIndirectlyPowered(world, X, Y, Z, direction);
            if (is_powered != READ_META_FIELD(meta, EXTENDED)) {
                if (is_powered) {
                    // CRAP, canExtend is used here...
                    if (canExtend(world, X, Y, Z, direction)) {
                        world.addBlockEvent(X, Y, Z, this.blockID, PISTON_EVENT_EXTENDING, direction);
                    }
                } else {
                    
                    int nextX = X + Facing.offsetsXForSide[direction] * 2;
                    int nextY = Y + Facing.offsetsYForSide[direction] * 2;
                    int nextZ = Z + Facing.offsetsZForSide[direction] * 2;
                    int next_block_id = world.getBlockId(nextX, nextY, nextZ);
                    TileEntity tile_entity;
                    if (
                        next_block_id == Block.pistonMoving.blockID &&
                        (tile_entity = world.getBlockTileEntity(nextX, nextY, nextZ)) instanceof TileEntityPiston &&
                        ((TileEntityPiston)tile_entity).getPistonOrientation() == direction &&
                        ((TileEntityPiston)tile_entity).isExtending() &&
                        (
                            ((TileEntityPiston)tile_entity).getProgress(0.0f) < 0.5f ||
                            world.getTotalWorldTime() == ((IBlockEntityPistonMixins)tile_entity).getLastTicked()
                            // something about "handling tick"?
                        )
                    ) {
                        world.addBlockEvent(X, Y, Z, this.blockID, PISTON_EVENT_IDK, direction);
                    } else {
                        //world.setBlockMetadataWithNotify(X, Y, Z, direction, UPDATE_CLIENTS);
                        world.addBlockEvent(X, Y, Z, this.blockID, PISTON_EVENT_RETRACTING, direction);
                    }
                }
            }
        }
    }
    
    //@Override
    public boolean onBlockEventReceived(World world, int X, int Y, int Z, int event_type, int direction) {
        PISTON_DEBUG("===== PistonCoords ("+X+" "+Y+" "+Z+")");
        if (!world.isRemote) {
            boolean is_powered = ((IPistonBaseAccessMixins)(Object)this).callIsIndirectlyPowered(world, X, Y, Z, direction);
            if (
                is_powered &&
                (
                    event_type == PISTON_EVENT_RETRACTING ||
                    event_type == PISTON_EVENT_IDK
                )
            ) {
                world.setBlockMetadataWithNotify(X, Y, Z, MERGE_META_FIELD(direction, EXTENDED, true), UPDATE_CLIENTS);
                return false;
            }
            if (!is_powered && event_type == PISTON_EVENT_EXTENDING) {
                return false;
            }
        }
        switch (event_type) {
            default:
                return true;
            case PISTON_EVENT_EXTENDING: // Extend
                //PISTON_DEBUG("Case PISTON_EVENT_EXTENDING");
                if (!this.moveBlocks(world, X, Y, Z, direction, true)) {
                    return false;
                }
                //world.setBlockMetadataWithNotify(X, Y, Z, direction | 8, UPDATE_CLIENTS);
                world.setBlockMetadataWithNotify(X, Y, Z, MERGE_META_FIELD(direction, EXTENDED, true), UPDATE_NEIGHBORS | UPDATE_CLIENTS | UPDATE_SUPPRESS_DROPS | UPDATE_MOVE_BY_PISTON);
                world.playSoundEffect((double)X + 0.5D, (double)Y + 0.5D, (double)Z + 0.5D, "tile.piston.out", 0.5F, world.rand.nextFloat() * 0.25F + 0.6F);
                return true;
            case PISTON_EVENT_RETRACTING: case PISTON_EVENT_IDK: // Retract
                //PISTON_DEBUG("Case PISTON_EVENT_RETRACTING");
                int nextX = X + Facing.offsetsXForSide[direction];
                int nextY = Y + Facing.offsetsYForSide[direction];
                int nextZ = Z + Facing.offsetsZForSide[direction];
                TileEntity tile_entity = world.getBlockTileEntity(nextX, nextY, nextZ);
                if (tile_entity instanceof TileEntityPiston) {
                    ((TileEntityPiston)tile_entity).clearPistonTileEntity();
                }
                //world.setBlock(X, Y, Z, Block.pistonMoving.blockID, direction, UPDATE_ALL);
                world.setBlock(X, Y, Z, Block.pistonMoving.blockID, direction, UPDATE_SUPPRESS_DROPS);
                world.setBlockTileEntity(X, Y, Z, BlockPistonMoving.getTileEntity(this.blockID, direction, direction, false, true));
                if (this.isSticky) {
                    int currentX = nextX + Facing.offsetsXForSide[direction];
                    int currentY = nextY + Facing.offsetsYForSide[direction];
                    int currentZ = nextZ + Facing.offsetsZForSide[direction];
                    int next_block_id = world.getBlockId(currentX, currentY, currentZ);
                    //int next_block_meta = par1World.getBlockMetadata(currentX, currentY, currentZ);
                    //boolean block_set_from_moving_piston = false;
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
                                    //next_block_id = piston_tile_entity.getStoredBlockID();
                                    //next_block_meta = piston_tile_entity.getBlockMetadata();
                                    //block_set_from_moving_piston = true;
                                    goto(block_set_from_moving_piston);
                                }
                            }
                        }
                        if (event_type == PISTON_EVENT_RETRACTING) {
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