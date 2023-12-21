package zero.test;
import net.minecraft.src.Block;
import net.minecraft.src.World;

public interface IBlockMixins {
    public boolean getWeakChanges(World world, int X, int Y, int Z, int neighbor_id);
    public boolean caresAboutUpdateDirection();
    public boolean isSticky(int X, int Y, int Z, int direction);
    public boolean canStickTo(int X, int Y, int Z, int direction, int neighbor_id);
}
