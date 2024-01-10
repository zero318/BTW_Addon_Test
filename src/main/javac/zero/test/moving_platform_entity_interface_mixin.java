package zero.test;

#include "feature_flags.h"

public interface IMovingPlatformEntityMixins {
#if ENABLE_PLATFORM_EXTENSIONS
    public void setBlockId(int blockId);
    public int getBlockId();
    public void setBlockMeta(int blockMeta);
    public int getBlockMeta();
    //public void setStickySides(int sides);
    //public int getStickySides();
#endif
}