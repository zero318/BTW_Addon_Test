package zero.test.block;
import net.minecraft.src.*;
import btw.block.BTWBlocks;
import btw.block.blocks.AestheticOpaqueBlock;
import btw.AddonHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import zero.test.sound.ZeroTestSounds;
// Block piston reactions

public class SlimeBlock extends Block {
    public SlimeBlock(int blockId) {
        super(blockId, Material.grass);
        this.slipperiness = 0.8F;
        this.setHardness(0.01F);
        this.setResistance(0.0F); // Zero blast resistance is important
        this.setShovelsEffectiveOn(true);
        this.setLightOpacity(1);
        this.setUnlocalizedName("slime_block");
        this.stepSound = ZeroTestSounds.slime_step_sound;
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }
    @Override
    public boolean dropComponentItemsOnBadBreak(World world, int x, int y, int z, int meta, float chance) {
        dropItemsIndividually(world, x, y, z, Item.slimeBall.itemID, 6, 0, chance);
        return true;
    }
    public int getMobilityFlag() {
        return 0;
    }
    public boolean isStickyForBlocks(World world, int x, int y, int z, int direction) {
        return true;
    }
    public boolean isBouncyWhenMoved(int direction, int meta) {
        return true;
    }
    public boolean canBeStuckTo(World world, int x, int y, int z, int direction, int neighborId) {
        return neighborId != 1321;
    }
    @Override
    public boolean isOpaqueCube() {
        return false;
    }
    // Force enable conductivity
    @Override
    public boolean isNormalCube(IBlockAccess blockAccess, int x, int y, int z) {
        return true;
    }
    @Override
    public boolean hasMortar(IBlockAccess blockAccess, int x, int y, int z) {
        return true;
    }
    public boolean permanentlySupportsMortarBlocks(World world, int x, int y, int z, int direction) {
        return true;
    }
    @Override
    public void onFallenUpon(World world, int x, int y, int z, Entity entity, float par6) {
        if (!entity.isSneaking()) {
            entity.fallDistance = 0.0F;
            double newY = entity.motionY;
            //AddonHandler.logMessage("Landed on slime "+newY);
            if (newY < 0.0D) {
                //entity.isAirBorne = true;
                // This doesn't work...?
                // Apparently it's because the game cancels the motion
                // via the generic "hit wall" code. I draw the line
                // at an @Overwrite for the entire movement code
                // just to add this, so RIP bouncy slime.
                if (entity instanceof EntityLiving) {
                    newY *= 0.8D;
                }
                entity.motionY = -newY;
            }
        } else {
            super.onFallenUpon(world, x, y, z, entity, par6);
        }
    }
    public int getPlatformMobilityFlag(World world, int x, int y, int z) {
        return 1;
    }
    @Environment(EnvType.CLIENT)
    @Override
    public int getRenderBlockPass() {
        return 1;
    }
    @Environment(EnvType.CLIENT)
    @Override
    public boolean shouldSideBeRendered(IBlockAccess blockAccess, int neighborX, int neighborY, int neighborZ, int neighborSide) {
        if (blockAccess.getBlockId(neighborX, neighborY, neighborZ) != this.blockID) {
            return super.shouldSideBeRendered(blockAccess, neighborX, neighborY, neighborZ, neighborSide);
        }
        return false;
    }
    @Environment(EnvType.CLIENT)
    @Override
    public boolean shouldRenderNeighborFullFaceSide(IBlockAccess blockAccess, int neighborX, int neighborY, int neighborZ, int neighborSide) {
        return true;
    }
    // Treat as transparent for AO
    @Environment(EnvType.CLIENT)
    @Override
    public float getAmbientOcclusionLightValue(IBlockAccess blockAccess, int x, int y, int z) {
        return 1.0F;
    }
}
