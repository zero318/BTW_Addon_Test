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
    public boolean isStickyForBlocks(World world, int X, int Y, int Z, int direction) {
        // Only attach to other platforms.
        // Check is done here to act as a whitelist
        // rather than blacklisting it from canBeStuckTo
        // on everything else.
        return ((PlatformBlock)(Object)this).blockID == world.getBlockId(X + Facing.offsetsXForSide[direction], Y + Facing.offsetsYForSide[direction], Z + Facing.offsetsZForSide[direction]);
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
    public boolean isRedstoneConductor(IBlockAccess block_access, int X, int Y, int Z) {
        return false;
    }
    public int getPlatformMobilityFlag(World world, int X, int Y, int Z) {
        return 1;
    }
    @Overwrite
    public void attemptToLiftBlockWithPlatform(World world, int X, int Y, int Z) {
        int block_id = world.getBlockId(X, Y, Z);
        Block block = Block.blocksList[block_id];
        if (
            !((block)==null) &&
            ((IBlockMixins)block).getPlatformMobilityFlag(world, X, Y, Z) == 2
        ) {
            //PLATFORM_LIFT_DEBUG("Trying to lift "+block_id);
            BlockLiftedByPlatformEntity lifted_entity = (BlockLiftedByPlatformEntity)EntityList.createEntityOfType(
                BlockLiftedByPlatformEntity.class, world,
                (double)X + 0.5D, (double)Y + 0.5D, (double)Z + 0.5D
            );
            lifted_entity.setBlockID(block_id);
            lifted_entity.setBlockMetadata(((IBlockMixins)block).adjustMetadataForPlatformMove(world.getBlockMetadata(X, Y, Z)));
            world.spawnEntityInWorld(lifted_entity);
            world.setBlock(X, Y, Z, 0, 0, 0x01 | 0x02 | 0x08 | 0x20 | 0x40);
        }
    }
}
