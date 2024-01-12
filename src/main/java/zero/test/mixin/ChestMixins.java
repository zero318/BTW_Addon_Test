package zero.test.mixin;
import net.minecraft.src.*;
import btw.block.blocks.AestheticOpaqueBlock;
import btw.AddonHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
// Block piston reactions
@Mixin(BlockChest.class)
public class ChestMixins {
    @Overwrite
    public boolean canPlaceBlockAt(World world, int X, int Y, int Z) {
        int allowed_chests = 2;
        int self_id = ((Block)(Object)this).blockID;
        if (world.getBlockId(X - 1, Y, Z) == self_id) {
            --allowed_chests;
            if (
                world.getBlockId(X - 2, Y, Z) == self_id ||
                world.getBlockId(X - 1, Y, Z - 1) == self_id ||
                world.getBlockId(X - 1, Y, Z + 1) == self_id
            ) {
                return false;
            }
        }
        if (world.getBlockId(X + 1, Y, Z) == self_id) {
            if (
                --allowed_chests == 0 ||
                world.getBlockId(X + 2, Y, Z) == self_id ||
                world.getBlockId(X + 1, Y, Z - 1) == self_id ||
                world.getBlockId(X + 1, Y, Z + 1) == self_id
            ) {
                return false;
            }
        }
        if (world.getBlockId(X, Y, Z - 1) == self_id) {
            if (
                --allowed_chests == 0 ||
                world.getBlockId(X, Y, Z - 2) == self_id ||
                world.getBlockId(X - 1, Y, Z - 1) == self_id ||
                world.getBlockId(X + 1, Y, Z - 1) == self_id
            ) {
                return false;
            }
        }
        if (world.getBlockId(X, Y, Z + 1) == self_id) {
            if (
                --allowed_chests == 0 ||
                world.getBlockId(X, Y, Z + 2) == self_id ||
                world.getBlockId(X - 1, Y, Z + 1) == self_id ||
                world.getBlockId(X + 1, Y, Z + 1) == self_id
            ) {
                return false;
            }
        }
        //AddonHandler.logMessage(""+allowed_chests+" "+Block.chest.blockID+" "+world.getBlockId(X, Y, Z));
        return true;
    }
    // This breaks the chest if it would
    // form a triple chest
    public int updateShape(World world, int x, int y, int z, int direction, int meta) {
        return ((BlockChest)(Object)this).canPlaceBlockAt(world, x, y, z) ? meta : -1;
    }
    public boolean isStickyForBlocks(World world, int x, int y, int z, int direction) {
        // Only attach to other chests.
        // Somehow the rendering when splitting double chests
        // got broken and I have no idea how to fix it, so this
        return ((direction)>=2) && ((BlockChest)(Object)this).blockID == world.getBlockId(x + Facing.offsetsXForSide[direction], y + Facing.offsetsYForSide[direction], z + Facing.offsetsZForSide[direction]);
    }
}
