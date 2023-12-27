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
// func_96440_m = updateNeighbourForOutputSignal
// func_94487_f = blockIdIsActiveOrInactive
// func_94485_e = getActiveBlockID
// func_94484_i = getInactiveBlockID
// func_96470_c(metadata) = getRepeaterPoweredState(metadata)
// func_94478_d = shouldTurnOn
// func_94488_g = getAlternateSignal
// func_94490_c = isSubtractMode
// func_94491_m = calculateOutputSignal
// func_94483_i_ = __notifyOpposite
// func_94481_j_ = getComparatorDelay
/// func_94482_f = getInputSignal
// func_96476_c = refreshOutputState
//#define getInputSignal(...) func_94482_f(__VA_ARGS__)
// Vanilla observers
// Slime blocks
// Push only and dead coral fans
/// Utility Macro Defs
/// Mutable Pos Move X
/// Mutable Pos Move Y
/// Mutable Pos Move Z
/// Mutable Pos Move
/// Mutable Pos Create
/// C-esque stuff
//#define printf(...) System.out.printf(__VA_ARGS__)
/// x86-esque stuff
/// Some operations are available
/// as and @IntrinsicCandidate, in
/// which case that form is preferred
//#define MOVSX(A) ((int)(A))
//#define MOVSXD(A) ((long)(A))
// Efficiently tests if [value] is within the range [min, max)
// Efficiently tests if [value] is within the range [min, max]
// Valid for both signed and unsigned integers
/// Random direction crap
/*
case NEIGHBOR_WEST:
case NEIGHBOR_EAST:
case NEIGHBOR_DOWN:
case NEIGHBOR_DOWN_WEST:
case NEIGHBOR_DOWN_EAST:
case NEIGHBOR_UP:
case NEIGHBOR_UP_WEST:
case NEIGHBOR_UP_EAST:
case NEIGHBOR_NORTH:
case NEIGHBOR_DOWN_NORTH:
case NEIGHBOR_UP_NORTH:
case NEIGHBOR_SOUTH:
case NEIGHBOR_DOWN_SOUTH:
case NEIGHBOR_UP_SOUTH:
*/
/// Expression Crap
/// Metadata stuff
// Meta write mask OFFSET, BITS
// Meta mask values OFFSET/BITS
// Meta mask values before shifting OFFSET, BITS
// Meta high value data OFFSET, BITS
// 0 = Needs != 0 if bool
// 1 = Is last field
// true = Is last field but uses != 0 anyway because it's 4 bits
// Meta const lookup OFFSET, VALUE
// Meta full write BITS, VALUE
//#define READ_META_FIELD_RAW(m,f)(    /*TEXT*/(m)    MACRO_IF_NOT(MACRO_IS_4(META_BITS(f)),        MACRO_IF_NOT(MACRO_IS_0(META_OFFSET(f)),            /*TEXT*/>>>META_OFFSET(f)        )        MACRO_IF_NOT(MACRO_IS_TRUTHY(META_IS_LAST(f)),            /*TEXT*/&META_MASK(f)        )    ))
//#define READ_META_FIELD_BOOL(m,f)(    /*TEXT*/((m)    MACRO_IF_NOT(MACRO_IS_TRUTHY(META_IS_ONLY_FIELD(f)),        MACRO_TERN(MACRO_IS_TRUTHY(META_IS_LAST(f)),            /*TEXT*/>META_BOOL_CMP(f)        /*ELSE*/,            /*TEXT*/&META_MASK_UNSHIFTED(f)        )    )    /*TEXT*/)    MACRO_IF_NOT(MACRO_IS_TRUTHY(META_BOOL_SKIPS_NEQ(f)),        /*TEXT*/!=0    ))
//#define READ_META_FIELD(m,f)(    MACRO_TERN(MACRO_IS_TRUTHY(META_IS_BOOL(f)),        READ_META_FIELD_BOOL(m,f)    /*ELSE*/,        READ_META_FIELD_RAW(m,f)    ))
//#define MERGE_META_FIELD_RAW(m,f,v)(    MACRO_TERN(MACRO_IS_TRUTHY(META_IS_ONLY_FIELD(f)),        /*TEXT*/(v)        MACRO_IF_NOT(META_VALID_CONST(f,v),            /*TEXT*/&META_WRITE_MASK(f)        )    /*ELSE*/,        /*TEXT*/(m)        MACRO_TERN(META_VALID_CONST(f,v),            MACRO_IF_NOT(META_IS_FULL_WRITE(f,v),                /*TEXT*/&META_WRITE_MASK(f)            )            MACRO_IF_NOT(MACRO_IS_FALSY(v),                /*TEXT*/|META_CONST_LOOKUP(f,v)            )        /*ELSE*/,            /*TEXT*/&META_WRITE_MASK(f)|(v)            MACRO_IF_NOT(MACRO_IS_0(META_OFFSET(f)),                /*TEXT*/<<META_OFFSET(f)            )        )    ))
//#define MERGE_META_FIELD_BOOL(m,f,v)(    MACRO_TERN(MACRO_IS_BOOL_ANY(v),        MACRO_TERN(MACRO_IS_TRUTHY(META_IS_ONLY_FIELD(f)),            /*TEXT*/MACRO_CAST_FROM_BOOL(v)        /*ELSE*/,            /*TEXT*/(m)            MACRO_TERN(MACRO_IS_TRUTHY(v),                /*TEXT*/|META_CONST_LOOKUP(f,1)            /*ELSE*/,                /*TEXT*/&META_WRITE_MASK(f)            )        )    /*ELSE*/,        MACRO_TERN(MACRO_IS_TRUTHY(META_IS_ONLY_FIELD(f)),            /*TEXT*/(v)&1        /*ELSE*/,            /*TEXT*/(m)&META_WRITE_MASK(f)|((v)&1)            MACRO_IF_NOT(MACRO_IS_0(META_OFFSET(f)),                /*TEXT*/<<META_OFFSET(f)            )        )    ))
//#define MERGE_META_FIELD(m,f,v)(    MACRO_TERN(MACRO_IS_TRUTHY(META_IS_BOOL(f)),        MERGE_META_FIELD_BOOL(m,f,v)    /*ELSE*/,        MERGE_META_FIELD_RAW(m,f,v)    ))
/// Fake Direction Metadata
/// Misc. Flags
// Glazed terracotta
// Z doesn't need to be masked because it's in the top bits anyway

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
                // Since the block is likely to try getting its own metadata,
                // silently swap out the metadata value during the face test
                self.worldObj.setBlockMetadataWithNotify(X, Y, Z, stored_meta, 0x04 | 0x10 | 0x80);
                boolean ret = stored_block.hasLargeCenterHardPointToFacing(self.worldObj, X, Y, Z, direction, ignore_transparency);
                self.worldObj.setBlockMetadataWithNotify(X, Y, Z, prev_meta, 0x04 | 0x10 | 0x80);
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
        if (!((stored_block)==null)) {
            ((IWorldMixins)self.worldObj).updateFromNeighborShapes(self.xCoord, self.yCoord, self.zCoord, stored_block_id, stored_meta);
        }
        // Set scanningTileEntities to true
        // so that the tile entity is always
        // placed correctly
        boolean scanning_tile_entities_temp = ((IWorldAccessMixins)self.worldObj).getScanningTileEntities();
        ((IWorldAccessMixins)self.worldObj).setScanningTileEntities(true);
        self.worldObj.setBlock(self.xCoord, self.yCoord, self.zCoord, stored_block_id, stored_meta, 0x01 | 0x02);
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
            //bounding_box = Block.pistonMoving.getAxisAlignedBB(self.worldObj, self.xCoord, self.yCoord, self.zCoord, stored_block_id, extending ? 1.0f - progress : progress - 1.0f, stored_direction);
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
