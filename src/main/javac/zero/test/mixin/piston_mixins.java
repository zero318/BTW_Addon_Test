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
import zero.test.mixin.IPistonBaseMixins;
import zero.test.IWorldMixins;

#include "..\func_aliases.h"
#include "..\feature_flags.h"
#include "..\util.h"

#define DIRECTION_META_OFFSET 0
#define STICKY_META_OFFSET 3
#define STICKY_META_BITS 1
#define STICKY_META_IS_BOOL true

#define PISTON_PRINT_DEBUGGING 0

#if PISTON_PRINT_DEBUGGING
#define PISTON_DEBUG(...) AddonHandler.logMessage(__VA_ARGS__)
#else
#define PISTON_DEBUG(...)
#endif

@Mixin(PistonBlockBase.class)
public abstract class PistonMixins MACRO_IF(MACRO_IS_1(ENABLE_MOVING_BLOCK_CHAINING), extends BlockPistonBase) {
    
#if ENABLE_MOVING_BLOCK_CHAINING

    public PistonMixins(int block_id, boolean is_sticky) {
        super(block_id, is_sticky);
    }
    
    //@Shadow
    //@Final
    //protected boolean isSticky;
    
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
     
#define PUSH_LIST_START_INDEX 0
#define PUSH_LIST_LENGTH PISTON_PUSH_LIMIT

#define SHOVEL_LIST_START_INDEX PUSH_LIST_LENGTH
#define SHOVEL_LIST_LENGTH PUSH_LIST_LENGTH

#define DESTROY_LIST_START_INDEX PUSH_LIST_LENGTH+SHOVEL_LIST_LENGTH
#define DESTROY_LIST_LENGTH PUSH_LIST_LENGTH

#define SHOVEL_DIRECTION_LIST_START_INDEX 0
#define SHOVEL_DIRECTION_LIST_LENGTH PUSH_LIST_LENGTH

#define SHOVEL_BLOCK_ID_LIST_START_INDEX SHOVEL_DIRECTION_LIST_LENGTH
#define SHOVEL_BLOCK_ID_LIST_LENGTH PUSH_LIST_LENGTH

#define SHOVEL_BLOCK_META_LIST_START_INDEX SHOVEL_DIRECTION_LIST_LENGTH+SHOVEL_BLOCK_ID_LIST_LENGTH
#define SHOVEL_BLOCK_META_LIST_LENGTH PUSH_LIST_LENGTH

    // Becase screw java and by value semantics
    private static int push_index_global;
    private static int shovel_index_global;
    private static int destroy_index_global;
    private static long piston_position;
    
    // The destroy list can't be longer than the push list, right?
    // TODO: TEST THIS ASSUMPTION
    private static long[] pushed_blocks = new long[PUSH_LIST_LENGTH+SHOVEL_LIST_LENGTH+DESTROY_LIST_LENGTH];
    private static int[] shovel_data_list = new int[SHOVEL_DIRECTION_LIST_LENGTH+SHOVEL_BLOCK_ID_LIST_LENGTH+SHOVEL_BLOCK_META_LIST_LENGTH];

    // This is only called in situations when the
    // current block is known to not be air
    protected boolean add_branch(World world, int X, int Y, int Z, int direction) {
        int block_id = world.getBlockId(X, Y, Z);
        Block block = Block.blocksList[block_id];
        PISTON_DEBUG("Branching Block ("+X+" "+Y+" "+Z+")"+direction);
        
        for (int facing = 0; facing < 6; ++facing) {
            PISTON_DEBUG("Facing/Direction ("+facing+"/"+direction+") ("+DIRECTION_AXIS(facing)+"/"+DIRECTION_AXIS(direction)+")");
            if (DIRECTION_AXIS(facing) != DIRECTION_AXIS(direction)) {
                PISTON_DEBUG("Adding branch "+facing);
                if (((IBlockMixins)block).isSticky(X, Y, Z, facing)) {
                    int nextX = X + Facing.offsetsXForSide[facing];
                    int nextY = Y + Facing.offsetsYForSide[facing];
                    int nextZ = Z + Facing.offsetsZForSide[facing];
                    Block neighbor_block = Block.blocksList[world.getBlockId(nextX, nextY, nextZ)];
                    if (
                        !BLOCK_IS_AIR(neighbor_block) &&
                        ((IBlockMixins)neighbor_block).canStickTo(nextX, nextY, nextZ, facing, block_id) &&
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
            !block.canBlockBePulledByPiston(world, X, Y, Z, direction)
        ) {
            return true;
        }
        PISTON_DEBUG("SearchForExistingPush ("+X+" "+Y+" "+Z+")");
        
        // This replaces toPush.contains
        int push_index = push_index_global;
        for (int i = PUSH_LIST_START_INDEX; i < push_index; ++i) {
            if (pushed_blocks[i] == packed_pos) {
                return true;
            }
        }
        
        int push_write_index = push_index;
        int nextX = X;
        int nextY = Y;
        int nextZ = Z;
        Block next_block = block;
        loop {
            PISTON_DEBUG("StickySearch ("+nextX+" "+nextY+" "+nextZ+")");
            if (++push_write_index == PISTON_PUSH_LIMIT) {
                PISTON_DEBUG("Push failed limit reached A");
                return false;
            }
            if (!((IBlockMixins)next_block).isSticky(nextX, nextY, nextZ, direction)) {
                PISTON_DEBUG("IsSticky false");
                break;
            }
            PISTON_DEBUG("IsSticky true");
            // Index in opposite direction
            int tempX = nextX - Facing.offsetsXForSide[direction];
            int tempY = nextY - Facing.offsetsYForSide[direction];
            int tempZ = nextZ - Facing.offsetsZForSide[direction];
            packed_pos = BLOCK_POS_PACK(tempX, tempY, tempZ);
            if (packed_pos == piston_position) {
                break;
            }
            int prev_block_id = block_id;
            block_id = world.getBlockId(tempX, tempY, tempZ);
            next_block = Block.blocksList[block_id];
            if (
                BLOCK_IS_AIR(next_block) ||
                !((IBlockMixins)next_block).canStickTo(tempX, tempY, tempZ, direction, prev_block_id) ||
                !next_block.canBlockBePulledByPiston(world, tempX, tempY, tempZ, direction)
            ) {
                break;
            }
            nextX = tempX;
            nextY = tempY;
            nextZ = tempZ;
        }
        
        int push_count = push_write_index - push_index;
        // current should still be the furthest away coord
        do {
            // Index back towards the start?
            // Should this index before or after adding to the list?
            PISTON_DEBUG("AddPush ("+nextX+" "+nextY+" "+nextZ+")");
            pushed_blocks[push_index++] = BLOCK_POS_PACK(nextX, nextY, nextZ);
            nextX += Facing.offsetsXForSide[direction];
            nextY += Facing.offsetsYForSide[direction];
            nextZ += Facing.offsetsZForSide[direction];
        } while (push_index != push_write_index);
        
        loop {
            push_index_global = push_index;
            PISTON_DEBUG("New push index "+push_index_global);
            nextX = X + Facing.offsetsXForSide[direction];
            nextY = Y + Facing.offsetsYForSide[direction];
            nextZ = Z + Facing.offsetsZForSide[direction];
            PISTON_DEBUG("ParsePush ("+nextX+" "+nextY+" "+nextZ+")");
            packed_pos = BLOCK_POS_PACK(nextX, nextY, nextZ);
            for (int i = PUSH_LIST_START_INDEX; i < push_index; ++i) {
                if (pushed_blocks[i] == packed_pos) {
                    // Does this even run? It looks like dead code...
                    PISTON_DEBUG("Hacky swap code");
                    
                    // Hacky in place array rotation
                    int swap_max = gcd(push_index, push_count) + i;
                    for (int j = i; j < swap_max; ++j) {
                        int k = j;
                        packed_pos = pushed_blocks[j];
                        loop {
                            int d = (k + push_count) % push_index;
                            if (d == j) {
                                break;
                            }
                            pushed_blocks[k] = pushed_blocks[d];
                            k = d;
                        }
                        pushed_blocks[k] = packed_pos;
                    }
                    
                    for (int j = PUSH_LIST_START_INDEX; j < i + push_count; ++j) {
                        packed_pos = pushed_blocks[j];
                        BLOCK_POS_UNPACK(packed_pos, nextX, nextY, nextZ);
                        block_id = world.getBlockId(nextX, nextY, nextZ);
                        block = Block.blocksList[block_id];
                        // Vanilla checks stickiness as part of this statement,
                        // but supporting directional stickiness required
                        // moving the check into add_branch
                        if (
                            !BLOCK_IS_AIR(block) &&
                            //((IBlockMixins)block).isSticky(nextX, nextY, nextZ, direction) &&
                            !this.add_branch(world, nextX, nextY, nextZ, direction)
                        ) {
                            PISTON_DEBUG("Push failed move block branching ("+nextX+" "+nextY+" "+nextZ+")");
                            return false;
                        }
                    }
                    return true;
                }
            }
            block_id = world.getBlockId(nextX, nextY, nextZ);
            next_block = Block.blocksList[block_id];
            if (BLOCK_IS_AIR(next_block)) {
                return true;
            }
            packed_pos = BLOCK_POS_PACK(nextX, nextY, nextZ);
            if (
                packed_pos == piston_position ||
                !next_block.canBlockBePushedByPiston(world, nextX, nextY, nextZ, direction)
            ) {
                PISTON_DEBUG("Push failed move block IDK ("+nextX+" "+nextY+" "+nextZ+")");
                return false;
            }
            if (next_block.getMobilityFlag() == PISTON_CAN_BREAK) {
                pushed_blocks[destroy_index_global++] = packed_pos;
                return true;
            }
            // START SHOVEL CODE
            if (next_block.canBePistonShoveled(world, nextX, nextY, nextZ)) {
                int eject_direction = block.getPistonShovelEjectDirection(world, X, Y, Z, direction);
                if (eject_direction >= 0) {
                    X = nextX + Facing.offsetsXForSide[eject_direction];
                    Y = nextY + Facing.offsetsYForSide[eject_direction];
                    Z = nextZ + Facing.offsetsZForSide[eject_direction];
                    block = Block.blocksList[world.getBlockId(X, Y, Z)];
                    if (
                        BLOCK_IS_AIR(block) ||
                        block.getMobilityFlag() == PISTON_CAN_BREAK
                    ) {
                        // Make sure the ejection block isn't already being used
                        packed_pos = BLOCK_POS_PACK(X, Y, Z);
                        goto_block(block_is_shovelled) {
                            int i = shovel_index_global;
                            while (--i >= SHOVEL_LIST_START_INDEX) {
                                if (pushed_blocks[i] == packed_pos) {
                                    goto(block_is_shovelled);
                                }
                            }
                            shovel_data_list[shovel_index_global-SHOVEL_DIRECTION_LIST_LENGTH] = eject_direction;
                            shovel_data_list[shovel_index_global] = block_id;
                            shovel_data_list[shovel_index_global+SHOVEL_BLOCK_ID_LIST_LENGTH] = next_block.adjustMetadataForPistonMove(world.getBlockMetadata(nextX, nextY, nextZ));
                            
                            // This prevents duping shovelled blocks
                            world.setBlock(nextX, nextY, nextZ, 0, 0, UPDATE_INVISIBLE);
                            
                            pushed_blocks[shovel_index_global++] = packed_pos;
                            return true;
                        } goto_target(block_is_shovelled);
                    }
                }
            }
            // END SHOVEL CODE
            if (push_index == PISTON_PUSH_LIMIT) {
                PISTON_DEBUG("Push failed limit reached B");
                return false;
            }
            pushed_blocks[push_index++] = packed_pos;
            ++push_count;
            X = nextX;
            Y = nextY;
            Z = nextZ;
            block = next_block;
        }
    }
    
    protected boolean resolve(World world, int X, int Y, int Z, int direction, boolean is_extending) {
        
        int block_id = world.getBlockId(X, Y, Z);
        Block block = Block.blocksList[block_id];
        if (BLOCK_IS_AIR(block)) {
            return true;
        }
        long packed_pos = BLOCK_POS_PACK(X,Y,Z);
        if (!(is_extending
            ? block.canBlockBePushedByPiston(world, X, Y, Z, direction)
            : block.canBlockBePulledByPiston(world, X, Y, Z, direction)
        )) {
            if (
                is_extending &&
                block.getMobilityFlag() == PISTON_CAN_BREAK
            ) {
                // Add destroy
                PISTON_DEBUG("Resolve destroy ("+X+" "+Y+" "+Z+")");
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
        for (int i = 0; i < push_index_global; ++i) {
            packed_pos = pushed_blocks[i];
            BLOCK_POS_UNPACK(packed_pos, X, Y, Z);
            block_id = world.getBlockId(X, Y, Z);
            block = Block.blocksList[block_id];
            if (
                !BLOCK_IS_AIR(block) &&
                // Vanilla checks stickiness as part of this statement,
                // but supporting directional stickiness required
                // moving the check into add_branch
                //((IBlockMixins)block).isSticky(X, Y, Z, direction) &&
                !this.add_branch(world, X, Y, Z, direction)
            ) {
                PISTON_DEBUG("Push failed branching");
                return false;
            }
        }
        return true;
    }
    
    public boolean moveBlocks(World world, int X, int Y, int Z, int direction, boolean is_extending) {
        
        piston_position = BLOCK_POS_PACK(X,Y,Z);
        push_index_global = PUSH_LIST_START_INDEX;
        shovel_index_global = SHOVEL_LIST_START_INDEX;
        destroy_index_global = DESTROY_LIST_START_INDEX;
        
        X += Facing.offsetsXForSide[direction];
        Y += Facing.offsetsYForSide[direction];
        Z += Facing.offsetsZForSide[direction];
        int nextX = X;
        int nextY = Y;
        int nextZ = Z;
        if (!is_extending) {
            world.setBlockToAir(X, Y, Z);
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
            packed_pos = pushed_blocks[i];
            BLOCK_POS_UNPACK(packed_pos, X, Y, Z);
            block_id = world.getBlockId(X, Y, Z);
            block = Block.blocksList[block_id];
            block_meta = world.getBlockMetadata(X, Y, Z);
            
            PISTON_DEBUG("Destroy "+block_id+"."+block_meta+"("+X+" "+Y+" "+Z+")");
            
            block.onBrokenByPistonPush(world, X, Y, Z, block_meta);
            world.setBlockToAir(X, Y, Z);
        }
        
        i = push_index_global;
        PISTON_DEBUG("PushIndex "+i);
        
        while (--i >= PUSH_LIST_START_INDEX) {
            packed_pos = pushed_blocks[i];
            BLOCK_POS_UNPACK(packed_pos, X, Y, Z);
            block_id = world.getBlockId(X, Y, Z);
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
                world.setBlockToAir(X, Y, Z);
            } goto_target(coord_will_move);
            
            
            X += Facing.offsetsXForSide[direction];
            Y += Facing.offsetsYForSide[direction];
            Z += Facing.offsetsZForSide[direction];
            if (!BLOCK_IS_AIR(block)) {
                block_meta = block.adjustMetadataForPistonMove(block_meta);
            }
            world.setBlock(X, Y, Z, Block.pistonMoving.blockID, block_meta, UPDATE_INVISIBLE);
            world.setBlockTileEntity(X, Y, Z, BlockPistonMoving.getTileEntity(block_id, block_meta, direction, true, false));
            if (tile_entity_data != null) {
                ((TileEntityPiston)world.getBlockTileEntity(X, Y, Z)).storeTileEntity(tile_entity_data);
            }
        }
        
        if (is_extending) {
            PISTON_DEBUG("PistonHead "+Block.pistonMoving.blockID+"."+(direction | (this.isSticky ? 8 : 0))+"("+nextX+" "+nextY+" "+nextZ+")");
            world.setBlock(nextX, nextY, nextZ, Block.pistonMoving.blockID, direction | (this.isSticky ? 8 : 0), UPDATE_INVISIBLE);
			world.setBlockTileEntity(nextX, nextY, nextZ, BlockPistonMoving.getTileEntity(Block.pistonExtension.blockID, direction | (this.isSticky ? 8 : 0), direction, true, false));
        }
        
        // START SHOVEL CODE
        i = shovel_index_global;
        while (--i >= SHOVEL_LIST_START_INDEX) {
            packed_pos = pushed_blocks[i];
            BLOCK_POS_UNPACK(packed_pos, X, Y, Z);
            block_id = world.getBlockId(X, Y, Z);
            block = Block.blocksList[block_id]; // Get ejection block
            nextX = shovel_data_list[i-SHOVEL_DIRECTION_LIST_LENGTH];
            block_id = shovel_data_list[i];
            block_meta = shovel_data_list[i+SHOVEL_BLOCK_ID_LIST_LENGTH];
            if (
                BLOCK_IS_AIR(block) ||
                block.getMobilityFlag() == PISTON_CAN_BREAK
            ) {
                onShovelEjectIntoBlock(world, X, Y, Z);
                world.setBlock(X, Y, Z, Block.pistonMoving.blockID, block_meta, UPDATE_INVISIBLE);
                world.setBlockTileEntity(X, Y, Z, PistonBlockMoving.getShoveledTileEntity(block_id, block_meta, nextX));
            } else if (!world.isRemote) {
                block = Block.blocksList[block_id]; // Get shovelled block
                if (!BLOCK_IS_AIR(block)) {
                    nextY = block.idDropped(block_meta, world.rand, 0);
                    if (nextY != 0) {
                        ItemUtils.ejectStackFromBlockTowardsFacing(
                            world,
                            X - Facing.offsetsXForSide[nextX],
                            Y - Facing.offsetsYForSide[nextX],
                            Z - Facing.offsetsZForSide[nextX],
                            new ItemStack(nextY, block.quantityDropped(world.rand), block.damageDropped(block_meta)),
                            nextX
                        );
                    }
                }
            }
        }
        // END SHOVEL CODE
        
        i = destroy_index_global;
        while (--i >= DESTROY_LIST_START_INDEX) {
            packed_pos = pushed_blocks[i];
            BLOCK_POS_UNPACK(packed_pos, X, Y, Z);
            block_id = world.getBlockId(X, Y, Z);
            world.notifyBlocksOfNeighborChange(X, Y, Z, block_id);
        }
        i = push_index_global;
        while (--i >= PUSH_LIST_START_INDEX) {
            packed_pos = pushed_blocks[i];
            BLOCK_POS_UNPACK(packed_pos, X, Y, Z);
            block_id = world.getBlockId(X, Y, Z);
            world.notifyBlocksOfNeighborChange(X, Y, Z, block_id);
        }
        return true;
    }
    
    // WTF is par5 tho
    //@Override
    public boolean onBlockEventReceived(World world, int X, int Y, int Z, int par5, int direction) {
        PISTON_DEBUG("===== PistonCoords ("+X+" "+Y+" "+Z+")");
        if (!world.isRemote) {
            boolean is_powered = ((IPistonBaseMixins)(Object)this).callIsIndirectlyPowered(world, X, Y, Z, direction);
            if (is_powered && (par5 == 1 || par5 == 2)) {
                world.setBlockMetadataWithNotify(X, Y, Z, direction | 8, UPDATE_CLIENTS);
                return false;
            }
            if (!is_powered && par5 == 0) {
                return false;
            }
        }
        switch (par5) {
            default:
                return true;
            case 0: // Extend
                //PISTON_DEBUG("Case 0");
                //if (!this.tryExtend(world, X, Y, Z, direction)) {
                if (!this.moveBlocks(world, X, Y, Z, direction, true)) {
                    return false;
                }
                world.setBlockMetadataWithNotify(X, Y, Z, direction | 8, UPDATE_CLIENTS);
                world.playSoundEffect((double)X + 0.5D, (double)Y + 0.5D, (double)Z + 0.5D, "tile.piston.out", 0.5F, world.rand.nextFloat() * 0.25F + 0.6F);
                return true;
            case 1: case 2: // Retract
                //PISTON_DEBUG("Case 1");
                int nextX = X + Facing.offsetsXForSide[direction];
                int nextY = Y + Facing.offsetsYForSide[direction];
                int nextZ = Z + Facing.offsetsZForSide[direction];
                TileEntity tile_entity = world.getBlockTileEntity(nextX, nextY, nextZ);
                if (tile_entity instanceof TileEntityPiston) {
                    ((TileEntityPiston)tile_entity).clearPistonTileEntity();
                }
                world.setBlock(X, Y, Z, Block.pistonMoving.blockID, direction, UPDATE_ALL);
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
                        if (par5 == 1) {
                            Block next_block = Block.blocksList[next_block_id];
                            if (
                                !BLOCK_IS_AIR(next_block) &&
                                next_block.canBlockBePulledByPiston(world, currentX, currentY, currentZ, Block.getOppositeFacing(direction))
                                // Modern vanilla adds something about checking push reaction here?
                            ) {
                                // Modern vanilla calls moveBlocks
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