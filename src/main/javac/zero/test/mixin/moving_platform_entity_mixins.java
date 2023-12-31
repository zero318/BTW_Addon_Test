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

import java.util.Random;

#include "..\feature_flags.h"
#include "..\util.h"

@Mixin(MovingPlatformEntity.class)
public class MovingPlatformEntityMixins implements IMovingPlatformEntityMixins {
#if ENABLE_PLATFORM_EXTENSIONS
    public int block_id;
    public int block_meta;
    //public int sticky_sides;
    
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
    }
    
    @Inject(
        method = "readEntityFromNBT(Lnet/minecraft/src/NBTTagCompound;)V",
        at = @At("TAIL")
    )
    public void readEntityFromNBT_inject(NBTTagCompound nbttagcompound, CallbackInfo info) {
        this.block_id = nbttagcompound.getInteger("BlockId");
        this.block_meta = nbttagcompound.getInteger("BlockMeta");
    }
    
    @Overwrite(remap=false)
    public void destroyPlatformWithDrop() {
        MovingPlatformEntity self = (MovingPlatformEntity)(Object)this;
        
		ItemUtils.ejectStackWithRandomOffset(
            self.worldObj,
            MathHelper.floor_double(self.posX),
            MathHelper.floor_double(self.posY),
            MathHelper.floor_double(self.posZ),
            new ItemStack(Block.blocksList[this.block_id])
        );
		
    	self.setDead();
    }
    
    @Overwrite(remap=false)
    public void convertToBlock(int X, int Y, int Z, MovingAnchorEntity associatedAnchor, boolean bMovingUpwards) {
        MovingPlatformEntity self = (MovingPlatformEntity)(Object)this;
        
    	int dest_block_id = self.worldObj.getBlockId(X, Y, Z);
    	
    	if (WorldUtils.isReplaceableBlock(self.worldObj, X, Y, Z)) {
    		//self.worldObj.setBlockWithNotify(X, Y, Z, this.block_id);
            self.worldObj.setBlock(X, Y, Z, this.block_id, this.block_meta, UPDATE_NEIGHBORS | UPDATE_CLIENTS);
    	}
    	else if (
            !Block.blocksList[dest_block_id].blockMaterial.isSolid() ||
            dest_block_id == Block.web.blockID ||
    		dest_block_id == BTWBlocks.web.blockID
        ) {
    		int iTargetMetadata = self.worldObj.getBlockMetadata(X, Y, Z);
    		
    		Block.blocksList[dest_block_id].dropBlockAsItem( 
				self.worldObj,
                X, Y, Z,
                iTargetMetadata,
                0
            );
    		
	        self.worldObj.playAuxSFX(
                BTWEffectManager.DESTROY_BLOCK_RESPECT_PARTICLE_SETTINGS_EFFECT_ID,
	        	X, Y, Z,
                dest_block_id + (iTargetMetadata << 12)
            );
	        
    		//self.worldObj.setBlockWithNotify(X, Y, Z, this.block_id);
            self.worldObj.setBlock(X, Y, Z, this.block_id, this.block_meta, UPDATE_NEIGHBORS | UPDATE_CLIENTS);
		}
    	else {
    		// this shouldn't usually happen, but if the block is already occupied, eject the platform
    		// as an item
    		
			ItemUtils.ejectSingleItemWithRandomOffset(
                self.worldObj,
                X, Y, Z,
                this.block_id,
                0
            );
    	}
    	
    	MiscUtils.positionAllNonPlayerMoveableEntitiesOutsideOfLocation(self.worldObj, X, Y, Z);
    	
		// FCTODO: hacky way of making sure players don't fall through platforms when they stop
		
    	MiscUtils.serverPositionAllPlayerEntitiesOutsideOfLocation(self.worldObj, X, Y + (!bMovingUpwards ? 1 : -1), Z);
        MiscUtils.serverPositionAllPlayerEntitiesOutsideOfLocation(self.worldObj, X, Y, Z);
    	
    	self.setDead();
    }
#endif
}