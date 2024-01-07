package zero.test.mixin;
import net.minecraft.src.*;
import btw.AddonHandler;
import btw.block.BTWBlocks;
import btw.block.blocks.PlatformBlock;
import btw.block.blocks.AnchorBlock;
import btw.entity.mechanical.platform.BlockLiftedByPlatformEntity;
import btw.entity.mechanical.platform.MovingAnchorEntity;
import btw.entity.mechanical.platform.MovingPlatformEntity;
import btw.block.tileentity.PulleyTileEntity;
import btw.item.util.ItemUtils;
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
import zero.test.IMovingPlatformEntityMixins;
import zero.test.mixin.IAnchorBlockAccessMixins;
import java.util.Random;
// Block piston reactions
@Mixin(AnchorBlock.class)
public class AnchorBlockMixins {
    private static final int PLATFORM_LIFT_LIMIT =
    25;
    private static final int PLATFORM_LIST_LENGTH = PLATFORM_LIFT_LIMIT;
    // IDK how to properly calculate this, so just add a lot of extra space?
    private static final int LIFT_LIST_LENGTH = PLATFORM_LIFT_LIMIT * 5;
    private static final int PLATFORM_LIST_START_INDEX = 0;
    private static final int LIFT_LIST_START_INDEX = PLATFORM_LIST_START_INDEX + PLATFORM_LIST_LENGTH;
    private static final int PLATFORM_STATE_LIST_LENGTH = PLATFORM_LIST_LENGTH;
    private static final int LIFT_STATE_LIST_LENGTH = LIFT_LIST_LENGTH;
    private static final int PLATFORM_STATE_LIST_START_INDEX = 0;
    private static final int LIFT_STATE_LIST_START_INDEX = PLATFORM_STATE_LIST_START_INDEX + PLATFORM_STATE_LIST_LENGTH;
    private static final long[] platform_blocks = new long[PLATFORM_LIST_LENGTH + LIFT_LIST_LENGTH];
    private static final int[] data_list = new int[PLATFORM_STATE_LIST_LENGTH + LIFT_STATE_LIST_LENGTH];
    private static long anchor_position;
    private static int platform_index_global;
    private static int lift_index_global;
    protected boolean addPlatformBlock(World world, int x, int y, int z, boolean isExtending) {
        int blockId = world.getBlockId(x, y, z);
        Block block = Block.blocksList[blockId];
        long packedPos;
        if (
            ((block)==null) ||
            (packedPos = ((long)(z)<<12 +26|(long)((x)&0x3FFFFFF)<<12|((y)&0xFFF))) == anchor_position ||
            ((IBlockMixins)block).getPlatformMobilityFlag(world, x, y, z) != 1
        ) {
                                      ;
            return true;
        }
                                                                            ;
        for (int i = platform_index_global; --i >= PLATFORM_LIST_START_INDEX;) {
            if (platform_blocks[i] == packedPos) {
                return true;
            }
        }
        if (platform_index_global == (PLATFORM_LIST_START_INDEX + PLATFORM_LIST_LENGTH)) {
                                                                  ;
            return false;
        }
        platform_blocks[platform_index_global] = packedPos;
        data_list[platform_index_global] = (((blockId)&0xFFFF)|((world.getBlockMetadata(x, y, z)))<<16);
        ++platform_index_global;
        int move_direction = isExtending ? 0 : 1;
        int facing = 0;
        do {
            int nextX = x + Facing.offsetsXForSide[facing];
            int nextY = y + Facing.offsetsYForSide[facing];
            int nextZ = z + Facing.offsetsZForSide[facing];
            int neighborId = world.getBlockId(nextX, nextY, nextZ);
            Block neighborBlock = Block.blocksList[neighborId];
            if (!((neighborBlock)==null)) {
                packedPos = ((long)(nextZ)<<12 +26|(long)((nextX)&0x3FFFFFF)<<12|((nextY)&0xFFF));
                switch (((IBlockMixins)neighborBlock).getPlatformMobilityFlag(world, nextX, nextY, nextZ)) {
                    case 1:
                                                                                               ;
                        if (!this.addPlatformBlock(world, nextX, nextY, nextZ, isExtending)) {
                            return false;
                        }
                        break;
                    case 0:
                                                                                                     ;
                        if (
                            packedPos == anchor_position ||
                            !(
                                ((IBlockMixins)block).isStickyForBlocks(world, x, y, z, facing) &&
                                ((IBlockMixins)neighborBlock).canBeStuckTo(world, nextX, nextY, nextZ, facing, blockId) &&
                                !(
                                    ((IBlockMixins)neighborBlock).getMobilityFlag(world, nextX, nextY, nextZ) != 4 &&
                                    !neighborBlock.canBlockBePulledByPiston(world, nextX, nextY, nextZ, move_direction)
                                ) &&
                                (
                                    facing >= 2 ||
                                    (
                                        facing == move_direction
                                            ? neighborBlock.canBlockBePushedByPiston(world, nextX, nextY, nextZ, facing)
                                            : neighborBlock.canBlockBePulledByPiston(world, nextX, nextY, nextZ, ((facing)^1))
                                    )
                                )
                            )
                        ) {
                            break;
                        }
                    case 2: {
                                                                                             ;
                        if (
                            facing == 1 ||
                            ((IBlockMixins)block).isStickyForBlocks(world, x, y, z, facing)
                        ) {
                            int i = lift_index_global;
                            do {
                                if (--i < LIFT_LIST_START_INDEX) {
                                    platform_blocks[lift_index_global] = packedPos;
                                    data_list[lift_index_global] = (((neighborId)&0xFFFF)|((world.getBlockMetadata(nextX, nextY, nextZ)))<<16);
                                    ++lift_index_global;
                                    break;
                                }
                            } while (platform_blocks[i] != packedPos);
                        }
                        break;
                    }
                }
            }
        } while (++facing < 6);
        return true;
    }
    @Overwrite(remap=false)
    public void convertConnectedPlatformsToEntities(World world, int x, int y, int z, MovingAnchorEntity associatedAnchorEntity) {
                                           ;
        anchor_position = ((long)(z)<<12 +26|(long)((x)&0x3FFFFFF)<<12|((y)&0xFFF));
        platform_index_global = PLATFORM_LIST_START_INDEX;
        lift_index_global = LIFT_LIST_START_INDEX;
        --y;
        if (this.addPlatformBlock(world, x, y, z, associatedAnchorEntity.motionY < 0.0D)) {
            long packedPos;
                                                                       ;
            for (int i = platform_index_global; --i >= PLATFORM_LIST_START_INDEX;) {
                packedPos = platform_blocks[i];
                {(x)=((int)((packedPos)<<26>>(64)-26));(z)=((int)((packedPos)>>(64)-26));(y)=((int)(packedPos)<<(32)-12>>(32)-12);};
                MovingPlatformEntity moving_entity = (MovingPlatformEntity)EntityList.createEntityOfType(
                    MovingPlatformEntity.class, world,
                    (float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F,
                    associatedAnchorEntity
                );
                int blockId;
                int blockMeta;
                int blockState = data_list[i];
                {(blockId)=((blockState)&0xFFFF);(blockMeta)=((blockState)>>>16);};
                ((IMovingPlatformEntityMixins)moving_entity).setBlockId(blockId);
                ((IMovingPlatformEntityMixins)moving_entity).setBlockMeta(blockMeta);
                world.spawnEntityInWorld(moving_entity);
                world.setBlock(x, y, z, 0, 0, 0x02 | 0x08 | 0x20 | 0x40);
            }
                                                               ;
            for (int i = lift_index_global; --i >= LIFT_LIST_START_INDEX;) {
                packedPos = platform_blocks[i];
                {(x)=((int)((packedPos)<<26>>(64)-26));(z)=((int)((packedPos)>>(64)-26));(y)=((int)(packedPos)<<(32)-12>>(32)-12);};
                int blockId;
                int blockMeta;
                int blockState = data_list[i];
                {(blockId)=((blockState)&0xFFFF);(blockMeta)=((blockState)>>>16);};
                Block block = Block.blocksList[blockId];
                BlockLiftedByPlatformEntity lifted_entity = (BlockLiftedByPlatformEntity)EntityList.createEntityOfType(
                    BlockLiftedByPlatformEntity.class, world,
                    (double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D
                );
                lifted_entity.setBlockID(blockId);
                lifted_entity.setBlockMetadata(((IBlockMixins)block).adjustMetadataForPlatformMove(blockMeta));
                world.spawnEntityInWorld(lifted_entity);
                world.setBlock(x, y, z, 0, 0, 0x02 | 0x08 | 0x20 | 0x40);
            }
            for (int i = lift_index_global; --i >= LIFT_LIST_START_INDEX;) {
                // Set x,y,z to position of block in lift list
                packedPos = platform_blocks[i];
                world.notifyBlocksOfNeighborChange(((int)((packedPos)<<26>>(64)-26)),((int)(packedPos)<<(32)-12>>(32)-12),((int)((packedPos)>>(64)-26)), ((data_list[i])&0xFFFF));
                //world.setBlock(BLOCK_POS_UNPACK_ARGS(packedPos), 0, 0, UPDATE_NEIGHBORS | UPDATE_CLIENTS | UPDATE_IMMEDIATE | UPDATE_SUPPRESS_DROPS | UPDATE_MOVE_BY_PISTON);
            }
            for (int i = platform_index_global; --i >= PLATFORM_LIST_START_INDEX;) {
                // Set x,y,z to position of block in platform list
                packedPos = platform_blocks[i];
                world.notifyBlocksOfNeighborChange(((int)((packedPos)<<26>>(64)-26)),((int)(packedPos)<<(32)-12>>(32)-12),((int)((packedPos)>>(64)-26)), ((data_list[i])&0xFFFF));
                //world.setBlock(BLOCK_POS_UNPACK_ARGS(packedPos), 0, 0, UPDATE_NEIGHBORS | UPDATE_CLIENTS | UPDATE_IMMEDIATE | UPDATE_SUPPRESS_DROPS | UPDATE_MOVE_BY_PISTON);
            }
        }
        /*
    	--y;
        
        Block target_block = Block.blocksList[world.getBlockId(X, Y, Z)];
        if (
            !BLOCK_IS_AIR(target_block) &&
            ((IBlockMixins)target_block).getPlatformMobilityFlag(world, X, Y, Z) == PLATFORM_MAIN_SUPPORT
        ) {
            ((PlatformBlock)BTWBlocks.platform).covertToEntitiesFromThisPlatform(
				world,
                X, Y, Z,
                associatedAnchorEntity
            );
        }
        */
    }
    @Overwrite(remap=false)
    public boolean notifyAnchorBlockOfAttachedPulleyStateChange(PulleyTileEntity tileEntityPulley, World world, int X, int Y, int Z){
  int iMovementDirection = 0;
  if (tileEntityPulley.isRaising()) {
   if (world.getBlockId(X, Y + 1, Z) == BTWBlocks.ropeBlock.blockID) {
    iMovementDirection = 1;
   }
  }
  else if (tileEntityPulley.isLowering()) {
            Block block = Block.blocksList[world.getBlockId(X, Y - 1, Z)];
            if (
                ((block)==null) ||
                ((IBlockMixins)block).getPlatformMobilityFlag(world, X, Y - 1, Z) == 1
            ) {
                iMovementDirection = -1;
            }
  }
  if (iMovementDirection != 0) {
   ((IAnchorBlockAccessMixins)this).callConvertAnchorToEntity(world, X, Y, Z, tileEntityPulley, iMovementDirection);
   return true;
  }
  return false;
 }
}
