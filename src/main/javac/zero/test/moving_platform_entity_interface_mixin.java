package zero.test;

import net.minecraft.src.*;

#include "feature_flags.h"

public interface IMovingPlatformEntityMixins {
#if ENABLE_PLATFORM_EXTENSIONS
    public void setBlockId(int blockId);
    public int getBlockId();
    public void setBlockMeta(int blockMeta);
    public int getBlockMeta();
    //public void setStickySides(int sides);
    //public int getStickySides();
    
    public void storeTileEntity(TileEntity tileEntity);
#endif
}