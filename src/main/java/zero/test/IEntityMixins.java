package zero.test;
import btw.AddonHandler;
import net.minecraft.src.*;
// Block piston reactions
public interface IEntityMixins {
    public void moveEntityByPiston(double X, double Y, double Z);
    default public int getPistonMobilityFlags(int direction) {
        return 1 | 2 | 4;
    }
    default public double getPistonBounceMultiplier(int direction) {
        return 1.0D;
    }
}
