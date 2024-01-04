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
import zero.test.IEntityMixins;

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
#define PISTON_TILE_ENTITY_DEBUG(...) if (!self.worldObj.isRemote) AddonHandler.logMessage(__VA_ARGS__)
#else
#define PISTON_TILE_ENTITY_DEBUG(...)
#endif

#define PISTON_ENTITY_PUSH_DEBUGGING 0

#if PISTON_ENTITY_PUSH_DEBUGGING
#define PISTON_ENTITY_PUSH_DEBUG(...) if (!self.worldObj.isRemote) AddonHandler.logMessage(__VA_ARGS__)
#else
#define PISTON_ENTITY_PUSH_DEBUG(...)
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
        int newMeta = -1;
        if (!BLOCK_IS_AIR(storedBlock)) {
            newMeta = ((IWorldMixins)self.worldObj).updateFromNeighborShapes(self.xCoord, self.yCoord, self.zCoord, storedBlockId, storedMeta);
        }
        
        // Set scanningTileEntities to true
        // so that the tile entity is always
        // placed correctly
        boolean scanningTileEntitiesTemp = ((IWorldAccessMixins)self.worldObj).getScanningTileEntities();
        ((IWorldAccessMixins)self.worldObj).setScanningTileEntities(true);
        
        //self.worldObj.setBlock(self.xCoord, self.yCoord, self.zCoord, storedBlockId, storedMeta, UPDATE_NEIGHBORS | UPDATE_CLIENTS);
        
        if (newMeta >= 0) {
            self.worldObj.setBlock(self.xCoord, self.yCoord, self.zCoord, storedBlockId, newMeta, UPDATE_NEIGHBORS | UPDATE_CLIENTS | UPDATE_MOVE_BY_PISTON);
        } else {
            self.worldObj.setBlock(self.xCoord, self.yCoord, self.zCoord, storedBlockId, storedMeta, UPDATE_INVISIBLE | UPDATE_KNOWN_SHAPE | UPDATE_MOVE_BY_PISTON | UPDATE_SUPPRESS_LIGHT);
        }
        
        //if (!self.worldObj.isRemote) AddonHandler.logMessage("PLACE BLOCK "+storedBlockId+"."+newMeta);
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
        
        if (newMeta >= 0) {
            self.worldObj.notifyBlockOfNeighborChange(self.xCoord, self.yCoord, self.zCoord, storedBlockId);
        } else {
            self.worldObj.destroyBlock(self.xCoord, self.yCoord, self.zCoord, true);
        }
        
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
    
    /*
    @Inject(
        method = "updateEntity()V",
        at = @At("HEAD")
    )
    public void track_update_tick(CallbackInfo info) {
        this.lastTicked = this.worldObj.getTotalWorldTime();
    }
    */
    
    
    public long getLastTicked() {
        return this.lastTicked;
    }
    public void setLastTicked(long time) {
        this.lastTicked = time;
    }
    
    public boolean noclip;
    
    //protected static final ThreadLocal<Integer> NOCLIP_DIRECTION = ThreadLocal.withInitial(() -> -1);
    
    public void getCollisionList(AxisAlignedBB maskBox, List list) {
        if (!this.noclip) {
            TileEntityPiston self = (TileEntityPiston)(Object)this;
            List<AxisAlignedBB> collisionBoxes = new ArrayList();
            
            int storedBlockId = self.getStoredBlockID();
            Block block = Block.blocksList[storedBlockId];
            if (!BLOCK_IS_AIR(block)) {
                // Hopefully no blocks are big enough to go outside this?
                // TODO: Check whether these are corner aligned...
                AxisAlignedBB fakeMask = AxisAlignedBB.getAABBPool().getAABB(self.xCoord - 1.0D, self.yCoord - 1.0D, self.zCoord - 1.0D, self.xCoord + 2.0D, self.yCoord + 2.0D, self.zCoord + 2.0D);
                
                block.addCollisionBoxesToList(self.worldObj, self.xCoord, self.yCoord, self.zCoord, fakeMask, collisionBoxes, (Entity)null);
                
                if (!collisionBoxes.isEmpty()) {
                    boolean isExtending = self.isExtending();
                    int direction = self.getPistonOrientation();
                    
                    if (!isExtending) {
                        direction = OPPOSITE_DIRECTION(direction);
                    }
                    
                    double dX = (double)Facing.offsetsXForSide[direction];
                    double dY = (double)Facing.offsetsYForSide[direction];
                    double dZ = (double)Facing.offsetsZForSide[direction];
                    
                    double progress = (double)((IBlockEntityPistonAccessMixins)self).getProgress();
                    double directionOffset = isExtending ? progress - 1.0D : 1.0D - progress;
                    for (AxisAlignedBB collisionBox : collisionBoxes) {
                        collisionBox.minX += directionOffset * dX;
                        collisionBox.minY += directionOffset * dY;
                        collisionBox.minZ += directionOffset * dZ;
                        collisionBox.maxX += directionOffset * dX;
                        collisionBox.maxY += directionOffset * dY;
                        collisionBox.maxZ += directionOffset * dZ;
                        if (collisionBox.intersectsWith(maskBox)) {
                            list.add(collisionBox);
                        }
                    }
                }
            }
        }
    }
    
    private static final double GLUE_BLOCK_FUDGE_FACTOR = 0.075D;
    
    @Overwrite
    public void updatePushedObjects(float progress, float lastProgress) {
        TileEntityPiston self = (TileEntityPiston)(Object)this;
        
        /// NEW CODE START
        int storedBlockId = self.getStoredBlockID();
        Block block = Block.blocksList[storedBlockId];
        if (!BLOCK_IS_AIR(block)) {
            List<AxisAlignedBB> collisionBoxes = new ArrayList();
            
            // Hopefully no blocks are big enough to go outside this?
            // TODO: Check whether these are corner aligned...
            AxisAlignedBB fakeMask = AxisAlignedBB.getAABBPool().getAABB(self.xCoord - 1.0D, self.yCoord - 1.0D, self.zCoord - 1.0D, self.xCoord + 2.0D, self.yCoord + 2.0D, self.zCoord + 2.0D);
            
            block.addCollisionBoxesToList(self.worldObj, self.xCoord, self.yCoord, self.zCoord, fakeMask, collisionBoxes, (Entity)null);
            
            if (!collisionBoxes.isEmpty()) {
                //if (!self.worldObj.isRemote) AddonHandler.logMessage("Collision boxes added to list");
                
                boolean isExtending = self.isExtending();
                int direction = self.getPistonOrientation();
                
                if (!isExtending) {
                    direction = OPPOSITE_DIRECTION(direction);
                }
                
                //if (!self.worldObj.isRemote) AddonHandler.logMessage("Stored direction: "+direction);
                
                double directionOffset = isExtending ? lastProgress - 1.0F : 1.0F - lastProgress;
                
                double dX = (double)Facing.offsetsXForSide[direction];
                double dY = (double)Facing.offsetsYForSide[direction];
                double dZ = (double)Facing.offsetsZForSide[direction];
                
                AxisAlignedBB boundingBox = null;
                for (AxisAlignedBB collisionBox : collisionBoxes) {
                    collisionBox.minX += directionOffset * dX;
                    collisionBox.minY += directionOffset * dY;
                    collisionBox.minZ += directionOffset * dZ;
                    collisionBox.maxX += directionOffset * dX;
                    collisionBox.maxY += directionOffset * dY;
                    collisionBox.maxZ += directionOffset * dZ;
                    if (boundingBox != null) {
                        boundingBox.expandToInclude(collisionBox);
                    } else {
                        boundingBox = collisionBox.copy();
                    }
                }
                
                double idkOffset = progress - lastProgress;
                
                AxisAlignedBB tempBox = boundingBox.copy();
                switch (direction) {
                    case DIRECTION_DOWN:
                        tempBox.maxY = tempBox.minY;
                        tempBox.minY += dY * idkOffset;
                        break;
                    case DIRECTION_UP:
                        tempBox.minY = tempBox.maxY;
                        tempBox.maxY += dY * idkOffset;
                        break;
                    case DIRECTION_NORTH:
                        tempBox.maxZ = tempBox.minZ;
                        tempBox.minZ += dZ * idkOffset;
                        break;
                    case DIRECTION_SOUTH:
                        tempBox.minZ = tempBox.maxZ;
                        tempBox.maxZ += dZ * idkOffset;
                        break;
                    case DIRECTION_WEST:
                        tempBox.maxX = tempBox.minX;
                        tempBox.minX += dX * idkOffset;
                        break;
                    default:
                        tempBox.minX = tempBox.maxX;
                        tempBox.maxX += dX * idkOffset;
                        break;
                }
                //boundingBox.expandToInclude(tempBox);
                tempBox.expandToInclude(boundingBox);
                int storedMeta = self.getBlockMetadata();
                
                List<Entity> entityList = self.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)null, tempBox);
                if (!entityList.isEmpty()) {
                    //if (!self.worldObj.isRemote) AddonHandler.logMessage("Entity list created");
                    
                    //List pushedObjects = ((IBlockEntityPistonAccessMixins)self).getPushedObjects();
                    //pushedObjects.addAll(entityList);
                    boolean isBouncy = ((IBlockMixins)(Object)block).isBouncyWhenMoved(direction, storedMeta);
                    
                    for (Entity entity : entityList) {
                        //if (entity instanceof EntityPlayerMP) {
                            //continue;
                        //}
                        // This is so awful, why doesn't
                        // getBoundingBox do anything for 90%
                        // of entities but getVisualBoundingBox
                        // just returns the normal bounding box?
                        AxisAlignedBB entityBox = entity.getVisualBoundingBox();
                        if (entityBox == null) {
                            continue;
                        }
                        if (isBouncy) {
                            entity.motionX += dX;
                            entity.motionY += dY;
                            entity.motionZ += dZ;
                        }
                        double pushDistance = 0.0D;
                        for (AxisAlignedBB collisionBox : collisionBoxes) {
                            tempBox.setBB(collisionBox);
                            switch (direction) {
                                case DIRECTION_DOWN:
                                    tempBox.maxY = tempBox.minY;
                                    tempBox.minY += dY * idkOffset;
                                    break;
                                case DIRECTION_UP:
                                    tempBox.minY = tempBox.maxY;
                                    tempBox.maxY += dY * idkOffset;
                                    break;
                                case DIRECTION_NORTH:
                                    tempBox.maxZ = tempBox.minZ;
                                    tempBox.minZ += dZ * idkOffset;
                                    break;
                                case DIRECTION_SOUTH:
                                    tempBox.minZ = tempBox.maxZ;
                                    tempBox.maxZ += dZ * idkOffset;
                                    break;
                                case DIRECTION_WEST:
                                    tempBox.maxX = tempBox.minX;
                                    tempBox.minX += dX * idkOffset;
                                    break;
                                default:
                                    tempBox.minX = tempBox.maxX;
                                    tempBox.maxX += dX * idkOffset;
                                    break;
                            }
                            if (tempBox.intersectsWith(entityBox)) {
                                //if (!self.worldObj.isRemote) AddonHandler.logMessage("Entity intersected");
                                double newDistance;
                                switch (direction) {
                                    case DIRECTION_DOWN:
                                        newDistance = entityBox.maxY - tempBox.minY;
                                        break;
                                    case DIRECTION_UP:
                                        newDistance = tempBox.maxY - entityBox.minY;
                                        break;
                                    case DIRECTION_NORTH:
                                        newDistance = entityBox.maxZ - tempBox.minZ;
                                        break;
                                    case DIRECTION_SOUTH:
                                        newDistance = tempBox.maxZ - entityBox.minZ;
                                        break;
                                    case DIRECTION_WEST:
                                        newDistance = entityBox.maxX - tempBox.minX;
                                        break;
                                    default:
                                        newDistance = tempBox.maxX - entityBox.minX;
                                        break;
                                }
                                pushDistance = Math.max(pushDistance, newDistance);
                                if (pushDistance >= idkOffset) {
                                    break;
                                }
                            }
                        }
                        if (pushDistance <= 0.0D) {
                            continue;
                        }
                        pushDistance = Math.min(pushDistance, idkOffset) + 0.01D;
                        
                        PISTON_ENTITY_PUSH_DEBUG(
                            "\nPre-Entity moving("+storedBlockId+"):"+
                            "\nX: "+entity.posX+" + "+(pushDistance * dX)+
                            "\nY: "+entity.posY+" + "+(pushDistance * dY)+
                            "\nZ: "+entity.posZ+" + "+(pushDistance * dZ)
                        );
                        
                        
                        //NOCLIP_DIRECTION.set(direction);
                        ((IEntityMixins)entity).moveEntityByPiston(
                            pushDistance * dX,
                            pushDistance * dY,
                            pushDistance * dZ
                        );
                        //NOCLIP_DIRECTION.set(-1);
                        
                        PISTON_ENTITY_PUSH_DEBUG(
                            "Post-Entity moving("+storedBlockId+"):"+
                            "\nX: "+entity.posX+
                            "\nY: "+entity.posY+
                            "\nZ: "+entity.posZ
                        );
                        
                        /*
                        if (!isExtending && isSourcePiston) {
                            
                        }
                        */
                    }
                    //pushedObjects.clear();
                }
                
                boolean isSticky = ((IBlockMixins)(Object)block).isStickyForEntitiesWhenMoved(direction, storedMeta);
                if (isSticky) {
                    /*
                    switch (storedDirection) {
                        case DIRECTION_DOWN: case DIRECTION_UP:
                            boundingBox.minX = Math.floor(boundingBox.minX);
                            //boundingBox.minY += progressOffset;
                            boundingBox.minZ = Math.floor(boundingBox.minZ);
                            boundingBox.maxX = Math.ceil(boundingBox.maxX);
                            //boundingBox.maxY += progressOffset;
                            boundingBox.maxZ = Math.ceil(boundingBox.maxZ);
                            break;
                        case DIRECTION_NORTH: case DIRECTION_SOUTH:
                            boundingBox.minX = Math.floor(boundingBox.minX);
                            boundingBox.minY = Math.floor(boundingBox.minY);
                            //boundingBox.minZ += progressOffset;
                            boundingBox.maxX = Math.ceil(boundingBox.maxX);
                            boundingBox.maxY = Math.ceil(boundingBox.maxY);
                            //boundingBox.maxZ += progressOffset;
                            break;
                        default:
                            //boundingBox.minX += progressOffset;
                            boundingBox.minY = Math.floor(boundingBox.minY);
                            boundingBox.minZ = Math.floor(boundingBox.minZ);
                            //boundingBox.maxX += progressOffset;
                            boundingBox.maxY = Math.ceil(boundingBox.maxY);
                            boundingBox.maxZ = Math.ceil(boundingBox.maxZ);
                            break;
                    }
                    */
                    if (direction != DIRECTION_WEST) {
                        boundingBox.minX -= GLUE_BLOCK_FUDGE_FACTOR;
                    }
                    if (direction != DIRECTION_DOWN) {
                        boundingBox.minY -= GLUE_BLOCK_FUDGE_FACTOR;
                    }
                    if (direction != DIRECTION_NORTH) {
                        boundingBox.minZ -= GLUE_BLOCK_FUDGE_FACTOR;
                    }
                    if (direction != DIRECTION_EAST) {
                        boundingBox.maxX += GLUE_BLOCK_FUDGE_FACTOR;
                    }
                    if (direction != DIRECTION_UP) {
                        boundingBox.maxY += GLUE_BLOCK_FUDGE_FACTOR;
                    }
                    if (direction != DIRECTION_SOUTH) {
                        boundingBox.maxZ += GLUE_BLOCK_FUDGE_FACTOR;
                    }
                    
                    entityList = self.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)null, boundingBox);
                    if (!entityList.isEmpty()) {
                        //NOCLIP_DIRECTION.set(direction);
                        for (Entity entity : entityList) {
                            PISTON_ENTITY_PUSH_DEBUG(
                                "Pre-Entity moving sticky("+storedBlockId+"):"+
                                "\nX: "+entity.posX+" + "+(idkOffset * dX)+
                                "\nY: "+entity.posY+" + "+(idkOffset * dY)+
                                "\nZ: "+entity.posZ+" + "+(idkOffset * dZ)
                            );
                            ((IEntityMixins)entity).moveEntityByPiston(
                                idkOffset * dX,
                                idkOffset * dY,
                                idkOffset * dZ
                            );
                            PISTON_ENTITY_PUSH_DEBUG(
                                "Post-Entity moving sticky("+storedBlockId+"):"+
                                "\nX: "+entity.posX+
                                "\nY: "+entity.posY+
                                "\nZ: "+entity.posZ
                            );
                        }
                        //NOCLIP_DIRECTION.set(-1);
                    }
                }
            }
        }
        /// NEW CODE END
        
        /*
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
            
            
            //if (!self.worldObj.isRemote) {
                //AddonHandler.logMessage(""+self.worldObj.getTotalWorldTime()+" "+progress+" "+lastProgress+" "+d);
                //AddonHandler.logMessage(" "+boundingBox.minX+" "+boundingBox.minY+" "+boundingBox.minZ);
                //AddonHandler.logMessage(" "+boundingBox.maxX+" "+boundingBox.maxY+" "+boundingBox.maxZ);
            //}
            
            
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
        */
    }
    
    @Overwrite
    public void updateEntity() {
        this.lastTicked = this.worldObj.getTotalWorldTime();
        TileEntityPiston self = (TileEntityPiston)(Object)this;
        IBlockEntityPistonAccessMixins self_access = (IBlockEntityPistonAccessMixins)self;
        
        float current_progress = self_access.getProgress();
        self_access.setLastProgress(current_progress);
        if (current_progress >= 1.0F) {
            // FCMOD: Added
            self_access.callAttemptToPackItems();
            // END FCMOD
            
            self.worldObj.removeBlockTileEntity(self.xCoord, self.yCoord, self.zCoord);
            self.invalidate();
            
            if (
                self.worldObj.getBlockId(self.xCoord, self.yCoord, self.zCoord) == Block.pistonMoving.blockID &&
                !self_access.callDestroyAndDropIfShoveled()
            ) {
                self_access.callPreBlockPlaced();
                self.restoreStoredBlock();
            }
            return;
        }
        float next_progress = current_progress + 0.5F;
        this.noclip = true;
        self_access.callUpdatePushedObjects(next_progress, current_progress);
        this.noclip = false;
        if (next_progress >= 1.0F) {
            next_progress = 1.0F;
        }
        self_access.setProgress(next_progress);
    }
}