package zero.test.block;
import net.minecraft.src.*;
public class PoweredRailBlock extends BlockRailPowered {
    public PoweredRailBlock(int blockId) {
        super(blockId);
        this.setPicksEffectiveOn();
        this.setHardness(0.7F);
        this.setStepSound(soundMetalFootstep);
        this.setUnlocalizedName("goldenRail");
    }
    public double cartBoostRatio(int meta) {
        return ((((meta)>7))) ? 0.06D : 0.0D;
    }
    public double cartSlowdownRatio(int meta) {
        return ((((meta)>7))) ? 1.0D : 0.5D;
    }
}
