package zero.test;
import net.minecraft.src.*;
// Block piston reactions
public interface IBaseRailBlockMixins {
    default public double getRailMaxSpeedFactor() {
        return 1.0D;
    }
}
