package zero.test;

import net.minecraft.src.Block;
import net.minecraft.src.World;

#include "feature_flags.h"

public interface IBlockMixins {
    public boolean getWeakChanges(World world, int X, int Y, int Z, int meta);
    
#if ENABLE_DIRECTIONAL_UPDATES
    public boolean caresAboutUpdateDirection();
#endif

#if ENABLE_MOVING_BLOCK_CHAINING
    public boolean isSticky(int X, int Y, int Z, int direction);
#endif
}