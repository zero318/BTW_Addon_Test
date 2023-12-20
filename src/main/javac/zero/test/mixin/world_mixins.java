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

#include "..\func_aliases.h"
#include "..\feature_flags.h"
#include "..\util.h"

@Mixin(World.class)
public class WorldMixins implements IWorldMixins {
    
    /*
        Changes:
        - Added getWeakChanges instead of hardcoding the comparator ID
        - Added horrible jank for directional updates
    */
    @Overwrite
    public void func_96440_m(int X, int Y, int Z, int neighbor_id) {
        World world = (World)(Object)this;
        for (int i = 0; i < 4; ++i) {
            int nextX = X + Direction.offsetX[i];
            int nextZ = Z + Direction.offsetZ[i];
            int block_id = world.getBlockId(nextX, Y, nextZ);

            if (block_id != 0) {
#if ENABLE_DIRECTIONAL_UPDATES
                int neighbor_id_ex = neighbor_id | UPDATE_DIRECTION_ENABLED_MASK | i + 2 << UPDATE_DIRECTION_OFFSET;
#endif
                Block block_instance = Block.blocksList[block_id];
                if (((IBlockMixins)block_instance).getWeakChanges(world, nextX, Y, nextZ, neighbor_id)) {
#if ENABLE_DIRECTIONAL_UPDATES
                    block_instance.onNeighborBlockChange(world, nextX, Y, nextZ, ((IBlockMixins)block_instance).caresAboutUpdateDirection() ? neighbor_id_ex : neighbor_id);
#else
                    block_instance.onNeighborBlockChange(world, nextX, Y, nextZ, neighbor_id);
#endif
                }
                // Crashes if this isn't an else? Why?
                // TODO: See if the null check fixed this
                else if (Block.isNormalCube(block_id)) {
                    nextX += Direction.offsetX[i];
                    nextZ += Direction.offsetZ[i];
                    block_id = world.getBlockId(nextX, Y, nextZ);
                    block_instance = Block.blocksList[block_id];
                    if (
                        !BLOCK_IS_AIR(block_instance) &&
                        ((IBlockMixins)block_instance).getWeakChanges(world, nextX, Y, nextZ, neighbor_id)
                    ) {
#if ENABLE_DIRECTIONAL_UPDATES
                        block_instance.onNeighborBlockChange(world, nextX, Y, nextZ, ((IBlockMixins)block_instance).caresAboutUpdateDirection() ? neighbor_id_ex : neighbor_id);
#else
                        block_instance.onNeighborBlockChange(world, nextX, Y, nextZ, neighbor_id);
#endif
                    }
                }
            }
        }
    }
    
#if ENABLE_DIRECTIONAL_UPDATES

    @Redirect(
		method = "notifyBlockOfNeighborChange(IIII)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/src/Block;onNeighborBlockChange(Lnet/minecraft/src/World;IIII)V"
		)
	)
    public void onNeighborBlockChange(Block block, World world, int X, int Y, int Z, int neighbor_id) {
        if (!((IBlockMixins)block).caresAboutUpdateDirection()) {
            neighbor_id &= BLOCK_ID_MASK;
        }
        block.onNeighborBlockChange(world, X, Y, Z, neighbor_id);
    }
    
    public void forceNotifyBlockOfNeighborChange(int X, int Y, int Z, int neighbor_id) {
        ((World)(Object)this).notifyBlockOfNeighborChange(X, Y, Z, neighbor_id | UPDATE_DIRECTION_FORCE_MASK);
    }
    
    @Overwrite
    public void notifyBlocksOfNeighborChange(int X, int Y, int Z, int neighbor_id) {
        World world = (World)(Object)this;
        world.notifyBlockOfNeighborChange(X - 1, Y, Z, neighbor_id | UPDATE_DIRECTION_WEST_MASK);
        world.notifyBlockOfNeighborChange(X + 1, Y, Z, neighbor_id | UPDATE_DIRECTION_EAST_MASK);
        world.notifyBlockOfNeighborChange(X, Y - 1, Z, neighbor_id | UPDATE_DIRECTION_DOWN_MASK);
        world.notifyBlockOfNeighborChange(X, Y + 1, Z, neighbor_id | UPDATE_DIRECTION_UP_MASK);
        world.notifyBlockOfNeighborChange(X, Y, Z - 1, neighbor_id | UPDATE_DIRECTION_NORTH_MASK);
        world.notifyBlockOfNeighborChange(X, Y, Z + 1, neighbor_id | UPDATE_DIRECTION_SOUTH_MASK);
    }
    
    @Overwrite
    public void notifyBlocksOfNeighborChange(int X, int Y, int Z, int neighbor_id, int direction) {
        World world = (World)(Object)this;
        if (direction != DIRECTION_WEST) {
            world.notifyBlockOfNeighborChange(X - 1, Y, Z, neighbor_id | UPDATE_DIRECTION_WEST_MASK);
        }
        if (direction != DIRECTION_EAST) {
            world.notifyBlockOfNeighborChange(X + 1, Y, Z, neighbor_id | UPDATE_DIRECTION_EAST_MASK);
        }
        if (direction != DIRECTION_DOWN) {
            world.notifyBlockOfNeighborChange(X, Y - 1, Z, neighbor_id | UPDATE_DIRECTION_DOWN_MASK);
        }
        if (direction != DIRECTION_UP) {
            world.notifyBlockOfNeighborChange(X, Y + 1, Z, neighbor_id | UPDATE_DIRECTION_UP_MASK);
        }
        if (direction != DIRECTION_NORTH) {
            world.notifyBlockOfNeighborChange(X, Y, Z - 1, neighbor_id | UPDATE_DIRECTION_NORTH_MASK);
        }
        if (direction != DIRECTION_SOUTH) {
            world.notifyBlockOfNeighborChange(X, Y, Z + 1, neighbor_id | UPDATE_DIRECTION_SOUTH_MASK);
        }
    }
#endif
}