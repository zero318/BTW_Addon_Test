package zero.test.mixin;
import net.minecraft.src.*;
import net.minecraft.server.MinecraftServer;
import btw.AddonHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import zero.test.IEntityMixins;
import zero.test.mixin.IEntityMinecartAccessMixins;
import zero.test.IBaseRailBlockMixins;
import java.util.List;
// Block piston reactions

@Mixin(EntityMinecart.class)
public abstract class EntityMinecartMixins extends Entity {
    public EntityMinecartMixins(World world) {
        super(world);
    }
    @Inject(
        method = "<init>(Lnet/minecraft/src/World;)V",
        at = @At("TAIL")
    )
    public void constructor_inject(World world, CallbackInfo info) {
        ((EntityMinecart)(Object)this).yOffset = 0.0F;
    }
    @Overwrite
    public double getMountedYOffset() {
        // Random BS to fix riding being too low
        return 0.125D;
    }
    @Shadow
    private static int[][][] matrix;
    @Shadow
    public abstract void updateOnTrack(int par1, int par2, int par3, double par4, double par6, int par8, int par9);
    @Shadow
    public abstract void func_94088_b(double par1);
    public double getMaxSpeed() {
        return 0.4D;
    }
    @Overwrite
    public void onUpdate() {
        EntityMinecart self = (EntityMinecart)(Object)this;
        IUpdatePlayerListBox field_82344_g_thing = ((IEntityMinecartAccessMixins)self).getField_82344_g();
        if (field_82344_g_thing != null) {
            field_82344_g_thing.update();
        }
        if (self.getRollingAmplitude() > 0) {
            self.setRollingAmplitude(self.getRollingAmplitude() - 1);
        }
        if (self.getDamage() > 0) {
            self.setDamage(self.getDamage() - 1);
        }
        if (self.posY < -64.0D) {
            this.kill();
        }
        int var2;
        if (
            !self.worldObj.isRemote &&
            self.worldObj instanceof WorldServer
        ) {
            self.worldObj.theProfiler.startSection("portal");
            MinecraftServer var1 = ((WorldServer)self.worldObj).getMinecraftServer();
            var2 = self.getMaxInPortalTime();
            if (this.inPortal) {
                if (var1.getAllowNether()) {
                    if (
                        self.ridingEntity == null &&
                        this.field_82153_h++ >= var2
                    ) {
                        this.field_82153_h = var2;
                        self.timeUntilPortal = self.getPortalCooldown();
                        byte var3;
                        if (self.worldObj.provider.dimensionId == -1) {
                            var3 = 0;
                        }
                        else {
                            var3 = -1;
                        }
                        self.travelToDimension(var3);
                    }
                    this.inPortal = false;
                }
            }
            else {
                if (this.field_82153_h > 0) {
                    this.field_82153_h -= 4;
                }
                if (this.field_82153_h < 0) {
                    this.field_82153_h = 0;
                }
            }
            if (self.timeUntilPortal > 0) {
                --self.timeUntilPortal;
            }
            self.worldObj.theProfiler.endSection();
        }
        if (self.worldObj.isRemote) {
            int turn_progress = ((IEntityMinecartAccessMixins)self).getTurnProgress();
            if (turn_progress > 0) {
                // Divisions swapped to multiplications for SPEED
                double dTurn = 1.0D / (double)turn_progress;
                double dX = self.posX + (((IEntityMinecartAccessMixins)self).getMinecartX() - self.posX) * dTurn;
                double dY = self.posY + (((IEntityMinecartAccessMixins)self).getMinecartY() - self.posY) * dTurn;
                double dZ = self.posZ + (((IEntityMinecartAccessMixins)self).getMinecartZ() - self.posZ) * dTurn;
                double dYaw = MathHelper.wrapAngleTo180_double(((IEntityMinecartAccessMixins)self).getMinecartYaw() - (double)self.rotationYaw);
                self.rotationYaw = (float)((double)self.rotationYaw + dYaw * dTurn);
                self.rotationPitch = (float)((double)self.rotationPitch + (((IEntityMinecartAccessMixins)self).getMinecartPitch() - (double)self.rotationPitch) * dTurn);
                ((IEntityMinecartAccessMixins)self).setTurnProgress(turn_progress - 1);
                self.setPosition(dX, dY, dZ);
            }
            else {
                self.setPosition(self.posX, self.posY, self.posZ);
            }
            this.setRotation(self.rotationYaw, self.rotationPitch);
        }
        else {
            self.prevPosX = self.posX;
            self.prevPosY = self.posY;
            self.prevPosZ = self.posZ;
            self.motionY -= 0.03999999910593033D;
            int x = MathHelper.floor_double(self.posX);
            var2 = MathHelper.floor_double(self.posY);
            int z = MathHelper.floor_double(self.posZ);
            if (BlockRailBase.isRailBlockAt(self.worldObj, x, var2 - 1, z)) {
                --var2;
            }
            int blockId = self.worldObj.getBlockId(x, var2, z);
            if (BlockRailBase.isRailBlock(blockId)) {
                int meta = self.worldObj.getBlockMetadata(x, var2, z);
                this.updateOnTrack(
                    x, var2, z,
                    this.getMaxSpeed() * ((IBaseRailBlockMixins)Block.blocksList[blockId]).getRailMaxSpeedFactor(),
                    0.0078125D, blockId, meta
                );
                if (blockId == Block.railActivator.blockID) {
                    self.onActivatorRailPass(x, var2, z, (meta & 8) != 0);
                }
            }
            else {
                this.func_94088_b(this.getMaxSpeed());
            }
            this.doBlockCollisions();
            self.rotationPitch = 0.0F;
            double xDelta = self.prevPosX - self.posX;
            double zDelta = self.prevPosZ - self.posZ;
            if (xDelta * xDelta + zDelta * zDelta > 0.001D) {
                self.rotationYaw = (float)(Math.atan2(zDelta, xDelta) * 180.0D / Math.PI);
                //if (((IEntityMinecartAccessMixins)self).getIsInReverse()) {
                    //self.rotationYaw += 180.0F;
                //}
            }
            double yawDelta = (double)MathHelper.wrapAngleTo180_float(self.rotationYaw - self.prevRotationYaw);
            if (yawDelta < -170.0D || yawDelta >= 170.0D) {
                //self.rotationYaw += 180.0F;
                ((IEntityMinecartAccessMixins)self).setIsInReverse(
                    !((IEntityMinecartAccessMixins)self).getIsInReverse()
                );
            }
            this.setRotation(self.rotationYaw, self.rotationPitch);
            // Still trying to fix parallel track collisions...
            /*
            double expandX = 0.0D;
            double expandZ = 0.0D;
            switch (YAW_FLAT_DIRECTION8(self.rotationYaw)) {
                case FLAT_DIRECTION8_NORTH: case FLAT_DIRECTION8_SOUTH:
                    expandX = 0.2D;
                    //expandZ = 0.2D;
                    break;
                case FLAT_DIRECTION8_EAST: case FLAT_DIRECTION8_WEST:
                    //expandX = 0.2D;
                    expandZ = 0.2D;
                    break;
            }
            
            //AddonHandler.logMessage(
                //YAW_FLAT_DIRECTION8(self.rotationYaw)+" "+self.rotationYaw
            //);
            
            
            List<Entity> entityList = self.worldObj.getEntitiesWithinAABBExcludingEntity(self, self.boundingBox.expand(expandX, 0.0D, expandZ));
            */
            List<Entity> entityList = self.worldObj.getEntitiesWithinAABBExcludingEntity(self, self.boundingBox.expand(0.2D, 0.0D, 0.2D));
            if (
                entityList != null &&
                !entityList.isEmpty()
            ) {
                for (Entity entity : entityList) {
                    if (
                        entity != self.riddenByEntity &&
                        entity.canBePushed() &&
                        entity instanceof EntityMinecart
                    ) {
                        entity.applyEntityCollision(self);
                    }
                }
            }
            if (
                self.riddenByEntity != null &&
                self.riddenByEntity.isDead
            ) {
                if (self.riddenByEntity.ridingEntity == self) {
                    self.riddenByEntity.ridingEntity = null;
                }
                self.riddenByEntity = null;
            }
        }
    }
    public void applyEntityCollision(Entity entity) {
        if (
            !this.worldObj.isRemote &&
            entity != this.riddenByEntity
        ) {
            EntityMinecart self = (EntityMinecart)(Object)this;
            if (
                entity instanceof EntityLiving &&
                !(entity instanceof EntityPlayer) &&
                !(entity instanceof EntityIronGolem) &&
                self.getMinecartType() == 0 &&
                this.motionX * this.motionX + this.motionZ * this.motionZ > 0.01D &&
                this.riddenByEntity == null &&
                entity.ridingEntity == null
            ) {
                entity.mountEntity(this);
            }
            double var2 = entity.posX - this.posX;
            double var4 = entity.posZ - this.posZ;
            double distance = var2 * var2 + var4 * var4;
            if (distance >= 9.999999747378752E-5D) {
                distance = Math.sqrt(distance);
                var2 /= distance;
                var4 /= distance;
                double invDistance = 1.0D / distance;
                if (invDistance > 1.0D) {
                    invDistance = 1.0D;
                }
                var2 *= invDistance;
                var4 *= invDistance;
                var2 *= 0.1D;
                var4 *= 0.1D;
                var2 *= (double)(1.0F - this.entityCollisionReduction);
                var4 *= (double)(1.0F - this.entityCollisionReduction);
                var2 *= 0.5D;
                var4 *= 0.5D;
                if (entity instanceof EntityMinecart) {
                    Vec3 posVec = this.worldObj.getWorldVec3Pool().getVecFromPool(entity.posX - this.posX, 0.0D, entity.posZ - this.posZ).normalize();
                    Vec3 rotVec = this.worldObj.getWorldVec3Pool().getVecFromPool(MathHelper.cos(((this.rotationYaw)*0.017453292F)), 0.0D, MathHelper.sin(((this.rotationYaw)*0.017453292F))).normalize();
                    double var16 = Math.abs(posVec.dotProduct(rotVec));
                    /*
                    AddonHandler.logMessage(
                        "    "+this.rotationYaw+" "+entity.rotationYaw+"\n"+
                        "    "+posVec.xCoord+" "+posVec.yCoord+" "+posVec.zCoord+"\n"+
                        "    "+rotVec.xCoord+" "+rotVec.yCoord+" "+rotVec.zCoord+"\n"+
                        "    "+var16+" "+this.entityId+" "+entity.entityId+"\n"+
                        "Self:  "+this.posX+" "+this.posZ+" "+this.width+" "+this.boundingBox.minX+" "+this.boundingBox.maxX+" "+this.boundingBox.minZ+" "+this.boundingBox.maxZ+"\n"+
                        "Other: "+entity.posX+" "+entity.posZ+" "+entity.width+" "+entity.boundingBox.minX+" "+entity.boundingBox.maxX+" "+entity.boundingBox.minZ+" "+entity.boundingBox.maxZ
                    );
                    */
                    /*
                    AddonHandler.logMessage(
                        this.entityId+" "+entity.entityId+" "+var16+" "+this.rotationYaw+" "+entity.rotationYaw
                    );
                    */
                    // There's still an edge case at 0.79
                    if (var16 < 0.78D) {
                        return;
                    }
                    // This seems to make furnace cart shunting work around
                    // corners, but I absolutely hate the magic number
                    if (var16 < 0.80D) {
                        var2 = -var2;
                        var4 = -var4;
                    }
                    if (
                        ((EntityMinecart)entity).getMinecartType() == 2 &&
                        /*(
                            ((EntityMinecart)entity).getMinecartType() == MINECART_FURNACE ||
                            ((EntityMinecartMixins)entity).shuntTime > 0
                        ) &&*/
                        self.getMinecartType() != 2
                    ) {
                        this.motionX *= 0.2D;
                        this.motionZ *= 0.2D;
                        this.addVelocity(entity.motionX - var2, 0.0D, entity.motionZ - var4);
                        entity.motionX *= 0.95D;
                        entity.motionZ *= 0.95D;
                        //AddonHandler.logMessage("ShuntTimesA: "+((EntityMinecartMixins)entity).shuntTime+" "+this.shuntTime);
                    }
                    else if (
                        ((EntityMinecart)entity).getMinecartType() != 2 &&
                        self.getMinecartType() == 2
                        /*(
                            self.getMinecartType() == MINECART_FURNACE ||
                            this.shuntTime > 0
                        )*/
                    ) {
                        entity.motionX *= 0.2D;
                        entity.motionZ *= 0.2D;
                        entity.addVelocity(this.motionX + var2, 0.0D, this.motionZ + var4);
                        this.motionX *= 0.95D;
                        this.motionZ *= 0.95D;
                        //AddonHandler.logMessage("ShuntTimesB: "+this.shuntTime+" "+((EntityMinecartMixins)entity).shuntTime);
                    }
                    else {
                        //AddonHandler.logMessage("ShuntTimesC: "+((EntityMinecartMixins)entity).shuntTime+" "+this.shuntTime);
                        double var18 = (entity.motionX + this.motionX) / 2.0D;
                        double var20 = (entity.motionZ + this.motionZ) / 2.0D;
                        this.motionX *= 0.2D;
                        this.motionZ *= 0.2D;
                        this.addVelocity(var18 - var2, 0.0D, var20 - var4);
                        entity.motionX *= 0.2D;
                        entity.motionZ *= 0.2D;
                        entity.addVelocity(var18 + var2, 0.0D, var20 + var4);
                    }
                }
                else {
                    this.addVelocity(-var2, 0.0D, -var4);
                    entity.addVelocity(var2 * 0.25D, 0.0D, var4 * 0.25D);
                }
            }
        }
    }
/*
    @Environment(EnvType.CLIENT)
    public Vec3 getPosOffs(double x, double y, double z, double par7) {
        EntityMinecart self = (EntityMinecart)(Object)this;
        
        int iX = MathHelper.floor_double(x);
        int iY = MathHelper.floor_double(y);
        int iZ = MathHelper.floor_double(z);

        if (BlockRailBase.isRailBlockAt(this.worldObj, iX, iY - 1, iZ)) {
            --iY;
        }

        int blockId = this.worldObj.getBlockId(iX, iY, iZ);

        if (BlockRailBase.isRailBlock(blockId)) {
            int railShape = this.worldObj.getBlockMetadata(iX, iY, iZ);

            if (((BlockRailBase)Block.blocksList[blockId]).isPowered()) {
                railShape &= 7;
            }

            y = (double)iY;

            if (RAIL_IS_ASCENDING(railShape)) {
                y = (double)(iY + 1);
            }

            int[][] exitPair = matrix[var13];
            double diffX = (double)(exitPair[1][0] - exitPair[0][0]);
            double diffZ = (double)(exitPair[1][2] - exitPair[0][2]);
            double var19 = Math.sqrt(diffX * diffX + diffZ * diffZ);
            x += (diffX / var19) * par7;
            z += (diffZ / var19) * par7;
            
            iX = MathHelper.floor_double(x) - iX;
            iZ = MathHelper.floor_double(z) - iZ;

            if (
                exitPair[0][1] != 0 &&
                iX == exitPair[0][0] &&
                iZ == exitPair[0][2]
            ) {
                y += (double)exitPair[0][1];
            }
            else if (
                exitPair[1][1] != 0 &&
                iX == exitPair[1][0] &&
                iZ == exitPair[1][2]
            ) {
                y += (double)exitPair[1][1];
            }

            return this.getPos(x, y, z);
        }
        return null;
    }
*/
    @Overwrite
    public Vec3 func_70489_a(double x, double y, double z) {
        EntityMinecart self = (EntityMinecart)(Object)this;
        int iX = MathHelper.floor_double(x);
        int iY = MathHelper.floor_double(y);
        int iZ = MathHelper.floor_double(z);
        if (BlockRailBase.isRailBlockAt(self.worldObj, iX, iY - 1, iZ)) {
            --iY;
        }
        int blockId = self.worldObj.getBlockId(iX, iY, iZ);
        if (BlockRailBase.isRailBlock(blockId)) {
            int railShape = this.worldObj.getBlockMetadata(iX, iY, iZ);
            y = (double)iY;
            if (((BlockRailBase)Block.blocksList[blockId]).isPowered()) {
                railShape &= 7;
            }
            if ((((railShape)>=2)&((railShape)<=5))) {
                y = (double)(iY + 1);
            }
            int[][] exitPair = matrix[railShape];
            double firstX = (double)iX + 0.5D + (double)exitPair[0][0] * 0.5D;
            double firstY = (double)iY + 0.0625D + (double)exitPair[0][1] * 0.5D;
            double firstZ = (double)iZ + 0.5D + (double)exitPair[0][2] * 0.5D;
            double diffX = ((double)iX + 0.5D + (double)exitPair[1][0] * 0.5D) - firstX;
            double diffY = (((double)iY + 0.0625D + (double)exitPair[1][1] * 0.5D) - firstY) * 2.0D;
            double diffZ = ((double)iZ + 0.5D + (double)exitPair[1][2] * 0.5D) - firstZ;
            double diffScale;
            if (diffX == 0.0D) {
                //x = (double)iX + 0.5D;
                diffScale = z - (double)iZ;
            }
            else if (diffZ == 0.0D) {
                //z = (double)iZ + 0.5D;
                diffScale = x - (double)iX;
            }
            else {
                diffScale = ((x - firstX) * diffX + (z - firstZ) * diffZ) * 2.0D;
            }
            x = firstX + diffX * diffScale;
            y = firstY + diffY * diffScale;
            z = firstZ + diffZ * diffScale;
            if (diffY < 0.0D) {
                ++y;
            }
            else if (diffY > 0.0D) {
                y += 0.5D;
            }
            return this.worldObj.getWorldVec3Pool().getVecFromPool(x, y, z);
        }
        return null;
    }
}
