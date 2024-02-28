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
// Block piston reactions
@Mixin(MovingPlatformEntity.class)
public abstract class MovingPlatformEntityMixins extends Entity implements IMovingPlatformEntityMixins {
    public MovingPlatformEntityMixins() {
        super(null);
    }
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
    }
    @Overwrite(remap=false)
    public Packet getSpawnPacketForThisEntity() {
        return new Packet23VehicleSpawn(this, 103, (((this.block_id)&0xFFFF)|((this.block_meta))<<16));
    }
    @Overwrite(remap=false)
    public void destroyPlatformWithDrop() {
        if (!this.worldObj.isRemote) {
            ItemUtils.ejectStackWithRandomOffset(
                this.worldObj,
                MathHelper.floor_double(this.posX),
                MathHelper.floor_double(this.posY),
                MathHelper.floor_double(this.posZ),
                new ItemStack(Block.blocksList[this.block_id])
            );
        }
     this.setDead();
    }
    @Overwrite(remap=false)
    public void convertToBlock(int x, int y, int z, MovingAnchorEntity associatedAnchor, boolean movingUpwards) {
        MovingPlatformEntity self = (MovingPlatformEntity)(Object)this;
     int destBlockId = this.worldObj.getBlockId(x, y, z);
     if (WorldUtils.isReplaceableBlock(this.worldObj, x, y, z)) {
      //this.worldObj.setBlockWithNotify(x, y, z, this.block_id);
            this.worldObj.setBlock(x, y, z, this.block_id, this.block_meta, 0x01 | 0x02);
     }
     else if (
            !Block.blocksList[destBlockId].blockMaterial.isSolid() ||
            destBlockId == Block.web.blockID ||
      destBlockId == BTWBlocks.web.blockID
        ) {
      int targetMetadata = this.worldObj.getBlockMetadata(x, y, z);
      Block.blocksList[destBlockId].dropBlockAsItem(
    this.worldObj,
                x, y, z,
                targetMetadata,
                0
            );
         this.worldObj.playAuxSFX(
                BTWEffectManager.DESTROY_BLOCK_RESPECT_PARTICLE_SETTINGS_EFFECT_ID,
          x, y, z,
                destBlockId + (targetMetadata << 12)
            );
      //this.worldObj.setBlockWithNotify(x, y, z, this.block_id);
            this.worldObj.setBlock(x, y, z, this.block_id, this.block_meta, 0x01 | 0x02);
  }
     else {
      // this shouldn't usually happen, but if the block is already occupied, eject the platform
      // as an item
   ItemUtils.ejectSingleItemWithRandomOffset(
                this.worldObj,
                x, y, z,
                this.block_id,
                0
            );
     }
     MiscUtils.positionAllNonPlayerMoveableEntitiesOutsideOfLocation(this.worldObj, x, y, z);
  // FCTODO: hacky way of making sure players don't fall through platforms when they stop
     MiscUtils.serverPositionAllPlayerEntitiesOutsideOfLocation(this.worldObj, x, y + (!movingUpwards ? 1 : -1), z);
        MiscUtils.serverPositionAllPlayerEntitiesOutsideOfLocation(this.worldObj, x, y, z);
     this.setDead();
    }
}
