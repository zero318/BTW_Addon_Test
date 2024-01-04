package zero.test;

import net.minecraft.src.*;

import java.util.List;

public interface IBlockEntityPistonMixins {
    public long getLastTicked();
    public void setLastTicked(long time);
    
    public boolean hasLargeCenterHardPointToFacing(int x, int y, int z, int direction, boolean ignoreTransparency);
    
    public void getCollisionList(AxisAlignedBB maskBox, List list);
}