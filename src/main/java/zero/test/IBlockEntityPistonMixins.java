package zero.test;
import net.minecraft.src.*;
import java.util.List;
// Block piston reactions

public interface IBlockEntityPistonMixins {
    public long getLastTicked();
    public void setLastTicked(long time);
    public boolean hasSmallCenterHardPointToFacing(int x, int y, int z, int direction, boolean ignoreTransparency);
    public boolean hasCenterHardPointToFacing(int x, int y, int z, int direction, boolean ignoreTransparency);
    public TileEntity getStoredTileEntity();
    public boolean isRetractingBase();
    public boolean hasLargeCenterHardPointToFacing(int x, int y, int z, int direction, boolean ignoreTransparency);
    public void getCollisionList(AxisAlignedBB maskBox, List list, Entity entity);
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState();
}
