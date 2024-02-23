package zero.test;

import net.minecraft.src.*;

import java.util.List;

#include "feature_flags.h"

public interface IBlockEntityPistonMixins {
    public long getLastTicked();
    public void setLastTicked(long time);
    
#if ENABLE_MORE_MOVING_BLOCK_HARDPOINTS
    public boolean hasSmallCenterHardPointToFacing(int x, int y, int z, int direction, boolean ignoreTransparency);
    public boolean hasCenterHardPointToFacing(int x, int y, int z, int direction, boolean ignoreTransparency);
#endif

    public boolean isPlacingBlock();

    public boolean isRetractingBase();

    public boolean hasLargeCenterHardPointToFacing(int x, int y, int z, int direction, boolean ignoreTransparency);
    
    public void getCollisionList(AxisAlignedBB maskBox, List list, Entity entity);
    
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState();
}