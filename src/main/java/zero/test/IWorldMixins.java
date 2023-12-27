package zero.test;
import net.minecraft.src.Block;
import net.minecraft.src.World;
// Vanilla observers
// Slime blocks
// Push only and dead coral fans

public interface IWorldMixins {
    public void updateNeighbourShapes(int X, int Y, int Z, int flags);
    public int updateFromNeighborShapes(int X, int Y, int Z, int block_id, int block_meta);
}
