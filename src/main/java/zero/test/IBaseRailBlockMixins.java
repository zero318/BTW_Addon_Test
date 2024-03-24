package zero.test;
import net.minecraft.src.*;
public interface IBaseRailBlockMixins {
    default public double getRailMaxSpeedFactor() {
        return 1.0D;
    }
    default public double cartBoostRatio(int meta) {
        return 0.0D;
    }
    default public double cartSlowdownRatio(int meta) {
        return 1.0D;
    }
}
