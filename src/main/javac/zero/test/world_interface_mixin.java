package zero.test;

import net.minecraft.src.Block;
import net.minecraft.src.World;

#include "feature_flags.h"

public interface IWorldMixins {
#if ENABLE_DIRECTIONAL_UPDATES
    public void forceNotifyBlockOfNeighborChange(int X, int Y, int Z, int neighbor_id);
#endif
}