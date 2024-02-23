package zero.test.mixin;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import zero.test.IWorldMixins;
import zero.test.mixin.IPistonBaseAccessMixins;
import zero.test.IBlockEntityPistonMixins;
//import zero.test.mixin.IBlockEntityPistonAccessMixins;
import zero.test.IBlockMixins;
import zero.test.IEntityMixins;
import zero.test.ZeroUtil;
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
// Block piston reactions
//#define getInputSignal(...) func_94482_f(__VA_ARGS__)

@Mixin(TileEntityPiston.class)
public abstract class BlockEntityPistonMixins extends TileEntity implements IBlockEntityPistonMixins {
    public long lastTicked;
    @Shadow
    public List pushedObjects;
    @Shadow
    public float progress;
    @Shadow
    public float lastProgress;
    @Shadow
    public boolean shouldHeadBeRendered;
    @Shadow
    public NBTTagCompound storedTileEntityData;
    @Shadow
    public abstract boolean destroyAndDropIfShoveled();
    @Shadow
    public abstract void preBlockPlaced();
    //@Shadow
    //public abstract void attemptToPackItems();
    public boolean hasSmallCenterHardPointToFacing(int x, int y, int z, int direction, boolean ignoreTransparency) {
        if (
            // Treat retracting bases as a stationary block
            this.isRetractingBase() ||
            // Treat blocks that're about to stop moving as stationary
            this.progress >= 1.0F
        ) {
            TileEntityPiston self = (TileEntityPiston)(Object)this;
            Block storedBlock = Block.blocksList[self.getStoredBlockID()];
            if (!((storedBlock)==null)) {
                return storedBlock.hasSmallCenterHardPointToFacing(self.worldObj, x, y, z, direction, ignoreTransparency);
            }
        }
        return false;
    }
    public boolean hasCenterHardPointToFacing(int x, int y, int z, int direction, boolean ignoreTransparency) {
        if (
            // Treat retracting bases as a stationary block
            this.isRetractingBase() ||
            // Treat blocks that're about to stop moving as stationary
            this.progress >= 1.0F
        ) {
            TileEntityPiston self = (TileEntityPiston)(Object)this;
            Block storedBlock = Block.blocksList[self.getStoredBlockID()];
            if (!((storedBlock)==null)) {
                return storedBlock.hasCenterHardPointToFacing(self.worldObj, x, y, z, direction, ignoreTransparency);
            }
        }
        return false;
    }
    public boolean hasLargeCenterHardPointToFacing(int x, int y, int z, int direction, boolean ignoreTransparency) {
        if (
            // Treat retracting bases as a stationary block
            this.isRetractingBase() ||
            // Treat blocks that're about to stop moving as stationary
            this.progress >= 1.0F
        ) {
            TileEntityPiston self = (TileEntityPiston)(Object)this;
            Block storedBlock = Block.blocksList[self.getStoredBlockID()];
            if (!((storedBlock)==null)) {
                return storedBlock.hasLargeCenterHardPointToFacing(self.worldObj, x, y, z, direction, ignoreTransparency);
            }
        }
        return false;
    }
    // Turns out this "cache" is actually just
    // creating an extra tile entity for no
    // reason. Scrap it.
    @Overwrite
    public void storeCachedTileEntity() {
    }
    @Overwrite
    public void restoreStoredBlock() {
        TileEntityPiston self = (TileEntityPiston)(Object)this;
        //if (!self.worldObj.isRemote) AddonHandler.logMessage("Restore block ("+self.xCoord+" "+self.yCoord+" "+self.zCoord+") at time "+last_ticked);
        int storedBlockId = self.getStoredBlockID();
        Block storedBlock = Block.blocksList[storedBlockId];
        int storedMeta = self.getBlockMetadata();
        int newMeta = -1;
        if (!((storedBlock)==null)) {
            newMeta = ((IWorldMixins)self.worldObj).updateFromNeighborShapes(self.xCoord, self.yCoord, self.zCoord, storedBlockId, storedMeta);
        }
        if (newMeta >= 0) {
            self.worldObj.setBlock(self.xCoord, self.yCoord, self.zCoord, storedBlockId, newMeta, 0x01 | 0x02 | 0x40);
        } else {
            // The block is going to be destroyed, 
            // so no need to render it on the client.
            self.worldObj.setBlock(self.xCoord, self.yCoord, self.zCoord, storedBlockId, storedMeta, 0x04 | 0x10 | 0x40 | 0x80);
        }
        if (newMeta >= 0) {
            self.worldObj.notifyBlockOfNeighborChange(self.xCoord, self.yCoord, self.zCoord, storedBlockId);
        } else {
            self.worldObj.destroyBlock(self.xCoord, self.yCoord, self.zCoord, true);
        }
    }
    @Overwrite
    public void clearPistonTileEntity() {
        if (this.worldObj != null) {
            if (this.lastProgress < 1.0F) {
                this.lastProgress = this.progress = 1.0F;
                if (
                    this.worldObj.getBlockId(this.xCoord, this.yCoord, this.zCoord) == Block.pistonMoving.blockID &&
                    !this.destroyAndDropIfShoveled()
                ) {
                    this.preBlockPlaced();
                    this.restoreStoredBlock();
                }
            }
            else {
                this.worldObj.removeBlockTileEntity(this.xCoord, this.yCoord, this.zCoord);
                this.invalidate();
                if (this.storedTileEntityData != null) {
                    // Set scanningTileEntities to true
                    // so that the tile entity is always
                    // placed correctly
                    boolean scanningTileEntitiesTemp = ((IWorldAccessMixins)this.worldObj).getScanningTileEntities();
                    ((IWorldAccessMixins)this.worldObj).setScanningTileEntities(true);
                    TileEntity newTileEntity = TileEntity.createAndLoadEntity(this.storedTileEntityData);
                    this.worldObj.setBlockTileEntity(this.xCoord, this.yCoord, this.zCoord, newTileEntity);
                    // Restore original value of scanningTileEntities
                    ((IWorldAccessMixins)this.worldObj).setScanningTileEntities(scanningTileEntitiesTemp);
                    if (!scanningTileEntitiesTemp) {
                        this.worldObj.setBlockTileEntity(this.xCoord, this.yCoord, this.zCoord, newTileEntity);
                    }
                }
            }
        }
    }
    public long getLastTicked() {
        return this.lastTicked;
    }
    public void setLastTicked(long time) {
        this.lastTicked = time;
    }
    public boolean isRetractingBase() {
        return !((TileEntityPiston)(Object)this).isExtending() && this.shouldHeadBeRendered;
    }
    private static final ThreadLocal<Integer> NOCLIP_DIRECTION = ThreadLocal.withInitial(() -> -1);
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState() {
        TileEntityPiston self = (TileEntityPiston)(Object)this;
        Block storedBlock = Block.blocksList[self.getStoredBlockID()];
        AxisAlignedBB boundingBox = null;
        if (storedBlock != null) {
            boundingBox = storedBlock.getAsPistonMovingBoundingBox(self.worldObj, self.xCoord, self.yCoord, self.zCoord);
            if (boundingBox != null && !this.isRetractingBase()) {
                double progress = (double)this.progress;
                double directionOffset = self.isExtending() ? progress - 1.0D : 1.0D - progress;
                int direction = self.getPistonOrientation();
                double dX = (double)Facing.offsetsXForSide[direction] * directionOffset;
                double dY = (double)Facing.offsetsYForSide[direction] * directionOffset;
                double dZ = (double)Facing.offsetsZForSide[direction] * directionOffset;
                boundingBox.minX += dX;
                boundingBox.minY += dY;
                boundingBox.minZ += dZ;
                boundingBox.maxX += dX;
                boundingBox.maxY += dY;
                boundingBox.maxZ += dZ;
            }
        }
        return boundingBox;
    }
    // This handles collisions that aren't directly triggered by
    // the piston moving, like players standing on top of a
    // horizontally moving block.
    public void getCollisionList(AxisAlignedBB maskBox, List list, Entity entity) {
        TileEntityPiston self = (TileEntityPiston)(Object)this;
        int direction = self.getPistonOrientation();
        int storedBlockId = self.getStoredBlockID();
        int x = self.xCoord;
        int y = self.yCoord;
        int z = self.zCoord;
        if (this.isRetractingBase()) {
            // Retracting piston bases are *technically* moving an
            // un-extended piston base in order to place the correct
            // block, so this has to exist in order to add the collision
            // for the base.
            // Future versions with block states should just query the
            // collision of the extended state directly rather than
            // hardcoding this.
            AxisAlignedBB tempBox = AxisAlignedBB.getAABBPool().getAABB(
                x, y, z,
                x + 1.0D, y + 1.0D, z + 1.0D
            );
            switch (direction) {
                case 0:
                    tempBox.minY += 0.25D;
                    --y;
                    break;
                case 1:
                    tempBox.maxY -= 0.25D;
                    ++y;
                    break;
                case 2:
                    tempBox.minZ += 0.25D;
                    --z;
                    break;
                case 3:
                    tempBox.maxZ -= 0.25D;
                    ++z;
                    break;
                case 4:
                    tempBox.minX += 0.25D;
                    --x;
                    break;
                default:
                    tempBox.maxX -= 0.25D;
                    ++x;
                    break;
            }
            if (tempBox.intersectsWith(maskBox)) {
                list.add(tempBox);
            }
            storedBlockId = Block.pistonExtension.blockID;
        }
        boolean isExtending = self.isExtending();
        if (!isExtending) {
            direction = ((direction)^1);
        }
        if (direction != NOCLIP_DIRECTION.get()) {
            //AddonHandler.logMessage("Adding boxes");
            Block block = Block.blocksList[storedBlockId];
            if (!((block)==null)) {
                // Hopefully no blocks are big enough to go outside this?
                AxisAlignedBB fakeMask = AxisAlignedBB.getAABBPool().getAABB(x - 1.0D, y - 1.0D, z - 1.0D, x + 2.0D, y + 2.0D, z + 2.0D);
                List<AxisAlignedBB> collisionBoxes = new ArrayList();
                block.addCollisionBoxesToList(self.worldObj, x, y, z, fakeMask, collisionBoxes, entity);
                if (!collisionBoxes.isEmpty()) {
                    double progress = (double)this.progress;
                    double directionOffset = isExtending ? progress - 1.0D : 1.0D - progress;
                    double dX = (double)Facing.offsetsXForSide[direction] * directionOffset;
                    double dY = (double)Facing.offsetsYForSide[direction] * directionOffset;
                    double dZ = (double)Facing.offsetsZForSide[direction] * directionOffset;
                    for (AxisAlignedBB collisionBox : collisionBoxes) {
                        collisionBox.minX += dX;
                        collisionBox.minY += dY;
                        collisionBox.minZ += dZ;
                        collisionBox.maxX += dX;
                        collisionBox.maxY += dY;
                        collisionBox.maxZ += dZ;
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
        int storedBlockId = self.getStoredBlockID();
        Block block = Block.blocksList[storedBlockId];
        if (!((block)==null)) {
            List<AxisAlignedBB> collisionBoxes = new ArrayList();
            int direction = self.getPistonOrientation();
            double dX = (double)Facing.offsetsXForSide[direction];
            double dY = (double)Facing.offsetsYForSide[direction];
            double dZ = (double)Facing.offsetsZForSide[direction];
            // Hopefully no blocks are big enough to go outside this?
            AxisAlignedBB tempBox = AxisAlignedBB.getAABBPool().getAABB(self.xCoord - 1.0D, self.yCoord - 1.0D, self.zCoord - 1.0D, self.xCoord + 2.0D, self.yCoord + 2.0D, self.zCoord + 2.0D);
            // When extending the piston base isn't changed to
            // a moving block, so nothing weird needs to be done.
            if (!this.isRetractingBase()) {
                // Get the list of collision boxes for the block being moved
                block.addCollisionBoxesToList(self.worldObj, self.xCoord, self.yCoord, self.zCoord, tempBox, collisionBoxes, (Entity)null);
            }
            else {
                // This is the base, so use the collision for the piston
                // head instead of the base block.
                Block.pistonExtension.addCollisionBoxesToList(self.worldObj, self.xCoord, self.yCoord, self.zCoord, tempBox, collisionBoxes, (Entity)null);
                // IDFK why this needs 1.5 instead of 1.0
                for (AxisAlignedBB collisionBox : collisionBoxes) {
                    collisionBox.minX += dX * 1.5D;
                    collisionBox.minY += dY * 1.5D;
                    collisionBox.minZ += dZ * 1.5D;
                    collisionBox.maxX += dX * 1.5D;
                    collisionBox.maxY += dY * 1.5D;
                    collisionBox.maxZ += dZ * 1.5D;
                }
            }
            if (!collisionBoxes.isEmpty()) {
                double directionOffset = lastProgress - 1.0F;
                if (!self.isExtending()) {
                    directionOffset = 1.0F - lastProgress;
                    direction = ((direction)^1);
                    dX = -dX;
                    dY = -dY;
                    dZ = -dZ;
                }
                AxisAlignedBB boundingBox = null;
                for (AxisAlignedBB collisionBox : collisionBoxes) {
                    // Offset the collision boxes in the correct
                    // direction for the piston movement.
                    collisionBox.minX += directionOffset * dX;
                    collisionBox.minY += directionOffset * dY;
                    collisionBox.minZ += directionOffset * dZ;
                    collisionBox.maxX += directionOffset * dX;
                    collisionBox.maxY += directionOffset * dY;
                    collisionBox.maxZ += directionOffset * dZ;
                    // boundingBox won't exist on the first iteration,
                    // so just duplicate the collision rather than expanding
                    if (boundingBox != null) {
                        boundingBox.expandToInclude(collisionBox);
                    } else {
                        boundingBox = collisionBox.copy();
                    }
                }
                double idkOffset = progress - lastProgress;
                // Stretch the entity check box a bit
                // to properly detect things in front
                // of the moving block.
                tempBox.setBB(boundingBox);
                switch (direction) {
                    case 0:
                        tempBox.maxY = tempBox.minY;
                        tempBox.minY += dY * idkOffset;
                        break;
                    case 1:
                        tempBox.minY = tempBox.maxY;
                        tempBox.maxY += dY * idkOffset;
                        break;
                    case 2:
                        tempBox.maxZ = tempBox.minZ;
                        tempBox.minZ += dZ * idkOffset;
                        break;
                    case 3:
                        tempBox.minZ = tempBox.maxZ;
                        tempBox.maxZ += dZ * idkOffset;
                        break;
                    case 4:
                        tempBox.maxX = tempBox.minX;
                        tempBox.minX += dX * idkOffset;
                        break;
                    default:
                        tempBox.minX = tempBox.maxX;
                        tempBox.maxX += dX * idkOffset;
                        break;
                }
                tempBox.expandToInclude(boundingBox);
                int storedMeta = self.getBlockMetadata();
                List<Entity> entityList = self.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)null, tempBox);
                if (!entityList.isEmpty()) {
                    boolean isBouncy = ((IBlockMixins)(Object)block).isBouncyWhenMoved(direction, storedMeta);
                    NOCLIP_DIRECTION.set(direction);
                    for (Entity entity : entityList) {
                        //if (entity instanceof EntityPlayerMP) {
                            //continue;
                        //}
                        int entityPushFlags = ((IEntityMixins)entity).getPistonMobilityFlags(direction);
                        if (!(((entityPushFlags)&(1|2))!=0)) {
                            continue;
                        }
                        // This is so awful, why doesn't
                        // getBoundingBox do anything for 90%
                        // of entities but getVisualBoundingBox
                        // just returns the normal bounding box?
                        AxisAlignedBB entityBox = entity.getVisualBoundingBox();
                        if (entityBox == null) {
                            continue;
                        }
                        if (isBouncy && (((entityPushFlags)&2)!=0)) {
                            double entityBounceMultiplier = ((IEntityMixins)entity).getPistonBounceMultiplier(direction);
                            switch (((direction)&~1)) {
                                case 0x4: {
                                    double tempMotion = entity.motionX;
                                    if (Math.abs(tempMotion) < Math.abs(dX)) {
                                        tempMotion = dX;
                                    }
                                    entity.motionX = tempMotion * entityBounceMultiplier;
                                    break;
                                }
                                case 0x0: {
                                    double tempMotion = entity.motionY;
                                    if (Math.abs(tempMotion) < Math.abs(dY)) {
                                        tempMotion = dY;
                                    }
                                    entity.motionY = tempMotion * entityBounceMultiplier;
                                    break;
                                }
                                default: {
                                    double tempMotion = entity.motionZ;
                                    if (Math.abs(tempMotion) < Math.abs(dZ)) {
                                        tempMotion = dZ;
                                    }
                                    entity.motionZ = tempMotion * entityBounceMultiplier;
                                    break;
                                }
                            }
                        }
                        if (!(((entityPushFlags)&1)!=0)) {
                            continue;
                        }
                        double pushDistance = 0.0D;
                        for (AxisAlignedBB collisionBox : collisionBoxes) {
                            tempBox.setBB(collisionBox);
                            switch (direction) {
                                case 0:
                                    tempBox.maxY = tempBox.minY;
                                    tempBox.minY += dY * idkOffset;
                                    break;
                                case 1:
                                    tempBox.minY = tempBox.maxY;
                                    tempBox.maxY += dY * idkOffset;
                                    break;
                                case 2:
                                    tempBox.maxZ = tempBox.minZ;
                                    tempBox.minZ += dZ * idkOffset;
                                    break;
                                case 3:
                                    tempBox.minZ = tempBox.maxZ;
                                    tempBox.maxZ += dZ * idkOffset;
                                    break;
                                case 4:
                                    tempBox.maxX = tempBox.minX;
                                    tempBox.minX += dX * idkOffset;
                                    break;
                                default:
                                    tempBox.minX = tempBox.maxX;
                                    tempBox.maxX += dX * idkOffset;
                                    break;
                            }
                            if (tempBox.intersectsWith(entityBox)) {
                                // Calculate how far the entity is intersecting
                                // the movement box.
                                double newDistance;
                                switch (direction) {
                                    case 0:
                                        newDistance = entityBox.maxY - tempBox.minY;
                                        break;
                                    case 1:
                                        newDistance = tempBox.maxY - entityBox.minY;
                                        break;
                                    case 2:
                                        newDistance = entityBox.maxZ - tempBox.minZ;
                                        break;
                                    case 3:
                                        newDistance = tempBox.maxZ - entityBox.minZ;
                                        break;
                                    case 4:
                                        newDistance = entityBox.maxX - tempBox.minX;
                                        break;
                                    default:
                                        newDistance = tempBox.maxX - entityBox.minX;
                                        break;
                                }
                                pushDistance = Math.max(pushDistance, newDistance);
                                if (pushDistance >= idkOffset) {
                                    // This breaks from the for loop
                                    // checking collision boxes
                                    break;
                                }
                            }
                        }
                        if (pushDistance <= 0.0D) {
                            // Continue to next entity
                            continue;
                        }
                        pushDistance = Math.min(pushDistance, idkOffset) + 0.01D;
                         ;
                        ((IEntityMixins)entity).moveEntityByPiston(
                            pushDistance * dX,
                            pushDistance * dY,
                            pushDistance * dZ
                        );
                         ;
                        if (this.isRetractingBase()) {
                            //AddonHandler.logMessage("Entity in baseA");
                            // tempBox is just getting reused from the AABB pool here
                            this.ejectEntityFromPistonBase(entity, entityBox, direction, idkOffset, tempBox);
                        }
                    }
                    NOCLIP_DIRECTION.set(-1);
                }
                if (((IBlockMixins)(Object)block).isStickyForEntitiesWhenMoved(direction, storedMeta)) {
                    // Extend the bounding box in every direction
                    // except the direction opposite the movement
                    if (direction != 4) {
                        boundingBox.minX -= GLUE_BLOCK_FUDGE_FACTOR;
                    }
                    if (direction != 0) {
                        boundingBox.minY -= GLUE_BLOCK_FUDGE_FACTOR;
                    }
                    if (direction != 2) {
                        boundingBox.minZ -= GLUE_BLOCK_FUDGE_FACTOR;
                    }
                    if (direction != 5) {
                        boundingBox.maxX += GLUE_BLOCK_FUDGE_FACTOR;
                    }
                    if (direction != 1) {
                        boundingBox.maxY += GLUE_BLOCK_FUDGE_FACTOR;
                    }
                    if (direction != 3) {
                        boundingBox.maxZ += GLUE_BLOCK_FUDGE_FACTOR;
                    }
                    entityList = self.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)null, boundingBox);
                    dX *= idkOffset;
                    dY *= idkOffset;
                    dZ *= idkOffset;
                    for (Entity entity : entityList) {
                        if ((((((IEntityMixins)entity).getPistonMobilityFlags(direction))&4)!=0)) {
                             ;
                            ((IEntityMixins)entity).moveEntityByPiston(dX, dY, dZ);
                             ;
                        }
                    }
                }
            }
        }
    }
    protected void ejectEntityFromPistonBase(Entity entity, AxisAlignedBB entityBox, int direction, double idkOffset, AxisAlignedBB box) {
        box.maxX = (box.minX = (double)this.xCoord) + 1.0D;
        box.maxY = (box.minY = (double)this.yCoord) + 1.0D;
        box.maxZ = (box.minZ = (double)this.zCoord) + 1.0D;
        if (entityBox.intersectsWith(box)) {
            double newDistanceA;
            double newDistanceB;
            direction = ((direction)^1);
            switch (direction) {
                case 0:
                    newDistanceA = entityBox.maxY - box.minY;
                    newDistanceB = Math.min(entityBox.maxY, box.maxY) - box.minY;
                    break;
                case 1:
                    newDistanceA = box.maxY - entityBox.minY;
                    newDistanceB = box.maxY - Math.max(entityBox.minY, box.minY);
                    break;
                case 2:
                    newDistanceA = entityBox.maxZ - box.minZ;
                    newDistanceB = Math.min(entityBox.maxZ, box.maxZ) - box.minZ;
                    break;
                case 3:
                    newDistanceA = box.maxZ - entityBox.minZ;
                    newDistanceB = box.maxZ - Math.max(entityBox.minZ, box.minZ);
                    break;
                case 4:
                    newDistanceA = entityBox.maxX - box.minX;
                    newDistanceB = Math.min(entityBox.maxX, box.maxX) - box.minX;
                    break;
                default:
                    newDistanceA = box.maxX - entityBox.minX;
                    newDistanceB = box.maxX - Math.max(entityBox.minX, box.minX);
                    break;
            }
            newDistanceA += 0.01D;
            newDistanceB += 0.01D;
            //AddonHandler.logMessage("Entity in baseB "+newDistanceA+" "+newDistanceB+" "+idkOffset);
            if (Math.abs(newDistanceA - newDistanceB) < 0.01D) {
                double pushDistance = Math.min(newDistanceA, idkOffset) + 0.01D;
                //AddonHandler.logMessage("Entity in baseC "+pushDistance);
                ((IEntityMixins)entity).moveEntityByPiston(
                    pushDistance * (double)Facing.offsetsXForSide[direction],
                    pushDistance * (double)Facing.offsetsYForSide[direction],
                    pushDistance * (double)Facing.offsetsZForSide[direction]
                );
            }
        }
    }
    @Overwrite
    public void updateEntity() {
        this.lastTicked = this.worldObj.getTotalWorldTime();
        float currentProgress = this.progress;
        this.lastProgress = currentProgress;
        if (currentProgress >= 1.0F) {
            // FCMOD: Added
            this.attemptToPackItems();
            // END FCMOD
            if (
                this.worldObj.getBlockId(this.xCoord, this.yCoord, this.zCoord) == Block.pistonMoving.blockID &&
                !this.destroyAndDropIfShoveled()
            ) {
                this.preBlockPlaced();
                this.restoreStoredBlock();
            }
            else {
                this.worldObj.removeBlockTileEntity(this.xCoord, this.yCoord, this.zCoord);
                this.invalidate();
            }
            return;
        }
        float nextProgress = currentProgress + 0.5F;
        this.updatePushedObjects(nextProgress, currentProgress);
        if (nextProgress >= 1.0F) {
            nextProgress = 1.0F;
        }
        this.progress = nextProgress;
    }
    @Inject(
        method = "readFromNBT(Lnet/minecraft/src/NBTTagCompound;)V",
        at = @At("TAIL")
    )
    public void readFromNBT_inject(NBTTagCompound compound, CallbackInfo info) {
        if (compound.hasKey("source")) {
            this.shouldHeadBeRendered = compound.getBoolean("source");
        }
    }
    @Inject(
        method = "writeToNBT(Lnet/minecraft/src/NBTTagCompound;)V",
        at = @At("TAIL")
    )
    public void writeToNBT_inject(NBTTagCompound compound, CallbackInfo info) {
        compound.setBoolean("source", this.shouldHeadBeRendered);
    }
    @Shadow
    public abstract boolean isLocationSuitableForPacking(int i, int j, int k, int iPistonDirection);
    @Shadow
    public abstract void removeItemsOfTypeFromList(ItemStack stack, int iCount, List list);
    @Shadow
    public abstract void createPackedBlockOfTypeAtLocation(int blockID, int metadata, int x, int y, int z);
    @Overwrite
    public void attemptToPackItems() {
        TileEntityPiston self = (TileEntityPiston)(Object)this;
        if (
            !this.worldObj.isRemote &&
            self.isExtending()
        ) {
            int direction = self.getPistonOrientation();
            int x = this.xCoord + Facing.offsetsXForSide[direction];
            int y = this.yCoord + Facing.offsetsYForSide[direction];
            int z = this.zCoord + Facing.offsetsZForSide[direction];
            if (this.isLocationSuitableForPacking(x, y, z, ((direction)^1))) {
                AxisAlignedBB targetBox = AxisAlignedBB.getAABBPool().getAABB(
                    x, y, z,
                    x + 1.0D, y + 1.0D, z + 1.0D
                );
                List<EntityItem> itemsWithinBox = this.worldObj.getEntitiesWithinAABB(EntityItem.class, targetBox);
                if (!itemsWithinBox.isEmpty()) {
                    PistonPackingRecipe recipe = PistonPackingCraftingManager.instance.getValidRecipeFromItemList(itemsWithinBox);
                    if (recipe != null) {
                        for (ItemStack stack : recipe.getInput()) {
                            this.removeItemsOfTypeFromList(stack, stack.stackSize, itemsWithinBox);
                        }
                        this.createPackedBlockOfTypeAtLocation(recipe.getOutput().blockID, recipe.getOutputMetadata(), x, y, z);
                    }
                }
            }
        }
    }
}
