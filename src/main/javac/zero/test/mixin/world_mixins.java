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
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.Overwrite;

import zero.test.IBlockMixins;
import zero.test.IWorldMixins;
import zero.test.IEntityMixins;
import zero.test.ZeroUtil;

import java.util.List;

#include "..\func_aliases.h"
#include "..\feature_flags.h"
#include "..\util.h"

#define FULL_3D_COMPARATOR_UPDATES 1

@Mixin(World.class)
public class WorldMixins implements IWorldMixins {
    
    /*
        Changes:
        - Added getWeakChanges instead of hardcoding the comparator ID
    */
    @Overwrite
    public void func_96440_m(int x, int y, int z, int neighborId) {
        World self = (World)(Object)this;
#if FULL_3D_COMPARATOR_UPDATES
        int facing = 0;
        do {
            int nextX = x + Facing.offsetsXForSide[facing];
            int nextY = y + Facing.offsetsYForSide[facing];
            int nextZ = z + Facing.offsetsZForSide[facing];
            
            Block block = Block.blocksList[self.getBlockId(nextX, nextY, nextZ)];
            
            if (!BLOCK_IS_AIR(block)) {
                if (((IBlockMixins)block).getWeakChanges(self, nextX, nextY, nextZ, neighborId)) {
                    block.onNeighborBlockChange(self, nextX, nextY, nextZ, neighborId);
                }
                else if (
#if ENABLE_MODERN_REDSTONE_WIRE
                    ((IBlockMixins)block).isRedstoneConductor(self, nextX, y, nextZ)
#else
                    block.isNormalCube()
#endif
                ) {
                    nextX += Facing.offsetsXForSide[facing];
                    nextY += Facing.offsetsYForSide[facing];
                    nextZ += Facing.offsetsZForSide[facing];
                    block = Block.blocksList[self.getBlockId(nextX, nextY, nextZ)];
                    if (
                        !BLOCK_IS_AIR(block) &&
                        ((IBlockMixins)block).getWeakChanges(self, nextX, nextY, nextZ, neighborId)
                    ) {
                        block.onNeighborBlockChange(self, nextX, nextY, nextZ, neighborId);
                    }
                }
            }
        } while (DIRECTION_IS_VALID(++facing));
#else
        for (int i = 0; i < 4; ++i) {
            int nextX = x + Direction.offsetX[i];
            int nextZ = z + Direction.offsetZ[i];
            int blockId = self.getBlockId(nextX, y, nextZ);

            if (blockId != 0) {
                Block block = Block.blocksList[blockId];
                if (((IBlockMixins)block).getWeakChanges(self, nextX, y, nextZ, neighborId)) {
                    block.onNeighborBlockChange(self, nextX, y, nextZ, neighborId);
                }
                else if (
#if ENABLE_MODERN_REDSTONE_WIRE
                    ((IBlockMixins)block).isRedstoneConductor(self, nextX, y, nextZ)
#else
                    Block.isNormalCube(blockId)
#endif
                ) {
                    nextX += Direction.offsetX[i];
                    nextZ += Direction.offsetZ[i];
                    block = Block.blocksList[self.getBlockId(nextX, y, nextZ)];
                    if (
                        !BLOCK_IS_AIR(block) &&
                        ((IBlockMixins)block).getWeakChanges(self, nextX, y, nextZ, neighborId)
                    ) {
                        block.onNeighborBlockChange(self, nextX, y, nextZ, neighborId);
                    }
                }
            }
        }
#endif
    }
    
    public void notifyBlockChangeAndComparators(int x, int y, int z, int blockId, int prevBlockId) {
        World self = (World)(Object)this;
        self.notifyBlockChange(x, y, z, prevBlockId);
        Block block = Block.blocksList[blockId];
        if (
            block != null &&
            block.hasComparatorInputOverride()
        ) {
            self.func_96440_m(x, y, z, blockId);
        }
    }
    
#if ENABLE_DIRECTIONAL_UPDATES

    public void updateNeighbourShapes(int x, int y, int z, int flags) {
        World world = (World)(Object)this;
        
        IBlockMixins neighborBlock;
        boolean allowDrops = (flags & UPDATE_SUPPRESS_DROPS) == 0;
        flags &= ~UPDATE_SUPPRESS_DROPS;
        int neighborMeta;
        int newMeta;
        
        // Offset from neutral to west
        --x;
        neighborBlock = (IBlockMixins)Block.blocksList[world.getBlockId(x, y, z)];
        if (!BLOCK_IS_AIR(neighborBlock)) {
            neighborMeta = world.getBlockMetadata(x, y, z);
            newMeta = neighborBlock.updateShape(world, x, y, z, DIRECTION_EAST, neighborMeta);
            if (newMeta != neighborMeta) {
                if (newMeta >= 0) {
                    world.setBlockMetadataWithNotify(x, y, z, flags);
                } else {
                    world.destroyBlock(x, y, z, allowDrops);
                }
            }
        }
        // Offset from west to east
        x += 2;
        neighborBlock = (IBlockMixins)Block.blocksList[world.getBlockId(x, y, z)];
        if (!BLOCK_IS_AIR(neighborBlock)) {
            neighborMeta = world.getBlockMetadata(x, y, z);
            newMeta = neighborBlock.updateShape(world, x, y, z, DIRECTION_WEST, neighborMeta);
            if (newMeta != neighborMeta) {
                if (newMeta >= 0) {
                    world.setBlockMetadataWithNotify(x, y, z, flags);
                } else {
                    world.destroyBlock(x, y, z, allowDrops);
                }
            }
        }
        // Offset from east to north
        --x;
        --z;
        neighborBlock = (IBlockMixins)Block.blocksList[world.getBlockId(x, y, z)];
        if (!BLOCK_IS_AIR(neighborBlock)) {
            neighborMeta = world.getBlockMetadata(x, y, z);
            newMeta = neighborBlock.updateShape(world, x, y, z, DIRECTION_SOUTH, neighborMeta);
            if (newMeta != neighborMeta) {
                if (newMeta >= 0) {
                    world.setBlockMetadataWithNotify(x, y, z, flags);
                } else {
                    world.destroyBlock(x, y, z, allowDrops);
                }
            }
        }
        // Offset from north to south
        z += 2;
        neighborBlock = (IBlockMixins)Block.blocksList[world.getBlockId(x, y, z)];
        if (!BLOCK_IS_AIR(neighborBlock)) {
            neighborMeta = world.getBlockMetadata(x, y, z);
            newMeta = neighborBlock.updateShape(world, x, y, z, DIRECTION_NORTH, neighborMeta);
            if (newMeta != neighborMeta) {
                if (newMeta >= 0) {
                    world.setBlockMetadataWithNotify(x, y, z, flags);
                } else {
                    world.destroyBlock(x, y, z, allowDrops);
                }
            }
        }
        // Offset from south to down
        --z;
        --y;
        neighborBlock = (IBlockMixins)Block.blocksList[world.getBlockId(x, y, z)];
        if (!BLOCK_IS_AIR(neighborBlock)) {
            neighborMeta = world.getBlockMetadata(x, y, z);
            newMeta = neighborBlock.updateShape(world, x, y, z, DIRECTION_UP, neighborMeta);
            if (newMeta != neighborMeta) {
                if (newMeta >= 0) {
                    world.setBlockMetadataWithNotify(x, y, z, flags);
                } else {
                    world.destroyBlock(x, y, z, allowDrops);
                }
            }
        }
        // Offset from down to up
        y += 2;
        neighborBlock = (IBlockMixins)Block.blocksList[world.getBlockId(x, y, z)];
        if (!BLOCK_IS_AIR(neighborBlock)) {
            neighborMeta = world.getBlockMetadata(x, y, z);
            newMeta = neighborBlock.updateShape(world, x, y, z, DIRECTION_DOWN, neighborMeta);
            if (newMeta != neighborMeta) {
                if (newMeta >= 0) {
                    world.setBlockMetadataWithNotify(x, y, z, flags);
                } else {
                    world.destroyBlock(x, y, z, allowDrops);
                }
            }
        }
    }
    
    public int updateFromNeighborShapes(int x, int y, int z, int blockId, int meta) {
        IBlockMixins block = (IBlockMixins)Block.blocksList[blockId];
        if (!BLOCK_IS_AIR(block)) {
            World world = (World)(Object)this;
            meta = block.updateShape(world, x, y, z, DIRECTION_WEST, meta);
            if (meta >= 0) meta = block.updateShape(world, x, y, z, DIRECTION_EAST, meta);
            if (meta >= 0) meta = block.updateShape(world, x, y, z, DIRECTION_NORTH, meta);
            if (meta >= 0) meta = block.updateShape(world, x, y, z, DIRECTION_SOUTH, meta);
            if (meta >= 0) meta = block.updateShape(world, x, y, z, DIRECTION_DOWN, meta);
            if (meta >= 0) meta = block.updateShape(world, x, y, z, DIRECTION_UP, meta);
        }
        return meta;
    }

    @Overwrite
    public boolean setBlock(int x, int y, int z, int blockId, int meta, int flags) {
        if (IS_VALID_BLOCK_XYZ_POS(x, y, z)) {
            World world = (World)(Object)this;
            Chunk chunk = world.getChunkFromChunkCoords(x >> 4, z >> 4);
            int currentBlockId = 0;
            if (
                //(flags & (UPDATE_NEIGHBORS | UPDATE_KNOWN_SHAPE)) != UPDATE_KNOWN_SHAPE
                (flags & UPDATE_NEIGHBORS) != 0
            ) {
                currentBlockId = chunk.getBlockID(x & 0xF, y, z & 0xF);
            }
            
            boolean blockChanged = chunk.setBlockIDWithMetadata(x & 0xF, y, z & 0xF, blockId, meta);
            
            if ((flags & UPDATE_SUPPRESS_LIGHT) == 0) {
                world.theProfiler.startSection("checkLight");
                world.updateAllLightTypes(x, y, z);
                world.theProfiler.endSection();
            }
            
            if (blockChanged) {
                if (
                    (flags & UPDATE_CLIENTS) != 0 &&
                    (
                        !world.isRemote ||
                        (flags & UPDATE_INVISIBLE) == 0
                    )
                ) {
                    world.markBlockForUpdate(x, y, z);
                }
                //Block block;
                if (
                    !world.isRemote &&
                    (flags & UPDATE_NEIGHBORS) != 0
                ) {
                    this.notifyBlockChangeAndComparators(x, y, z, blockId, currentBlockId);
                    /*
                    world.notifyBlockChange(x, y, z, currentBlockId);
                    Block block = Block.blocksList[blockId];
                    if (
                        !BLOCK_IS_AIR(block) &&
                        block.hasComparatorInputOverride()
                    ) {
                        world.func_96440_m(x, y, z, blockId);
                    }
                    */
                }
                if (
                    (flags & UPDATE_KNOWN_SHAPE) == 0
                ) {
                    //block = Block.blocksList[currentBlockId];
                    //if (!BLOCK_IS_AIR(block)) {
                        //((IBlockMixins)block).updateIndirectNeighbourShapes(world, x, y, z);
                    //}
                    this.updateNeighbourShapes(x, y, z, flags & ~(UPDATE_NEIGHBORS | UPDATE_SUPPRESS_DROPS));
                    //block = Block.blocksList[blockId];
                    //if (!BLOCK_IS_AIR(block)) {
                        //((IBlockMixins)block).updateIndirectNeighbourShapes(world, x, y, z);
                    //}
                }
            }
            return blockChanged;
        }
        return false;
    }
    
    @Overwrite
    public boolean setBlockMetadataWithNotify(int x, int y, int z, int meta, int flags) {
        if (IS_VALID_BLOCK_XYZ_POS(x, y, z)) {
            World world = (World)(Object)this;
            Chunk chunk = world.getChunkFromChunkCoords(x >> 4, z >> 4);
            boolean blockChanged = chunk.setBlockMetadata(x & 0xF, y, z & 0xF, meta);
            
            // Should this be enabled?
#if ENABLE_LIGHT_UPDATES_ON_METADATA_CHANGE
            if ((flags & UPDATE_SUPPRESS_LIGHT) == 0) {
                world.theProfiler.startSection("checkLight");
                world.updateAllLightTypes(x, y, z);
                world.theProfiler.endSection();
            }
#endif
            
            if (blockChanged) {
                //int currentBlockId = 0;
                //if (
                    //(flags & (UPDATE_NEIGHBORS | UPDATE_KNOWN_SHAPE)) != UPDATE_KNOWN_SHAPE
                //) {
                    //currentBlockId = chunk.getBlockID(x & 0xF, y, z & 0xF);
                //}
                if (
                    (flags & UPDATE_CLIENTS) != 0 &&
                    (
                        !world.isRemote ||
                        (flags & UPDATE_INVISIBLE) == 0
                    )
                ) {
                    world.markBlockForUpdate(x, y, z);
                }
                //Block block = Block.blocksList[currentBlockId];
                if (
                    !world.isRemote &&
                    (flags & UPDATE_NEIGHBORS) != 0
                ) {
                    int currentBlockId = chunk.getBlockID(x & 0xF, y, z & 0xF);
                    this.notifyBlockChangeAndComparators(x, y, z, currentBlockId, currentBlockId);
                    /*
                    world.notifyBlockChange(x, y, z, currentBlockId);
                    Block block = Block.blocksList[currentBlockId];
                    if (
                        !BLOCK_IS_AIR(block) &&
                        block.hasComparatorInputOverride()
                    ) {
                        world.func_96440_m(x, y, z, currentBlockId);
                    }
                    */
                }
                if (
                    (flags & UPDATE_KNOWN_SHAPE) == 0
                ) {
                    //if (!BLOCK_IS_AIR(block)) {
                        //((IBlockMixins)block).updateIndirectNeighbourShapes(world, x, y, z);
                    //}
                    this.updateNeighbourShapes(x, y, z, flags & ~(UPDATE_NEIGHBORS | UPDATE_SUPPRESS_DROPS));
                }
            }
            return blockChanged;
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
    public boolean isBlockGettingPowered(int x, int y, int z) {
        return ((World)(Object)this).isBlockIndirectlyGettingPowered(x, y, z);
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
    public boolean isBlockNormalCube_redirect(World world, int x, int y, int z) {
        return ((IWorldMixins)world).isBlockRedstoneConductor(x, y, z);
    }
#endif

    public int getBlockStrongPowerInputExceptFacing(int x, int y, int z, int facing) {
        World self = (World)(Object)this;
        
        int power = 0;
        int direction = 0;
        do {
            if (direction != facing) {
                power = Math.max(power, self.isBlockProvidingPowerTo(
                    x + Facing.offsetsXForSide[direction],
                    y + Facing.offsetsYForSide[direction],
                    z + Facing.offsetsZForSide[direction],
                    direction
                ));
                if (power >= MAX_REDSTONE_POWER) {
                    break;
                }
            }
        } while (DIRECTION_IS_VALID(++direction));
        return power;
    }
    
    public int getBlockWeakPowerInputExceptFacing(int x, int y, int z, int facing) {
        World self = (World)(Object)this;
        
        int power = 0;
        int direction = 0;
        do {
            if (direction != facing) {
                power = Math.max(power, self.getIndirectPowerLevelTo(
                    x + Facing.offsetsXForSide[direction],
                    y + Facing.offsetsYForSide[direction],
                    z + Facing.offsetsZForSide[direction],
                    direction
                ));
                if (power >= MAX_REDSTONE_POWER) {
                    break;
                }
            }
        } while (DIRECTION_IS_VALID(++direction));
        return power;
    }

#if ENABLE_MODERN_SUPPORT_LOGIC == MODERN_SUPPORT_LOGIC_GLOBAL
    @Overwrite
    public boolean doesBlockHaveSolidTopSurface(int x, int y, int z) {
        World self = (World)(Object)this;
        Block block = Block.blocksList[self.getBlockId(x, y, z)];
        return !BLOCK_IS_AIR(block) && block.hasLargeCenterHardPointToFacing(self, x, y, z, DIRECTION_UP, true);
    }
#endif
/*
    @Overwrite
    public List getCollidingBoundingBoxes(Entity entity, AxisAlignedBB mask) {
        World self = (World)(Object)this;
        
        List<AxisAlignedBB> collisionList = ((IWorldAccessMixins)self).getCollidingBoundingBoxes();
        collisionList.clear();
        
        int minX = MathHelper.floor_double(mask.minX);
        int maxX = MathHelper.floor_double(mask.maxX + 1.0D);
        int minY = MathHelper.floor_double(mask.minY);
        int maxY = MathHelper.floor_double(mask.maxY + 1.0D);
        int minZ = MathHelper.floor_double(mask.minZ);
        int maxZ = MathHelper.floor_double(mask.maxZ + 1.0D);

        for (int X = minX - 1; X <= maxX; ++X) {
            for (int Z = minZ - 1; Z <= maxZ; ++Z) {
                if (self.blockExists(X, 0, Z)) {
                    for (int Y = minY - 1; Y < maxY; ++Y) {
                        Block block = Block.blocksList[self.getBlockId(X, Y, Z)];

                        if (block != null) {
                            block.addCollisionBoxesToList(self, X, Y, Z, mask, collisionList, entity);
                        }
                    }
                }
            }
        }

        List<Entity> entityList = self.getEntitiesWithinAABBExcludingEntity(entity, mask.expand(0.25D, 2.0D, 0.25D));
        
        for (Entity entityInList : entityList) {
            // FCMOD: Code added
        	if (!entity.canCollideWithEntity(entityInList)) {
        		continue;
        	}
        	// END FCMOD
            AxisAlignedBB tempBox = entityInList.getBoundingBox();

            if (tempBox != null && tempBox.intersectsWith(mask)) {
                collisionList.add(tempBox);
            }

            tempBox = entity.getCollisionBox(entityInList);

            if (tempBox != null && tempBox.intersectsWith(mask)) {
                collisionList.add(tempBox);
            }
        }

        return collisionList;
    }
    
    @Overwrite
    public List getCollidingBlockBounds(AxisAlignedBB mask) {
        World self = (World)(Object)this;
        
        List<AxisAlignedBB> collisionList = ((IWorldAccessMixins)self).getCollidingBoundingBoxes();
        collisionList.clear();
        
        int minX = MathHelper.floor_double(mask.minX);
        int maxX = MathHelper.floor_double(mask.maxX + 1.0D);
        int minY = MathHelper.floor_double(mask.minY);
        int maxY = MathHelper.floor_double(mask.maxY + 1.0D);
        int minZ = MathHelper.floor_double(mask.minZ);
        int maxZ = MathHelper.floor_double(mask.maxZ + 1.0D);

        for (int X = minX - 1; X <= maxX; ++X) {
            for (int Z = minZ - 1; Z <= maxZ; ++Z) {
                if (self.blockExists(X, 0, Z)) {
                    for (int Y = minY - 1; Y < maxY; ++Y) {
                        Block block = Block.blocksList[self.getBlockId(X, Y, Z)];
                        if (block != null) {
                            block.addCollisionBoxesToList(self, X, Y, Z, mask, collisionList, (Entity)null);
                        }
                    }
                }
            }
        }

        return collisionList;
    }
*/

    // These fix entities falling through moving blocks (MC-1230)
    @Redirect(
        method = { "getCollidingBoundingBoxes", "getCollidingBlockBounds" },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/MathHelper;floor_double(D)I",
            ordinal = 0
        )
    )
    public int floor_double_minX_redirect(double value) {
        return MathHelper.floor_double(value) - 1;
    }
    @Redirect(
        method = { "getCollidingBoundingBoxes", "getCollidingBlockBounds" },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/MathHelper;floor_double(D)I",
            ordinal = 1
        )
    )
    public int floor_double_maxX_redirect(double value) {
        return MathHelper.floor_double(value) + 1;
    }
    @Redirect(
        method = { "getCollidingBoundingBoxes", "getCollidingBlockBounds" },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/MathHelper;floor_double(D)I",
            ordinal = 4
        )
    )
    public int floor_double_minZ_redirect(double value) {
        return MathHelper.floor_double(value) - 1;
    }
    @Redirect(
        method = { "getCollidingBoundingBoxes", "getCollidingBlockBounds" },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/MathHelper;floor_double(D)I",
            ordinal = 5
        )
    )
    public int floor_double_maxZ_redirect(double value) {
        return MathHelper.floor_double(value) + 1;
    }

    public boolean isRailBlockWithExitTowards(int x, int y, int z, int direction) {
        World self = (World)(Object)this;
        Block block = Block.blocksList[self.getBlockId(x, y, z)];
        if (block instanceof BlockRailBase) {
            BlockRailBase railBlock = (BlockRailBase)block;
            int meta = self.getBlockMetadata(x, y, z);
            if (railBlock.isPowered()) {
                meta &= 7;
            }
            meta += meta;
            return ZeroUtil.rail_exit_directions[meta] == direction || ZeroUtil.rail_exit_directions[meta + 1] == direction;
        }
        return false;
    }
    
#if ENABLE_LIGHT_STICKS
    public long lightOverrideCoord = BLOCK_POS_HASH_IMPOSSIBLE;
    public int lightOverride;
    
    @Override
    public void setLightOverride(int x, int y, int z, int light) {
        World self = (World)(Object)this;
        
        //Chunk chunk = self.getChunkFromChunkCoords(x >> 4, z >> 4);
        
        //boolean modified = chunk.isModified;
        
        
        this.lightOverrideCoord = BLOCK_POS_HASH_PACK(x, y, z);
        this.lightOverride = light;
        
        self.updateAllLightTypes(x, y, z);
        
        //self.markBlockForRenderUpdate(x, y, z);
        
        //if (self.isRemote) {
            //self.markBlockForUpdate(x, y, z);
            //self.notifyBlockChange(x, y, z, self.getBlockId(x, y, z));
        //}
        
        this.lightOverrideCoord = BLOCK_POS_HASH_IMPOSSIBLE;
        
        //chunk.isModified = modified;
        //
    }
    
    @Overwrite
    public int computeLightValue(int x, int y, int z, EnumSkyBlock lightType) {
        World self = (World)(Object)this;
        if (
            lightType != EnumSkyBlock.Sky ||
            !self.canBlockSeeTheSky(x, y, z)
        ) {
            int blockId = self.getBlockId(x, y, z);
            Block block = Block.blocksList[blockId];
            int light = 0;
            if (lightType != EnumSkyBlock.Sky) {
                light = Block.getLightValueForBlock(self, x, y, z, block);
                if (
                    light < this.lightOverride &&
                    this.lightOverrideCoord == BLOCK_POS_HASH_PACK(x, y, z)
                ) {
                    light = this.lightOverride;
                }
            }
            
            int opacity = Block.lightOpacity[blockId];
            if (opacity >= 15) {
                if (Block.getLightValueForBlock(self, x, y, z, block) <= 0) {
                    return 0;
                }
                opacity = 1;
            }
            else if (opacity <= 0) {
                opacity = 1;
            }
            
            if (light < 14) {
                int facing = 0;
                do {
                    light = Math.max(
                        light,
                        self.getSavedLightValue(
                            lightType,
                            x + Facing.offsetsXForSide[facing],
                            y + Facing.offsetsYForSide[facing],
                            z + Facing.offsetsZForSide[facing]
                        ) - opacity
                    );
                } while (light < 14 && ++facing <= 5);
            }
            return light;
        }
        return 15;
    }
#endif
}