package zero.test.mixin;
import net.minecraft.src.Block;
import net.minecraft.src.World;
import net.minecraft.src.BlockRedstoneLogic;
import net.minecraft.src.BlockComparator;
import net.minecraft.src.*;
import btw.AddonHandler;
import btw.BTWAddon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.Overwrite;
import zero.test.IBlockMixins;
import zero.test.IWorldMixins;

@Mixin(World.class)
public class WorldMixins implements IWorldMixins {
    @Overwrite
    public void func_96440_m(int X, int Y, int Z, int neighbor_id) {
        World world = (World)(Object)this;
        for (int i = 0; i < 4; ++i) {
            int nextX = X + Direction.offsetX[i];
            int nextZ = Z + Direction.offsetZ[i];
            int block_id = world.getBlockId(nextX, Y, nextZ);
            if (block_id != 0) {
                Block block_instance = Block.blocksList[block_id];
                if (((IBlockMixins)block_instance).getWeakChanges(world, nextX, Y, nextZ, neighbor_id)) {
                    block_instance.onNeighborBlockChange(world, nextX, Y, nextZ, neighbor_id);
                }
                else if (Block.isNormalCube(block_id)) {
                    nextX += Direction.offsetX[i];
                    nextZ += Direction.offsetZ[i];
                    block_id = world.getBlockId(nextX, Y, nextZ);
                    block_instance = Block.blocksList[block_id];
                    if (
                        !((block_instance)==null) &&
                        ((IBlockMixins)block_instance).getWeakChanges(world, nextX, Y, nextZ, neighbor_id)
                    ) {
                        block_instance.onNeighborBlockChange(world, nextX, Y, nextZ, neighbor_id);
                    }
                }
            }
        }
    }
    public void updateNeighbourShapes(int X, int Y, int Z, int flags) {
        World world = (World)(Object)this;
        IBlockMixins neighbor_block;
        boolean allow_drops = (flags & 0x20) == 0;
        flags &= ~0x20;
        int neighbor_meta;
        int new_meta;
        --X;
        neighbor_block = (IBlockMixins)Block.blocksList[world.getBlockId(X, Y, Z)];
        if (!((neighbor_block)==null)) {
            neighbor_meta = world.getBlockMetadata(X, Y, Z);
            new_meta = neighbor_block.updateShape(world, X, Y, Z, 5, neighbor_meta);
            if (new_meta != neighbor_meta) {
                if (new_meta >= 0) {
                    world.setBlockMetadataWithNotify(X, Y, Z, flags);
                } else {
                    world.destroyBlock(X, Y, Z, allow_drops);
                }
            }
        }
        X += 2;
        neighbor_block = (IBlockMixins)Block.blocksList[world.getBlockId(X, Y, Z)];
        if (!((neighbor_block)==null)) {
            neighbor_meta = world.getBlockMetadata(X, Y, Z);
            new_meta = neighbor_block.updateShape(world, X, Y, Z, 4, neighbor_meta);
            if (new_meta != neighbor_meta) {
                if (new_meta >= 0) {
                    world.setBlockMetadataWithNotify(X, Y, Z, flags);
                } else {
                    world.destroyBlock(X, Y, Z, allow_drops);
                }
            }
        }
        --X;
        --Z;
        neighbor_block = (IBlockMixins)Block.blocksList[world.getBlockId(X, Y, Z)];
        if (!((neighbor_block)==null)) {
            neighbor_meta = world.getBlockMetadata(X, Y, Z);
            new_meta = neighbor_block.updateShape(world, X, Y, Z, 3, neighbor_meta);
            if (new_meta != neighbor_meta) {
                if (new_meta >= 0) {
                    world.setBlockMetadataWithNotify(X, Y, Z, flags);
                } else {
                    world.destroyBlock(X, Y, Z, allow_drops);
                }
            }
        }
        Z += 2;
        neighbor_block = (IBlockMixins)Block.blocksList[world.getBlockId(X, Y, Z)];
        if (!((neighbor_block)==null)) {
            neighbor_meta = world.getBlockMetadata(X, Y, Z);
            new_meta = neighbor_block.updateShape(world, X, Y, Z, 2, neighbor_meta);
            if (new_meta != neighbor_meta) {
                if (new_meta >= 0) {
                    world.setBlockMetadataWithNotify(X, Y, Z, flags);
                } else {
                    world.destroyBlock(X, Y, Z, allow_drops);
                }
            }
        }
        --Z;
        --Y;
        neighbor_block = (IBlockMixins)Block.blocksList[world.getBlockId(X, Y, Z)];
        if (!((neighbor_block)==null)) {
            neighbor_meta = world.getBlockMetadata(X, Y, Z);
            new_meta = neighbor_block.updateShape(world, X, Y, Z, 1, neighbor_meta);
            if (new_meta != neighbor_meta) {
                if (new_meta >= 0) {
                    world.setBlockMetadataWithNotify(X, Y, Z, flags);
                } else {
                    world.destroyBlock(X, Y, Z, allow_drops);
                }
            }
        }
        Y += 2;
        neighbor_block = (IBlockMixins)Block.blocksList[world.getBlockId(X, Y, Z)];
        if (!((neighbor_block)==null)) {
            neighbor_meta = world.getBlockMetadata(X, Y, Z);
            new_meta = neighbor_block.updateShape(world, X, Y, Z, 0, neighbor_meta);
            if (new_meta != neighbor_meta) {
                if (new_meta >= 0) {
                    world.setBlockMetadataWithNotify(X, Y, Z, flags);
                } else {
                    world.destroyBlock(X, Y, Z, allow_drops);
                }
            }
        }
    }
    public int updateFromNeighborShapes(int X, int Y, int Z, int block_id, int meta) {
        IBlockMixins block = (IBlockMixins)Block.blocksList[block_id];
        if (!((block)==null)) {
            World world = (World)(Object)this;
            meta = block.updateShape(world, X, Y, Z, 4, meta);
            meta = block.updateShape(world, X, Y, Z, 5, meta);
            meta = block.updateShape(world, X, Y, Z, 2, meta);
            meta = block.updateShape(world, X, Y, Z, 3, meta);
            meta = block.updateShape(world, X, Y, Z, 0, meta);
            meta = block.updateShape(world, X, Y, Z, 1, meta);
        }
        return meta;
    }
    @Overwrite
    public boolean setBlock(int X, int Y, int Z, int block_id, int meta, int flags) {
        if ((((((Integer.compareUnsigned(((Y))-(0),(255)-(0)))<=0))) && (((((Integer.compareUnsigned((((X)))-(-30000000),(29999999)-(-30000000)))<=0))) && ((((Integer.compareUnsigned((((Z)))-(-30000000),(29999999)-(-30000000)))<=0)))))) {
            World world = (World)(Object)this;
            Chunk chunk = world.getChunkFromChunkCoords(X >> 4, Z >> 4);
            int current_block_id = 0;
            if (
                (flags & 0x01) != 0
            ) {
                current_block_id = chunk.getBlockID(X & 0xF, Y, Z & 0xF);
            }
            boolean block_changed = chunk.setBlockIDWithMetadata(X & 0xF, Y, Z & 0xF, block_id, meta);
            if ((flags & 0x80) == 0) {
                world.theProfiler.startSection("checkLight");
                world.updateAllLightTypes(X, Y, Z);
                world.theProfiler.endSection();
            }
            if (block_changed) {
                if (
                    (flags & 0x02) != 0 &&
                    (
                        !world.isRemote ||
                        (flags & 0x04) == 0
                    )
                ) {
                    world.markBlockForUpdate(X, Y, Z);
                }
                if (
                    !world.isRemote &&
                    (flags & 0x01) != 0
                ) {
                    world.notifyBlockChange(X, Y, Z, current_block_id);
                    Block block = Block.blocksList[block_id];
                    if (
                        !((block)==null) &&
                        block.hasComparatorInputOverride()
                    ) {
                        world.func_96440_m(X, Y, Z, block_id);
                    }
                }
                if (
                    (flags & 0x10) == 0
                ) {
                    this.updateNeighbourShapes(X, Y, Z, flags & ~(0x01 | 0x20));
                }
            }
            return block_changed;
        }
        return false;
    }
    @Overwrite
    public boolean setBlockMetadataWithNotify(int X, int Y, int Z, int meta, int flags) {
        if ((((((Integer.compareUnsigned(((Y))-(0),(255)-(0)))<=0))) && (((((Integer.compareUnsigned((((X)))-(-30000000),(29999999)-(-30000000)))<=0))) && ((((Integer.compareUnsigned((((Z)))-(-30000000),(29999999)-(-30000000)))<=0)))))) {
            World world = (World)(Object)this;
            Chunk chunk = world.getChunkFromChunkCoords(X >> 4, Z >> 4);
            boolean block_changed = chunk.setBlockMetadata(X & 0xF, Y, Z & 0xF, meta);
            if (block_changed) {
                if (
                    (flags & 0x02) != 0 &&
                    (
                        !world.isRemote ||
                        (flags & 0x04) == 0
                    )
                ) {
                    world.markBlockForUpdate(X, Y, Z);
                }
                if (
                    !world.isRemote &&
                    (flags & 0x01) != 0
                ) {
                    int current_block_id = chunk.getBlockID(X & 0xF, Y, Z & 0xF);
                    world.notifyBlockChange(X, Y, Z, current_block_id);
                    Block block = Block.blocksList[current_block_id];
                    if (
                        !((block)==null) &&
                        block.hasComparatorInputOverride()
                    ) {
                        world.func_96440_m(X, Y, Z, current_block_id);
                    }
                }
                if (
                    (flags & 0x10) == 0
                ) {
                    this.updateNeighbourShapes(X, Y, Z, flags & ~(0x01 | 0x20));
                }
            }
            return block_changed;
        }
        return false;
    }
}
