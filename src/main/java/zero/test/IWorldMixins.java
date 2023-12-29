package zero.test;
import net.minecraft.src.Block;
import net.minecraft.src.World;
// Vanilla observers
// Slime blocks
// Push only and dead coral fans
// Allow slime to keep loose blocks
// suspended in midair as if they
// had mortar applied
// Fix how most BTW blocks recieve power
// Allow block dispensers to respond to short pulses
// Block Breaker and Block Placer

public interface IWorldMixins {
    //public boolean get_is_handling_piston_move();
    public void updateNeighbourShapes(int X, int Y, int Z, int flags);
    public int updateFromNeighborShapes(int X, int Y, int Z, int block_id, int block_meta);
}
