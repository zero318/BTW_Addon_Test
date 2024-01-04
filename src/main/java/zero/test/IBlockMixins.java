package zero.test;
import btw.AddonHandler;
import net.minecraft.src.Block;
import net.minecraft.src.World;
import net.minecraft.src.IBlockAccess;

public interface IBlockMixins {
    // Whether or not the block should have onNeighborBlockChange
    // called in response to comparator updates
    default public boolean getWeakChanges(World world, int X, int Y, int Z, int neighbor_id) {
        return false;
    }
    default public int updateShape(World world, int X, int Y, int Z, int direction, int meta) {
        return meta;
    }
    //default public void updateIndirectNeighbourShapes(World world, int X, int Y, int Z) {
    //}
    public int getMobilityFlag(World world, int X, int Y, int Z);
    // The direction argument is intended to allow for
    // blocks that are only sticky on specific faces
    default public boolean isStickyForBlocks(World world, int X, int Y, int Z, int direction) {
        return false;
    }
    // This is only called after the face shared with the
    // neighbor block is already known to be sticky
    default public boolean canBeStuckTo(World world, int X, int Y, int Z, int direction, int neighbor_id) {
        return true;
    }
    // Will entities be yeeted by this block?
    default public boolean isBouncyWhenMoved(int direction, int meta) {
        return false;
    }
    // Will entities be moved by this block?
    default public boolean isStickyForEntitiesWhenMoved(int direction, int meta) {
        return false;
    }
    default public boolean permanentlySupportsMortarBlocks(World world, int X, int Y, int Z, int direction) {
        return false;
    }
    // Default to the old behavior for conductivity testing
    default public boolean isRedstoneConductor(IBlockAccess block_access, int X, int Y, int Z) {
        //return ((Block)(Object)this).isNormalCube(world, X, Y, Z);
        return block_access.isBlockNormalCube(X, Y, Z);
    }
    // Default to the old behavior for dust connections
    default public boolean canRedstoneConnectToSide(IBlockAccess block_access, int X, int Y, int Z, int flat_direction) {
        return ((Block)(Object)this).canProvidePower();
    }
}
