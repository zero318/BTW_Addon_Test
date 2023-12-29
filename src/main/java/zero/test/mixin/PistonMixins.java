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
// func_96440_m = updateNeighbourForOutputSignal
// func_94487_f = blockIdIsActiveOrInactive
// func_94485_e = getActiveBlockID
// func_94484_i = getInactiveBlockID
// func_96470_c(metadata) = getRepeaterPoweredState(metadata)
// func_94478_d = shouldTurnOn
// func_94488_g = getAlternateSignal
// func_94490_c = isSubtractMode
// func_94491_m = calculateOutputSignal
// func_94483_i_ = __notifyOpposite
// func_94481_j_ = getComparatorDelay
/// func_94482_f = getInputSignal
// func_96476_c = refreshOutputState
//#define getInputSignal(...) func_94482_f(__VA_ARGS__)
// Vanilla observers
// Slime blocks
// Push only and dead coral fans
// Allow slime to keep loose blocks
// suspended in midair as if they
// had mortar applied
// Fix how most BTW blocks recieve power
// Allow block dispensers to respond to short pulses
// Block Breaker and Block Placer
/// Utility Macro Defs
/// Mutable Pos Move X
/// Mutable Pos Move Y
/// Mutable Pos Move Z
/// Mutable Pos Move
/// Mutable Pos Create
/// C-esque stuff
//#define printf(...) System.out.printf(__VA_ARGS__)
/// x86-esque stuff
/// Some operations are available
/// as and @IntrinsicCandidate, in
/// which case that form is preferred
//#define MOVSX(A) ((int)(A))
//#define MOVSXD(A) ((long)(A))
// Efficiently tests if [value] is within the range [min, max)
// Efficiently tests if [value] is within the range [min, max]
// Valid for both signed and unsigned integers
/// Random direction crap
/*
case NEIGHBOR_WEST:
case NEIGHBOR_EAST:
case NEIGHBOR_DOWN:
case NEIGHBOR_DOWN_WEST:
case NEIGHBOR_DOWN_EAST:
case NEIGHBOR_UP:
case NEIGHBOR_UP_WEST:
case NEIGHBOR_UP_EAST:
case NEIGHBOR_NORTH:
case NEIGHBOR_DOWN_NORTH:
case NEIGHBOR_UP_NORTH:
case NEIGHBOR_SOUTH:
case NEIGHBOR_DOWN_SOUTH:
case NEIGHBOR_UP_SOUTH:
*/
/// Expression Crap
/// Metadata stuff
// Meta write mask OFFSET, BITS
// Meta mask values OFFSET/BITS
// Meta mask values before shifting OFFSET, BITS
// Meta high value data OFFSET, BITS
// 0 = Needs != 0 if bool
// 1 = Is last field
// true = Is last field but uses != 0 anyway because it's 4 bits
// Meta const lookup OFFSET, VALUE
// Meta full write BITS, VALUE
//#define READ_META_FIELD_RAW(m,f)(    /*TEXT*/(m)    MACRO_IF_NOT(MACRO_IS_4(META_BITS(f)),        MACRO_IF_NOT(MACRO_IS_0(META_OFFSET(f)),            /*TEXT*/>>>META_OFFSET(f)        )        MACRO_IF_NOT(MACRO_IS_TRUTHY(META_IS_LAST(f)),            /*TEXT*/&META_MASK(f)        )    ))
//#define READ_META_FIELD_BOOL(m,f)(    /*TEXT*/((m)    MACRO_IF_NOT(MACRO_IS_TRUTHY(META_IS_ONLY_FIELD(f)),        MACRO_TERN(MACRO_IS_TRUTHY(META_IS_LAST(f)),            /*TEXT*/>META_BOOL_CMP(f)        /*ELSE*/,            /*TEXT*/&META_MASK_UNSHIFTED(f)        )    )    /*TEXT*/)    MACRO_IF_NOT(MACRO_IS_TRUTHY(META_BOOL_SKIPS_NEQ(f)),        /*TEXT*/!=0    ))
//#define READ_META_FIELD(m,f)(    MACRO_TERN(MACRO_IS_TRUTHY(META_IS_BOOL(f)),        READ_META_FIELD_BOOL(m,f)    /*ELSE*/,        READ_META_FIELD_RAW(m,f)    ))
//#define MERGE_META_FIELD_RAW(m,f,v)(    MACRO_TERN(MACRO_IS_TRUTHY(META_IS_ONLY_FIELD(f)),        /*TEXT*/(v)        MACRO_IF_NOT(META_VALID_CONST(f,v),            /*TEXT*/&META_WRITE_MASK(f)        )    /*ELSE*/,        /*TEXT*/(m)        MACRO_TERN(META_VALID_CONST(f,v),            MACRO_IF_NOT(META_IS_FULL_WRITE(f,v),                /*TEXT*/&META_WRITE_MASK(f)            )            MACRO_IF_NOT(MACRO_IS_FALSY(v),                /*TEXT*/|META_CONST_LOOKUP(f,v)            )        /*ELSE*/,            /*TEXT*/&META_WRITE_MASK(f)|(v)            MACRO_IF_NOT(MACRO_IS_0(META_OFFSET(f)),                /*TEXT*/<<META_OFFSET(f)            )        )    ))
//#define MERGE_META_FIELD_BOOL(m,f,v)(    MACRO_TERN(MACRO_IS_BOOL_ANY(v),        MACRO_TERN(MACRO_IS_TRUTHY(META_IS_ONLY_FIELD(f)),            /*TEXT*/MACRO_CAST_FROM_BOOL(v)        /*ELSE*/,            /*TEXT*/(m)            MACRO_TERN(MACRO_IS_TRUTHY(v),                /*TEXT*/|META_CONST_LOOKUP(f,1)            /*ELSE*/,                /*TEXT*/&META_WRITE_MASK(f)            )        )    /*ELSE*/,        MACRO_TERN(MACRO_IS_TRUTHY(META_IS_ONLY_FIELD(f)),            /*TEXT*/(v)&1        /*ELSE*/,            /*TEXT*/(m)&META_WRITE_MASK(f)|((v)&1)            MACRO_IF_NOT(MACRO_IS_0(META_OFFSET(f)),                /*TEXT*/<<META_OFFSET(f)            )        )    ))
//#define MERGE_META_FIELD(m,f,v)(    MACRO_TERN(MACRO_IS_TRUTHY(META_IS_BOOL(f)),        MERGE_META_FIELD_BOOL(m,f,v)    /*ELSE*/,        MERGE_META_FIELD_RAW(m,f,v)    ))
/// Fake Direction Metadata
/// Misc. Flags
// Glazed terracotta
// Z doesn't need to be masked because it's in the top bits anyway
@Mixin(PistonBlockBase.class)
public abstract class PistonMixins extends BlockPistonBase {
    public PistonMixins(int block_id, boolean is_sticky) {
        super(block_id, is_sticky);
    }
    public boolean hasLargeCenterHardPointToFacing(IBlockAccess block_access, int X, int Y, int Z, int direction, boolean ignore_transparency) {
        int meta = block_access.getBlockMetadata(X, Y, Z);
        return !((((meta)>7))) || ((direction)^1) == (((meta)&7));
    }
    @Shadow
    protected abstract int getPistonShovelEjectionDirection(World world, int X, int Y, int Z, int direction);
    @Shadow
    protected abstract void onShovelEjectIntoBlock(World world, int X, int Y, int Z);
    private static final int PISTON_PUSH_LIMIT =
    12;
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
                                                                     ;
        int facing = 0;
        do {
            //PISTON_DEBUG("Facing/Direction ("+facing+"/"+direction+") ("+DIRECTION_AXIS(facing)+"/"+DIRECTION_AXIS(direction)+")");
            if (((facing)&~1) != ((direction)&~1)) {
                //PISTON_DEBUG("Adding branch "+facing);
                if (((IBlockMixins)block).isStickyForBlocks(world, X, Y, Z, facing)) {
                    int nextX = X + Facing.offsetsXForSide[facing];
                    int nextY = Y + Facing.offsetsYForSide[facing];
                    int nextZ = Z + Facing.offsetsZForSide[facing];
                    Block neighbor_block = Block.blocksList[world.getBlockId(nextX, nextY, nextZ)];
                    if (
                        !((neighbor_block)==null) &&
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
        if (!((((Integer.compareUnsigned(((Y))-(0),(255)-(0)))<=0)))) {
            return false;
        }
        int block_id = world.getBlockId(X, Y, Z);
        Block block = Block.blocksList[block_id];
        long packed_pos;
        if (
            ((block)==null) ||
            (packed_pos = ((long)(Z)<<12 +26|(long)((X)&0x3FFFFFF)<<12|((Y)&0xFFF))) == piston_position ||
            (
                // Compatibility shim:
                // Since canBlockBePulledByPiston is used to prevent
                // destroyable blocks from getting pushed by adjacent
                // sticky blocks, blocks that can't be pulled need
                // to be detected separately. Vanilla solves this by
                // having a more complicated implementation of pushing
                // logic.
                ((IBlockMixins)block).getMobilityFlag(world, X, Y, Z) != 4 &&
                !block.canBlockBePulledByPiston(world, X, Y, Z, direction)
            )
        ) {
            return true;
        }
                                                                 ;
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
        for(;;) {
                                                                        ;
            // Check the push limit
            if (push_write_index == (PUSH_LIST_START_INDEX + PUSH_LIST_LENGTH)) {
                                                           ;
                return false;
            }
            ++push_write_index;
            if (!((IBlockMixins)next_block).isStickyForBlocks(world, nextX, nextY, nextZ, direction)) {
                                              ;
                break;
            }
                                         ;
            // Index in opposite direction
            int tempX = nextX - Facing.offsetsXForSide[direction];
            int tempY = nextY - Facing.offsetsYForSide[direction];
            int tempZ = nextZ - Facing.offsetsZForSide[direction];
            long temp_packed_pos = ((long)(tempZ)<<12 +26|(long)((tempX)&0x3FFFFFF)<<12|((tempY)&0xFFF));
            if (temp_packed_pos == piston_position) {
                break;
            }
            block_id = world.getBlockId(tempX, tempY, tempZ);
            next_block = Block.blocksList[block_id];
            if (
                ((next_block)==null) ||
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
                                                                   ;
            data_list[push_index] = (((next_block_id)&0xFFFF)|((world.getBlockMetadata(nextX, nextY, nextZ)))<<16);
            pushed_blocks[push_index++] = next_packed_pos;
            nextX += Facing.offsetsXForSide[direction];
            nextY += Facing.offsetsYForSide[direction];
            nextZ += Facing.offsetsZForSide[direction];
            next_packed_pos = ((long)(nextZ)<<12 +26|(long)((nextX)&0x3FFFFFF)<<12|((nextY)&0xFFF));
            next_block_id = world.getBlockId(nextX, nextY, nextZ);
        } while (push_index != push_write_index);
                                             ;
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
        for(;;) {
            push_index_global = push_index;
                                                             ;
                                                                     ;
            // This loop must index forwards
            for (int i = PUSH_LIST_START_INDEX; i < push_index; ++i) {
                // Loop will never continue when this is true
                if (pushed_blocks[i] == next_packed_pos) {
                    // This branch handles two
                    // sticky blocks trying to push
                    // the same block at once
                                                   ;
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
                        for(;;) {
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
                        {(X)=(int)((packed_pos)<<26>>(64)-26);(Z)=(int)((packed_pos)>>(64)-26);(Y)=(int)(packed_pos)<<(32)-12>>(32)-12;};
                        // Vanilla checks stickiness as part of this statement,
                        // but supporting directional stickiness required
                        // moving the check into add_branch
                        if (
                            // Air can't be added to the push list,
                            // so no need to check for that here
                            !this.add_branch(world, X, Y, Z, direction)
                        ) {
                                                                                                ;
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
            if (((next_block)==null)) {
                return true;
            }
            if (
                next_packed_pos == piston_position ||
                !next_block.canBlockBePushedByPiston(world, nextX, nextY, nextZ, direction)
            ) {
                                                                                          ;
                return false;
            }
            int next_block_meta = world.getBlockMetadata(nextX, nextY, nextZ);
            if (next_block.getMobilityFlag() == 1) {
                data_list[destroy_index_global] = (((next_block_id)&0xFFFF)|((next_block_meta))<<16);
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
                        ((block)==null) ||
                        eject_destination_block_id == Block.pistonMoving.blockID ||
                        block.getMobilityFlag() == 1
                    ) {
                        packed_pos = ((long)(Z)<<12 +26|(long)((X)&0x3FFFFFF)<<12|((Y)&0xFFF));
                        block_is_shoveled: do {
                            // Make sure the ejection block isn't already being used
                            for (int i = shovel_index_global; --i >= SHOVEL_EJECT_LIST_START_INDEX;) {
                                if (pushed_blocks[i] == packed_pos) {
                                    break block_is_shoveled;
                                }
                            }
                            data_list[shovel_index_global] = (((next_block_id)&0xFFFF)|((next_block_meta))<<16);
                            data_list[shovel_index_global + SHOVEL_DIRECTION_LIST_OFFSET] = eject_direction;
                            pushed_blocks[shovel_index_global] = packed_pos;
                            pushed_blocks[shovel_index_global + SHOVEL_LIST_OFFSET] = next_packed_pos;
                            ++shovel_index_global;
                            return true;
                        } while(false);
                    }
                }
            }
            // END SHOVEL CODE
            // Check the push limit
            if (push_index == (PUSH_LIST_START_INDEX + PUSH_LIST_LENGTH)) {
                                                           ;
                return false;
            }
            data_list[push_index] = (((next_block_id)&0xFFFF)|((next_block_meta))<<16);
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
            next_packed_pos = ((long)(nextZ)<<12 +26|(long)((nextX)&0x3FFFFFF)<<12|((nextY)&0xFFF));
            next_block_id = world.getBlockId(nextX, nextY, nextZ);
        }
        // unreachable
    }
    protected boolean resolve(World world, int X, int Y, int Z, int direction, boolean is_extending) {
        int block_id = world.getBlockId(X, Y, Z);
        Block block = Block.blocksList[block_id];
        if (((block)==null)) {
            return true;
        }
        long packed_pos = ((long)(Z)<<12 +26|(long)((X)&0x3FFFFFF)<<12|((Y)&0xFFF));
        if (
            ((IBlockMixins)block).getMobilityFlag(world, X, Y, Z) != 4 &&
            !block.canBlockBePulledByPiston(world, X, Y, Z, direction)
        ) {
            if (
                is_extending &&
                block.getMobilityFlag() == 1
            ) {
                // Add destroy
                                                                   ;
                data_list[destroy_index_global] = (((block_id)&0xFFFF)|((world.getBlockMetadata(X, Y, Z)))<<16);
                pushed_blocks[destroy_index_global++] = packed_pos;
                return true;
            }
                                                                                ;
            return false;
        }
                                                              ;
        if (!this.add_moved_block(world, X, Y, Z, direction)) {
                                                  ;
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
                !this.add_branch(world, (int)((packed_pos)<<26>>(64)-26),(int)(packed_pos)<<(32)-12>>(32)-12,(int)((packed_pos)>>(64)-26), direction)
            ) {
                                                     ;
                return false;
            }
        }
        return true;
    }
    public boolean moveBlocks(World world, int X, int Y, int Z, int direction, boolean is_extending) {
        // Set initial global values
        piston_position = ((long)(Z)<<12 +26|(long)((X)&0x3FFFFFF)<<12|((Y)&0xFFF));
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
                world.setBlock(X, Y, Z, 0, 0, 0x20);
            }
            X += Facing.offsetsXForSide[direction];
            Y += Facing.offsetsYForSide[direction];
            Z += Facing.offsetsZForSide[direction];
            direction = ((direction)^1);
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
                                       ;
        while (--i >= DESTROY_LIST_START_INDEX) {
            // Set X,Y,Z to position of block in destroy list
            packed_pos = pushed_blocks[i];
            {(X)=(int)((packed_pos)<<26>>(64)-26);(Z)=(int)((packed_pos)>>(64)-26);(Y)=(int)(packed_pos)<<(32)-12>>(32)-12;};
            // Get data about destroyed block
            block_state = data_list[i];
            {(block_id)=((block_state)&0xFFFF);(block_meta)=((block_state)>>>16);};
            block = Block.blocksList[block_id];
            block_meta = block.adjustMetadataForPistonMove(block_meta);
                                                                                  ;
            block.onBrokenByPistonPush(world, X, Y, Z, block_meta);
            world.setBlock(X, Y, Z, 0, 0, 0x08 | 0x10);
        }
        i = push_index_global;
                                    ;
        while (--i >= PUSH_LIST_START_INDEX) {
            // Set X,Y,Z to position of block in push list
            packed_pos = pushed_blocks[i];
            {(X)=(int)((packed_pos)<<26>>(64)-26);(Z)=(int)((packed_pos)>>(64)-26);(Y)=(int)(packed_pos)<<(32)-12>>(32)-12;};
            // Get data about pushed block
            block_state = data_list[i];
            {(block_id)=((block_state)&0xFFFF);(block_meta)=((block_state)>>>16);};
            block = Block.blocksList[block_id];
            block_meta = block.adjustMetadataForPistonMove(block_meta);
                                                                               ;
            NBTTagCompound tile_entity_data = getBlockTileEntityData(world, X, Y, Z);
            world.removeBlockTileEntity(X, Y, Z);
            packed_pos = ((long)(Z + Facing.offsetsZForSide[direction])<<12 +26|(long)((X + Facing.offsetsXForSide[direction])&0x3FFFFFF)<<12|((Y + Facing.offsetsYForSide[direction])&0xFFF));
            coord_will_move: do {
                //for (int j = 0; j < i; ++j) {
                for (int j = i; --j > PUSH_LIST_START_INDEX;) {
                    if (pushed_blocks[j] == packed_pos) {
                        break coord_will_move;
                    }
                }
                // This is to make sure that blocks being pulled leave air
                // behind when another block isn't moving into that space.
                // This replaces the hashmap from vanilla
                world.setBlock(X, Y, Z, 0, 0, 0x02 | 0x80);
            } while(false);
            // Offset X,Y,Z to the destination position
            X += Facing.offsetsXForSide[direction];
            Y += Facing.offsetsYForSide[direction];
            Z += Facing.offsetsZForSide[direction];
            world.setBlock(X, Y, Z, Block.pistonMoving.blockID, block_meta, 0x08 | 0x20 | 0x40);
            world.setBlockTileEntity(X, Y, Z, BlockPistonMoving.getTileEntity(block_id, block_meta, direction, true, false));
            if (tile_entity_data != null) {
                ((TileEntityPiston)world.getBlockTileEntity(X, Y, Z)).storeTileEntity(tile_entity_data);
            }
        }
        // Place a moving piston entity for the head
        if (is_extending) {
            block_meta = direction | (this.isSticky ? 8 : 0);
                                                                                                                   ;
            world.setBlock(headX, headY, headZ, Block.pistonMoving.blockID, block_meta, 0x08 | 0x20 | 0x40);
   world.setBlockTileEntity(headX, headY, headZ, BlockPistonMoving.getTileEntity(Block.pistonExtension.blockID, block_meta, direction, true, false));
        }
        // START SHOVEL CODE
        i = shovel_index_global;
        while (--i >= SHOVEL_EJECT_LIST_START_INDEX) {
            // Set X,Y,Z to position of ejection destination block in shovel list
            packed_pos = pushed_blocks[i];
            {(X)=(int)((packed_pos)<<26>>(64)-26);(Z)=(int)((packed_pos)>>(64)-26);(Y)=(int)(packed_pos)<<(32)-12>>(32)-12;};
            block_state = data_list[i];
            {(block_id)=((block_state)&0xFFFF);(block_meta)=((block_state)>>>16);};
            block = Block.blocksList[block_id];
            block_meta = block.adjustMetadataForPistonMove(block_meta);
            int eject_direction = data_list[i + SHOVEL_DIRECTION_LIST_OFFSET];
            if (
                ((block)==null) ||
                block.getMobilityFlag() == 1
            ) {
                onShovelEjectIntoBlock(world, X, Y, Z);
                world.setBlock(X, Y, Z, Block.pistonMoving.blockID, block_meta, 0x04);
                world.setBlockTileEntity(X, Y, Z, PistonBlockMoving.getShoveledTileEntity(block_id, block_meta, eject_direction));
            } else if (!world.isRemote) {
                block = Block.blocksList[block_id]; // Get shoveled block
                if (!((block)==null)) {
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
            world.notifyBlocksOfNeighborChange((int)((packed_pos)<<26>>(64)-26),(int)(packed_pos)<<(32)-12>>(32)-12,(int)((packed_pos)>>(64)-26), data_list[i] & 0xFFFF);
        }
        i = push_index_global;
        while (--i >= PUSH_LIST_START_INDEX) {
            // Set X,Y,Z to position of block in push list
            packed_pos = pushed_blocks[i];
            world.notifyBlocksOfNeighborChange((int)((packed_pos)<<26>>(64)-26),(int)(packed_pos)<<(32)-12>>(32)-12,(int)((packed_pos)>>(64)-26), data_list[i] & 0xFFFF);
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
        piston_position = ((long)(Z)<<12 +26|(long)((X)&0x3FFFFFF)<<12|((Y)&0xFFF));
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
            int direction = (((meta)&7));
            // Why does the 1.5 code check for 7?
            if (direction != 7) {
                boolean is_powered = ((IPistonBaseAccessMixins)(Object)this).callIsIndirectlyPowered(world, X, Y, Z, direction);
                if (is_powered != ((((meta)>7)))) {
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
                world.setBlockMetadataWithNotify(X, Y, Z, (((direction)|8)), 0x02);
                return false;
            }
            if (!is_powered && event_type == PISTON_EVENT_EXTENDING) {
                return false;
            }
        }
                                                              ;
        switch (event_type) {
            default:
                return true;
            case PISTON_EVENT_EXTENDING: // Extend
                                                           ;
                if (!this.moveBlocks(world, X, Y, Z, direction, true)) {
                    return false;
                }
                world.setBlockMetadataWithNotify(X, Y, Z, (((direction)|8)), 0x01 | 0x02 | 0x20 | 0x40);
                world.playSoundEffect((double)X + 0.5D, (double)Y + 0.5D, (double)Z + 0.5D, "tile.piston.out", 0.5F, world.rand.nextFloat() * 0.25F + 0.6F);
                return true;
            case PISTON_EVENT_RETRACTING_NORMAL: case PISTON_EVENT_RETRACTING_ZERO_TICK: // Retract
                                                                        ;
                int nextX = X + Facing.offsetsXForSide[direction];
                int nextY = Y + Facing.offsetsYForSide[direction];
                int nextZ = Z + Facing.offsetsZForSide[direction];
                TileEntity tile_entity = world.getBlockTileEntity(nextX, nextY, nextZ);
                if (tile_entity instanceof TileEntityPiston) {
                    ((TileEntityPiston)tile_entity).clearPistonTileEntity();
                }
                world.setBlock(X, Y, Z, Block.pistonMoving.blockID, direction, 0x20);
                world.setBlockTileEntity(X, Y, Z, BlockPistonMoving.getTileEntity(this.blockID, direction, direction, false, true));
                if (this.isSticky) {
                    int currentX = nextX + Facing.offsetsXForSide[direction];
                    int currentY = nextY + Facing.offsetsYForSide[direction];
                    int currentZ = nextZ + Facing.offsetsZForSide[direction];
                    int next_block_id = world.getBlockId(currentX, currentY, currentZ);
                    block_set_from_moving_piston: do {
                        if (next_block_id == Block.pistonMoving.blockID) {
                            TileEntity next_tile_entity = world.getBlockTileEntity(currentX, currentY, currentZ);
                            if (next_tile_entity instanceof TileEntityPiston) {
                                TileEntityPiston piston_tile_entity = (TileEntityPiston)next_tile_entity;
                                if (
                                    piston_tile_entity.getPistonOrientation() == direction &&
                                    piston_tile_entity.isExtending()
                                ) {
                                    piston_tile_entity.clearPistonTileEntity();
                                    break block_set_from_moving_piston;
                                }
                            }
                        }
                        if (event_type == PISTON_EVENT_RETRACTING_NORMAL) {
                            Block next_block = Block.blocksList[next_block_id];
                            if (
                                !((next_block)==null) &&
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
                    } while(false);
                } else {
                    world.setBlockToAir(nextX, nextY, nextZ);
                }
                world.playSoundEffect((double)X + 0.5D, (double)Y + 0.5D, (double)Z + 0.5D, "tile.piston.in", 0.5F, world.rand.nextFloat() * 0.15F + 0.6F);
                return true;
        }
    }
}
