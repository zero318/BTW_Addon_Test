package zero.test;

import net.minecraft.src.Block;
import net.minecraft.src.World;

public interface IBlockMixin {
    public boolean getWeakChanges(World world, int X, int Y, int Z, int meta);
}