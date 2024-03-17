package zero.test;
import net.minecraft.src.*;
import btw.block.BTWBlocks;
import btw.block.blocks.PistonBlockBase;
import btw.block.blocks.PistonBlockMoving;
import btw.item.util.ItemUtils;
import btw.world.util.WorldUtils;
import btw.AddonHandler;
import btw.BTWAddon;
import zero.test.IBlockMixins;
import zero.test.mixin.IPistonBaseAccessMixins;
import zero.test.IWorldMixins;
import zero.test.IBlockEntityPistonMixins;
import zero.test.ZeroUtil;
import zero.test.ZeroMetaUtil;
// Block piston reactions
public class TurntableResolver {
    private static final int TURNTABLE_HEIGHT_LIMIT =
    12;
    // Backwards compatibility hack for existing builds
    private static final int FORCE_ROTATE_HEIGHT = 2;
    private static final int PILLAR_LIST_LENGTH = TURNTABLE_HEIGHT_LIMIT;
    private static final int ATTACHMENT_LIST_LENGTH = PILLAR_LIST_LENGTH * 4;
    private static final int PILLAR_LIST_START_INDEX = 0;
    private static final int ATTACHMENT_LIST_START_INDEX = PILLAR_LIST_START_INDEX + PILLAR_LIST_LENGTH;
    private static final int PILLAR_ID_LIST_LENGTH = PILLAR_LIST_LENGTH;
    private static final int ATTACHMENT_STATE_LIST_LENGTH = ATTACHMENT_LIST_LENGTH;
    private static final int SPIN_OFFSET_LIST_LENGTH = ATTACHMENT_LIST_LENGTH;
    private static final int PILLAR_ID_LIST_START_INDEX = 0;
    private static final int ATTACHMENT_STATE_LIST_START_INDEX = PILLAR_ID_LIST_START_INDEX + PILLAR_ID_LIST_LENGTH;
    private static final int SPIN_OFFSET_LIST_START_INDEX = ATTACHMENT_STATE_LIST_START_INDEX + ATTACHMENT_STATE_LIST_LENGTH;
    private static final int SPIN_OFFSET_LIST_OFFSET = ATTACHMENT_STATE_LIST_LENGTH;
    private static final byte[] spin_offsets = new byte[] {
        -1, -1, // SOUTH_EAST_OFFSETS (North clockwise, West  counterclockwise) 2, 4  (0, 3)
         1, -1, // SOUTH_WEST_OFFSETS (East  clockwise, North counterclockwise) 5, 2  (1, 0)
         1, 1, // NORTH_WEST_OFFSETS (South clockwise, East  counterclockwise) 3, 5  (2, 1)
        -1, 1, // NORTH_EAST_OFFSETS (West  clockwise, South counterclockwise) 4, 3  (3, 2)
        -1, -1, // SOUTH_EAST_OFFSETS (North clockwise, West  counterclockwise) 2, 4  (0, 3) COPIED INDICES
    };
    private final long[] pillar_blocks = new long[PILLAR_LIST_LENGTH + ATTACHMENT_LIST_LENGTH];
    private final long[] data_list = new long[PILLAR_ID_LIST_LENGTH + ATTACHMENT_STATE_LIST_LENGTH + SPIN_OFFSET_LIST_LENGTH];
    private final TileEntity[] tile_entity_list = new TileEntity[ATTACHMENT_STATE_LIST_LENGTH];
    private int max_height;
    private int pillar_index_global;
    private int attachment_index_global;
    private boolean addAttachmentBlock(World world, int x, int y, int z, int direction, boolean sticky) {
        int blockId = world.getBlockId(x, y, z);
        if (sticky) {
            Block block = Block.blocksList[blockId];
            int facing = 2;
            do {
                if (
                    facing != direction &&
                    ((IBlockMixins)block).isStickyForBlocks(world, x, y, z, facing)
                ) {
                    int nextX = x + Facing.offsetsXForSide[facing];
                    int nextY = y + Facing.offsetsYForSide[facing];
                    int nextZ = z + Facing.offsetsZForSide[facing];
                    Block neighborBlock = Block.blocksList[world.getBlockId(nextX, nextY, nextZ)];
                    if (
                        !((neighborBlock)==null) &&
                        neighborBlock.canBlockBePulledByPiston(world, nextX, nextY, nextZ, -1) &&
                        ((IBlockMixins)neighborBlock).canBeStuckTo(world, nextX, nextY, nextZ, facing, blockId)
                    ) {
                        return false;
                    }
                }
            } while (((++facing)<=5));
        }
        pillar_blocks[attachment_index_global] = ((long)(z)<<12 +26|(long)((x)&0x3FFFFFF)<<12|((y)&0xFFF));
        // getNewMetadataRotatedAroundBlockOnTurntableToFacing
        // is an awful function and this is simpler without it.
        data_list[attachment_index_global] = ((long)(((blockId)&0xFFFF)|(world.getBlockMetadata(x, y, z))<<16)|(long)ZeroMetaUtil.getBlockExtMetadata(world, x, y, z)<<32);
        data_list[attachment_index_global + SPIN_OFFSET_LIST_OFFSET] = direction;
        ++attachment_index_global;
        return true;
    }
    private boolean addPillarBlock(World world, int x, int y, int z) {
        int blockId = world.getBlockId(x, y, z);
        Block block = Block.blocksList[blockId];
        if (
            !((block)==null) &&
            block.canRotateOnTurntable(world, x, y, z)
        ) {
            Block neighborBlock;
            int direction = 2;
            do {
                int nextX = x + Facing.offsetsXForSide[direction];
                int nextZ = z + Facing.offsetsZForSide[direction];
                neighborBlock = Block.blocksList[world.getBlockId(nextX, y, nextZ)];
                if (!((neighborBlock)==null)) {
                    boolean sticky = ((IBlockMixins)block).isStickyForBlocks(world, x, y, z, direction) &&
                                     neighborBlock.canBlockBePulledByPiston(world, nextX, y, nextZ, -1) &&
                                     ((IBlockMixins)neighborBlock).canBeStuckTo(world, nextX, y, nextZ, direction, blockId);
                    if (
                        (
                            sticky ||
                            (
                                ((IBlockMixins)block).canTransmitRotationHorizontallyOnTurntable(world, x, y, z, direction) &&
                                neighborBlock.canRotateAroundBlockOnTurntableToFacing(world, nextX, y, nextZ, ((direction)^1))
                            )
                        ) &&
                        neighborBlock.onRotatedAroundBlockOnTurntableToFacing(world, nextX, y, nextZ, ((direction)^1)) &&
                        // Check failure conditions
                        !this.addAttachmentBlock(world, nextX, y, nextZ, ((direction)^1), sticky)
                    ) {
                        return false;
                    }
                }
            } while (((++direction)<=5));
            pillar_blocks[pillar_index_global] = ((long)(z)<<12 +26|(long)((x)&0x3FFFFFF)<<12|((y)&0xFFF));
            data_list[pillar_index_global] = blockId;
            ++pillar_index_global;
            int nextY = y + 1;
            neighborBlock = Block.blocksList[world.getBlockId(x, nextY, z)];
            if (
                !((neighborBlock)==null) &&
                (
                    ((IBlockMixins)block).isStickyForBlocks(world, x, y, z, 1)
                        // If the block is sticky, make sure that it only
                        // turns things it'll actually stick to. Currently
                        // assuming that all sticky blocks can transmit vertically.
                        ? (
                            // Just like in the piston resolver, this filters
                            // out blocks that break when pushed.
                            neighborBlock.canBlockBePulledByPiston(world, x, nextY, z, -1) &&
                            ((IBlockMixins)neighborBlock).canBeStuckTo(world, x, nextY, z, 1, blockId)
                        )
                        // Otherwise most blocks are allowed to rotate if they're
                        // within the forced rotation height.
                        : (
                            pillar_index_global < FORCE_ROTATE_HEIGHT &&
                            block.canTransmitRotationVerticallyOnTurntable(world, x, y, z) &&
                            // Just make sure not to rotate slime in this case
                            !((IBlockMixins)neighborBlock).isStickyForBlocks(world, x, nextY, z, 0)
                        )
                ) &&
                // Check the failure conditions
                (
                    nextY == max_height ||
                    !this.addPillarBlock(world, x, nextY, z)
                )
            ) {
                return false;
            }
        }
        return true;
    }
    public int turnBlocks(World world, int x, int y, int z, boolean reverse, int crafting_counter) {
        pillar_index_global = PILLAR_LIST_START_INDEX;
        attachment_index_global = ATTACHMENT_LIST_START_INDEX;
        max_height = y + TURNTABLE_HEIGHT_LIMIT;
        ++y;
        if (this.addPillarBlock(world, x, y, z)) {
            long packedPos;
            int blockId;
            int blockMeta;
            int blockExtMeta;
            long blockState;
            int tile_entity_index = 0;
            for (int i = attachment_index_global; --i >= ATTACHMENT_LIST_START_INDEX;) {
                packedPos = pillar_blocks[i];
                {(x)=((int)((packedPos)<<26>>(64)-26));(z)=((int)((packedPos)>>(64)-26));(y)=((int)(packedPos)<<(32)-12>>(32)-12);};
                blockId = ((int)(data_list[i])&0xFFFF);
                Block block = Block.blocksList[blockId];
                if (block.hasTileEntity()) {
                    tile_entity_list[tile_entity_index++] = world.getBlockTileEntity(x, y, z);
                    world.removeBlockTileEntity(x, y, z);
                }
                world.setBlock(x, y, z, 0, 0, 0x01 | 0x02);
                if (block.hasComparatorInputOverride()) {
                    world.func_96440_m(x, y, z, blockId);
                }
            }
            for (int i = pillar_index_global; --i >= PILLAR_LIST_START_INDEX;) {
                packedPos = pillar_blocks[i];
                crafting_counter = Block.blocksList[(int)data_list[i]].rotateOnTurntable(world, ((int)((packedPos)<<26>>(64)-26)),((int)(packedPos)<<(32)-12>>(32)-12),((int)((packedPos)>>(64)-26)), reverse, crafting_counter);
            }
            tile_entity_index = 0;
            for (int i = attachment_index_global; --i >= ATTACHMENT_LIST_START_INDEX;) {
                packedPos = pillar_blocks[i];
                {(x)=((int)((packedPos)<<26>>(64)-26));(z)=((int)((packedPos)>>(64)-26));(y)=((int)(packedPos)<<(32)-12>>(32)-12);};
                blockState = data_list[i];
                {(blockId)=((int)(blockState)&0xFFFF);(blockMeta)=((int)(blockState)>>>16);(blockExtMeta)=((int)((blockState)>>>32));};
                Block block = Block.blocksList[blockId];
                int spin_index = (net.minecraft.src.Direction.facingToDirection[(((int)data_list[i + SPIN_OFFSET_LIST_OFFSET])^1)]);
                if (reverse) {
                    ++spin_index;
                }
                spin_index += spin_index;
                int nextX = x + spin_offsets[spin_index];
                int nextZ = z + spin_offsets[spin_index + 1];
                TileEntity tile_entity = null;
                if (block.hasTileEntity()) {
                    tile_entity = tile_entity_list[tile_entity_index];
                    tile_entity_list[tile_entity_index++] = null;
                }
                boolean breakBlock = false;
                Block neighborBlock = Block.blocksList[world.getBlockId(nextX, y, nextZ)];
                if (
                    ((neighborBlock)==null) ||
                    neighborBlock.blockMaterial.isReplaceable() ||
                    (breakBlock = neighborBlock.getMobilityFlag() == 1)
                ) {
                    if (breakBlock) {
                        neighborBlock.onBrokenByPistonPush(world, nextX, y, nextZ, block.adjustMetadataForPistonMove(world.getBlockMetadata(nextX, y, nextZ)));
                    }
                    if (tile_entity != null) {
                        tile_entity.validate();
                        world.setBlockTileEntity(nextX, y, nextZ, tile_entity);
                    }
                    int newMeta = ((IWorldMixins)world).updateFromNeighborShapes(nextX, y, nextZ, blockId, blockMeta);
                    if (newMeta >= 0) {
                        // Fortunately no blocks have important calls in
                        // getNewMetadataRotatedAroundBlockOnTurntableToFacing
                        // that can't be handled by a normal metadata rotation
                        // and most of the side effects of rotateAroundJAxis
                        // are already handled by setting the block itself.
                        // Major exception is mechanical power not snapping axles.
                        ZeroMetaUtil.setBlockWithExtra(world, nextX, y, nextZ, blockId, block.rotateMetadataAroundJAxis(newMeta, ((reverse)^true)), blockExtMeta, 0x01 | 0x02);
                    }
                    else {
                        ZeroMetaUtil.setBlockWithExtra(world, nextX, y, nextZ, blockId, blockMeta, blockExtMeta, 0x04 | 0x10 | 0x40 | 0x80);
                        world.destroyBlock(nextX, y, nextZ, true);
                    }
                }
                else {
                    if (tile_entity != null) {
                        ZeroUtil.break_tile_entity(world, x, y, z, tile_entity);
                    }
                    // This seems like it could be an issue.
                    // Maybe destroy the block instead?
                    block.dropBlockAsItem(world, x, y, z, blockId, blockMeta);
                }
            }
        }
        return crafting_counter;
    }
}
