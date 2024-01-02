package zero.test.mixin;

import net.minecraft.src.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import zero.test.IWorldMixins;
import zero.test.mixin.IPistonBaseAccessMixins;
import zero.test.IBlockEntityPistonMixins;
import zero.test.mixin.IBlockEntityPistonAccessMixins;
import zero.test.IBlockMixins;

import btw.block.BTWBlocks;
import btw.client.fx.BTWEffectManager;
import btw.crafting.manager.PistonPackingCraftingManager;
import btw.crafting.recipe.types.PistonPackingRecipe;
import btw.item.util.ItemUtils;
import btw.world.util.BlockPos;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.Sys;
import btw.AddonHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

#include "..\func_aliases.h"
#include "..\feature_flags.h"
#include "..\util.h"
#include "..\ids.h"

#define PISTON_TILE_ENTITY_PRINT_DEBUGGING 0

#if PISTON_TILE_ENTITY_PRINT_DEBUGGING
#define PISTON_TILE_ENTITY_DEBUG(...)
#else
#define PISTON_TILE_ENTITY_DEBUG(...)
#endif

@Mixin(TileEntityPiston.class)
public class BlockEntityPistonMixins extends TileEntity implements IBlockEntityPistonMixins {
    
    public long lastTicked;
    
    public boolean hasLargeCenterHardPointToFacing(int x, int y, int z, int direction, boolean ignoreTransparency) {
        TileEntityPiston self = (TileEntityPiston)(Object)this;
        if (((IBlockEntityPistonAccessMixins)self).getProgress() >= 1.0F) {
            
            int storedBlockId = self.getStoredBlockID();
            Block storedBlock = Block.blocksList[storedBlockId];
            if (!BLOCK_IS_AIR(storedBlock)) {
                int storedMeta = self.getBlockMetadata();
                int prevMeta = self.worldObj.getBlockMetadata(x, y, z);
                
                // Since the block is likely to try getting its own metadata,
                // silently swap out the metadata value during the face test
                self.worldObj.setBlockMetadataWithNotify(x, y, z, storedMeta, UPDATE_INVISIBLE | UPDATE_KNOWN_SHAPE | UPDATE_SUPPRESS_LIGHT);
                boolean ret = storedBlock.hasLargeCenterHardPointToFacing(self.worldObj, x, y, z, direction, ignoreTransparency);
                self.worldObj.setBlockMetadataWithNotify(x, y, z, prevMeta, UPDATE_INVISIBLE | UPDATE_KNOWN_SHAPE | UPDATE_SUPPRESS_LIGHT);
                //if (!ret) {
                    //AddonHandler.logMessage("SupportPoint FAIL BLOCK FALSE "+storedBlockId+"("+storedMeta+")");
                //}
                return ret;
            }
            //AddonHandler.logMessage("SupportPoint FAIL AIR");
            //return false;
        }
        //AddonHandler.logMessage("SupportPoint FAIL PROGRESS "+((IBlockEntityPistonAccessMixins)self).getProgress());
        return false;
    }
    
    @Overwrite
    public void restoreStoredBlock() {
        TileEntityPiston self = (TileEntityPiston)(Object)this;
        
        //if (!self.worldObj.isRemote) AddonHandler.logMessage("Restore block ("+self.xCoord+" "+self.yCoord+" "+self.zCoord+") at time "+last_ticked);
        
        int storedBlockId = self.getStoredBlockID();
        Block storedBlock = Block.blocksList[storedBlockId];
        int storedMeta = self.getBlockMetadata();
        if (!BLOCK_IS_AIR(storedBlock)) {
            ((IWorldMixins)self.worldObj).updateFromNeighborShapes(self.xCoord, self.yCoord, self.zCoord, storedBlockId, storedMeta);
        }
        
        // Set scanningTileEntities to true
        // so that the tile entity is always
        // placed correctly
        boolean scanningTileEntitiesTemp = ((IWorldAccessMixins)self.worldObj).getScanningTileEntities();
        ((IWorldAccessMixins)self.worldObj).setScanningTileEntities(true);
        
        self.worldObj.setBlock(self.xCoord, self.yCoord, self.zCoord, storedBlockId, storedMeta, UPDATE_NEIGHBORS | UPDATE_CLIENTS);
        
        //if (!self.worldObj.isRemote) AddonHandler.logMessage("PLACE BLOCK "+storedBlockId+"."+storedMeta);
#if PISTON_TILE_ENTITY_PRINT_DEBUGGING
        NBTTagCompound prevTileEntityData = self.storedTileEntityData;
#endif
        if (self.storedTileEntityData != null) {
            // setBlockTileEntity updates the entity
            // coordinates itself when scanningTileEntities
            // is true
            worldObj.setBlockTileEntity(self.xCoord, self.yCoord, self.zCoord, TileEntity.createAndLoadEntity(self.storedTileEntityData));
            self.cachedTileEntity = null;
        }
        
        self.worldObj.notifyBlockOfNeighborChange(self.xCoord, self.yCoord, self.zCoord, storedBlockId);
        
#if PISTON_TILE_ENTITY_PRINT_DEBUGGING
        NBTTagCompound newTileEntityData = null;
        TileEntity newTileEntity = self.worldObj.getBlockTileEntity(self.xCoord, self.yCoord, self.zCoord);
        if (newTileEntity != null) {
            newTileEntityData = new NBTTagCompound();
            newTileEntity.writeToNBT(newTileEntityData);
        }
        if (
            prevTileEntityData != null &&
            prevTileEntityData != newTileEntityData
        ) {
            AddonHandler.logMessage("META FAIL");
            AddonHandler.logMessage("Old: "+prevTileEntityData.toString());
            if (newTileEntityData != null) {
                AddonHandler.logMessage("New: "+newTileEntityData.toString());
            } else {
                AddonHandler.logMessage("New: null");
            }
        }
#endif
        
        // Restore original value of scanningTileEntities
        ((IWorldAccessMixins)self.worldObj).setScanningTileEntities(scanningTileEntitiesTemp);
        
        //if (storedBlockId != this.worldObj.getBlockId(self.xCoord, self.yCoord, self.zCoord)) {
            //AddonHandler.logMessage("PLACE BLOCK BROKE DURING UPDATE "+storedBlockId+"."+storedMeta);
        //}
    }
    
    @Overwrite
    public void clearPistonTileEntity() {
        TileEntityPiston self = (TileEntityPiston)(Object)this;
        if (
            ((IBlockEntityPistonAccessMixins)self).getLastProgress() < 1.0F &&
            self.worldObj != null
        ) {
            ((IBlockEntityPistonAccessMixins)self).setProgress(1.0F);
            ((IBlockEntityPistonAccessMixins)self).setLastProgress(1.0F);
            self.worldObj.removeBlockTileEntity(self.xCoord, self.yCoord, self.zCoord);
            self.invalidate();
            
            if (
                self.worldObj.getBlockId(self.xCoord, self.yCoord, self.zCoord) == Block.pistonMoving.blockID &&
                !((IBlockEntityPistonAccessMixins)self).callDestroyAndDropIfShoveled()
            ) {
                ((IBlockEntityPistonAccessMixins)self).callPreBlockPlaced();
                self.restoreStoredBlock();
            }
        }
    }
    
    
    @Inject(
        method = "updateEntity()V",
        at = @At("HEAD")
    )
    public void track_update_tick(CallbackInfo info) {
        this.lastTicked = this.worldObj.getTotalWorldTime();
    }
    
    public long getLastTicked() {
        return this.lastTicked;
    }
    public void setLastTicked(long time) {
        this.lastTicked = time;
    }
    
    @Overwrite
    public void updatePushedObjects(float progress, float par2) {
        TileEntityPiston self = (TileEntityPiston)(Object)this;
        boolean extending = self.isExtending();
        
        double d = par2;
        
        int storedDirection = self.getPistonOrientation();
        int storedBlockId = self.getStoredBlockID();
        boolean isBouncy = false;
        boolean isSticky = false;
        Block block = Block.blocksList[storedBlockId];
        if (!BLOCK_IS_AIR(block)) {
            int storedMeta = self.getBlockMetadata();
            if (extending) {
                isBouncy = ((IBlockMixins)(Object)block).isBouncyWhenMoved(storedDirection, storedMeta);
            }
            isSticky = ((IBlockMixins)(Object)block).isStickyForEntitiesWhenMoved(storedDirection, storedMeta);
        }
        
        AxisAlignedBB boundingBox = Block.pistonMoving.getAxisAlignedBB(self.worldObj, self.xCoord, self.yCoord, self.zCoord, storedBlockId, extending ? 1.0F - progress : progress - 1.0F, storedDirection);
        if (boundingBox != null) {
            List entityList = self.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)null, boundingBox);
            
            if (!entityList.isEmpty()) {
                List pushedObjects = ((IBlockEntityPistonAccessMixins)self).getPushedObjects();
                
                pushedObjects.addAll(entityList);
                Iterator var5 = pushedObjects.iterator();
                
                while (var5.hasNext()) {
                    Entity entity = (Entity)var5.next();
                    entity.moveEntity(
                        d * (double)Facing.offsetsXForSide[storedDirection],
                        d * (double)Facing.offsetsYForSide[storedDirection],
                        d * (double)Facing.offsetsZForSide[storedDirection]
                    );
                    if (isBouncy) {
                        entity.motionX += (double)Facing.offsetsXForSide[storedDirection];
                        entity.motionY += (double)Facing.offsetsYForSide[storedDirection];
                        entity.motionZ += (double)Facing.offsetsZForSide[storedDirection];
                    }
                }
                pushedObjects.clear();
            }
        }
        float lastProgress;
        if (isSticky && (lastProgress = ((IBlockEntityPistonAccessMixins)self).getLastProgress()) < 1.0F) {
            //boundingBox = Block.pistonMoving.getAxisAlignedBB(self.worldObj, self.xCoord, self.yCoord, self.zCoord, storedBlockId, extending ? 1.0F - progress : progress - 1.0F, storedDirection);
            
            boundingBox = block.getAsPistonMovingBoundingBox(self.worldObj, self.xCoord - Facing.offsetsXForSide[storedDirection], self.yCoord - Facing.offsetsYForSide[storedDirection], self.zCoord - Facing.offsetsZForSide[storedDirection]);
            
            double progressOffset = (storedDirection & 1) == 0 ? -lastProgress : lastProgress;
            switch (storedDirection) {
                case DIRECTION_DOWN: case DIRECTION_UP:
                    boundingBox.minX = Math.floor(boundingBox.minX);
                    boundingBox.minY += progressOffset;
                    boundingBox.minZ = Math.floor(boundingBox.minZ);
                    boundingBox.maxX = Math.ceil(boundingBox.maxX);
                    boundingBox.maxY += progressOffset;
                    boundingBox.maxZ = Math.ceil(boundingBox.maxZ);
                    break;
                case DIRECTION_NORTH: case DIRECTION_SOUTH:
                    boundingBox.minX = Math.floor(boundingBox.minX);
                    boundingBox.minY = Math.floor(boundingBox.minY);
                    boundingBox.minZ += progressOffset;
                    boundingBox.maxX = Math.ceil(boundingBox.maxX);
                    boundingBox.maxY = Math.ceil(boundingBox.maxY);
                    boundingBox.maxZ += progressOffset;
                    break;
                default:
                    boundingBox.minX += progressOffset;
                    boundingBox.minY = Math.floor(boundingBox.minY);
                    boundingBox.minZ = Math.floor(boundingBox.minZ);
                    boundingBox.maxX += progressOffset;
                    boundingBox.maxY = Math.ceil(boundingBox.maxY);
                    boundingBox.maxZ = Math.ceil(boundingBox.maxZ);
                    break;
            }
            List entityList = self.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)null, boundingBox);
            
            /*
            if (!self.worldObj.isRemote) {
                AddonHandler.logMessage(""+self.worldObj.getTotalWorldTime()+" "+progress+" "+lastProgress+" "+d);
                AddonHandler.logMessage(" "+boundingBox.minX+" "+boundingBox.minY+" "+boundingBox.minZ);
                AddonHandler.logMessage(" "+boundingBox.maxX+" "+boundingBox.maxY+" "+boundingBox.maxZ);
            }
            */
            
            if (!entityList.isEmpty()) {
                List pushedObjects = ((IBlockEntityPistonAccessMixins)self).getPushedObjects();
                
                pushedObjects.addAll(entityList);
                Iterator var5 = pushedObjects.iterator();
                
                d = progress - lastProgress;
                while (var5.hasNext()) {
                    ((Entity)var5.next()).moveEntity(
                        d * (double)Facing.offsetsXForSide[storedDirection],
                        d * (double)Facing.offsetsYForSide[storedDirection],
                        d * (double)Facing.offsetsZForSide[storedDirection]
                    );
                }
                pushedObjects.clear();
            }
        }
    }
}