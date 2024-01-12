package zero.test.mixin;
import net.minecraft.src.*;
import btw.AddonHandler;
import btw.block.blocks.PlatformBlock;
import btw.entity.mechanical.platform.BlockLiftedByPlatformEntity;
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
import zero.test.IBlockMixins;
import java.util.Random;
// Block piston reactions
@Mixin(PlatformBlock.class)
public class PlatformBlockMixins {
    public boolean isStickyForBlocks(World world, int x, int y, int z, int direction) {
        // Only attach to other platforms.
        // Check is done here to act as a whitelist
        // rather than blacklisting it from canBeStuckTo
        // on everything else.
        return ((PlatformBlock)(Object)this).blockID == world.getBlockId(x + Facing.offsetsXForSide[direction], y + Facing.offsetsYForSide[direction], z + Facing.offsetsZForSide[direction]);
    }
    // Platforms override isNormalBlock to true
    // for some dang reason, which makes the renderer
    // think that redstone dust can connect when it
    // shouldn't.
    // 
    // This happens because World::isBlockNormalCube
    // chains through Block::isNormalCube (thus the override)
    // but ChunkCache::isBlockNormalCube directly tests block
    // properties and avoids the override.
    public boolean isRedstoneConductor(IBlockAccess block_access, int x, int y, int z) {
        return false;
    }
    public int getPlatformMobilityFlag(World world, int x, int y, int z) {
        return 1;
    }
    @Overwrite
    public void attemptToLiftBlockWithPlatform(World world, int x, int y, int z) {
        int blockId = world.getBlockId(x, y, z);
        Block block = Block.blocksList[blockId];
        if (
            !((block)==null) &&
            ((IBlockMixins)block).getPlatformMobilityFlag(world, x, y, z) == 2
        ) {
            //PLATFORM_LIFT_DEBUG("Trying to lift "+blockId);
            BlockLiftedByPlatformEntity liftedEntity = (BlockLiftedByPlatformEntity)EntityList.createEntityOfType(
                BlockLiftedByPlatformEntity.class, world,
                (double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D
            );
            liftedEntity.setBlockID(blockId);
            liftedEntity.setBlockMetadata(((IBlockMixins)block).adjustMetadataForPlatformMove(world.getBlockMetadata(x, y, z)));
            world.spawnEntityInWorld(liftedEntity);
            world.setBlock(x, y, z, 0, 0, 0x01 | 0x02 | 0x08 | 0x20 | 0x40);
        }
    }
}
