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
@Mixin(PistonBlockBase.class)
public abstract class PistonMixins extends BlockPistonBase {
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
    private static int push_index_global;
    private static int shovel_index_global;
    private static int destroy_index_global;
    private static long piston_position;
    private static long[] pushed_blocks = new long[12 +12 +12];
    private static int[] shovel_data_list = new int[12 +12 +12];
    protected boolean add_branch(World world, int X, int Y, int Z, int direction) {
        int block_id = world.getBlockId(X, Y, Z);
        Block block = Block.blocksList[block_id];
                                                                     ;
        for (int facing = 0; facing < 6; ++facing) {
                                                                                                                                  ;
            if (((facing)&~1) != ((direction)&~1)) {
                                                     ;
                if (((IBlockMixins)block).isSticky(X, Y, Z, facing)) {
                    int nextX = X + Facing.offsetsXForSide[facing];
                    int nextY = Y + Facing.offsetsYForSide[facing];
                    int nextZ = Z + Facing.offsetsZForSide[facing];
                    Block neighbor_block = Block.blocksList[world.getBlockId(nextX, nextY, nextZ)];
                    if (
                        !((neighbor_block)==null) &&
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
        if (!((((Integer.compareUnsigned(((Y))-(0),(255)-(0)))<=0)))) {
            return false;
        }
        int block_id = world.getBlockId(X, Y, Z);
        Block block = Block.blocksList[block_id];
        long packed_pos;
        if (
            ((block)==null) ||
            (packed_pos = ((long)(Z)<<12 +26|(long)((X)&0x3FFFFFF)<<12|((Y)&0xFFF))) == piston_position ||
            !block.canBlockBePulledByPiston(world, X, Y, Z, direction)
        ) {
            return true;
        }
                                                                 ;
        int push_index = push_index_global;
        for (int i = 0; i < push_index; ++i) {
            if (pushed_blocks[i] == packed_pos) {
                return true;
            }
        }
        int push_write_index = push_index;
        int nextX = X;
        int nextY = Y;
        int nextZ = Z;
        Block next_block = block;
        for(;;) {
                                                                        ;
            if (++push_write_index == 12) {
                                                           ;
                return false;
            }
            if (!((IBlockMixins)next_block).isSticky(nextX, nextY, nextZ, direction)) {
                                              ;
                break;
            }
                                         ;
            int tempX = nextX - Facing.offsetsXForSide[direction];
            int tempY = nextY - Facing.offsetsYForSide[direction];
            int tempZ = nextZ - Facing.offsetsZForSide[direction];
            packed_pos = ((long)(tempZ)<<12 +26|(long)((tempX)&0x3FFFFFF)<<12|((tempY)&0xFFF));
            if (packed_pos == piston_position) {
                break;
            }
            int prev_block_id = block_id;
            block_id = world.getBlockId(tempX, tempY, tempZ);
            next_block = Block.blocksList[block_id];
            if (
                ((next_block)==null) ||
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
        do {
                                                                   ;
            pushed_blocks[push_index++] = ((long)(nextZ)<<12 +26|(long)((nextX)&0x3FFFFFF)<<12|((nextY)&0xFFF));
            nextX += Facing.offsetsXForSide[direction];
            nextY += Facing.offsetsYForSide[direction];
            nextZ += Facing.offsetsZForSide[direction];
        } while (push_index != push_write_index);
        for(;;) {
            push_index_global = push_index;
                                                             ;
            nextX = X + Facing.offsetsXForSide[direction];
            nextY = Y + Facing.offsetsYForSide[direction];
            nextZ = Z + Facing.offsetsZForSide[direction];
                                                                     ;
            packed_pos = ((long)(nextZ)<<12 +26|(long)((nextX)&0x3FFFFFF)<<12|((nextY)&0xFFF));
            for (int i = 0; i < push_index; ++i) {
                if (pushed_blocks[i] == packed_pos) {
                                                   ;
                    int swap_max = gcd(push_index, push_count) + i;
                    for (int j = i; j < swap_max; ++j) {
                        int k = j;
                        packed_pos = pushed_blocks[j];
                        for(;;) {
                            int d = (k + push_count) % push_index;
                            if (d == j) {
                                break;
                            }
                            pushed_blocks[k] = pushed_blocks[d];
                            k = d;
                        }
                        pushed_blocks[k] = packed_pos;
                    }
                    for (int j = 0; j < i + push_count; ++j) {
                        packed_pos = pushed_blocks[j];
                        {(nextX)=(int)((packed_pos)<<26>>(64)-26);(nextZ)=(int)((packed_pos)>>(64)-26);(nextY)=(int)(packed_pos)<<(32)-12>>(32)-12;};
                        block_id = world.getBlockId(nextX, nextY, nextZ);
                        block = Block.blocksList[block_id];
                        if (
                            !((block)==null) &&
                            !this.add_branch(world, nextX, nextY, nextZ, direction)
                        ) {
                                                                                                            ;
                            return false;
                        }
                    }
                    return true;
                }
            }
            block_id = world.getBlockId(nextX, nextY, nextZ);
            next_block = Block.blocksList[block_id];
            if (((next_block)==null)) {
                return true;
            }
            packed_pos = ((long)(nextZ)<<12 +26|(long)((nextX)&0x3FFFFFF)<<12|((nextY)&0xFFF));
            if (
                packed_pos == piston_position ||
                !next_block.canBlockBePushedByPiston(world, nextX, nextY, nextZ, direction)
            ) {
                                                                                          ;
                return false;
            }
            if (next_block.getMobilityFlag() == 1) {
                pushed_blocks[destroy_index_global++] = packed_pos;
                return true;
            }
            if (next_block.canBePistonShoveled(world, nextX, nextY, nextZ)) {
                int eject_direction = block.getPistonShovelEjectDirection(world, X, Y, Z, direction);
                if (eject_direction >= 0) {
                    X = nextX + Facing.offsetsXForSide[eject_direction];
                    Y = nextY + Facing.offsetsYForSide[eject_direction];
                    Z = nextZ + Facing.offsetsZForSide[eject_direction];
                    block = Block.blocksList[world.getBlockId(X, Y, Z)];
                    if (
                        ((block)==null) ||
                        block.getMobilityFlag() == 1
                    ) {
                        packed_pos = ((long)(Z)<<12 +26|(long)((X)&0x3FFFFFF)<<12|((Y)&0xFFF));
                        block_is_shovelled: do {
                            int i = shovel_index_global;
                            while (--i >= 12) {
                                if (pushed_blocks[i] == packed_pos) {
                                    break block_is_shovelled;
                                }
                            }
                            shovel_data_list[shovel_index_global-12] = eject_direction;
                            shovel_data_list[shovel_index_global] = block_id;
                            shovel_data_list[shovel_index_global+12] = next_block.adjustMetadataForPistonMove(world.getBlockMetadata(nextX, nextY, nextZ));
                            world.setBlock(nextX, nextY, nextZ, 0, 0, 0x04);
                            pushed_blocks[shovel_index_global++] = packed_pos;
                            return true;
                        } while(false);
                    }
                }
            }
            if (push_index == 12) {
                                                           ;
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
        if (((block)==null)) {
            return true;
        }
        long packed_pos = ((long)(Z)<<12 +26|(long)((X)&0x3FFFFFF)<<12|((Y)&0xFFF));
        if (!(is_extending
            ? block.canBlockBePushedByPiston(world, X, Y, Z, direction)
            : block.canBlockBePulledByPiston(world, X, Y, Z, direction)
        )) {
            if (
                is_extending &&
                block.getMobilityFlag() == 1
            ) {
                                                                   ;
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
        for (int i = 0; i < push_index_global; ++i) {
            packed_pos = pushed_blocks[i];
            {(X)=(int)((packed_pos)<<26>>(64)-26);(Z)=(int)((packed_pos)>>(64)-26);(Y)=(int)(packed_pos)<<(32)-12>>(32)-12;};
            block_id = world.getBlockId(X, Y, Z);
            block = Block.blocksList[block_id];
            if (
                !((block)==null) &&
                !this.add_branch(world, X, Y, Z, direction)
            ) {
                                                     ;
                return false;
            }
        }
        return true;
    }
    public boolean moveBlocks(World world, int X, int Y, int Z, int direction, boolean is_extending) {
        piston_position = ((long)(Z)<<12 +26|(long)((X)&0x3FFFFFF)<<12|((Y)&0xFFF));
        push_index_global = 0;
        shovel_index_global = 12;
        destroy_index_global = 12 +12;
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
                                       ;
        while (--i >= 12 +12) {
            packed_pos = pushed_blocks[i];
            {(X)=(int)((packed_pos)<<26>>(64)-26);(Z)=(int)((packed_pos)>>(64)-26);(Y)=(int)(packed_pos)<<(32)-12>>(32)-12;};
            block_id = world.getBlockId(X, Y, Z);
            block = Block.blocksList[block_id];
            block_meta = world.getBlockMetadata(X, Y, Z);
                                                                                  ;
            block.onBrokenByPistonPush(world, X, Y, Z, block_meta);
            world.setBlockToAir(X, Y, Z);
        }
        i = push_index_global;
                                    ;
        while (--i >= 0) {
            packed_pos = pushed_blocks[i];
            {(X)=(int)((packed_pos)<<26>>(64)-26);(Z)=(int)((packed_pos)>>(64)-26);(Y)=(int)(packed_pos)<<(32)-12>>(32)-12;};
            block_id = world.getBlockId(X, Y, Z);
            block = Block.blocksList[block_id];
            block_meta = world.getBlockMetadata(X, Y, Z);
                                                                               ;
            NBTTagCompound tile_entity_data = getBlockTileEntityData(world, X, Y, Z);
            world.removeBlockTileEntity(X, Y, Z);
            packed_pos = ((long)(Z + Facing.offsetsZForSide[direction])<<12 +26|(long)((X + Facing.offsetsXForSide[direction])&0x3FFFFFF)<<12|((Y + Facing.offsetsYForSide[direction])&0xFFF));
            coord_will_move: do {
                for (int j = 0; j < i; ++j) {
                    if (pushed_blocks[j] == packed_pos) {
                        break coord_will_move;
                    }
                }
                world.setBlockToAir(X, Y, Z);
            } while(false);
            X += Facing.offsetsXForSide[direction];
            Y += Facing.offsetsYForSide[direction];
            Z += Facing.offsetsZForSide[direction];
            if (!((block)==null)) {
                block_meta = block.adjustMetadataForPistonMove(block_meta);
            }
            world.setBlock(X, Y, Z, Block.pistonMoving.blockID, block_meta, 0x04);
            world.setBlockTileEntity(X, Y, Z, BlockPistonMoving.getTileEntity(block_id, block_meta, direction, true, false));
            if (tile_entity_data != null) {
                ((TileEntityPiston)world.getBlockTileEntity(X, Y, Z)).storeTileEntity(tile_entity_data);
            }
        }
        if (is_extending) {
                                                                                                                                              ;
            world.setBlock(nextX, nextY, nextZ, Block.pistonMoving.blockID, direction | (this.isSticky ? 8 : 0), 0x04);
   world.setBlockTileEntity(nextX, nextY, nextZ, BlockPistonMoving.getTileEntity(Block.pistonExtension.blockID, direction | (this.isSticky ? 8 : 0), direction, true, false));
        }
        i = shovel_index_global;
        while (--i >= 12) {
            packed_pos = pushed_blocks[i];
            {(X)=(int)((packed_pos)<<26>>(64)-26);(Z)=(int)((packed_pos)>>(64)-26);(Y)=(int)(packed_pos)<<(32)-12>>(32)-12;};
            block_id = world.getBlockId(X, Y, Z);
            block = Block.blocksList[block_id];
            nextX = shovel_data_list[i-12];
            block_id = shovel_data_list[i];
            block_meta = shovel_data_list[i+12];
            if (
                ((block)==null) ||
                block.getMobilityFlag() == 1
            ) {
                onShovelEjectIntoBlock(world, X, Y, Z);
                world.setBlock(X, Y, Z, Block.pistonMoving.blockID, block_meta, 0x04);
                world.setBlockTileEntity(X, Y, Z, PistonBlockMoving.getShoveledTileEntity(block_id, block_meta, nextX));
            } else if (!world.isRemote) {
                block = Block.blocksList[block_id];
                if (!((block)==null)) {
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
        i = destroy_index_global;
        while (--i >= 12 +12) {
            packed_pos = pushed_blocks[i];
            {(X)=(int)((packed_pos)<<26>>(64)-26);(Z)=(int)((packed_pos)>>(64)-26);(Y)=(int)(packed_pos)<<(32)-12>>(32)-12;};
            block_id = world.getBlockId(X, Y, Z);
            world.notifyBlocksOfNeighborChange(X, Y, Z, block_id);
        }
        i = push_index_global;
        while (--i >= 0) {
            packed_pos = pushed_blocks[i];
            {(X)=(int)((packed_pos)<<26>>(64)-26);(Z)=(int)((packed_pos)>>(64)-26);(Y)=(int)(packed_pos)<<(32)-12>>(32)-12;};
            block_id = world.getBlockId(X, Y, Z);
            world.notifyBlocksOfNeighborChange(X, Y, Z, block_id);
        }
        return true;
    }
    public boolean onBlockEventReceived(World world, int X, int Y, int Z, int par5, int direction) {
                                                              ;
        if (!world.isRemote) {
            boolean is_powered = ((IPistonBaseMixins)(Object)this).callIsIndirectlyPowered(world, X, Y, Z, direction);
            if (is_powered && (par5 == 1 || par5 == 2)) {
                world.setBlockMetadataWithNotify(X, Y, Z, direction | 8, 0x02);
                return false;
            }
            if (!is_powered && par5 == 0) {
                return false;
            }
        }
        switch (par5) {
            default:
                return true;
            case 0:
                if (!this.moveBlocks(world, X, Y, Z, direction, true)) {
                    return false;
                }
                world.setBlockMetadataWithNotify(X, Y, Z, direction | 8, 0x02);
                world.playSoundEffect((double)X + 0.5D, (double)Y + 0.5D, (double)Z + 0.5D, "tile.piston.out", 0.5F, world.rand.nextFloat() * 0.25F + 0.6F);
                return true;
            case 1: case 2:
                int nextX = X + Facing.offsetsXForSide[direction];
                int nextY = Y + Facing.offsetsYForSide[direction];
                int nextZ = Z + Facing.offsetsZForSide[direction];
                TileEntity tile_entity = world.getBlockTileEntity(nextX, nextY, nextZ);
                if (tile_entity instanceof TileEntityPiston) {
                    ((TileEntityPiston)tile_entity).clearPistonTileEntity();
                }
                world.setBlock(X, Y, Z, Block.pistonMoving.blockID, direction, (0x01 | 0x02));
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
                        if (par5 == 1) {
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
