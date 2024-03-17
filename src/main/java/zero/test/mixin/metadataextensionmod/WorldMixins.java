package zero.test.mixin.metadataextensionmod;
import net.minecraft.src.*;
import btw.community.arminias.metadata.extension.ChunkExtension;
import btw.community.arminias.metadata.extension.WorldExtension;
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
// Block piston reactions
@Mixin(
    value = World.class,
    priority = 1100
)
public abstract class WorldMixins
// Hopefully this will clobber the mixin from the other base mod...
implements WorldExtension
{
    @Override
    public boolean setBlockWithExtra(int x, int y, int z, int blockId, int meta, int flags, int extMeta) {
        if ((((((Integer.compareUnsigned(((y))-(0),(255)-(0)))<=0))) && (((((Integer.compareUnsigned((((x)))-(-30000000),(29999999)-(-30000000)))<=0))) && ((((Integer.compareUnsigned((((z)))-(-30000000),(29999999)-(-30000000)))<=0)))))) {
            World world = (World)(Object)this;
            Chunk chunk = world.getChunkFromChunkCoords(x >> 4, z >> 4);
            int currentBlockId = 0;
            if (
                (flags & 0x01) != 0
            ) {
                currentBlockId = chunk.getBlockID(x & 0xF, y, z & 0xF);
            }
            boolean blockChanged = ((ChunkExtension)chunk).setBlockIDWithMetadataAndExtraMetadata(x & 0xF, y, z & 0xF, blockId, meta, extMeta);
            if ((flags & 0x80) == 0) {
                world.theProfiler.startSection("checkLight");
                world.updateAllLightTypes(x, y, z);
                world.theProfiler.endSection();
            }
            if (blockChanged) {
                if (
                    (flags & 0x02) != 0 &&
                    (
                        !world.isRemote ||
                        (flags & 0x04) == 0
                    )
                ) {
                    world.markBlockForUpdate(x, y, z);
                }
                if (
                    !world.isRemote &&
                    (flags & 0x01) != 0
                ) {
                    ((IWorldMixins)this).notifyBlockChangeAndComparators(x, y, z, blockId, currentBlockId);
                }
                if (
                    (flags & 0x10) == 0
                ) {
                    //block = Block.blocksList[currentBlockId];
                    //if (!BLOCK_IS_AIR(block)) {
                        //((IBlockMixins)block).updateIndirectNeighbourShapes(world, x, y, z);
                    //}
                    ((IWorldMixins)this).updateNeighbourShapes(x, y, z, flags & ~(0x01 | 0x20));
                    //block = Block.blocksList[blockId];
                    //if (!BLOCK_IS_AIR(block)) {
                        //((IBlockMixins)block).updateIndirectNeighbourShapes(world, x, y, z);
                    //}
                }
            }
            return blockChanged;
        }
        return false;
    }
    @Override
    public boolean setBlockMetadataAndExtraWithNotify(int x, int y, int z, int meta, int flags, int extMeta) {
        if ((((((Integer.compareUnsigned(((y))-(0),(255)-(0)))<=0))) && (((((Integer.compareUnsigned((((x)))-(-30000000),(29999999)-(-30000000)))<=0))) && ((((Integer.compareUnsigned((((z)))-(-30000000),(29999999)-(-30000000)))<=0)))))) {
            World world = (World)(Object)this;
            Chunk chunk = world.getChunkFromChunkCoords(x >> 4, z >> 4);
            boolean blockChanged = chunk.setBlockMetadata(x & 0xF, y, z & 0xF, meta) |
                                   ((ChunkExtension)chunk).setBlockExtraMetadata(x & 0xF, y, z & 0xF, extMeta);
            // Should this be enabled?
            if (blockChanged) {
                if (
                    (flags & 0x02) != 0 &&
                    (
                        !world.isRemote ||
                        (flags & 0x04) == 0
                    )
                ) {
                    world.markBlockForUpdate(x, y, z);
                }
                if (
                    !world.isRemote &&
                    (flags & 0x01) != 0
                ) {
                    int currentBlockId = chunk.getBlockID(x & 0xF, y, z & 0xF);
                    ((IWorldMixins)this).notifyBlockChangeAndComparators(x, y, z, currentBlockId, currentBlockId);
                }
                if (
                    (flags & 0x10) == 0
                ) {
                    //if (!BLOCK_IS_AIR(block)) {
                        //((IBlockMixins)block).updateIndirectNeighbourShapes(world, x, y, z);
                    //}
                    ((IWorldMixins)this).updateNeighbourShapes(x, y, z, flags & ~(0x01 | 0x20));
                }
            }
            return blockChanged;
        }
        return false;
    }
    @Override
    public boolean setBlockExtraMetadataWithNotify(int x, int y, int z, int extMeta, int flags) {
        if ((((((Integer.compareUnsigned(((y))-(0),(255)-(0)))<=0))) && (((((Integer.compareUnsigned((((x)))-(-30000000),(29999999)-(-30000000)))<=0))) && ((((Integer.compareUnsigned((((z)))-(-30000000),(29999999)-(-30000000)))<=0)))))) {
            World world = (World)(Object)this;
            Chunk chunk = world.getChunkFromChunkCoords(x >> 4, z >> 4);
            boolean blockChanged = ((ChunkExtension)chunk).setBlockExtraMetadata(x & 0xF, y, z & 0xF, extMeta);
            // Should this be enabled?
            if (blockChanged) {
                if (
                    (flags & 0x02) != 0 &&
                    (
                        !world.isRemote ||
                        (flags & 0x04) == 0
                    )
                ) {
                    world.markBlockForUpdate(x, y, z);
                }
                if (
                    !world.isRemote &&
                    (flags & 0x01) != 0
                ) {
                    int currentBlockId = chunk.getBlockID(x & 0xF, y, z & 0xF);
                    ((IWorldMixins)this).notifyBlockChangeAndComparators(x, y, z, currentBlockId, currentBlockId);
                }
                if (
                    (flags & 0x10) == 0
                ) {
                    //if (!BLOCK_IS_AIR(block)) {
                        //((IBlockMixins)block).updateIndirectNeighbourShapes(world, x, y, z);
                    //}
                    ((IWorldMixins)this).updateNeighbourShapes(x, y, z, flags & ~(0x01 | 0x20));
                }
            }
            return blockChanged;
        }
        return false;
    }
}
