package zero.test.block;
import net.minecraft.src.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import zero.test.IBaseRailBlockMixins;
// Block piston reactions
public class WoodenRailBlock extends BlockRail implements IBaseRailBlockMixins {
    public WoodenRailBlock(int blockId) {
        super(blockId);
        this.setHardness(0.7F);
        this.setAxesEffectiveOn();
        this.setStepSound(Block.soundWoodFootstep);
        this.setUnlocalizedName("wooden_rail");
        this.setFireProperties(5, 20);
    }
    @Override
    public double getRailMaxSpeedFactor() {
        return 0.5D;
    }
    @Override
    public int getChanceOfFireSpreadingDirectlyTo(IBlockAccess blockAccess, int x, int y, int z) {
  return 60; // same chance as leaves and other highly flammable objects
    }
    @Environment(EnvType.CLIENT)
    private Icon cornerIcon;
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister iconRegister) {
        super.registerIcons(iconRegister);
        this.cornerIcon = iconRegister.registerIcon("wooden_rail_corner");
    }
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int par1, int par2) {
        return par2 >= 6 ? this.cornerIcon : this.blockIcon;
    }
}
