package zero.test.block;
import net.minecraft.src.*;
import btw.block.BTWBlocks;
import btw.block.blocks.AestheticOpaqueBlock;
import btw.AddonHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import zero.test.sound.ZeroTestSounds;
// Block piston reactions

public class GlueBlock extends Block {
    public GlueBlock(int block_id) {
        super(block_id, Material.grass);
        this.setHardness(0.0f);
        this.setUnlocalizedName("glue_block");
        this.setLightOpacity(1);
        this.stepSound = ZeroTestSounds.slime_step_sound;
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }
    public int getMobilityFlag() {
        return 0;
    }
    public boolean isStickyForBlocks(World world, int X, int Y, int Z, int direction) {
        return true;
    }
    public boolean isStickyForEntitiesWhenMoved(int direction, int meta) {
        return true;
    }
    public boolean canBeStuckTo(World world, int X, int Y, int Z, int direction, int neighbor_id) {
        return neighbor_id != 1320;
    }
    @Override
    public boolean isOpaqueCube() {
        return false;
    }
    // Force enable conductivity
    @Override
    public boolean isNormalCube(IBlockAccess block_access, int X, int Y, int Z) {
        return true;
    }
    @Override
    public boolean hasMortar(IBlockAccess block_access, int X, int Y, int Z) {
        return true;
    }
    public boolean isRedstoneConductor(IBlockAccess block_access, int X, int Y, int Z) {
        return false;
    }
    public boolean permanentlySupportsMortarBlocks(World world, int X, int Y, int Z, int direction) {
        return true;
    }
    @Override
    public void onFallenUpon(World world, int X, int Y, int Z, Entity entity, float par6) {
        entity.fallDistance = 0.0f;
    }
    public int getPlatformMobilityFlag(World world, int X, int Y, int Z) {
        return 1;
    }
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int X, int Y, int Z) {
        double dX = X;
        double dY = Y;
        double dZ = Z;
        return AxisAlignedBB.getAABBPool().getAABB(dX + 0.0625, dY + 0.0625, dZ + 0.0625, dX + 0.9375, dY + 0.9375, dZ + 0.9375);
    }
    @Override
    public void onEntityCollidedWithBlock(World world, int X, int Y, int Z, Entity entity) {
        entity.motionX *= 0.4;
        entity.motionY *= 0.05;
        entity.motionZ *= 0.4;
    }
    @Override
    public MapColor getMapColor(int meta) {
        // Kinda orange? Don't feel like defining a custom material for this.
        return MapColor.dirtColor;
    }
    @Environment(EnvType.CLIENT)
    @Override
    public int getRenderBlockPass() {
        return 1;
    }
    @Environment(EnvType.CLIENT)
    @Override
    public boolean shouldSideBeRendered(IBlockAccess block_access, int neighborX, int neighborY, int neighborZ, int neighbor_side) {
        return block_access.getBlockId(neighborX, neighborY, neighborZ) != 1321
                ? super.shouldSideBeRendered(block_access, neighborX, neighborY, neighborZ, neighbor_side)
                : false;
    }
    @Environment(EnvType.CLIENT)
    @Override
    public boolean shouldRenderNeighborFullFaceSide(IBlockAccess block_access, int neighborX, int neighborY, int neighborZ, int neighbor_side) {
        return true;
    }
    // Treat as transparent for AO
    @Environment(EnvType.CLIENT)
    @Override
    public float getAmbientOcclusionLightValue(IBlockAccess block_access, int X, int Y, int Z) {
        return 1.0f;
    }
}
