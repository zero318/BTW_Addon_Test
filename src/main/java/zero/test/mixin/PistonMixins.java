package zero.test.mixin;
import net.minecraft.src.Block;
import net.minecraft.src.World;
import net.minecraft.src.BlockPistonBase;
import net.minecraft.src.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
// Block piston reactions
//#define getInputSignal(...) func_94482_f(__VA_ARGS__)
@Mixin(PistonBlockBase.class)
public abstract class PistonMixins extends BlockPistonBase {
    public PistonMixins(int blockId, boolean isSticky) {
        super(blockId, isSticky);
    }
    @Override
    public boolean triggersBuddy() {
        return false;
    }
    // Don't conduct redstone despite being "normal"
    public boolean isRedstoneConductor(IBlockAccess blockAccess, int x, int y, int z) {
        return false;
    }
    // Suffocate entities inside a retracted piston
    @Override
    public boolean isNormalCube(IBlockAccess blockAccess, int x, int y, int z) {
        return !((((blockAccess.getBlockMetadata(x, y, z))>7)));
    }
    @Override
    public boolean hasCenterHardPointToFacing(IBlockAccess blockAccess, int x, int y, int z, int facing, boolean ignoreTransparency) {
        int meta = blockAccess.getBlockMetadata(x, y, z);
        if (((((meta)>7)))) {
            int direction = (((meta)&7));
            if (
                direction == facing ||
                ((direction)<2) && ((facing)>=2)
            ) {
                return false;
            }
        }
        return true;
    }
    // The back of a piston is still a large hardpoint when extended
    @Override
    public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int x, int y, int z, int facing, boolean ignoreTransparency) {
        int meta = blockAccess.getBlockMetadata(x, y, z);
        return !((((meta)>7))) || ((facing)^1) == (((meta)&7));
    }
    @Shadow
    protected abstract int getPistonShovelEjectionDirection(World world, int x, int y, int z, int direction);
    @Shadow
    protected abstract void onShovelEjectIntoBlock(World world, int x, int y, int z);
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
    protected boolean addBranch(World world, int x, int y, int z, int direction) {
        // This could be pulled from the static lists if an index was passed in
        int blockId = world.getBlockId(x, y, z);
        Block block = Block.blocksList[blockId];
                                                                     ;
        int facing = 0;
        do {
            //PISTON_DEBUG("Facing/Direction ("+facing+"/"+direction+") ("+DIRECTION_AXIS(facing)+"/"+DIRECTION_AXIS(direction)+")");
            if (((facing)&~1) != ((direction)&~1)) {
                //PISTON_DEBUG("Adding branch "+facing);
                if (((IBlockMixins)block).isStickyForBlocks(world, x, y, z, facing)) {
                    int nextX = x + Facing.offsetsXForSide[facing];
                    int nextY = y + Facing.offsetsYForSide[facing];
                    int nextZ = z + Facing.offsetsZForSide[facing];
                    Block neighborBlock = Block.blocksList[world.getBlockId(nextX, nextY, nextZ)];
                    if (
                        !((neighborBlock)==null) &&
                        ((IBlockMixins)neighborBlock).canBeStuckTo(world, nextX, nextY, nextZ, facing, blockId) &&
                        !this.addMovedBlock(world, nextX, nextY, nextZ, direction)
                    ) {
                        return false;
                    }
                }
            }
        } while (((++facing)<=5));
        return true;
    }
    // TODO: Make this less naive
    public int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }
    // Returns false if the block chain is invalid,
    // which cancels all movement.
    // Returns true otherwise.
    protected boolean addMovedBlock(World world, int x, int y, int z, int direction) {
        // IDK where vanilla checks for valid block positions, but the old
        // 1.5 logic did a Y check here so I copied it.
        if (!((((Integer.compareUnsigned(((y))-(0),(255)-(0)))<=0)))) {
            return false;
        }
        int blockId = world.getBlockId(x, y, z);
        Block block = Block.blocksList[blockId];
        long packedPos;
        if (
            ((block)==null) ||
            (packedPos = ((long)(z)<<12 +26|(long)((x)&0x3FFFFFF)<<12|((y)&0xFFF))) == piston_position ||
            (
                // Compatibility shim:
                // Since canBlockBePulledByPiston is used to prevent
                // destroyable blocks from getting pushed by adjacent
                // sticky blocks, blocks that can't be pulled need
                // to be detected separately. Vanilla solves this by
                // having a more complicated implementation of pushing
                // logic.
                ((IBlockMixins)block).getMobilityFlag(world, x, y, z) != 4 &&
                !block.canBlockBePulledByPiston(world, x, y, z, direction)
            )
        ) {
            return true;
        }
                                                                 ;
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
        for(;;) {
                                                                        ;
            // Check the push limit
            if (pushWriteIndex == (PUSH_LIST_START_INDEX + PUSH_LIST_LENGTH)) {
                                                           ;
                return false;
            }
            ++pushWriteIndex;
            if (!((IBlockMixins)nextBlock).isStickyForBlocks(world, nextX, nextY, nextZ, ((direction)^1))) {
                                              ;
                break;
            }
                                         ;
            // Index in opposite direction
            int tempX = nextX - Facing.offsetsXForSide[direction];
            int tempY = nextY - Facing.offsetsYForSide[direction];
            int tempZ = nextZ - Facing.offsetsZForSide[direction];
            long tempPackedPos = ((long)(tempZ)<<12 +26|(long)((tempX)&0x3FFFFFF)<<12|((tempY)&0xFFF));
            if (tempPackedPos == piston_position) {
                break;
            }
            blockId = world.getBlockId(tempX, tempY, tempZ);
            nextBlock = Block.blocksList[blockId];
            if (
                ((nextBlock)==null) ||
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
                                                                   ;
            data_list[pushIndex] = (((nextBlockId)&0xFFFF)|((world.getBlockMetadata(nextX, nextY, nextZ)))<<16);
            pushed_blocks[pushIndex++] = nextPackedPos;
            nextX += Facing.offsetsXForSide[direction];
            nextY += Facing.offsetsYForSide[direction];
            nextZ += Facing.offsetsZForSide[direction];
            nextPackedPos = ((long)(nextZ)<<12 +26|(long)((nextX)&0x3FFFFFF)<<12|((nextY)&0xFFF));
            nextBlockId = world.getBlockId(nextX, nextY, nextZ);
        } while (pushIndex != pushWriteIndex);
                                            ;
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
        for(;;) {
            push_index_global = pushIndex;
                                                             ;
                                                                     ;
            // This loop must index forwards
            for (int i = PUSH_LIST_START_INDEX; i < pushIndex; ++i) {
                // Loop will never continue when this is true
                if (pushed_blocks[i] == nextPackedPos) {
                    // This branch handles two
                    // sticky blocks trying to push
                    // the same block at once
                                                   ;
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
                    for (int j = i; j < swapMax; ++j) {
                        int k = j;
                        blockId = data_list[j];
                        packedPos = pushed_blocks[j];
                        for(;;) {
                            int d = i + (k + pushWriteIndex) % pushIndex;
                            if (d == j) {
                                break;
                            }
                            data_list[k] = data_list[d];
                            pushed_blocks[k] = pushed_blocks[d];
                            k = d;
                        }
                        data_list[k] = blockId;
                        pushed_blocks[k] = packedPos;
                    }
                    i += pushCount;
                    //PISTON_DEBUG(""+" "+pushed_blocks[PUSH_LIST_START_INDEX]+" "+pushed_blocks[PUSH_LIST_START_INDEX+1]+" "+pushed_blocks[PUSH_LIST_START_INDEX+2]+" "+pushed_blocks[PUSH_LIST_START_INDEX+3]+" "+pushed_blocks[PUSH_LIST_START_INDEX+4]+" "+pushed_blocks[PUSH_LIST_START_INDEX+5]+" "+pushed_blocks[PUSH_LIST_START_INDEX+6]+" "+pushed_blocks[PUSH_LIST_START_INDEX+7]+" "+pushed_blocks[PUSH_LIST_START_INDEX+8]+" "+pushed_blocks[PUSH_LIST_START_INDEX+9]+" "+pushed_blocks[PUSH_LIST_START_INDEX+10]+" "+pushed_blocks[PUSH_LIST_START_INDEX+11]);
                    for (int j = PUSH_LIST_START_INDEX; j < i; ++j) {
                        packedPos = pushed_blocks[j];
                        {(x)=((int)((packedPos)<<26>>(64)-26));(z)=((int)((packedPos)>>(64)-26));(y)=((int)(packedPos)<<(32)-12>>(32)-12);};
                        // Vanilla checks stickiness as part of this statement,
                        // but supporting directional stickiness required
                        // moving the check into add_branch
                        if (
                            // Air can't be added to the push list,
                            // so no need to check for that here
                            !this.addBranch(world, x, y, z, direction)
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
            nextBlock = Block.blocksList[nextBlockId];
            if (((nextBlock)==null)) {
                return true;
            }
            if (
                nextPackedPos == piston_position ||
                !nextBlock.canBlockBePushedByPiston(world, nextX, nextY, nextZ, direction)
            ) {
                                                                                          ;
                return false;
            }
            int nextBlockMeta = world.getBlockMetadata(nextX, nextY, nextZ);
            if (nextBlock.getMobilityFlag() == 1) {
                data_list[destroy_index_global] = (((nextBlockId)&0xFFFF)|((nextBlockMeta))<<16);
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
                        ((block)==null) ||
                        ejectDestinationBlockId == Block.pistonMoving.blockID ||
                        block.getMobilityFlag() == 1
                    ) {
                        packedPos = ((long)(z)<<12 +26|(long)((x)&0x3FFFFFF)<<12|((y)&0xFFF));
                        // Make sure the ejection block isn't already being used
                        int i = shovel_index_global;
                        do {
                            if (--i < SHOVEL_EJECT_LIST_START_INDEX) {
                                data_list[shovel_index_global] = (((nextBlockId)&0xFFFF)|((nextBlockMeta))<<16);
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
                                                           ;
                return false;
            }
            data_list[pushIndex] = (((nextBlockId)&0xFFFF)|((nextBlockMeta))<<16);
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
            nextPackedPos = ((long)(nextZ)<<12 +26|(long)((nextX)&0x3FFFFFF)<<12|((nextY)&0xFFF));
            nextBlockId = world.getBlockId(nextX, nextY, nextZ);
        }
        // unreachable
    }
    protected boolean resolve(World world, int x, int y, int z, int direction, boolean isExtending) {
        int blockId = world.getBlockId(x, y, z);
        Block block = Block.blocksList[blockId];
        if (((block)==null)) {
            return true;
        }
        long packedPos = ((long)(z)<<12 +26|(long)((x)&0x3FFFFFF)<<12|((y)&0xFFF));
        if (
            ((IBlockMixins)block).getMobilityFlag(world, x, y, z) != 4 &&
            !block.canBlockBePulledByPiston(world, x, y, z, direction)
        ) {
            if (
                isExtending &&
                block.getMobilityFlag() == 1
            ) {
                // Add destroy
                                                                   ;
                data_list[destroy_index_global] = (((blockId)&0xFFFF)|((world.getBlockMetadata(x, y, z)))<<16);
                pushed_blocks[destroy_index_global++] = packedPos;
                return true;
            }
                                                                               ;
            return false;
        }
                                                              ;
        if (!this.addMovedBlock(world, x, y, z, direction)) {
                                                  ;
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
                !this.addBranch(world, ((int)((packedPos)<<26>>(64)-26)),((int)(packedPos)<<(32)-12>>(32)-12),((int)((packedPos)>>(64)-26)), direction)
            ) {
                                                     ;
                return false;
            }
        }
        return true;
    }
    public boolean moveBlocks(World world, int x, int y, int z, int direction, boolean isExtending) {
        // Set initial global values
        piston_position = ((long)(z)<<12 +26|(long)((x)&0x3FFFFFF)<<12|((y)&0xFFF));
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
                world.setBlock(x, y, z, 0, 0, 0x20);
            }
            x += Facing.offsetsXForSide[direction];
            y += Facing.offsetsYForSide[direction];
            z += Facing.offsetsZForSide[direction];
            direction = ((direction)^1);
        }
        if (!this.resolve(world, x, y, z, direction, isExtending)) {
            return false;
        }
        long packedPos;
        int blockId;
        int blockMeta;
        int blockState;
        Block block;
                                                          ;
        for (int i = destroy_index_global; --i >= DESTROY_LIST_START_INDEX;) {
            // Set x,y,z to position of block in destroy list
            packedPos = pushed_blocks[i];
            {(x)=((int)((packedPos)<<26>>(64)-26));(z)=((int)((packedPos)>>(64)-26));(y)=((int)(packedPos)<<(32)-12>>(32)-12);};
            // Get data about destroyed block
            blockState = data_list[i];
            {(blockId)=((blockState)&0xFFFF);(blockMeta)=((blockState)>>>16);};
            block = Block.blocksList[blockId];
            blockMeta = block.adjustMetadataForPistonMove(blockMeta);
                                                                                ;
            block.onBrokenByPistonPush(world, x, y, z, blockMeta);
            world.setBlock(x, y, z, 0, 0, 0x08 | 0x10);
        }
                                                    ;
        for (int i = push_index_global; --i >= PUSH_LIST_START_INDEX;) {
            // Set x,y,z to position of block in push list
            packedPos = pushed_blocks[i];
            {(x)=((int)((packedPos)<<26>>(64)-26));(z)=((int)((packedPos)>>(64)-26));(y)=((int)(packedPos)<<(32)-12>>(32)-12);};
            // Get data about pushed block
            blockState = data_list[i];
            {(blockId)=((blockState)&0xFFFF);(blockMeta)=((blockState)>>>16);};
            block = Block.blocksList[blockId];
            blockMeta = block.adjustMetadataForPistonMove(blockMeta);
                                                                             ;
            NBTTagCompound tileEntityData = getBlockTileEntityData(world, x, y, z);
            world.removeBlockTileEntity(x, y, z);
            packedPos = ((long)(z + Facing.offsetsZForSide[direction])<<12 +26|(long)((x + Facing.offsetsXForSide[direction])&0x3FFFFFF)<<12|((y + Facing.offsetsYForSide[direction])&0xFFF));
            int j = i;
            do {
                if (--j <= PUSH_LIST_START_INDEX) {
                    // This is to make sure that blocks being pulled leave air
                    // behind when another block isn't moving into that space.
                    // This replaces the hashmap from vanilla
                    world.setBlock(x, y, z, 0, 0, 0x02 | 0x80);
                    break;
                }
            } while (pushed_blocks[j] != packedPos);
            // Offset x,y,z to the destination position
            x += Facing.offsetsXForSide[direction];
            y += Facing.offsetsYForSide[direction];
            z += Facing.offsetsZForSide[direction];
            world.setBlock(x, y, z, Block.pistonMoving.blockID, blockMeta, 0x08 | 0x20 | 0x40);
            world.setBlockTileEntity(x, y, z, BlockPistonMoving.getTileEntity(blockId, blockMeta, direction, true, false));
            if (tileEntityData != null) {
                ((TileEntityPiston)world.getBlockTileEntity(x, y, z)).storeTileEntity(tileEntityData);
            }
        }
        // Place a moving piston entity for the head
        if (isExtending) {
            blockMeta = direction | (this.isSticky ? 8 : 0);
                                                                                                                  ;
            world.setBlock(headX, headY, headZ, Block.pistonMoving.blockID, blockMeta, 0x08 | 0x20 | 0x40);
   world.setBlockTileEntity(headX, headY, headZ, BlockPistonMoving.getTileEntity(Block.pistonExtension.blockID, blockMeta, direction, true, true));
        }
        // START SHOVEL CODE
        for (int i = shovel_index_global; --i >= SHOVEL_EJECT_LIST_START_INDEX;) {
            // Set x,y,z to position of ejection destination block in shovel list
            packedPos = pushed_blocks[i];
            {(x)=((int)((packedPos)<<26>>(64)-26));(z)=((int)((packedPos)>>(64)-26));(y)=((int)(packedPos)<<(32)-12>>(32)-12);};
            blockState = data_list[i];
            {(blockId)=((blockState)&0xFFFF);(blockMeta)=((blockState)>>>16);};
            block = Block.blocksList[blockId];
            blockMeta = block.adjustMetadataForPistonMove(blockMeta);
            int ejectDirection = data_list[i + SHOVEL_DIRECTION_LIST_OFFSET];
            if (
                ((block)==null) ||
                block.getMobilityFlag() == 1
            ) {
                onShovelEjectIntoBlock(world, x, y, z);
                world.setBlock(x, y, z, Block.pistonMoving.blockID, blockMeta, 0x04);
                world.setBlockTileEntity(x, y, z, PistonBlockMoving.getShoveledTileEntity(blockId, blockMeta, ejectDirection));
            }
            else if (!world.isRemote) {
                block = Block.blocksList[blockId]; // Get shoveled block
                if (!((block)==null)) {
                    // BUG: Some blocks don't seem to be returning the correct
                    // items when called like this, particularly packed earth.
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
            }
        }
        // END SHOVEL CODE
        for (int i = destroy_index_global; --i >= DESTROY_LIST_START_INDEX;) {
            // Set x,y,z to position of block in destroy list
            packedPos = pushed_blocks[i];
            world.notifyBlocksOfNeighborChange(((int)((packedPos)<<26>>(64)-26)),((int)(packedPos)<<(32)-12>>(32)-12),((int)((packedPos)>>(64)-26)), ((data_list[i])&0xFFFF));
        }
        for (int i = push_index_global; --i >= PUSH_LIST_START_INDEX;) {
            // Set x,y,z to position of block in push list
            packedPos = pushed_blocks[i];
            world.notifyBlocksOfNeighborChange(((int)((packedPos)<<26>>(64)-26)),((int)(packedPos)<<(32)-12>>(32)-12),((int)((packedPos)>>(64)-26)), ((data_list[i])&0xFFFF));
        }
        if (isExtending) {
            world.notifyBlocksOfNeighborChange(headX, headY, headZ, Block.pistonExtension.blockID);
        }
        return true;
    }
    @Overwrite
    public boolean tryExtend(World world, int x, int y, int z, int direction) {
        return this.moveBlocks(world, x, y, z, direction, true);
    }
    @Overwrite
    public boolean canExtend(World world, int x, int y, int z, int direction) {
        piston_position = ((long)(z)<<12 +26|(long)((x)&0x3FFFFFF)<<12|((y)&0xFFF));
        push_index_global = PUSH_LIST_START_INDEX;
        destroy_index_global = DESTROY_LIST_START_INDEX;
        shovel_index_global = SHOVEL_EJECT_LIST_START_INDEX;
        x += Facing.offsetsXForSide[direction];
        y += Facing.offsetsYForSide[direction];
        z += Facing.offsetsZForSide[direction];
        return this.resolve(world, x, y, z, direction, true);
    }
    private static final int PISTON_EVENT_EXTENDING = 0;
    private static final int PISTON_EVENT_RETRACTING_NORMAL = 1;
    private static final int PISTON_EVENT_RETRACTING_DROP_BLOCK = 2;
    @Override
    public void updatePistonState(World world, int x, int y, int z) {
        if (!world.isRemote) {
            int meta = world.getBlockMetadata(x, y, z);
            int direction = (((meta)&7));
            // Why does the 1.5 code check for 7?
            // Apparently directions 6 and 7 have been well known
            // to crash older versions. Extended this check to try
            // and prevent that.
            if (((direction)<=5)) {
                boolean isPowered = ((IPistonBaseAccessMixins)(Object)this).callIsIndirectlyPowered(world, x, y, z, direction);
                if (isPowered != ((((meta)>7)))) {
                    if (isPowered) {
                        // Yes, it does matter. Without this, the standard
                        // redstone block engine doesn't work.
                        // TODO: Why?
                        if (this.canExtend(world, x, y, z, direction)) {
                            world.addBlockEvent(x, y, z, this.blockID, PISTON_EVENT_EXTENDING, direction);
                        }
                    }
                    else {
                        int nextX = Facing.offsetsXForSide[direction];
                        int nextY = Facing.offsetsYForSide[direction];
                        int nextZ = Facing.offsetsZForSide[direction];
                        nextX += nextX + x;
                        nextY += nextY + y;
                        nextZ += nextZ + z;
                        int nextBlockId = world.getBlockId(nextX, nextY, nextZ);
                        TileEntity tileEntity;
                        world.addBlockEvent(
                            x, y, z,
                            this.blockID,
                            (
                                nextBlockId == Block.pistonMoving.blockID &&
                                (tileEntity = world.getBlockTileEntity(nextX, nextY, nextZ)) instanceof TileEntityPiston &&
                                ((TileEntityPiston)tileEntity).getPistonOrientation() == direction &&
                                ((TileEntityPiston)tileEntity).isExtending() &&
                                (
                                    ((TileEntityPiston)tileEntity).getProgress(0.0F) < 0.5F ||
                                    world.getTotalWorldTime() == ((IBlockEntityPistonMixins)tileEntity).getLastTicked()
                                    // something about "handling tick"?
                                )
                            ) ? PISTON_EVENT_RETRACTING_DROP_BLOCK : PISTON_EVENT_RETRACTING_NORMAL,
                            direction
                        );
                    }
                }
            }
        }
    }
    @Override
    public boolean onBlockEventReceived(World world, int x, int y, int z, int eventType, int direction) {
        if (!world.isRemote) {
            boolean isPowered = ((IPistonBaseAccessMixins)(Object)this).callIsIndirectlyPowered(world, x, y, z, direction);
            if (
                isPowered &&
                (
                    eventType == PISTON_EVENT_RETRACTING_NORMAL ||
                    eventType == PISTON_EVENT_RETRACTING_DROP_BLOCK
                )
            ) {
                world.setBlockMetadataWithNotify(x, y, z, (((direction)|8)), 0x02);
                return false;
            }
            if (!isPowered && eventType == PISTON_EVENT_EXTENDING) {
                return false;
            }
        }
                                                              ;
        switch (eventType) {
            case PISTON_EVENT_EXTENDING: // Extend
                                                           ;
                if (!this.moveBlocks(world, x, y, z, direction, true)) {
                    return false;
                }
                world.setBlockMetadataWithNotify(x, y, z, (((direction)|8)), 0x01 | 0x02 | 0x20 | 0x40);
                world.playSoundEffect((double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, "tile.piston.out", 0.5F, world.rand.nextFloat() * 0.25F + 0.6F);
            default:
                return true;
            case PISTON_EVENT_RETRACTING_NORMAL: case PISTON_EVENT_RETRACTING_DROP_BLOCK: // Retract
                                                                       ;
                int nextX = x + Facing.offsetsXForSide[direction];
                int nextY = y + Facing.offsetsYForSide[direction];
                int nextZ = z + Facing.offsetsZForSide[direction];
                TileEntity tileEntity = world.getBlockTileEntity(nextX, nextY, nextZ);
                if (tileEntity instanceof TileEntityPiston) {
                    ((TileEntityPiston)tileEntity).clearPistonTileEntity();
                }
                // This is the only time that the metadata of the moving block itself
                // and the metadata of the moving block entity are different.
                // This is done specifically so that a non-extended piston will be placed
                // by the moving block but an extended piston will be used for all
                // property tests while the block is moving.
                world.setBlock(x, y, z, Block.pistonMoving.blockID, (((direction)|8)), 0x20);
                world.setBlockTileEntity(x, y, z, BlockPistonMoving.getTileEntity(this.blockID, direction, direction, false, true));
                //world.notifyBlockChange(x, y, z, Block.pistonMoving.blockID);
                //((IWorldMixins)world).updateNeighbourShapes(x, y, z, UPDATE_CLIENTS);
                if (this.isSticky) {
                    int currentX = nextX + Facing.offsetsXForSide[direction];
                    int currentY = nextY + Facing.offsetsYForSide[direction];
                    int currentZ = nextZ + Facing.offsetsZForSide[direction];
                    int nextBlockId = world.getBlockId(currentX, currentY, currentZ);
                    if (nextBlockId == Block.pistonMoving.blockID) {
                        TileEntity nextTileEntity = world.getBlockTileEntity(currentX, currentY, currentZ);
                        if (nextTileEntity instanceof TileEntityPiston) {
                            TileEntityPiston pistonTileEntity = (TileEntityPiston)nextTileEntity;
                            if (
                                pistonTileEntity.getPistonOrientation() == direction &&
                                pistonTileEntity.isExtending()
                            ) {
                                pistonTileEntity.clearPistonTileEntity();
                                break;
                            }
                        }
                    }
                    if (eventType != PISTON_EVENT_RETRACTING_NORMAL) {
                        break;
                    }
                    Block nextBlock = Block.blocksList[nextBlockId];
                    if (
                        !((nextBlock)==null) &&
                        nextBlock.canBlockBePulledByPiston(world, currentX, currentY, currentZ, Block.getOppositeFacing(direction))
                        // What does this block do in modern vanilla?
                        /*&& (
                            nextBlockId == Block.pistonBase.blockID ||
                            nextBlockId == Block.pistonStickyBase.blockID ||
                            block.getMobilityFlag() == PISTON_CAN_PUSH
                        )*/
                    ) {
                        this.moveBlocks(world, x, y, z, direction, false);
                        break;
                    }
                }
                world.setBlockToAir(nextX, nextY, nextZ);
                break;
        }
        world.playSoundEffect((double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, "tile.piston.in", 0.5F, world.rand.nextFloat() * 0.15F + 0.6F);
        return true;
    }
    //@Environment(EnvType.CLIENT)
    //public boolean shouldRenderNeighborHalfSlabSide(IBlockAccess blockAccess, int x, int y, int z, int iNeighborSlabSide, boolean bNeighborUpsideDown) {
        //return !isOpaqueCube();
    //}
    @Environment(EnvType.CLIENT)
    @Override
    public boolean shouldRenderNeighborFullFaceSide(IBlockAccess blockAccess, int x, int y, int z, int direction) {
        return !this.hasLargeCenterHardPointToFacing(blockAccess, x, y, z, direction, true);
    }
}
