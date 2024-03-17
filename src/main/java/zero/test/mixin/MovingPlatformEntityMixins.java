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
import zero.test.ZeroMetaUtil;
import zero.test.mixin.IWorldAccessMixins;
import java.util.Random;
// Block piston reactions
@Mixin(MovingPlatformEntity.class)
public abstract class MovingPlatformEntityMixins extends Entity implements IMovingPlatformEntityMixins {
    public MovingPlatformEntityMixins() {
        super(null);
    }
    public int block_id;
    public int block_meta;
    //public int sticky_sides;
    public NBTTagCompound storedTileEntityData = null;
    public TileEntity cachedTileEntity = null;
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
        (this.cachedTileEntity = tileEntity).writeToNBT(this.storedTileEntityData = new NBTTagCompound());
    }
    public TileEntity getStoredTileEntity() {
        if (this.storedTileEntityData != null) {
            if (this.cachedTileEntity != null) {
                return this.cachedTileEntity;
            }
            return TileEntity.createAndLoadEntity(this.storedTileEntityData);
        }
        return null;
    }
    public boolean setBoundingBox() {
        int x = MathHelper.floor_double(this.posX);
        //int y = MathHelper.floor_double(this.posY);
        int z = MathHelper.floor_double(this.posZ);
        int prev_meta = this.worldObj.getBlockMetadata(x, 0, z);
        this.worldObj.setBlockMetadataWithNotify(x, 0, z, this.block_meta, 0x04 | 0x10 | 0x80);
        AxisAlignedBB block_hitbox = Block.blocksList[this.block_id].getAsPistonMovingBoundingBox(this.worldObj, x, 0, z);
        this.worldObj.setBlockMetadataWithNotify(x, 0, z, prev_meta, 0x04 | 0x10 | 0x80);
        if (block_hitbox != null) {
            double temp = this.posY - (double)this.yOffset + (double)this.ySize;
            block_hitbox.minY += temp;
            block_hitbox.maxY = block_hitbox.maxY * this.height + temp;
            this.boundingBox.setBB(block_hitbox);
            return true;
        }
        return false;
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
        return new Packet23VehicleSpawn(this, 103, (((this.block_id)&0xFFFF)|((this.block_meta))<<16));
    }
    @Overwrite(remap=false)
    public void destroyPlatformWithDrop() {
        if (!this.worldObj.isRemote) {
            int x = MathHelper.floor_double(this.posX);
            int y = MathHelper.floor_double(this.posY);
            int z = MathHelper.floor_double(this.posZ);
            Block.blocksList[this.block_id].dropBlockAsItem(
                this.worldObj,
                x, y, z,
                this.block_id, this.block_meta
            );
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
        this.cachedTileEntity = null;
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
            int extMeta = ZeroMetaUtil.getMovingPlatformEntityExtMeta(self);
            // Set scanningTileEntities to true so
            // that the tile entity is placed correctly.
            // This is still necessary for platforms
            // because entities are parsed during a
            // different part of a tick than pistons.
            boolean scanningTileEntitiesTemp = ((IWorldAccessMixins)this.worldObj).getScanningTileEntities();
            ((IWorldAccessMixins)this.worldObj).setScanningTileEntities(true);
            if (tileEntity != null) {
                //tileEntity.xCoord = x;
                //tileEntity.yCoord = y;
                //tileEntity.zCoord = z;
                tileEntity.validate();
                                                                 ;
                this.worldObj.setBlockTileEntity(x, y, z, tileEntity);
            }
            if (newMeta >= 0) {
                ZeroMetaUtil.setBlockWithExtra(this.worldObj, x, y, z, this.block_id, newMeta, extMeta, 0x01 | 0x02);
                this.worldObj.notifyBlockOfNeighborChange(x, y, z, this.block_id);
            } else {
                ZeroMetaUtil.setBlockWithExtra(this.worldObj, x, y, z, this.block_id, this.block_meta, extMeta, 0x04 | 0x10 | 0x80);
                this.worldObj.destroyBlock(x, y, z, true);
            }
            // Restore original value of scanningTileEntities
            ((IWorldAccessMixins)this.worldObj).setScanningTileEntities(scanningTileEntitiesTemp);
        }
     else {
      // this shouldn't usually happen, but if the block is already occupied, eject the platform
      // as an item
            Block.blocksList[this.block_id].dropBlockAsItem(
                this.worldObj,
                x, y, z,
                this.block_id, this.block_meta
            );
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
}
