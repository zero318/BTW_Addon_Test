package zero.test.mixin;
import net.minecraft.src.Block;
import net.minecraft.src.World;
import net.minecraft.src.BlockRedstoneLogic;
import net.minecraft.src.BlockComparator;
import net.minecraft.src.*;
import btw.AddonHandler;
import btw.BTWAddon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.Overwrite;
import zero.test.IBlockMixins;
import zero.test.IWorldMixins;

@Mixin(World.class)
public class WorldMixins implements IWorldMixins {
    @Overwrite
    public void func_96440_m(int X, int Y, int Z, int neighbor_id) {
        World world = (World)(Object)this;
        for (int i = 0; i < 4; ++i) {
            int nextX = X + Direction.offsetX[i];
            int nextZ = Z + Direction.offsetZ[i];
            int block_id = world.getBlockId(nextX, Y, nextZ);
            if (block_id != 0) {
                int neighbor_id_ex = neighbor_id | 0x10000000 | i + 2 << 28;
                Block block_instance = Block.blocksList[block_id];
                if (((IBlockMixins)block_instance).getWeakChanges(world, nextX, Y, nextZ, neighbor_id)) {
                    block_instance.onNeighborBlockChange(world, nextX, Y, nextZ, ((IBlockMixins)block_instance).caresAboutUpdateDirection() ? neighbor_id_ex : neighbor_id);
                }
                else if (Block.isNormalCube(block_id)) {
                    nextX += Direction.offsetX[i];
                    nextZ += Direction.offsetZ[i];
                    block_id = world.getBlockId(nextX, Y, nextZ);
                    block_instance = Block.blocksList[block_id];
                    if (
                        !((block_instance)==null) &&
                        ((IBlockMixins)block_instance).getWeakChanges(world, nextX, Y, nextZ, neighbor_id)
                    ) {
                        block_instance.onNeighborBlockChange(world, nextX, Y, nextZ, ((IBlockMixins)block_instance).caresAboutUpdateDirection() ? neighbor_id_ex : neighbor_id);
                    }
                }
            }
        }
    }
    @Redirect(
  method = "notifyBlockOfNeighborChange(IIII)V",
  at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/src/Block;onNeighborBlockChange(Lnet/minecraft/src/World;IIII)V"
  )
 )
    public void onNeighborBlockChange(Block block, World world, int X, int Y, int Z, int neighbor_id) {
        if (!((IBlockMixins)block).caresAboutUpdateDirection()) {
            neighbor_id &= 0xFFF;
        }
        block.onNeighborBlockChange(world, X, Y, Z, neighbor_id);
    }
    public void forceNotifyBlockOfNeighborChange(int X, int Y, int Z, int neighbor_id) {
        ((World)(Object)this).notifyBlockOfNeighborChange(X, Y, Z, neighbor_id | 0xD0000000);
    }
    @Overwrite
    public void notifyBlocksOfNeighborChange(int X, int Y, int Z, int neighbor_id) {
        World world = (World)(Object)this;
        world.notifyBlockOfNeighborChange(X - 1, Y, Z, neighbor_id | 0x90000000);
        world.notifyBlockOfNeighborChange(X + 1, Y, Z, neighbor_id | 0xB0000000);
        world.notifyBlockOfNeighborChange(X, Y - 1, Z, neighbor_id | 0x10000000);
        world.notifyBlockOfNeighborChange(X, Y + 1, Z, neighbor_id | 0x30000000);
        world.notifyBlockOfNeighborChange(X, Y, Z - 1, neighbor_id | 0x50000000);
        world.notifyBlockOfNeighborChange(X, Y, Z + 1, neighbor_id | 0x70000000);
    }
    @Overwrite
    public void notifyBlocksOfNeighborChange(int X, int Y, int Z, int neighbor_id, int direction) {
        World world = (World)(Object)this;
        if (direction != 4) {
            world.notifyBlockOfNeighborChange(X - 1, Y, Z, neighbor_id | 0x90000000);
        }
        if (direction != 5) {
            world.notifyBlockOfNeighborChange(X + 1, Y, Z, neighbor_id | 0xB0000000);
        }
        if (direction != 0) {
            world.notifyBlockOfNeighborChange(X, Y - 1, Z, neighbor_id | 0x10000000);
        }
        if (direction != 1) {
            world.notifyBlockOfNeighborChange(X, Y + 1, Z, neighbor_id | 0x30000000);
        }
        if (direction != 2) {
            world.notifyBlockOfNeighborChange(X, Y, Z - 1, neighbor_id | 0x50000000);
        }
        if (direction != 3) {
            world.notifyBlockOfNeighborChange(X, Y, Z + 1, neighbor_id | 0x70000000);
        }
    }
}
