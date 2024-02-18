package zero.test.block;
import net.minecraft.src.*;
import btw.block.model.BlockModel;
import btw.block.util.RayTraceUtils;
import btw.AddonHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.Random;
import java.util.List;
import zero.test.block.model.ScaffoldingModel;
import zero.test.block.model.ScaffoldingBottomModel;
// Block piston reactions
public class ScaffoldingBlock extends Block {
    public ScaffoldingBlock(int blockId) {
        super(blockId, Material.circuits);
        this.setUnlocalizedName("scaffolding");
        this.setCreativeTab(CreativeTabs.tabTransport);
    }
    public static BlockModel normal_model = (BlockModel)new ScaffoldingModel();
    public static BlockModel bottom_model = (BlockModel)new ScaffoldingBottomModel();
    @Override
    public boolean isBlockClimbable(World world, int x, int y, int z) {
        return true;
    }
    @Override
    public boolean isOpaqueCube() {
        return false;
    }
    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }
    @Override
    public int getMobilityFlag() {
        return 1;
    }
    @Override
    public boolean canMobsSpawnOn(World world, int x, int y, int z) {
        return false;
    }
    @Override
    public boolean isFallingBlock()
    {
        return true;
    }
    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        if (!world.isRemote) {
            world.scheduleBlockUpdate(x, y, z, this.blockID, 1);
        }
    }
    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float clickX, float clickY, float clickZ, int meta) {
        int distance = this.calculateDistance(world, x, y, z);
        return distance | (this.isBottom(world, x, y, z, distance) ? 8 : 0);
    }
    @Override
    public boolean canBlockStay(World world, int x, int y, int z) {
        return (((world.getBlockMetadata(x, y, z))&7)) < 7;
    }
    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        int distance = this.calculateDistance(world, x, y, z);
        if (distance == 7) {
            if ((((world.getBlockMetadata(x, y, z))&7)) == 7) {
                this.checkForFall(world, x, y, z);
            } else {
                world.destroyBlock(x, y, z, true);
            }
        } else {
            world.setBlockMetadataWithNotify(x, y, z, distance | (this.isBottom(world, x, y, z, distance) ? 8 : 0), 0x01 | 0x02);
        }
    }
    public int updateShape(World world, int x, int y, int z, int direction, int meta) {
        if (!world.isRemote) {
            world.scheduleBlockUpdate(x, y, z, this.blockID, 1);
        }
        return meta;
    }
    @Override
    public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int x, int y, int z, int facing, boolean ignoreTransparency) {
        return facing == 1 ||
               (
                   facing == 0 &&
                   ((((blockAccess.getBlockMetadata(x, y, z))>7)))
               );
    }
    private boolean isBottom(World world, int x, int y, int z, int distance) {
        return distance > 0 && world.getBlockId(x, y - 1, z) != this.blockID;
    }
    private int calculateDistance(World world, int x, int y, int z) {
        int blockId = world.getBlockId(x, y - 1, z);
        int distance = 7;
        if (blockId == this.blockID) {
            distance = (((world.getBlockMetadata(x, y - 1, z))&7));
        } else {
            Block block = Block.blocksList[blockId];
            if (
                !((block)==null) &&
                block.hasLargeCenterHardPointToFacing(world, x, y, z, 1, true)
            ) {
                return 0;
            }
        }
        int direction = 2;
        do {
            int nextX = x + Facing.offsetsXForSide[direction];
            int nextZ = z + Facing.offsetsZForSide[direction];
            if (world.getBlockId(nextX, y, nextZ) == this.blockID) {
                distance = Math.min((((world.getBlockMetadata(nextX, y, nextZ))&7)) + 1, distance);
            }
        } while (distance != 1 && ((++direction)<=5));
        return distance;
    }
    @Override
    public boolean canRotateOnTurntable(IBlockAccess blockAccess, int x, int y, int z) {
        return true;
    }
    @Override
    public boolean canTransmitRotationVerticallyOnTurntable(IBlockAccess blockAccess, int x, int y, int z) {
        return true;
    }
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
    }
    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB intersectBox, List list, Entity entity) {
        if (entity != null) {
            AABBPool pool = AxisAlignedBB.getAABBPool();
            double dX = (double)x;
            double dY = (double)y;
            double dZ = (double)z;
            if (
                entity.boundingBox.minY >= dY + 1.0D &&
                !entity.isSneaking()
            ) {
                pool.getAABB(dX, dY, dZ, dX + 1.0D, dY + 1.0D, dZ + 1.0D).addToListIfIntersects(intersectBox, list);
            }
            else if (
                // Metadata checks DISTANCE >= 1 && HAS_BOTTOM
                world.getBlockMetadata(x, y, z) >= 9 &&
                entity.boundingBox.minY >= dY + 0.125D
            ) {
                pool.getAABB(dX, dY, dZ, dX + 1.0D, dY + 0.125D, dZ + 1.0D).addToListIfIntersects(intersectBox, list);
            }
        }
    }
    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderBlocks, int x, int y, int z) {
        return (((((renderBlocks.blockAccess.getBlockMetadata(x, y, z))>7))) ? bottom_model : normal_model).renderAsBlockWithColorMultiplier(renderBlocks, this, x, y, z);
    }
    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderBlocks, int damage, float brightness) {
        normal_model.renderAsItemBlock(renderBlocks, this, damage);
    }
}
