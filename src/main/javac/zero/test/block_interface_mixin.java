package zero.test;

import net.minecraft.src.Block;
import net.minecraft.src.World;

#include "feature_flags.h"

public interface IBlockMixin {
    public boolean getWeakChanges(World world, int X, int Y, int Z, int meta);
    
#if ENABLE_DIRECTIONAL_UPDATES
    public boolean caresAboutUpdateDirection();
#endif
}