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

@Mixin(TileEntityPiston.class)
public class BlockEntityPistonMixins extends TileEntity implements IBlockEntityPistonMixins {
    public long last_ticked;
    public boolean hasLargeCenterHardPointToFacing(int X, int Y, int Z, int direction, boolean ignore_transparency) {
        TileEntityPiston self = (TileEntityPiston)(Object)this;
        if (((IBlockEntityPistonAccessMixins)self).getProgress() >= 1.0f) {
            int stored_block_id = self.getStoredBlockID();
            Block stored_block = Block.blocksList[stored_block_id];
            if (!((stored_block)==null)) {
                int stored_meta = self.getBlockMetadata();
                int prev_meta = self.worldObj.getBlockMetadata(X, Y, Z);
                self.worldObj.setBlockMetadataWithNotify(X, Y, Z, stored_meta, 0x04 | 0x10 | 0x80);
                boolean ret = stored_block.hasLargeCenterHardPointToFacing(self.worldObj, X, Y, Z, direction, ignore_transparency);
                self.worldObj.setBlockMetadataWithNotify(X, Y, Z, prev_meta, 0x04 | 0x10 | 0x80);
                if (!ret) {
                }
                return ret;
            }
            return false;
        }
        return false;
    }
    @Overwrite
    public void restoreStoredBlock() {
        TileEntityPiston self = (TileEntityPiston)(Object)this;
        int stored_block_id = self.getStoredBlockID();
        Block stored_block = Block.blocksList[stored_block_id];
        int stored_meta = self.getBlockMetadata();
        if (!((stored_block)==null)) {
            ((IWorldMixins)self.worldObj).updateFromNeighborShapes(self.xCoord, self.yCoord, self.zCoord, stored_block_id, stored_meta);
        }
        boolean scanning_tile_entities_temp = ((IWorldAccessMixins)self.worldObj).getScanningTileEntities();
        ((IWorldAccessMixins)self.worldObj).setScanningTileEntities(true);
        self.worldObj.setBlock(self.xCoord, self.yCoord, self.zCoord, stored_block_id, stored_meta, 0x01 | 0x02);
        if (self.storedTileEntityData != null) {
            worldObj.setBlockTileEntity(self.xCoord, self.yCoord, self.zCoord, TileEntity.createAndLoadEntity(self.storedTileEntityData));
        }
        self.worldObj.notifyBlockOfNeighborChange(self.xCoord, self.yCoord, self.zCoord, stored_block_id);
        ((IWorldAccessMixins)self.worldObj).setScanningTileEntities(scanning_tile_entities_temp);
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
    private void updatePushedObjects(float progress, float par2) {
        TileEntityPiston self = (TileEntityPiston)(Object)this;
  boolean extending = self.isExtending();
        double d = par2;
        int stored_direction = self.getPistonOrientation();
        int stored_block_id = self.getStoredBlockID();
        boolean is_bouncy = false;
        boolean is_sticky = false;
        Block block = Block.blocksList[stored_block_id];
        if (!((block)==null)) {
            int stored_meta = self.getBlockMetadata();
            if (extending) {
                is_bouncy = ((IBlockMixins)(Object)block).isBouncyWhenMoved(stored_direction, stored_meta);
            }
            is_sticky = ((IBlockMixins)(Object)block).isStickyForEntitiesWhenMoved(stored_direction, stored_meta);
        }
  AxisAlignedBB bounding_box = Block.pistonMoving.getAxisAlignedBB(self.worldObj, self.xCoord, self.yCoord, self.zCoord, stored_block_id, extending ? 1.0f - progress : progress - 1.0f, stored_direction);
  if (bounding_box != null) {
   List var4 = self.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)null, bounding_box);
   if (!var4.isEmpty()) {
                List pushed_objects = ((IBlockEntityPistonAccessMixins)self).getPushedObjects();
    pushed_objects.addAll(var4);
    Iterator var5 = pushed_objects.iterator();
    while (var5.hasNext()) {
     Entity entity = (Entity)var5.next();
                    entity.moveEntity(
                        d * (double)Facing.offsetsXForSide[stored_direction],
                        d * (double)Facing.offsetsYForSide[stored_direction],
                        d * (double)Facing.offsetsZForSide[stored_direction]
                    );
                    if (is_bouncy) {
                        entity.motionX += (double)Facing.offsetsXForSide[stored_direction];
                        entity.motionY += (double)Facing.offsetsYForSide[stored_direction];
                        entity.motionZ += (double)Facing.offsetsZForSide[stored_direction];
                    }
    }
    pushed_objects.clear();
   }
  }
        float last_progress;
        if (is_sticky && (last_progress = ((IBlockEntityPistonAccessMixins)self).getLastProgress()) < 1.0f) {
            bounding_box = block.getAsPistonMovingBoundingBox(self.worldObj, self.xCoord - Facing.offsetsXForSide[stored_direction], self.yCoord - Facing.offsetsYForSide[stored_direction], self.zCoord - Facing.offsetsZForSide[stored_direction]);
            double progress_offset = (stored_direction & 1) == 0 ? -last_progress : last_progress;
            switch (stored_direction) {
                case 0: case 1:
                    bounding_box.minX = Math.floor(bounding_box.minX);
                    bounding_box.minY += progress_offset;
                    bounding_box.minZ = Math.floor(bounding_box.minZ);
                    bounding_box.maxX = Math.ceil(bounding_box.maxX);
                    bounding_box.maxY += progress_offset;
                    bounding_box.maxZ = Math.ceil(bounding_box.maxZ);
                    break;
                case 2: case 3:
                    bounding_box.minX = Math.floor(bounding_box.minX);
                    bounding_box.minY = Math.floor(bounding_box.minY);
                    bounding_box.minZ += progress_offset;
                    bounding_box.maxX = Math.ceil(bounding_box.maxX);
                    bounding_box.maxY = Math.ceil(bounding_box.maxY);
                    bounding_box.maxZ += progress_offset;
                    break;
                default:
                    bounding_box.minX += progress_offset;
                    bounding_box.minY = Math.floor(bounding_box.minY);
                    bounding_box.minZ = Math.floor(bounding_box.minZ);
                    bounding_box.maxX += progress_offset;
                    bounding_box.maxY = Math.ceil(bounding_box.maxY);
                    bounding_box.maxZ = Math.ceil(bounding_box.maxZ);
                    break;
            }
            List var4 = self.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)null, bounding_box);
            if (!self.worldObj.isRemote) {
                AddonHandler.logMessage(""+self.worldObj.getTotalWorldTime()+" "+progress+" "+last_progress+" "+d);
                AddonHandler.logMessage(" "+bounding_box.minX+" "+bounding_box.minY+" "+bounding_box.minZ);
                AddonHandler.logMessage(" "+bounding_box.maxX+" "+bounding_box.maxY+" "+bounding_box.maxZ);
            }
   if (!var4.isEmpty()) {
                List pushed_objects = ((IBlockEntityPistonAccessMixins)self).getPushedObjects();
    pushed_objects.addAll(var4);
    Iterator var5 = pushed_objects.iterator();
                d = progress - last_progress;
    while (var5.hasNext()) {
                    ((Entity)var5.next()).moveEntity(
                        d * (double)Facing.offsetsXForSide[stored_direction],
                        d * (double)Facing.offsetsYForSide[stored_direction],
                        d * (double)Facing.offsetsZForSide[stored_direction]
                    );
    }
    pushed_objects.clear();
   }
        }
 }
}
