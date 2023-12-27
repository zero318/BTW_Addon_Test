package zero.test;
import btw.AddonHandler;
import net.minecraft.src.Block;
import net.minecraft.src.World;

public interface IBlockMixins {
    default public boolean getWeakChanges(World world, int X, int Y, int Z, int neighbor_id) {
        return false;
    }
    default public int updateShape(World world, int X, int Y, int Z, int direction, int meta) {
        return meta;
    }
    public int getMobilityFlag(World world, int X, int Y, int Z);
    default public boolean isStickyForBlocks(World world, int X, int Y, int Z, int direction) {
        return false;
    }
    default public boolean canBeStuckTo(World world, int X, int Y, int Z, int direction, int neighbor_id) {
        return true;
    }
    default public boolean isBouncyWhenMoved(int direction, int meta) {
        return false;
    }
    default public boolean isStickyForEntitiesWhenMoved(int direction, int meta) {
        return false;
    }
    default public boolean permanentlySupportsMortarBlocks(World world, int X, int Y, int Z, int direction) {
        return false;
    }
}
