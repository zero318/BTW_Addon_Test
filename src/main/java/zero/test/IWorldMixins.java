package zero.test;
import net.minecraft.src.Block;
import net.minecraft.src.World;
import zero.test.IBlockMixins;
public interface IWorldMixins {
    //public boolean get_is_handling_piston_move();
    public void updateNeighbourShapes(int X, int Y, int Z, int flags);
    public int updateFromNeighborShapes(int X, int Y, int Z, int block_id, int block_meta);
    public int getBlockStrongPowerInputExceptFacing(int X, int Y, int Z, int facing);
    public int getBlockWeakPowerInputExceptFacing(int X, int Y, int Z, int facing);
    default public boolean isBlockRedstoneConductor(int X, int Y, int Z) {
        World self = (World)(Object)this;
        Block block = Block.blocksList[self.getBlockId(X, Y, Z)];
        return !((block)==null) && ((IBlockMixins)block).isRedstoneConductor(self, X, Y, Z);
    }
}
