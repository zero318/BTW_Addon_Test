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
    public boolean is_handling_piston_move = false;
    
    public boolean get_is_handling_piston_move() {
        return this.is_handling_piston_move;
    }
    */
    
    /*
        Changes:
        - Added getWeakChanges instead of hardcoding the comparator ID
    */
    @Overwrite
    public void func_96440_m(int X, int Y, int Z, int neighbor_id) {
        World self = (World)(Object)this;
        for (int i = 0; i < 4; ++i) {
            int nextX = X + Direction.offsetX[i];
            int nextZ = Z + Direction.offsetZ[i];
            int block_id = self.getBlockId(nextX, Y, nextZ);

            if (block_id != 0) {
                Block block = Block.blocksList[block_id];
                if (((IBlockMixins)block).getWeakChanges(self, nextX, Y, nextZ, neighbor_id)) {
                    block.onNeighborBlockChange(self, nextX, Y, nextZ, neighbor_id);
                }
                // Crashes if this isn't an else? Why?
                // TODO: See if the null check fixed this
                else if (
#if ENABLE_MODERN_REDSTONE_WIRE
                    ((IBlockMixins)block).isRedstoneConductor(self, X, Y, Z)
#else
                    Block.isNormalCube(block_id)
#endif
                ) {
                    nextX += Direction.offsetX[i];
                    nextZ += Direction.offsetZ[i];
                    block_id = self.getBlockId(nextX, Y, nextZ);
                    block = Block.blocksList[block_id];
                    if (
                        !BLOCK_IS_AIR(block) &&
                        ((IBlockMixins)block).getWeakChanges(self, nextX, Y, nextZ, neighbor_id)
                    ) {
                        block.onNeighborBlockChange(self, nextX, Y, nextZ, neighbor_id);
                    }
                }
            }
        }
    }
    
#if ENABLE_DIRECTIONAL_UPDATES

    public void updateNeighbourShapes(int X, int Y, int Z, int flags) {
        World world = (World)(Object)this;
        
        IBlockMixins neighbor_block;
        boolean allow_drops = (flags & UPDATE_SUPPRESS_DROPS) == 0;
        flags &= ~UPDATE_SUPPRESS_DROPS;
        int neighbor_meta;
        int new_meta;
        
        // Offset from neutral to west
        --X;
        neighbor_block = (IBlockMixins)Block.blocksList[world.getBlockId(X, Y, Z)];
        if (!BLOCK_IS_AIR(neighbor_block)) {
            neighbor_meta = world.getBlockMetadata(X, Y, Z);
            new_meta = neighbor_block.updateShape(world, X, Y, Z, DIRECTION_EAST, neighbor_meta);
            if (new_meta != neighbor_meta) {
                if (new_meta >= 0) {
                    world.setBlockMetadataWithNotify(X, Y, Z, flags);
                } else {
                    world.destroyBlock(X, Y, Z, allow_drops);
                }
            }
        }
        // Offset from west to east
        X += 2;
        neighbor_block = (IBlockMixins)Block.blocksList[world.getBlockId(X, Y, Z)];
        if (!BLOCK_IS_AIR(neighbor_block)) {
            neighbor_meta = world.getBlockMetadata(X, Y, Z);
            new_meta = neighbor_block.updateShape(world, X, Y, Z, DIRECTION_WEST, neighbor_meta);
            if (new_meta != neighbor_meta) {
                if (new_meta >= 0) {
                    world.setBlockMetadataWithNotify(X, Y, Z, flags);
                } else {
                    world.destroyBlock(X, Y, Z, allow_drops);
                }
            }
        }
        // Offset from east to north
        --X;
        --Z;
        neighbor_block = (IBlockMixins)Block.blocksList[world.getBlockId(X, Y, Z)];
        if (!BLOCK_IS_AIR(neighbor_block)) {
            neighbor_meta = world.getBlockMetadata(X, Y, Z);
            new_meta = neighbor_block.updateShape(world, X, Y, Z, DIRECTION_SOUTH, neighbor_meta);
            if (new_meta != neighbor_meta) {
                if (new_meta >= 0) {
                    world.setBlockMetadataWithNotify(X, Y, Z, flags);
                } else {
                    world.destroyBlock(X, Y, Z, allow_drops);
                }
            }
        }
        // Offset from north to south
        Z += 2;
        neighbor_block = (IBlockMixins)Block.blocksList[world.getBlockId(X, Y, Z)];
        if (!BLOCK_IS_AIR(neighbor_block)) {
            neighbor_meta = world.getBlockMetadata(X, Y, Z);
            new_meta = neighbor_block.updateShape(world, X, Y, Z, DIRECTION_NORTH, neighbor_meta);
            if (new_meta != neighbor_meta) {
                if (new_meta >= 0) {
                    world.setBlockMetadataWithNotify(X, Y, Z, flags);
                } else {
                    world.destroyBlock(X, Y, Z, allow_drops);
                }
            }
        }
        // Offset from south to down
        --Z;
        --Y;
        neighbor_block = (IBlockMixins)Block.blocksList[world.getBlockId(X, Y, Z)];
        if (!BLOCK_IS_AIR(neighbor_block)) {
            neighbor_meta = world.getBlockMetadata(X, Y, Z);
            new_meta = neighbor_block.updateShape(world, X, Y, Z, DIRECTION_UP, neighbor_meta);
            if (new_meta != neighbor_meta) {
                if (new_meta >= 0) {
                    world.setBlockMetadataWithNotify(X, Y, Z, flags);
                } else {
                    world.destroyBlock(X, Y, Z, allow_drops);
                }
            }
        }
        // Offset from down to up
        Y += 2;
        neighbor_block = (IBlockMixins)Block.blocksList[world.getBlockId(X, Y, Z)];
        if (!BLOCK_IS_AIR(neighbor_block)) {
            neighbor_meta = world.getBlockMetadata(X, Y, Z);
            new_meta = neighbor_block.updateShape(world, X, Y, Z, DIRECTION_DOWN, neighbor_meta);
            if (new_meta != neighbor_meta) {
                if (new_meta >= 0) {
                    world.setBlockMetadataWithNotify(X, Y, Z, flags);
                } else {
                    world.destroyBlock(X, Y, Z, allow_drops);
                }
            }
        }
    }
    
    public int updateFromNeighborShapes(int X, int Y, int Z, int block_id, int meta) {
        IBlockMixins block = (IBlockMixins)Block.blocksList[block_id];
        if (!BLOCK_IS_AIR(block)) {
            World world = (World)(Object)this;
            meta = block.updateShape(world, X, Y, Z, DIRECTION_WEST, meta);
            if (meta >= 0) meta = block.updateShape(world, X, Y, Z, DIRECTION_EAST, meta);
            if (meta >= 0) meta = block.updateShape(world, X, Y, Z, DIRECTION_NORTH, meta);
            if (meta >= 0) meta = block.updateShape(world, X, Y, Z, DIRECTION_SOUTH, meta);
            if (meta >= 0) meta = block.updateShape(world, X, Y, Z, DIRECTION_DOWN, meta);
            if (meta >= 0) meta = block.updateShape(world, X, Y, Z, DIRECTION_UP, meta);
        }
        return meta;
    }

    @Overwrite
    public boolean setBlock(int X, int Y, int Z, int block_id, int meta, int flags) {
        if (IS_VALID_BLOCK_XYZ_POS(X, Y, Z)) {
            World world = (World)(Object)this;
            Chunk chunk = world.getChunkFromChunkCoords(X >> 4, Z >> 4);
            int current_block_id = 0;
            if (
                //(flags & (UPDATE_NEIGHBORS | UPDATE_KNOWN_SHAPE)) != UPDATE_KNOWN_SHAPE
                (flags & UPDATE_NEIGHBORS) != 0
            ) {
                current_block_id = chunk.getBlockID(X & 0xF, Y, Z & 0xF);
            }
            
            /*
            boolean prev_handling_piston = false;
            if (!world.isRemote) {
                prev_handling_piston = is_handling_piston_move;
                is_handling_piston_move = (flags & UPDATE_MOVE_BY_PISTON) != 0;
            }
            AddonHandler.logMessage("Piston move state: "+is_handling_piston_move);
            */
            
            boolean block_changed = chunk.setBlockIDWithMetadata(X & 0xF, Y, Z & 0xF, block_id, meta);
            
            if ((flags & UPDATE_SUPPRESS_LIGHT) == 0) {
                world.theProfiler.startSection("checkLight");
                world.updateAllLightTypes(X, Y, Z);
                world.theProfiler.endSection();
            }
            
            if (block_changed) {
                if (
                    (flags & UPDATE_CLIENTS) != 0 &&
                    (
                        !world.isRemote ||
                        (flags & UPDATE_INVISIBLE) == 0
                    )
                ) {
                    world.markBlockForUpdate(X, Y, Z);
                }
                //Block block;
                if (
                    !world.isRemote &&
                    (flags & UPDATE_NEIGHBORS) != 0
                ) {
                    world.notifyBlockChange(X, Y, Z, current_block_id);
                    Block block = Block.blocksList[block_id];
                    if (
                        !BLOCK_IS_AIR(block) &&
                        block.hasComparatorInputOverride()
                    ) {
                        world.func_96440_m(X, Y, Z, block_id);
                    }
                }
                if (
                    (flags & UPDATE_KNOWN_SHAPE) == 0
                ) {
                    //block = Block.blocksList[current_block_id];
                    //if (!BLOCK_IS_AIR(block)) {
                        //((IBlockMixins)block).updateIndirectNeighbourShapes(world, X, Y, Z);
                    //}
                    this.updateNeighbourShapes(X, Y, Z, flags & ~(UPDATE_NEIGHBORS | UPDATE_SUPPRESS_DROPS));
                    //block = Block.blocksList[block_id];
                    //if (!BLOCK_IS_AIR(block)) {
                        //((IBlockMixins)block).updateIndirectNeighbourShapes(world, X, Y, Z);
                    //}
                }
            }
            /*
            if (!world.isRemote) {
                is_handling_piston_move = prev_handling_piston;
            }
            */
            return block_changed;
        }
        return false;
    }
    
    @Overwrite
    public boolean setBlockMetadataWithNotify(int X, int Y, int Z, int meta, int flags) {
        if (IS_VALID_BLOCK_XYZ_POS(X, Y, Z)) {
            World world = (World)(Object)this;
            Chunk chunk = world.getChunkFromChunkCoords(X >> 4, Z >> 4);
            boolean block_changed = chunk.setBlockMetadata(X & 0xF, Y, Z & 0xF, meta);
            
            // Should this be enabled?
            /*
            if ((flags & UPDATE_SUPPRESS_LIGHT) == 0) {
                world.theProfiler.startSection("checkLight");
                world.updateAllLightTypes(X, Y, Z);
                world.theProfiler.endSection();
            }
            */
            
            if (block_changed) {
                /*
                boolean prev_handling_piston = false;
                if (!world.isRemote) {
                    prev_handling_piston= is_handling_piston_move;
                    is_handling_piston_move = (flags & UPDATE_MOVE_BY_PISTON) != 0;
                }
                */
                
                //int current_block_id = 0;
                //if (
                    //(flags & (UPDATE_NEIGHBORS | UPDATE_KNOWN_SHAPE)) != UPDATE_KNOWN_SHAPE
                //) {
                    //current_block_id = chunk.getBlockID(X & 0xF, Y, Z & 0xF);
                //}
                if (
                    (flags & UPDATE_CLIENTS) != 0 &&
                    (
                        !world.isRemote ||
                        (flags & UPDATE_INVISIBLE) == 0
                    )
                ) {
                    world.markBlockForUpdate(X, Y, Z);
                }
                //Block block = Block.blocksList[current_block_id];
                if (
                    !world.isRemote &&
                    (flags & UPDATE_NEIGHBORS) != 0
                ) {
                    int current_block_id = chunk.getBlockID(X & 0xF, Y, Z & 0xF);
                    world.notifyBlockChange(X, Y, Z, current_block_id);
                    Block block = Block.blocksList[current_block_id];
                    if (
                        !BLOCK_IS_AIR(block) &&
                        block.hasComparatorInputOverride()
                    ) {
                        world.func_96440_m(X, Y, Z, current_block_id);
                    }
                }
                if (
                    (flags & UPDATE_KNOWN_SHAPE) == 0
                ) {
                    //if (!BLOCK_IS_AIR(block)) {
                        //((IBlockMixins)block).updateIndirectNeighbourShapes(world, X, Y, Z);
                    //}
                    this.updateNeighbourShapes(X, Y, Z, flags & ~(UPDATE_NEIGHBORS | UPDATE_SUPPRESS_DROPS));
                }
                /*
                if (!world.isRemote) {
                    is_handling_piston_move = prev_handling_piston;
                }
                */
            }
            return block_changed;
        }
        return false;
    }
#endif

#if ENABLE_LESS_CRAP_BTW_BLOCK_POWERING
    // Ideally this would be changed inside the code
    // of each BTW block, but since nothing in vanilla
    // calls this function anyway it can be changed here
    // to simplify the mixins.
    @Overwrite
    public boolean isBlockGettingPowered(int X, int Y, int Z) {
        return ((World)(Object)this).isBlockIndirectlyGettingPowered(X, Y, Z);
    }
#endif


#if ENABLE_MODERN_REDSTONE_WIRE
    @Redirect(
        method = "getIndirectPowerLevelTo",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/World;isBlockNormalCube(III)Z"
        )
    )
    public boolean redirect_isBlockNormalCube(World world, int X, int Y, int Z) {
        return ((IWorldMixins)world).isBlockRedstoneConductor(X, Y, Z);
    }
#endif
}