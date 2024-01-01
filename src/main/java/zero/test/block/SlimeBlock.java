package zero.test.block;
import net.minecraft.src.*;
import btw.block.BTWBlocks;
import btw.block.blocks.AestheticOpaqueBlock;
import btw.AddonHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import zero.test.sound.ZeroTestSounds;

public class SlimeBlock extends Block {
    public SlimeBlock(int block_id) {
        super(block_id, Material.grass);
        this.slipperiness = 0.8f;
        this.setHardness(0.0f);
        this.setLightOpacity(1);
        this.setUnlocalizedName("slime_block");
        this.stepSound = ZeroTestSounds.slime_step_sound;
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }
    public int getMobilityFlag() {
        return 0;
    }
    public boolean isStickyForBlocks(World world, int X, int Y, int Z, int direction) {
        return true;
    }
    public boolean isBouncyWhenMoved(int direction, int meta) {
        return true;
    }
    public boolean canBeStuckTo(World world, int X, int Y, int Z, int direction, int neighbor_id) {
        return neighbor_id != 1321;
    }
    @Override
    public boolean isNormalCube(IBlockAccess block_access, int X, int Y, int Z) {
        return true;
    }
    @Override
    public boolean hasMortar(IBlockAccess block_access, int X, int Y, int Z) {
        return true;
    }
    public boolean permanentlySupportsMortarBlocks(World world, int X, int Y, int Z, int direction) {
        return true;
    }
    @Override
    public void onFallenUpon(World world, int X, int Y, int Z, Entity entity, float par6) {
        if (!entity.isSneaking()) {
            entity.fallDistance = 0.0f;
            double newY = entity.motionY;
            //AddonHandler.logMessage("Landed on slime "+newY);
            if (newY < 0.0) {
                //entity.isAirBorne = true;
                // This doesn't work...?
                if (entity instanceof EntityLiving) {
                    newY *= 0.8;
                }
                entity.motionY = -newY;
            }
        } else {
            super.onFallenUpon(world, X, Y, Z, entity, par6);
        }
    }
    @Environment(EnvType.CLIENT)
    @Override
    public int getRenderBlockPass() {
        return 1;
    }
    @Environment(EnvType.CLIENT)
    @Override
    public boolean shouldSideBeRendered(IBlockAccess block_access, int neighborX, int neighborY, int neighborZ, int neighbor_side) {
        return block_access.getBlockId(neighborX, neighborY, neighborZ) != 1320
                ? super.shouldSideBeRendered(block_access, neighborX, neighborY, neighborZ, neighbor_side)
                : false;
    }
    @Environment(EnvType.CLIENT)
    @Override
    public boolean shouldRenderNeighborFullFaceSide(IBlockAccess block_access, int neighborX, int neighborY, int neighborZ, int neighbor_side) {
        return true;
    }
    @Environment(EnvType.CLIENT)
    @Override
    public float getAmbientOcclusionLightValue(IBlockAccess block_access, int X, int Y, int Z) {
        return 1.0f;
    }
}
