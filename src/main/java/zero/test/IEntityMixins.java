package zero.test;
import btw.AddonHandler;
import net.minecraft.src.*;
public interface IEntityMixins {
    public void moveEntityByPiston(double x, double y, double z, int direction, boolean isRegularPush);
    default public int getPistonMobilityFlags(int direction) {
        return 1 | 2 | 4;
    }
    default public double getPistonBounceMultiplier(int direction) {
        return 1.0D;
    }
}
