package zero.test;
import net.minecraft.src.Block;
import net.minecraft.src.World;

public interface IWorldMixins {
    public void forceNotifyBlockOfNeighborChange(int X, int Y, int Z, int neighbor_id);
}
