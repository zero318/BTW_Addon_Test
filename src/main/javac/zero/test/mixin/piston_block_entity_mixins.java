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

@Mixin(TileEntityPiston.class)
public class BlockEntityPistonMixins extends TileEntity implements IBlockEntityPistonMixins {
    
    public long last_ticked;
    
    public boolean hasLargeCenterHardPointToFacing(int X, int Y, int Z, int direction, boolean ignore_transparency) {
        TileEntityPiston self = (TileEntityPiston)(Object)this;
        if (((IBlockEntityPistonAccessMixins)self).getProgress() >= 1.0f) {
            int stored_block_id = self.getStoredBlockID();
            Block stored_block = Block.blocksList[stored_block_id];
            if (!BLOCK_IS_AIR(stored_block)) {
                int stored_meta = self.getBlockMetadata();
                int prev_meta = self.worldObj.getBlockMetadata(X, Y, Z);
                
                // Since the block is likely to try getting its own metadata,
                // silently swap out the metadata value during the face test
                self.worldObj.setBlockMetadataWithNotify(X, Y, Z, stored_meta, UPDATE_INVISIBLE | UPDATE_KNOWN_SHAPE | UPDATE_SUPPRESS_LIGHT);
                boolean ret = stored_block.hasLargeCenterHardPointToFacing(self.worldObj, X, Y, Z, direction, ignore_transparency);
                self.worldObj.setBlockMetadataWithNotify(X, Y, Z, prev_meta, UPDATE_INVISIBLE | UPDATE_KNOWN_SHAPE | UPDATE_SUPPRESS_LIGHT);
                if (!ret) {
                    //AddonHandler.logMessage("SupportPoint FAIL BLOCK FALSE "+stored_block_id+"("+stored_meta+")");
                }
                return ret;
            }
            //AddonHandler.logMessage("SupportPoint FAIL AIR");
            return false;
        }
        //AddonHandler.logMessage("SupportPoint FAIL PROGRESS "+((IBlockEntityPistonAccessMixins)self).getProgress());
        return false;
    }
    
    @Overwrite
    public void restoreStoredBlock() {
        TileEntityPiston self = (TileEntityPiston)(Object)this;
        
        //if (!self.worldObj.isRemote) AddonHandler.logMessage("Restore block ("+self.xCoord+" "+self.yCoord+" "+self.zCoord+") at time "+last_ticked);
        
        int stored_block_id = self.getStoredBlockID();
        Block stored_block = Block.blocksList[stored_block_id];
        int stored_meta = self.getBlockMetadata();
        if (!BLOCK_IS_AIR(stored_block)) {
            ((IWorldMixins)self.worldObj).updateFromNeighborShapes(self.xCoord, self.yCoord, self.zCoord, stored_block_id, stored_meta);
        }
        
        // Set scanningTileEntities to true
        // so that the tile entity is always
        // placed correctly
        boolean scanning_tile_entities_temp = ((IWorldAccessMixins)self.worldObj).getScanningTileEntities();
        ((IWorldAccessMixins)self.worldObj).setScanningTileEntities(true);
        
        self.worldObj.setBlock(self.xCoord, self.yCoord, self.zCoord, stored_block_id, stored_meta, UPDATE_NEIGHBORS | UPDATE_CLIENTS);
        
        //if (!self.worldObj.isRemote) AddonHandler.logMessage("PLACE BLOCK "+stored_block_id+"."+stored_meta);
        
        if (self.storedTileEntityData != null) {
            // setBlockTileEntity updates the entity
            // coordinates itself when scanningTileEntities
            // is true
            worldObj.setBlockTileEntity(self.xCoord, self.yCoord, self.zCoord, TileEntity.createAndLoadEntity(self.storedTileEntityData));
        }
        
        self.worldObj.notifyBlockOfNeighborChange(self.xCoord, self.yCoord, self.zCoord, stored_block_id);
        
        // Restore original value of scanningTileEntities
        ((IWorldAccessMixins)self.worldObj).setScanningTileEntities(scanning_tile_entities_temp);
        
        //if (stored_block_id != this.worldObj.getBlockId(self.xCoord, self.yCoord, self.zCoord)) {
            //AddonHandler.logMessage("PLACE BLOCK BROKE DURING UPDATE "+stored_block_id+"."+stored_meta);
        //}
    }
    
    @Overwrite
    public void clearPistonTileEntity() {
        TileEntityPiston self = (TileEntityPiston)(Object)this;
        if (
            ((IBlockEntityPistonAccessMixins)self).getLastProgress() < 1.0f &&
            self.worldObj != null
        ) {
            ((IBlockEntityPistonAccessMixins)self).setProgress(1.0f);
            ((IBlockEntityPistonAccessMixins)self).setLastProgress(1.0f);
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
        this.last_ticked = this.worldObj.getTotalWorldTime();
    }
    
    public long getLastTicked() {
        return this.last_ticked;
    }
    public void setLastTicked(long time) {
        this.last_ticked = time;
    }
    
    @Overwrite
    private void updatePushedObjects(float par1, float par2) {
        TileEntityPiston self = (TileEntityPiston)(Object)this;
		boolean extending = self.isExtending();
        if (extending) {
			par1 = 1.0F - par1;
		}
		else {
			--par1;
		}
                
        int stored_direction = self.getPistonOrientation();
        int stored_block_id = self.getStoredBlockID();
		
		AxisAlignedBB var3 = Block.pistonMoving.getAxisAlignedBB(self.worldObj, self.xCoord, self.yCoord, self.zCoord, stored_block_id, par1, stored_direction);
		
		if (var3 != null) {
			List var4 = self.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)null, var3);
			
			if (!var4.isEmpty()) {
                boolean is_bouncy = false;
                boolean is_sticky = false;
                if (extending) {
                    Block block = Block.blocksList[stored_block_id];
                    if (!BLOCK_IS_AIR(block)) {
                        int stored_meta = self.getBlockMetadata();
                        is_bouncy = ((IBlockMixins)(Object)block).isBouncyWhenMoved(stored_direction, stored_meta);
                        is_sticky = ((IBlockMixins)(Object)block).isStickyForEntitiesWhenMoved(stored_direction, stored_meta);
                    }
                }
                List pushed_objects = ((IBlockEntityPistonAccessMixins)self).getPushedObjects();
                
				pushed_objects.addAll(var4);
				Iterator var5 = pushed_objects.iterator();
                
				
				while (var5.hasNext()) {
                    //AddonHandler.logMessage("ENTITY PUSH");
					Entity entity = (Entity)var5.next();
                    entity.moveEntity(
                        (double)(par2 * (float)Facing.offsetsXForSide[stored_direction]),
                        (double)(par2 * (float)Facing.offsetsYForSide[stored_direction]),
                        (double)(par2 * (float)Facing.offsetsZForSide[stored_direction])
                    );
                    if (is_bouncy) {
                        entity.motionX += Facing.offsetsXForSide[stored_direction];
                        entity.motionY += Facing.offsetsYForSide[stored_direction];
                        entity.motionZ += Facing.offsetsZForSide[stored_direction];
                    }
				}
				
				pushed_objects.clear();
			}
		}
	}
}