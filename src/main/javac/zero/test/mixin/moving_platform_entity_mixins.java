package zero.test.mixin;

import net.minecraft.src.*;

import btw.AddonHandler;
import btw.client.fx.BTWEffectManager;
import btw.block.blocks.PlatformBlock;
import btw.block.BTWBlocks;
import btw.entity.mechanical.platform.MovingPlatformEntity;
import btw.entity.mechanical.platform.MovingAnchorEntity;
import btw.item.util.ItemUtils;
import btw.world.util.WorldUtils;
import btw.util.MiscUtils;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

import zero.test.IWorldMixins;
import zero.test.IBlockMixins;
import zero.test.IMovingPlatformEntityMixins;
import zero.test.ZeroUtil;
import zero.test.ZeroCompatUtil;
import zero.test.mixin.IWorldAccessMixins;

import java.util.Random;

#include "..\feature_flags.h"
#include "..\util.h"

#define ENABLE_GROSS_METADATA_HACK 1

#define BREAK_BLOCK_WHEN_FAILING 1

#define PLATFORM_TILE_ENTITY_PRINT_DEBUGGING 0

#if PLATFORM_TILE_ENTITY_PRINT_DEBUGGING
#define PLATFORM_TILE_ENTITY_DEBUG(...) AddonHandler.logMessage(__VA_ARGS__)
#else
#define PLATFORM_TILE_ENTITY_DEBUG(...)
#endif

#if ENABLE_METADATA_EXTENSION_COMPAT
#define PLATFORM_SET_BLOCK(world, x, y, z, blockId, meta, extMeta, flags) ZeroCompatUtil.setBlockWithExtra(world, x, y, z, blockId, meta, extMeta, flags)
#else
#define PLATFORM_SET_BLOCK(world, x, y, z, blockId, meta, extMeta, flags) world.setBlock(x, y, z, blockId, meta, flags)
#endif

@Mixin(MovingPlatformEntity.class)
public abstract class MovingPlatformEntityMixins extends Entity implements IMovingPlatformEntityMixins {
    
    public MovingPlatformEntityMixins() {
        super(null);
    }
    
#if ENABLE_PLATFORM_EXTENSIONS
    public int block_id;
    public int block_meta;
    //public int sticky_sides;
    
    public NBTTagCompound storedTileEntityData = null;
#if ENABLE_PLATFORM_TILE_ENTITY_CACHE
    public TileEntity cachedTileEntity = null;
#endif
    
    public void setBlockId(int blockId) {
        this.block_id = blockId;
    }
    public int getBlockId() {
        return this.block_id;
    }
    public void setBlockMeta(int blockMeta) {
        this.block_meta = blockMeta;
    }
    public int getBlockMeta() {
        return this.block_meta;
    }
    
    public void storeTileEntity(TileEntity tileEntity) {
#if ENABLE_PLATFORM_TILE_ENTITY_CACHE
        (this.cachedTileEntity = tileEntity).writeToNBT(this.storedTileEntityData = new NBTTagCompound());
#else
        tileEntity.writeToNBT(this.storedTileEntityData = new NBTTagCompound());
#endif
    }
    
    public TileEntity getStoredTileEntity() {
        if (this.storedTileEntityData != null) {
#if ENABLE_PLATFORM_TILE_ENTITY_CACHE
            if (this.cachedTileEntity != null) {
                return this.cachedTileEntity;
            }
#endif
            return TileEntity.createAndLoadEntity(this.storedTileEntityData);
        }
        return null;
    }
    
    public boolean setBoundingBox() {
#if ENABLE_GROSS_METADATA_HACK
        int x = MathHelper.floor_double(this.posX);
        //int y = MathHelper.floor_double(this.posY);
        int z = MathHelper.floor_double(this.posZ);
        
        int prev_meta = this.worldObj.getBlockMetadata(x, 0, z);
        
        this.worldObj.setBlockMetadataWithNotify(x, 0, z, this.block_meta, UPDATE_INVISIBLE | UPDATE_KNOWN_SHAPE | UPDATE_SUPPRESS_LIGHT);
        
        AxisAlignedBB block_hitbox = Block.blocksList[this.block_id].getAsPistonMovingBoundingBox(this.worldObj, x, 0, z);
        
        this.worldObj.setBlockMetadataWithNotify(x, 0, z, prev_meta, UPDATE_INVISIBLE | UPDATE_KNOWN_SHAPE | UPDATE_SUPPRESS_LIGHT);
        
        if (block_hitbox != null) {
            double temp = this.posY - (double)this.yOffset + (double)this.ySize;
            block_hitbox.minY += temp;
            block_hitbox.maxY = block_hitbox.maxY * this.height + temp;
            this.boundingBox.setBB(block_hitbox);
            return true;
        }
        return false;
#else
    
#error I don't have any better ideas yet

#endif
    }
    
    @Inject(
        method = "moveEntityInternal(DDD)V",
        at = @At("HEAD"),
        remap = false
    )
    public void configure_collision_jank(CallbackInfo info) {
        this.setBoundingBox();
    }
    
    @Overwrite(remap=false)
    public AxisAlignedBB getBoundingBox() {
        if (this.setBoundingBox()) {
            return this.boundingBox;
        }
        return null;
    }
    
    /*
    public void setStickySides(int sides) {
        this.sticky_sides = sides;
    }
    public int getStickySides() {
        return this.sticky_sides;
    }
    */
    
    @Inject(
        method = "writeEntityToNBT(Lnet/minecraft/src/NBTTagCompound;)V",
        at = @At("TAIL")
    )
    public void writeEntityToNBT_inject(NBTTagCompound nbttagcompound, CallbackInfo info) {
        nbttagcompound.setInteger("BlockId", this.block_id);
        nbttagcompound.setInteger("BlockMeta", this.block_meta);
        nbttagcompound.setCompoundTag("TileEntityData", this.storedTileEntityData);
    }
    
    @Inject(
        method = "readEntityFromNBT(Lnet/minecraft/src/NBTTagCompound;)V",
        at = @At("TAIL")
    )
    public void readEntityFromNBT_inject(NBTTagCompound nbttagcompound, CallbackInfo info) {
        if (nbttagcompound.hasKey("BlockId")) {
            this.block_id = nbttagcompound.getInteger("BlockId");
        } else {
            this.block_id = BTWBlocks.platform.blockID;
        }
        if (nbttagcompound.hasKey("BlockMeta")) {
            this.block_meta = nbttagcompound.getInteger("BlockMeta");
        }
        if (nbttagcompound.hasKey("TileEntityData")) {
            this.storedTileEntityData = nbttagcompound.getCompoundTag("TileEntityData");
        }
    }
    
    @Overwrite(remap=false)
    public Packet getSpawnPacketForThisEntity() {
        // Send the block state info for proper rendering
        return new Packet23VehicleSpawn(this, 103, BLOCK_STATE_PACK(this.block_id, this.block_meta));
    }
    
    @Overwrite(remap=false)
    public void destroyPlatformWithDrop() {
        if (!this.worldObj.isRemote) {
            int x = MathHelper.floor_double(this.posX);
            int y = MathHelper.floor_double(this.posY);
            int z = MathHelper.floor_double(this.posZ);
            
#if !BREAK_BLOCK_WHEN_FAILING
            ItemUtils.ejectStackWithRandomOffset(
                this.worldObj,
                x, y, z,
                new ItemStack(Block.blocksList[this.block_id])
            );
#else
            Block.blocksList[this.block_id].dropBlockAsItem(
                this.worldObj,
                x, y, z,
                this.block_id, this.block_meta
            );
#endif
            
            TileEntity tileEntity;
            if ((tileEntity = this.getStoredTileEntity()) != null) {
                ZeroUtil.break_tile_entity(this.worldObj, x, y, z, tileEntity);
            }
        }
		
    	this.setDead();
    }
    
    @Overwrite(remap=false)
    public void convertToBlock(int x, int y, int z, MovingAnchorEntity associatedAnchor, boolean movingUpwards) {
        MovingPlatformEntity self = (MovingPlatformEntity)(Object)this;
        
    	int destBlockId = this.worldObj.getBlockId(x, y, z);
        TileEntity tileEntity = this.getStoredTileEntity();
        //this.storedTileEntityData = null;
#if ENABLE_PLATFORM_TILE_ENTITY_CACHE
        this.cachedTileEntity = null;
#endif
    	
        boolean isReplaceable = WorldUtils.isReplaceableBlock(this.worldObj, x, y, z);
        
        if (
            isReplaceable ||
            (
                !Block.blocksList[destBlockId].blockMaterial.isSolid() ||
                destBlockId == Block.web.blockID ||
                destBlockId == BTWBlocks.web.blockID
            )
        ) {
            if (!isReplaceable) {
                int targetMetadata = this.worldObj.getBlockMetadata(x, y, z);
    		
                Block.blocksList[destBlockId].dropBlockAsItem( 
                    this.worldObj,
                    x, y, z,
                    //destBlockId,
                    targetMetadata
                    ,0
                );
                
                this.worldObj.playAuxSFX(
                    BTWEffectManager.DESTROY_BLOCK_RESPECT_PARTICLE_SETTINGS_EFFECT_ID,
                    x, y, z,
                    destBlockId + (targetMetadata << 12)
                );
            }
            
            int newMeta = ((IWorldMixins)this.worldObj).updateFromNeighborShapes(x, y, z, this.block_id, this.block_meta);
#if ENABLE_METADATA_EXTENSION_COMPAT
            int extMeta = ZeroCompatUtil.getMovingPlatformEntityExtMeta(self);
#endif
            
                
            // Set scanningTileEntities to true so
            // that the tile entity is placed correctly.
            // This is still necessary for platforms
            // because entities are parsed during a
            // different part of a tick than pistons.
            boolean scanningTileEntitiesTemp = ((IWorldAccessMixins)this.worldObj).getScanningTileEntities();
            ((IWorldAccessMixins)this.worldObj).setScanningTileEntities(true);
            
#if PLATFORM_TILE_ENTITY_PRINT_DEBUGGING
            TileEntity newTileEntityA = null;
#endif
            if (tileEntity != null) {
                //tileEntity.xCoord = x;
                //tileEntity.yCoord = y;
                //tileEntity.zCoord = z;
                tileEntity.validate();
                PLATFORM_TILE_ENTITY_DEBUG("Placing tile entity");
                
                this.worldObj.setBlockTileEntity(x, y, z, tileEntity);
                
#if PLATFORM_TILE_ENTITY_PRINT_DEBUGGING
                newTileEntityA = this.worldObj.getBlockTileEntity(x, y, z);
#endif
            }
            
            if (newMeta >= 0) {
                PLATFORM_SET_BLOCK(this.worldObj, x, y, z, this.block_id, newMeta, extMeta, UPDATE_NEIGHBORS | UPDATE_CLIENTS);
                this.worldObj.notifyBlockOfNeighborChange(x, y, z, this.block_id);
            } else {
                PLATFORM_SET_BLOCK(this.worldObj, x, y, z, this.block_id, this.block_meta, extMeta, UPDATE_INVISIBLE | UPDATE_KNOWN_SHAPE | UPDATE_SUPPRESS_LIGHT);
                this.worldObj.destroyBlock(x, y, z, true);
            }

#if PLATFORM_TILE_ENTITY_PRINT_DEBUGGING
            if (tileEntity != null) {
                TileEntity newTileEntityB = this.worldObj.getBlockTileEntity(x, y, z);
                
                NBTTagCompound newTileEntityDataA = null;
                NBTTagCompound newTileEntityDataB = null;
                if (newTileEntityA != null) {
                    newTileEntityA.writeToNBT(newTileEntityDataA = new NBTTagCompound());
                }
                if (newTileEntityB != null) {
                    newTileEntityB.writeToNBT(newTileEntityDataB = new NBTTagCompound());
                }
                if (!this.storedTileEntityData.equals(newTileEntityDataB)) {
                    AddonHandler.logMessage("FAIL");
                    AddonHandler.logMessage("Old : "+this.storedTileEntityData.toString());
                    if (newTileEntityDataA != null) {
                        AddonHandler.logMessage("NewA: "+newTileEntityDataA.toString());
                    } else {
                        AddonHandler.logMessage("NewA: null");
                    }
                    if (newTileEntityDataB != null) {
                        AddonHandler.logMessage("NewB: "+newTileEntityDataB.toString());
                    } else {
                        AddonHandler.logMessage("NewB: null");
                    }
                }
            }
#endif
            // Restore original value of scanningTileEntities
            ((IWorldAccessMixins)this.worldObj).setScanningTileEntities(scanningTileEntitiesTemp);
        }
    	else {
    		// this shouldn't usually happen, but if the block is already occupied, eject the platform
    		// as an item
#if !BREAK_BLOCK_WHEN_FAILING
			ItemUtils.ejectSingleItemWithRandomOffset(
                this.worldObj,
                x, y, z,
                this.block_id,
                this.block_meta
            );
#else
            Block.blocksList[this.block_id].dropBlockAsItem(
                this.worldObj,
                x, y, z,
                this.block_id, this.block_meta
            );
#endif
            if (tileEntity != null) {
                ZeroUtil.break_tile_entity(this.worldObj, x, y, z, tileEntity);
            }
    	}
    	
    	MiscUtils.positionAllNonPlayerMoveableEntitiesOutsideOfLocation(this.worldObj, x, y, z);
    	
		// FCTODO: hacky way of making sure players don't fall through platforms when they stop
		
    	MiscUtils.serverPositionAllPlayerEntitiesOutsideOfLocation(this.worldObj, x, y + (!movingUpwards ? 1 : -1), z);
        MiscUtils.serverPositionAllPlayerEntitiesOutsideOfLocation(this.worldObj, x, y, z);
    	
    	this.setDead();
    }
#endif
}