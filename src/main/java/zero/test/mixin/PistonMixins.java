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
@Mixin(PistonBlockBase.class)
public abstract class PistonMixins extends BlockPistonBase {
    public boolean hasLargeCenterHardPointToFacing(IBlockAccess block_access, int X, int Y, int Z, int direction, boolean ignore_transparency) {
        int meta = block_access.getBlockMetadata(X, Y, Z);
        return !((((meta)>7))) || ((direction)^1) == (((meta)&7));
    }
    public PistonMixins(int block_id, boolean is_sticky) {
        super(block_id, is_sticky);
    }
    @Shadow
    protected abstract int getPistonShovelEjectionDirection(World world, int X, int Y, int Z, int direction);
    @Shadow
    protected abstract void onShovelEjectIntoBlock(World world, int X, int Y, int Z);
    @Overwrite
    public boolean canExtend(World world, int X, int Y, int Z, int direction) {
        int pushes_remaining = 12;
        do {
            X += Facing.offsetsXForSide[direction];
            Y += Facing.offsetsYForSide[direction];
            Z += Facing.offsetsZForSide[direction];
            if (!((((Integer.compareUnsigned(((Y))-(0),(255)-(0)))<=0)))) {
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
                next_block.getMobilityFlag() == 1 ||
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
        invalid_block: do {
            int pushes_remaining = 12;
            do {
                nextX += Facing.offsetsXForSide[direction];
                nextY += Facing.offsetsYForSide[direction];
                nextZ += Facing.offsetsZForSide[direction];
                if (!((((Integer.compareUnsigned(((nextY))-(0),(255)-(0)))<=0)))) {
                    return false;
                }
                int next_block_id = world.getBlockId(nextX, nextY, nextZ);
                Block next_block = Block.blocksList[next_block_id];
                if (next_block == null) {
                    break invalid_block;
                }
                if (!next_block.canBlockBePushedByPiston(world, nextX, nextY, nextZ, direction)) {
                    return false;
                }
                if (next_block.getMobilityFlag() == 1) {
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
                    world.setBlock(tempX, tempY, tempZ, Block.pistonMoving.blockID, shoveling_meta, 0x04);
                    world.setBlockTileEntity(tempX, tempY, tempZ, PistonBlockMoving.getShoveledTileEntity(next_block_id, shoveling_meta, shovel_direction));
                    break;
                }
            } while (--pushes_remaining >= 0);
            world.setBlockToAir(nextX, nextY, nextZ);
        } while(false);
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
                world.setBlock(currentX, currentY, currentZ, Block.pistonMoving.blockID, direction | (this.isSticky ? 8 : 0), 0x04);
                world.setBlockTileEntity(currentX, currentY, currentZ, BlockPistonMoving.getTileEntity(Block.pistonExtension.blockID, direction | (this.isSticky ? 8 : 0), direction, true, false));
            } else {
                int moving_meta = world.getBlockMetadata(tempX, tempY, tempZ);
                Block moving_block = Block.blocksList[moving_block_id];
                if (moving_block != null) {
                    moving_meta = moving_block.adjustMetadataForPistonMove(moving_meta);
                }
                world.setBlock(currentX, currentY, currentZ, Block.pistonMoving.blockID, moving_meta, 0x04);
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
    private static final int PISTON_PUSH_LIMIT =
    12;
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
    private static long[] pushed_blocks = new long[SHOVEL_LIST_LENGTH + PUSH_LIST_LENGTH + DESTROY_LIST_LENGTH];
    private static int[] data_list = new int[SHOVEL_BLOCK_ID_LIST_LENGTH + PUSH_BLOCK_ID_LIST_LENGTH + DESTROY_BLOCK_ID_LIST_LENGTH + SHOVEL_DIRECTION_LIST_LENGTH + SHOVEL_BLOCK_META_LIST_LENGTH];
    private static long piston_position;
    private static int push_index_global;
    private static int shovel_index_global;
    private static int destroy_index_global;
    protected boolean add_branch(World world, int X, int Y, int Z, int direction) {
        int block_id = world.getBlockId(X, Y, Z);
        Block block = Block.blocksList[block_id];
        AddonHandler.logMessage("Branching Block ("+X+" "+Y+" "+Z+")"+direction);
        for (int facing = 0; facing < 6; ++facing) {
            AddonHandler.logMessage("Facing/Direction ("+facing+"/"+direction+") ("+((facing)&~1)+"/"+((direction)&~1)+")");
            if (((facing)&~1) != ((direction)&~1)) {
                AddonHandler.logMessage("Adding branch "+facing);
                if (((IBlockMixins)block).isStickyForBlocks(world, X, Y, Z, facing)) {
                    int nextX = X + Facing.offsetsXForSide[facing];
                    int nextY = Y + Facing.offsetsYForSide[facing];
                    int nextZ = Z + Facing.offsetsZForSide[facing];
                    Block neighbor_block = Block.blocksList[world.getBlockId(nextX, nextY, nextZ)];
                    if (
                        !((neighbor_block)==null) &&
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
    protected boolean add_moved_block(World world, int X, int Y, int Z, int direction) {
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
                ((IBlockMixins)block).getMobilityFlag(world, X, Y, Z) != 4 &&
                !block.canBlockBePulledByPiston(world, X, Y, Z, direction)
            )
        ) {
            return true;
        }
        AddonHandler.logMessage("SearchForExistingPush ("+X+" "+Y+" "+Z+")");
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
        int next_block_id = block_id;
        Block next_block = block;
        for(;;) {
            AddonHandler.logMessage("StickySearch ("+nextX+" "+nextY+" "+nextZ+")");
            if (++push_write_index == (PUSH_LIST_START_INDEX + PUSH_LIST_LENGTH)) {
                AddonHandler.logMessage("Push failed limit reached A");
                return false;
            }
            if (!((IBlockMixins)next_block).isStickyForBlocks(world, nextX, nextY, nextZ, direction)) {
                AddonHandler.logMessage("IsSticky false");
                break;
            }
            AddonHandler.logMessage("IsSticky true");
            int tempX = nextX - Facing.offsetsXForSide[direction];
            int tempY = nextY - Facing.offsetsYForSide[direction];
            int tempZ = nextZ - Facing.offsetsZForSide[direction];
            if (((long)(tempZ)<<12 +26|(long)((tempX)&0x3FFFFFF)<<12|((tempY)&0xFFF)) == piston_position) {
                break;
            }
            block_id = world.getBlockId(tempX, tempY, tempZ);
            next_block = Block.blocksList[block_id];
            if (
                ((next_block)==null) ||
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
        do {
            AddonHandler.logMessage("AddPush ("+nextX+" "+nextY+" "+nextZ+")");
            data_list[push_index] = next_block_id;
            pushed_blocks[push_index++] = ((long)(nextZ)<<12 +26|(long)((nextX)&0x3FFFFFF)<<12|((nextY)&0xFFF));
            nextX += Facing.offsetsXForSide[direction];
            nextY += Facing.offsetsYForSide[direction];
            nextZ += Facing.offsetsZForSide[direction];
            next_block_id = world.getBlockId(nextX, nextY, nextZ);
        } while (push_index != push_write_index);
        for(;;) {
            push_index_global = push_index;
            AddonHandler.logMessage("New push index "+push_index_global);
            AddonHandler.logMessage("ParsePush ("+nextX+" "+nextY+" "+nextZ+")");
            packed_pos = ((long)(nextZ)<<12 +26|(long)((nextX)&0x3FFFFFF)<<12|((nextY)&0xFFF));
            for (int i = PUSH_LIST_START_INDEX; i < push_index; ++i) {
                if (pushed_blocks[i] == packed_pos) {
                    AddonHandler.logMessage("Hacky swap code");
                    int swap_max = gcd(push_index - PUSH_LIST_START_INDEX, push_count) + i;
                    for (int j = i; j < swap_max; ++j) {
                        int k = j;
                        block_id = data_list[j];
                        packed_pos = pushed_blocks[j];
                        for(;;) {
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
                        {(X)=(int)((packed_pos)<<26>>(64)-26);(Z)=(int)((packed_pos)>>(64)-26);(Y)=(int)(packed_pos)<<(32)-12>>(32)-12;};
                        block_id = data_list[j];
                        block = Block.blocksList[block_id];
                        if (
                            !((block)==null) &&
                            !this.add_branch(world, X, Y, Z, direction)
                        ) {
                            AddonHandler.logMessage("Push failed move block branching ("+X+" "+Y+" "+Z+")");
                            return false;
                        }
                    }
                    return true;
                }
            }
            next_block = Block.blocksList[next_block_id];
            if (((next_block)==null)) {
                return true;
            }
            if (
                packed_pos == piston_position ||
                !next_block.canBlockBePushedByPiston(world, nextX, nextY, nextZ, direction)
            ) {
                AddonHandler.logMessage("Push failed move block IDK ("+nextX+" "+nextY+" "+nextZ+")");
                return false;
            }
            if (next_block.getMobilityFlag() == 1) {
                data_list[destroy_index_global] = next_block_id;
                pushed_blocks[destroy_index_global++] = packed_pos;
                return true;
            }
            if (next_block.canBePistonShoveled(world, nextX, nextY, nextZ)) {
                int eject_direction = block.getPistonShovelEjectDirection(world, X, Y, Z, direction);
                if (eject_direction >= 0) {
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
                            int i = shovel_index_global;
                            while (--i >= SHOVEL_LIST_START_INDEX) {
                                if (pushed_blocks[i] == packed_pos) {
                                    break block_is_shoveled;
                                }
                            }
                            data_list[shovel_index_global + SHOVEL_BLOCK_ID_LIST_START_INDEX] = next_block_id;
                            data_list[shovel_index_global + SHOVEL_DIRECTION_LIST_START_INDEX] = eject_direction;
                            data_list[shovel_index_global + SHOVEL_BLOCK_META_LIST_START_INDEX] = next_block.adjustMetadataForPistonMove(world.getBlockMetadata(nextX, nextY, nextZ));
                            world.setBlock(nextX, nextY, nextZ, 0, 0, 0x04);
                            pushed_blocks[shovel_index_global++] = packed_pos;
                            return true;
                        } while(false);
                    }
                }
            }
            if (push_index == (PUSH_LIST_START_INDEX + PUSH_LIST_LENGTH)) {
                AddonHandler.logMessage("Push failed limit reached B");
                return false;
            }
            data_list[push_index] = next_block_id;
            pushed_blocks[push_index++] = packed_pos;
            ++push_count;
            X = nextX;
            Y = nextY;
            Z = nextZ;
            nextX += Facing.offsetsXForSide[direction];
            nextY += Facing.offsetsYForSide[direction];
            nextZ += Facing.offsetsZForSide[direction];
            block = next_block;
            next_block_id = world.getBlockId(nextX, nextY, nextZ);
        }
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
                AddonHandler.logMessage("Resolve destroy ("+X+" "+Y+" "+Z+")");
                data_list[destroy_index_global] = block_id;
                pushed_blocks[destroy_index_global++] = packed_pos;
                return true;
            }
            AddonHandler.logMessage("Push failed immobile "+block_id+"("+X+" "+Y+" "+Z+")");
            return false;
        }
        AddonHandler.logMessage("Resolve add blocks ("+X+" "+Y+" "+Z+")");
        if (!this.add_moved_block(world, X, Y, Z, direction)) {
            AddonHandler.logMessage("Push failed move block");
            return false;
        }
        for (int i = PUSH_LIST_START_INDEX; i < push_index_global; ++i) {
            packed_pos = pushed_blocks[i];
            {(X)=(int)((packed_pos)<<26>>(64)-26);(Z)=(int)((packed_pos)>>(64)-26);(Y)=(int)(packed_pos)<<(32)-12>>(32)-12;};
            block_id = data_list[i];
            block = Block.blocksList[block_id];
            if (
                !this.add_branch(world, X, Y, Z, direction)
            ) {
                AddonHandler.logMessage("Push failed branching");
                return false;
            }
        }
        return true;
    }
    public boolean moveBlocks(World world, int X, int Y, int Z, int direction, boolean is_extending) {
        piston_position = ((long)(Z)<<12 +26|(long)((X)&0x3FFFFFF)<<12|((Y)&0xFFF));
        shovel_index_global = SHOVEL_LIST_START_INDEX;
        push_index_global = PUSH_LIST_START_INDEX;
        destroy_index_global = DESTROY_LIST_START_INDEX;
        X += Facing.offsetsXForSide[direction];
        Y += Facing.offsetsYForSide[direction];
        Z += Facing.offsetsZForSide[direction];
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
        Block block;
        int i = destroy_index_global;
        AddonHandler.logMessage("DestroyIndex "+i);
        while (--i >= DESTROY_LIST_START_INDEX) {
            packed_pos = pushed_blocks[i];
            {(X)=(int)((packed_pos)<<26>>(64)-26);(Z)=(int)((packed_pos)>>(64)-26);(Y)=(int)(packed_pos)<<(32)-12>>(32)-12;};
            block_id = data_list[i];
            block = Block.blocksList[block_id];
            block_meta = world.getBlockMetadata(X, Y, Z);
            AddonHandler.logMessage("Destroy "+block_id+"."+block_meta+"("+X+" "+Y+" "+Z+")");
            block.onBrokenByPistonPush(world, X, Y, Z, block_meta);
            world.setBlock(X, Y, Z, 0, 0, 0x08 | 0x10);
        }
        i = push_index_global;
        AddonHandler.logMessage("PushIndex "+i);
        while (--i >= PUSH_LIST_START_INDEX) {
            packed_pos = pushed_blocks[i];
            {(X)=(int)((packed_pos)<<26>>(64)-26);(Z)=(int)((packed_pos)>>(64)-26);(Y)=(int)(packed_pos)<<(32)-12>>(32)-12;};
            block_id = data_list[i];
            block = Block.blocksList[block_id];
            block_meta = world.getBlockMetadata(X, Y, Z);
            AddonHandler.logMessage("Push "+block_id+"."+block_meta+"("+X+" "+Y+" "+Z+")");
            NBTTagCompound tile_entity_data = getBlockTileEntityData(world, X, Y, Z);
            world.removeBlockTileEntity(X, Y, Z);
            packed_pos = ((long)(Z + Facing.offsetsZForSide[direction])<<12 +26|(long)((X + Facing.offsetsXForSide[direction])&0x3FFFFFF)<<12|((Y + Facing.offsetsYForSide[direction])&0xFFF));
            coord_will_move: do {
                for (int j = 0; j < i; ++j) {
                    if (pushed_blocks[j] == packed_pos) {
                        break coord_will_move;
                    }
                }
                world.setBlock(X, Y, Z, 0, 0, 0x02 | 0x80);
            } while(false);
            X += Facing.offsetsXForSide[direction];
            Y += Facing.offsetsYForSide[direction];
            Z += Facing.offsetsZForSide[direction];
                block_meta = block.adjustMetadataForPistonMove(block_meta);
            world.setBlock(X, Y, Z, Block.pistonMoving.blockID, block_meta, 0x08 | 0x20 | 0x40);
            world.setBlockTileEntity(X, Y, Z, BlockPistonMoving.getTileEntity(block_id, block_meta, direction, true, false));
            if (tile_entity_data != null) {
                ((TileEntityPiston)world.getBlockTileEntity(X, Y, Z)).storeTileEntity(tile_entity_data);
            }
        }
        if (is_extending) {
            AddonHandler.logMessage("PistonHead "+Block.pistonMoving.blockID+"."+(direction | (this.isSticky ? 8 : 0))+"("+headX+" "+headY+" "+headZ+")");
            world.setBlock(headX, headY, headZ, Block.pistonMoving.blockID, direction | (this.isSticky ? 8 : 0), 0x08 | 0x20 | 0x40);
   world.setBlockTileEntity(headX, headY, headZ, BlockPistonMoving.getTileEntity(Block.pistonExtension.blockID, direction | (this.isSticky ? 8 : 0), direction, true, false));
        }
        i = shovel_index_global;
        while (--i >= SHOVEL_LIST_START_INDEX) {
            packed_pos = pushed_blocks[i];
            {(X)=(int)((packed_pos)<<26>>(64)-26);(Z)=(int)((packed_pos)>>(64)-26);(Y)=(int)(packed_pos)<<(32)-12>>(32)-12;};
            block_id = world.getBlockId(X, Y, Z);
            block = Block.blocksList[block_id];
            block_id = data_list[i + SHOVEL_BLOCK_ID_LIST_START_INDEX];
            int eject_direction = data_list[i + SHOVEL_DIRECTION_LIST_START_INDEX];
            block_meta = data_list[i + SHOVEL_BLOCK_META_LIST_START_INDEX];
            if (
                ((block)==null) ||
                block.getMobilityFlag() == 1
            ) {
                onShovelEjectIntoBlock(world, X, Y, Z);
                world.setBlock(X, Y, Z, Block.pistonMoving.blockID, block_meta, 0x04);
                world.setBlockTileEntity(X, Y, Z, PistonBlockMoving.getShoveledTileEntity(block_id, block_meta, eject_direction));
            } else if (!world.isRemote) {
                block = Block.blocksList[block_id];
                if (!((block)==null)) {
                    int item_id = block.idDropped(block_meta, world.rand, 0);
                    if (item_id != 0) {
                        ItemUtils.ejectStackFromBlockTowardsFacing(
                            world,
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
        i = destroy_index_global;
        while (--i >= DESTROY_LIST_START_INDEX) {
            packed_pos = pushed_blocks[i];
            world.notifyBlocksOfNeighborChange((int)((packed_pos)<<26>>(64)-26),(int)(packed_pos)<<(32)-12>>(32)-12,(int)((packed_pos)>>(64)-26), data_list[i]);
        }
        i = push_index_global;
        while (--i >= PUSH_LIST_START_INDEX) {
            packed_pos = pushed_blocks[i];
            world.notifyBlocksOfNeighborChange((int)((packed_pos)<<26>>(64)-26),(int)(packed_pos)<<(32)-12>>(32)-12,(int)((packed_pos)>>(64)-26), data_list[i]);
        }
        if (is_extending) {
            world.notifyBlocksOfNeighborChange(headX, headY, headZ, Block.pistonExtension.blockID);
        }
        return true;
    }
    private static final int PISTON_EVENT_EXTENDING = 0;
    private static final int PISTON_EVENT_RETRACTING = 1;
    private static final int PISTON_EVENT_IDK = 2;
    protected void updatePistonState(World world, int X, int Y, int Z) {
        int meta = world.getBlockMetadata(X, Y, Z);
        int direction = (((meta)&7));
        if (direction != 7) {
            boolean is_powered = ((IPistonBaseAccessMixins)(Object)this).callIsIndirectlyPowered(world, X, Y, Z, direction);
            if (is_powered != ((((meta)>7)))) {
                if (is_powered) {
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
                        )
                    ) {
                        world.addBlockEvent(X, Y, Z, this.blockID, PISTON_EVENT_IDK, direction);
                    } else {
                        world.addBlockEvent(X, Y, Z, this.blockID, PISTON_EVENT_RETRACTING, direction);
                    }
                }
            }
        }
    }
    public boolean onBlockEventReceived(World world, int X, int Y, int Z, int event_type, int direction) {
        AddonHandler.logMessage("===== PistonCoords ("+X+" "+Y+" "+Z+")");
        if (!world.isRemote) {
            boolean is_powered = ((IPistonBaseAccessMixins)(Object)this).callIsIndirectlyPowered(world, X, Y, Z, direction);
            if (
                is_powered &&
                (
                    event_type == PISTON_EVENT_RETRACTING ||
                    event_type == PISTON_EVENT_IDK
                )
            ) {
                world.setBlockMetadataWithNotify(X, Y, Z, (((direction)|8)), 0x02);
                return false;
            }
            if (!is_powered && event_type == PISTON_EVENT_EXTENDING) {
                return false;
            }
        }
        switch (event_type) {
            default:
                return true;
            case PISTON_EVENT_EXTENDING:
                if (!this.moveBlocks(world, X, Y, Z, direction, true)) {
                    return false;
                }
                world.setBlockMetadataWithNotify(X, Y, Z, (((direction)|8)), 0x01 | 0x02 | 0x20 | 0x40);
                world.playSoundEffect((double)X + 0.5D, (double)Y + 0.5D, (double)Z + 0.5D, "tile.piston.out", 0.5F, world.rand.nextFloat() * 0.25F + 0.6F);
                return true;
            case PISTON_EVENT_RETRACTING: case PISTON_EVENT_IDK:
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
                        if (event_type == PISTON_EVENT_RETRACTING) {
                            Block next_block = Block.blocksList[next_block_id];
                            if (
                                !((next_block)==null) &&
                                next_block.canBlockBePulledByPiston(world, currentX, currentY, currentZ, Block.getOppositeFacing(direction))
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
