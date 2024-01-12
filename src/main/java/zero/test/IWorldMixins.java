package zero.test;
import net.minecraft.src.Block;
import net.minecraft.src.World;
import zero.test.IBlockMixins;
// Block piston reactions
public interface IWorldMixins {
    //public boolean get_is_handling_piston_move();
    public void updateNeighbourShapes(int x, int y, int z, int flags);
    public int updateFromNeighborShapes(int x, int y, int z, int blockId, int blockMeta);
    public int getBlockStrongPowerInputExceptFacing(int x, int y, int z, int facing);
    public int getBlockWeakPowerInputExceptFacing(int x, int y, int z, int facing);
    default public boolean isBlockRedstoneConductor(int x, int y, int z) {
        World self = (World)(Object)this;
        Block block = Block.blocksList[self.getBlockId(x, y, z)];
        return !((block)==null) && ((IBlockMixins)block).isRedstoneConductor(self, x, y, z);
    }
}
