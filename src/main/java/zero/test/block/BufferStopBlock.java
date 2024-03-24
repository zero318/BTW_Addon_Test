package zero.test.block;
import net.minecraft.src.*;
import btw.block.model.BlockModel;
import btw.block.util.RayTraceUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.List;
import zero.test.block.model.BufferStopModel;
// Yes, this is offset 3 just so it can share
// preprocessor code with rail powered state
public class BufferStopBlock extends Block {
    public BufferStopBlock(int blockId) {
        super(blockId, Material.circuits);
        this.setHardness(2.0F);
        this.setAxesEffectiveOn();
        this.setStepSound(Block.soundWoodFootstep);
        this.setUnlocalizedName("buffer_stop");
        this.setCreativeTab(CreativeTabs.tabTransport);
    }
    public static BufferStopModel model;
    public static BlockModel transformedModel;
    static {
        transformedModel = model = new BufferStopModel();
    }
    @Override
    public int getMobilityFlag() {
        return 0;
    }
    @Override
    public boolean isOpaqueCube() {
        return false;
    }
    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }
    public boolean isRedstoneConductor(IBlockAccess blockAccess, int x, int y, int z) {
        return true;
    }
/*
#if ENABLE_BETTER_REDSTONE_WIRE_CONNECTIONS
    public boolean canRedstoneConnectToSide(IBlockAccess blockAccess, int x, int y, int z, int flatDirection) {
        return true;
    }
#endif
*/
    public int getPlatformMobilityFlag(World world, int x, int y, int z) {
        return 2;
    }
    @Override
    public int getRenderType() {
        return 10;
    }
    @Override
 public boolean canRotateOnTurntable(IBlockAccess blockAccess, int x, int y, int z) {
  return true;
 }
    @Override
    public int rotateMetadataAroundJAxis(int meta, boolean reverse) {
        return (((meta)&12|(meta + (reverse ? 1 : -1) & 3)));
    }
/*
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving entity, ItemStack itemStack) {
        world.setBlockMetadataWithNotify(x, y, z, YAW_FLAT_DIRECTION(entity.rotationYaw), UPDATE_CLIENTS);
    }
*/
    @Override
    public int preBlockPlacedBy(World world, int x, int y, int z, int meta, EntityLiving entity) {
        return ((int)MathHelper.floor_double((double)(entity.rotationYaw)*0.01111111111111111111111111111111D+0.5D)&3);
    }
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborId) {
        int meta = world.getBlockMetadata(x, y, z);
        if (((((meta)>7))) != world.isBlockIndirectlyGettingPowered(x, y, z)) {
            world.setBlockMetadataWithNotify(x, y, z, ((meta)^8), 0x01 | 0x02);
        }
    }
    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB intersectBox, List list, Entity entity) {
        AABBPool pool = AxisAlignedBB.getAABBPool();
        double dX = (double)x;
        double dY = (double)y;
        double dZ = (double)z;
        double dX2 = dX + 1.0D;
        double dZ2 = dZ + 1.0D;
        pool.getAABB(dX, dY, dZ, dX2, dY + 0.5625D, dZ2).addToListIfIntersects(intersectBox, list);
        switch ((((world.getBlockMetadata(x, y, z))&3))) {
            case 0:
                dZ -= 0.125D;
                dZ2 -= 0.5D;
                break;
            case 1:
                dX += 0.5D;
                dX2 += 0.125D;
                break;
            case 2:
                dZ += 0.5D;
                dZ2 += 0.125D;
                break;
            default: // FLAT_DIRECTION_WEST
                dX -= 0.125D;
                dX2 -= 0.5D;
                break;
        }
        pool.getAABB(dX, dY + 0.5625D, dZ, dX2, dY + 1.0D, dZ2).addToListIfIntersects(intersectBox, list);
    }
    @Override
    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 startRay, Vec3 endRay) {
        RayTraceUtils rayTrace = new RayTraceUtils(world, x, y, z, startRay, endRay);
        getTransformedModelForMetadata((((net.minecraft.src.Direction.directionToFacing[(((world.getBlockMetadata(x, y, z))&3))])^1))).addToRayTrace(rayTrace);
        return rayTrace.getFirstIntersection();
    }
    private BlockModel getTransformedModelForMetadata(int direction) {
        (transformedModel = model.makeTemporaryCopy()).rotateAroundYToFacing(direction);
        return transformedModel;
    }
    @Environment(EnvType.CLIENT)
    private Icon bottomTexture;
    @Environment(EnvType.CLIENT)
    private Icon bumperTexture;
    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        this.blockIcon = Block.planks.getIcon(0, 0);
        this.bottomTexture = Block.stoneDoubleSlab.getBlockTextureFromSide(1);
        this.bumperTexture = Block.cauldron.func_94375_b("cauldron_inner");
    }
    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int side, int meta) {
        switch (transformedModel.getActivePrimitiveID()) {
            case 0:
                return this.bottomTexture;
            case 1:
                return this.blockIcon;
            default:
                return this.bumperTexture;
        }
    }
    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int neighborX, int neighborY, int neighborZ, int side) {
        return currentBlockRenderer.shouldSideBeRenderedBasedOnCurrentBounds(neighborX, neighborY, neighborZ, side);
    }
    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks renderBlocks, int x, int y, int z) {
        return getTransformedModelForMetadata((((net.minecraft.src.Direction.directionToFacing[(((renderBlocks.blockAccess.getBlockMetadata(x, y, z))&3))])^1))).renderAsBlockWithColorMultiplier(renderBlocks, this, x, y, z);
    }
    @Override
    @Environment(EnvType.CLIENT)
    public void renderBlockAsItem(RenderBlocks renderBlocks, int damage, float brightness) {
        getTransformedModelForMetadata(3).renderAsItemBlock(renderBlocks, this, damage);
    }
    @Override
    @Environment(EnvType.CLIENT)
    public void renderFallingBlock(RenderBlocks renderBlocks, int x, int y, int z, int meta) {
        getTransformedModelForMetadata((((net.minecraft.src.Direction.directionToFacing[(((meta)&3))])^1))).renderAsFallingBlock(renderBlocks, this, x, y, z, meta);
    }
}
