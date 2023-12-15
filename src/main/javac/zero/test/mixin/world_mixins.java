package zero.test.mixin;

import net.minecraft.src.Block;
import net.minecraft.src.World;
import net.minecraft.src.BlockRedstoneLogic;
import net.minecraft.src.BlockComparator;
import net.minecraft.src.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.Overwrite;

import zero.test.IBlockMixin;

#include "..\func_aliases.h"

@Mixin(World.class)
public class WorldMixins {
    /*
    @Redirect(
		method = "func_96440_m(IIII)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/src/BlockComparator;func_94487_f(I)Z"
		),
		require = 2
	)
    private boolean block_id_is_comparator(int id) {
        return Block.redstoneComparatorIdle.blockIdIsActiveOrInactive(id);
    }
    */
    
    /**
     * Piss off about a missing doc comment being a warnning
     */
    @Overwrite
    public void func_96440_m(int X, int Y, int Z, int neighbor_id) {
        World world = (World)(Object)this;
        for (int i = 0; i < 4; ++i) {
            int nextX = X + Direction.offsetX[i];
            int nextZ = Z + Direction.offsetZ[i];
            int block_id = world.getBlockId(nextX, Y, nextZ);

            if (block_id != 0) {
                Block block_instance = Block.blocksList[block_id];
                if (((IBlockMixin)block_instance).getWeakChanges(world, nextX, Y, nextZ, neighbor_id)) {
                    block_instance.onNeighborBlockChange(world, nextX, Y, nextZ, neighbor_id);
                }
                // Crashes if this isn't an else? Why?
                else if (Block.isNormalCube(block_id)) {
                    nextX += Direction.offsetX[i];
                    nextZ += Direction.offsetZ[i];
                    block_id = world.getBlockId(nextX, Y, nextZ);
                    block_instance = Block.blocksList[block_id];

                    if (((IBlockMixin)block_instance).getWeakChanges(world, nextX, Y, nextZ, neighbor_id)) {
                        block_instance.onNeighborBlockChange(world, nextX, Y, nextZ, neighbor_id);
                    }
                }
            }
        }
    }
}