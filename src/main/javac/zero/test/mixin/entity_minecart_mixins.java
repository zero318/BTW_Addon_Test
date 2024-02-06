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

import zero.test.IWorldMixins;
import zero.test.IBlockMixins;
import zero.test.IEntityMixins;
import zero.test.mixin.IEntityMinecartAccessMixins;
import zero.test.IBaseRailBlockMixins;
import zero.test.ZeroUtil;

import java.util.List;

#include "..\feature_flags.h"
#include "..\util.h"
#include "..\ids.h"

#define MINECART_RIDEABLE 0
#define MINECART_CHEST 1
#define MINECART_FURNACE 2
#define MINECART_TNT 3
#define MINECART_SPAWNER 4
#define MINECART_HOPPER 5
#define MINECART_COMMAND_BLOCK 6
#define MINECART_BLOCK_DISPENSER 7

#define getPos(...) func_70489_a(__VA_ARGS__)
#define getPosOffs(...) func_70495_a(__VA_ARGS__)

// For rails
#define POWERED_META_OFFSET 3

// For buffer stops
#define FLAT_DIRECTION_META_OFFSET 0

@Mixin(EntityMinecart.class)
public abstract class EntityMinecartMixins extends Entity {
    public EntityMinecartMixins(World world) {
        super(world);
    }
    
/*
#if ENABLE_MODERN_REDSTONE_WIRE
    @Redirect(
        method = "updateOnTrack(IIIDDII)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/World;isBlockNormalCube(III)Z"
        )
    )
    private boolean isBlockNormalCube_redirect(World world, int x, int y, int z) {
#if !ENABLE_RAIL_BUFFER_STOP
        return ((IWorldMixins)world).isBlockRedstoneConductor(x, y, z);
#else
        int blockId = world.getBlockId(x, y, z);
        if (blockId != BUFFER_STOP_ID) {
            Block block = Block.blocksList[blockId];
            if (
                BLOCK_IS_AIR(block) ||
                !((IBlockMixins)block).isRedstoneConductor(world, x, y, z)
            ) {
                return false;
            }
        }
        return true;
        
#endif
    }
#endif
*/

#if ENABLE_MINECART_LERP_FIXES || ENABLE_MINECART_FIXES
    @Shadow
    public int turnProgress;
    @Shadow
    public double minecartX;
    @Shadow
    public double minecartY;
    @Shadow
    public double minecartZ;
    @Shadow
    public double minecartYaw;
    @Shadow
    public double minecartPitch;
#endif
    
#if ENABLE_MINECART_LERP_FIXES
/*
    public double lerpTargetX() {
        return this.turnProgress > 0 ? this.minecartX : (double)this.posX;
    }
    public double lerpTargetY() {
        return this.turnProgress > 0 ? this.minecartY : (double)this.posY;
    }
    public double lerpTargetZ() {
        return this.turnProgress > 0 ? this.minecartZ : (double)this.posZ;
    }
*/
    public float lerpTargetPitch() {
        return this.turnProgress > 0 ? (float)this.minecartPitch : this.rotationPitch;
    }
    public float lerpTargetYaw() {
        return this.turnProgress > 0 ? (float)this.minecartYaw : this.rotationYaw;
    }
#endif
    
    
#if ENABLE_MINECART_FIXES

    @Shadow
    public IUpdatePlayerListBox field_82344_g;
    @Shadow
    public boolean isInReverse;

#if ENABLE_MINECART_HITBOX_FIXES
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
#endif

    @Shadow
    private static int[][][] matrix;
    
    //@Shadow
    //public abstract void updateOnTrack(int x, int y, int z, double maxSpeed, double slopeSpeed, int blockId, int meta);
    
    @Shadow
    public abstract void func_94088_b(double par1);

    public double getMaxSpeed() {
        return 0.4D;
    }
    
#if ENABLE_RAIL_BUFFER_STOP
    
#define DEBOUNCE_TIME 5

#define DEBOUNCE_MOTION_THRESHOLD 0.001D

    public int debounce = 0;
    
    /*
    @Inject(
        method = "updateOnTrack(IIIDDII)V",
        at = @At("TAIL")
    )
    public void updateOnTrack_buffer_inject(int x, int y, int z, double maxSpeed, double slopeSpeed, int blockId, int meta, CallbackInfo info) {
        EntityMinecart self = (EntityMinecart)(Object)this;
        int[][] exits = matrix[meta];
        meta += meta;
        int x2 = x + exits[0][0];
        int y2 = y + exits[0][1];
        int z2 = z + exits[0][2];
        int debounceTime = (this.motionX * this.motionX + this.motionZ * this.motionZ) <= DEBOUNCE_MOTION_THRESHOLD ? DEBOUNCE_TIME : -DEBOUNCE_TIME;
        
        if (
            self.worldObj.getBlockId(x2, y2, z2) == BUFFER_STOP_ID &&
            READ_META_FIELD(self.worldObj.getBlockMetadata(x2, y2, z2), FLAT_DIRECTION) == ZeroUtil.rail_exit_flat_directions[meta]
        ) {
            debounce = debounceTime;
        }
        else {
            x2 = x + exits[1][0];
            y2 = y + exits[1][1];
            z2 = z + exits[1][2];
            if (
                self.worldObj.getBlockId(x2, y2, z2) == BUFFER_STOP_ID &&
                READ_META_FIELD(self.worldObj.getBlockMetadata(x2, y2, z2), FLAT_DIRECTION) == ZeroUtil.rail_exit_flat_directions[meta + 1]
            ) {
                debounce = debounceTime;
            }
        }
    }
    
    @Redirect(
        method = "updateOnTrack(IIIDDII)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/Entity;moveEntity(DDD)V"
        )
    )
    public void moveEntity_redirect_debounce(double moveX, double moveY, double moveZ) {
        
        this.moveEntity(moveX, moveY, moveZ);
    }
    */
#endif
    
    @Overwrite
    public void onUpdate() {
        EntityMinecart self = (EntityMinecart)(Object)this;
        
        
        if (this.field_82344_g != null) {
            this.field_82344_g.update();
        }

        if (self.getRollingAmplitude() > 0) {
            self.setRollingAmplitude(self.getRollingAmplitude() - 1);
        }

        if (self.getDamage() > 0) {
            self.setDamage(self.getDamage() - 1);
        }

        if (this.posY < -64.0D) {
            this.kill();
        }

        if (
            !this.worldObj.isRemote &&
            this.worldObj instanceof WorldServer
        ) {
            this.worldObj.theProfiler.startSection("portal");
            int maxPortalTime = this.getMaxInPortalTime();

            if (this.inPortal) {
                if (((WorldServer)this.worldObj).getMinecraftServer().getAllowNether()) {
                    if (
                        this.ridingEntity == null &&
                        this.field_82153_h++ >= maxPortalTime
                    ) {
                        this.field_82153_h = maxPortalTime;
                        this.timeUntilPortal = this.getPortalCooldown();
                        byte var3;

                        if (this.worldObj.provider.dimensionId == -1) {
                            var3 = 0;
                        }
                        else {
                            var3 = -1;
                        }

                        this.travelToDimension(var3);
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

            if (this.timeUntilPortal > 0) {
                --this.timeUntilPortal;
            }

            this.worldObj.theProfiler.endSection();
        }
        
#define USE_SERVER_MOTION_ON_CLIENT 0

#if ENABLE_MINECART_FULL_ROTATION
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
#endif

#if !USE_SERVER_MOTION_ON_CLIENT
        if (this.worldObj.isRemote) {
            int turn_progress = this.turnProgress;
            if (turn_progress > 0) {
                // Divisions swapped to multiplications for SPEED
                double dTurn = 1.0D / (double)turn_progress;
                double dX = this.posX + (this.minecartX - this.posX) * dTurn;
                double dY = this.posY + (this.minecartY - this.posY) * dTurn;
                double dZ = this.posZ + (this.minecartZ - this.posZ) * dTurn;
#if !(ENABLE_BETTER_MINECART_CLIENT_YAW || ENABLE_MINECART_FULL_ROTATION)
                double dYaw = MathHelper.wrapAngleTo180_double(this.minecartYaw - (double)this.rotationYaw);
                this.rotationYaw = (float)((double)this.rotationYaw + dYaw * dTurn);
#endif
                this.rotationPitch = (float)((double)this.rotationPitch + (this.minecartPitch - (double)this.rotationPitch) * dTurn);
                --this.turnProgress;
#if ENABLE_VERTICAL_CLIENT_CART_SNAPPING
                if (this.minecartY > dY) {
                    int x = MathHelper.floor_double(dX);
                    int y = MathHelper.floor_double(dY);
                    int z = MathHelper.floor_double(dZ);
                    if (
                        !BlockRailBase.isRailBlock(this.worldObj.getBlockId(x, y, z)) &&
                        BlockRailBase.isRailBlock(this.worldObj.getBlockId(x, y + 1, z))
                    ) {
                        dY += 1.0D;
                    }
                }
#endif
                this.setPosition(dX, dY, dZ);
#if ENABLE_BETTER_MINECART_CLIENT_YAW || ENABLE_MINECART_FULL_ROTATION
                double xDelta = this.prevPosX - this.posX;
                double zDelta = this.prevPosZ - this.posZ;
                if (xDelta * xDelta + zDelta * zDelta > 0.001D) {
                    this.prevRotationYaw = this.rotationYaw;
                    this.rotationYaw = DEGF(Math.atan2(zDelta, xDelta));
                }
#if ENABLE_MINECART_FULL_ROTATION
                if (ZeroUtil.angle_diff_abs(this.rotationYaw, this.prevRotationYaw) > 170.0F) {
                    this.isInReverse = BOOL_INVERT(this.isInReverse);
                }
#endif
#endif
            }
            else {
                this.setPosition(this.posX, this.posY, this.posZ);
            }
            this.setRotation(this.rotationYaw, this.rotationPitch);
        }
        else {
#endif
#if !ENABLE_MINECART_FULL_ROTATION
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
#endif
            this.motionY -= 0.03999999910593033D;
            int x = MathHelper.floor_double(this.posX);
            int y = MathHelper.floor_double(this.posY);
            int z = MathHelper.floor_double(this.posZ);

            if (BlockRailBase.isRailBlockAt(this.worldObj, x, y - 1, z)) {
                --y;
            }
            
#if ENABLE_RAIL_BUFFER_STOP
            if (this.debounce > 0) {
                --this.debounce;
            }
            if (this.debounce < 0) {
                ++this.debounce;
            }
#endif

            int blockId = this.worldObj.getBlockId(x, y, z);

            double maxSpeed = this.getMaxSpeed();
            if (BlockRailBase.isRailBlock(blockId)) {
                int meta = this.worldObj.getBlockMetadata(x, y, z);
                this.updateOnTrack(
                    x, y, z,
                    maxSpeed * ((IBaseRailBlockMixins)Block.blocksList[blockId]).getRailMaxSpeedFactor(),
                    0.0078125D, blockId, meta
                );

                if (blockId == Block.railActivator.blockID) {
                    self.onActivatorRailPass(x, y, z, READ_META_FIELD(meta, POWERED));
                }
            }
            else {
                this.func_94088_b(maxSpeed);
            }

            this.doBlockCollisions();
            this.rotationPitch = 0.0F;
            double xDelta = this.prevPosX - this.posX;
            double zDelta = this.prevPosZ - this.posZ;

            if (xDelta * xDelta + zDelta * zDelta > 0.001D) {
                this.rotationYaw = DEGF(Math.atan2(zDelta, xDelta));
                //if (this.isInReverse) {
                    //this.rotationYaw += 180.0F;
                //}
            }

#if 1
            //double yawDelta = (double)MathHelper.wrapAngleTo180_float(this.rotationYaw - this.prevRotationYaw);
            //float yawDelta = ;

            //if (yawDelta < -170.0D || yawDelta >= 170.0D) {
            if (ZeroUtil.angle_diff_abs(this.rotationYaw, this.prevRotationYaw) > 170.0F) {
                //this.rotationYaw += 180.0F;
                this.isInReverse = BOOL_INVERT(this.isInReverse);
            }
#endif
            

            //this.setRotation(this.rotationYaw, this.rotationPitch);
            
            // Still trying to fix parallel track collisions...
            /*
            double expandX = 0.05D;
            double expandZ = 0.05D;
            switch (YAW_FLAT_DIRECTION8(this.rotationYaw)) {
                case FLAT_DIRECTION8_NORTH: case FLAT_DIRECTION8_SOUTH:
                    expandX = 0.2D;
                    //expandZ = 0.0D;
                    break;
                case FLAT_DIRECTION8_EAST: case FLAT_DIRECTION8_WEST:
                    //expandX = 0.0D;
                    expandZ = 0.2D;
            }
            
            //AddonHandler.logMessage(
                //YAW_FLAT_DIRECTION8(this.rotationYaw)+" "+this.rotationYaw
            //);
            
            
            List<Entity> entityList = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(expandX, 0.0D, expandZ));
            */
            
#if USE_SERVER_MOTION_ON_CLIENT
            if (!this.worldObj.isRemote) {
#endif

            List<Entity> entityList = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(0.2D, 0.0D, 0.2D));

            if (
                entityList != null &&
                !entityList.isEmpty()
            ) {
                for (Entity entity : entityList) {
                    if (
                        entity != this.riddenByEntity &&
                        entity.canBePushed() &&
                        entity instanceof EntityMinecart
                    ) {
                        entity.applyEntityCollision(this);
                    }
                }
            }

            if (
                this.riddenByEntity != null &&
                this.riddenByEntity.isDead
            ) {
                if (this.riddenByEntity.ridingEntity == this) {
                    this.riddenByEntity.ridingEntity = null;
                }

                this.riddenByEntity = null;
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
                self.getMinecartType() == MINECART_RIDEABLE &&
                this.motionX * this.motionX + this.motionZ * this.motionZ > 0.01D &&
                this.riddenByEntity == null &&
                entity.ridingEntity == null
            ) {
                entity.mountEntity(this);
            }

            double xDiff = entity.posX - this.posX;
            double zDiff = entity.posZ - this.posZ;
            double distance = xDiff * xDiff + zDiff * zDiff;

            if (distance >= 0.0001D) {
                
                double invDistance = 1.0D / Math.sqrt(distance);
                
                xDiff *= invDistance;
                zDiff *= invDistance;
                if (invDistance > 1.0D) {
                    invDistance = 1.0D;
                }
                xDiff *= invDistance;
                zDiff *= invDistance;
                
                xDiff *= 0.1D;
                zDiff *= 0.1D;
                xDiff *= (double)(1.0F - this.entityCollisionReduction);
                zDiff *= (double)(1.0F - this.entityCollisionReduction);
                xDiff *= 0.5D;
                zDiff *= 0.5D;
                
#define REWORK_COLLISION_CODE 0
#define CART_DEBUG_LOGGING 0

                if (entity instanceof EntityMinecart) {
                    
#if !REWORK_COLLISION_CODE
                    Vec3 posVec = this.worldObj.getWorldVec3Pool().getVecFromPool(entity.posX - this.posX, 0.0D, entity.posZ - this.posZ).normalize();
                    Vec3 rotVec = this.worldObj.getWorldVec3Pool().getVecFromPool(MathHelper.cos(RADF(this.rotationYaw)), 0.0D, MathHelper.sin(RADF(this.rotationYaw))).normalize();
                    double similarity = Math.abs(posVec.dotProduct(rotVec));
                    /*
                    AddonHandler.logMessage(
                        "    "+this.rotationYaw+" "+entity.rotationYaw+"\n"+
                        "    "+posVec.xCoord+" "+posVec.yCoord+" "+posVec.zCoord+"\n"+
                        "    "+rotVec.xCoord+" "+rotVec.yCoord+" "+rotVec.zCoord+"\n"+
                        "    "+similarity+" "+this.entityId+" "+entity.entityId+"\n"+
                        "Self:  "+this.posX+" "+this.posZ+" "+this.width+" "+this.boundingBox.minX+" "+this.boundingBox.maxX+" "+this.boundingBox.minZ+" "+this.boundingBox.maxZ+"\n"+
                        "Other: "+entity.posX+" "+entity.posZ+" "+entity.width+" "+entity.boundingBox.minX+" "+entity.boundingBox.maxX+" "+entity.boundingBox.minZ+" "+entity.boundingBox.maxZ
                    );
                    */
                    
                    
                    //AddonHandler.logMessage(
                        //this.entityId+" "+entity.entityId+" "+similarity+" "+this.rotationYaw+" "+entity.rotationYaw
                    //);
                    
                    
                    // There's still an edge case at 0.79
                    if (similarity < 0.7D) {
                        return;
                    }
                    // This seems to make furnace cart shunting work around
                    // corners, but I absolutely hate the magic number.
                    // HACK: This is speed dependent, come up with a better solution that
                    // directly tests angles instead
                    //if (similarity < 0.84D) {
                        //xDiff = -xDiff;
                        //zDiff = -zDiff;
                    //}
#else
                    double xVec = entity.posX - this.posX;
                    double zVec = entity.posZ - this.posZ;
                    //double vecLength = Math.sqrt(xVec * xVec + zVec * zVec)
                    float posAngle = DEGF((float)Math.atan2(zVec, xVec));
                    float rotAngle = this.rotationYaw;
                    
                    float angleDiff = ZeroUtil.angle_plane_diff(rotAngle, posAngle);
                    /*
                    switch (YAW_FLAT_DIRECTION8(rotAngle)) {
                        default:
                            if (self.getMinecartType() == MINECART_RIDEABLE) {
                                AddonHandler.logMessage(
                                    "A "+xVec+" "+zVec+" "+posAngle+" "+rotAngle+" "+angleDiff
                                );
                            } else {
                                AddonHandler.logMessage(
                                    "B "+xVec+" "+zVec+" "+posAngle+" "+rotAngle+" "+angleDiff
                                );
                            }
                        case FLAT_DIRECTION8_NORTH: case FLAT_DIRECTION8_SOUTH:
                        case FLAT_DIRECTION8_EAST: case FLAT_DIRECTION8_WEST:
                    }
                    */
                    
                    if (angleDiff > 50.0F) {
                        return;
                    }
                    
                    
                    /*
                    float angleDiff = ZeroUtil.angle_plane_diff(rotAngle, posAngle);
                    double sideways = MathHelper.cos(RADF(posAngle + angleDiff));
                    if (sideways < 0.0) {
                        sideways = -sideways;
                    }
                    double forwards = MathHelper.sin(RADF(posAngle + angleDiff));
                    if (forwards < 0.0) {
                        forwards = -forwards;
                    }
                    
                    AddonHandler.logMessage(
                        forwards+" "+sideways+" "+angleDiff+" "+posAngle+" "+rotAngle
                    );
                    
                    
                    if (sideways > forwards) {
                        return;
                    }
                    */
#endif

#if ENABLE_RAIL_BUFFER_STOP
                    if (this.debounce < 0 || ((EntityMinecartMixins)entity).debounce < 0) {
                        ((EntityMinecartMixins)entity).debounce = this.debounce = -DEBOUNCE_TIME;
                    } else {
                        double selfMotionSquared = this.motionX * this.motionX + this.motionZ * this.motionZ;
                        double otherMotionSquared = entity.motionX * entity.motionX + entity.motionZ * entity.motionZ;
                        if (
                            (
                                this.debounce > 0 && selfMotionSquared <= DEBOUNCE_MOTION_THRESHOLD ||
                                ((EntityMinecartMixins)entity).debounce > 0 && otherMotionSquared <= DEBOUNCE_MOTION_THRESHOLD
                            ) &&
                            !self.boundingBox.intersectsWith(entity.boundingBox) // Not expanded
                        ) {
                            ((EntityMinecartMixins)entity).debounce = this.debounce = DEBOUNCE_TIME;
                            return;
                        }
                    }
#endif

#if CART_DEBUG_LOGGING
                    double XSA1 = Math.signum(this.motionX);
                    double ZSA1 = Math.signum(this.motionZ);
                    double XSB1 = Math.signum(entity.motionX);
                    double ZSB1 = Math.signum(entity.motionZ);
#endif


                    if (
                        ((EntityMinecart)entity).getMinecartType() == MINECART_FURNACE &&
                        self.getMinecartType() != MINECART_FURNACE
                    ) {
                        this.motionX *= 0.2D;
                        this.motionZ *= 0.2D;
                        this.addVelocity(entity.motionX - xDiff, 0.0D, entity.motionZ - zDiff);
                        entity.motionX *= 0.95D;
                        entity.motionZ *= 0.95D;
                    }
                    else if (
                        ((EntityMinecart)entity).getMinecartType() != MINECART_FURNACE &&
                        self.getMinecartType() == MINECART_FURNACE
                    ) {
                        entity.motionX *= 0.2D;
                        entity.motionZ *= 0.2D;
                        entity.addVelocity(this.motionX + xDiff, 0.0D, this.motionZ + zDiff);
                        this.motionX *= 0.95D;
                        this.motionZ *= 0.95D;
                    }
                    else {
                        double xAverage = (entity.motionX + this.motionX) * 0.5D;
                        double zAverage = (entity.motionZ + this.motionZ) * 0.5D;
                        this.motionX *= 0.2D;
                        this.motionZ *= 0.2D;
                        this.addVelocity(xAverage - xDiff, 0.0D, zAverage - zDiff);
                        entity.motionX *= 0.2D;
                        entity.motionZ *= 0.2D;
                        entity.addVelocity(xAverage + xDiff, 0.0D, zAverage + zDiff);
                    }
#if CART_DEBUG_LOGGING
                    double XSA2 = Math.signum(this.motionX);
                    double ZSA2 = Math.signum(this.motionZ);
                    double XSB2 = Math.signum(entity.motionX);
                    double ZSB2 = Math.signum(entity.motionZ);
                    if (
                        XSA1 != XSA2 ||
                        ZSA1 != ZSA2 ||
                        XSB1 != XSB2 ||
                        ZSB1 != ZSB2
                    ) {
                        AddonHandler.logMessage(
                            this.entityId+" "+entity.entityId+" "+similarity+" "+this.rotationYaw+" "+entity.rotationYaw
                        );
                    }
#endif
                }
                else {
                    this.addVelocity(-xDiff, 0.0D, -zDiff);
                    entity.addVelocity(xDiff * 0.25D, 0.0D, zDiff * 0.25D);
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

#if ENABLE_MINECART_HITBOX_FIXES
    @Overwrite
    public Vec3 getPos(double x, double y, double z) {
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

            if (RAIL_IS_ASCENDING(railShape)) {
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
#endif
#endif

#if ENABLE_MINECART_WITH_BLOCK_DISPENSER
    @Overwrite
    public boolean onBlockDispenserConsume(BlockDispenserBlock blockDispenser, BlockDispenserTileEntity tileEntityDispenser) {
        EntityMinecart self = (EntityMinecart)(Object)this;
        
        // dismount any entities riding the minecart
        if(self.riddenByEntity != null) {
            self.riddenByEntity.mountEntity(self);
        }
        
        self.setDead();
        int itemId;
        switch (self.getMinecartType()) {
            default:
                itemId = Item.minecartEmpty.itemID;
                break;
            case MINECART_CHEST:
                itemId = Item.minecartCrate.itemID;
                break;
            case MINECART_FURNACE:
                itemId = Item.minecartPowered.itemID;
                break;
            /*
            case MINECART_TNT:
                itemId = Item.minecartTnt.itemID;
                break;
            case MINECART_HOPPER:
                itemId = Item.minecartHopper.itemID;
                break;
            */
            case MINECART_BLOCK_DISPENSER:
                itemId = MINECART_BLOCK_DISPENSER;
                break;
        }
        InventoryUtils.addSingleItemToInventory(tileEntityDispenser, itemId, 0);
        
        worldObj.playAuxSFX(1001, (int)self.posX, (int)self.posY, (int)self.posZ, 0); // high pitch click
        return true;
	}
#endif

    @Shadow
    public abstract void applyDrag();
    
#define SAFE_CORNERING_SPEED 0.65D

    @Overwrite
    public void updateOnTrack(int x, int y, int z, double maxSpeed, double slopeSpeed, int blockId, int meta) {
        this.fallDistance = 0.0F;
        Vec3 startingRailPos = this.getPos(this.posX, this.posY, this.posZ);
        this.posY = (double)y;
        
        Block railBlock = Block.blocksList[blockId];
        
        double boostRatio = ((IBaseRailBlockMixins)railBlock).cartBoostRatio(meta);
        double slowdownRatio = ((IBaseRailBlockMixins)railBlock).cartSlowdownRatio(meta);

        if (((BlockRailBase)railBlock).isPowered()) {
            meta &= 7;
        }

        if (RAIL_IS_ASCENDING(meta)) {
            this.posY += 1.0D;
            switch (meta) {
                case RAIL_ASCENDING_EAST:
                    this.motionX -= slopeSpeed;
                    break;
                case RAIL_ASCENDING_WEST:
                    this.motionX += slopeSpeed;
                    break;
                case RAIL_ASCENDING_NORTH:
                    this.motionZ += slopeSpeed;
                    break;
                case RAIL_ASCENDING_SOUTH:
                    this.motionZ -= slopeSpeed;
                    break;
            }
        }
        else if (RAIL_IS_CURVED(meta)) {
            if (maxSpeed > SAFE_CORNERING_SPEED) {
                maxSpeed = SAFE_CORNERING_SPEED;
            }
        }

        int[][] exitPair = matrix[meta];
        double xDiff = (double)(exitPair[1][0] - exitPair[0][0]);
        double zDiff = (double)(exitPair[1][2] - exitPair[0][2]);

        if (this.motionX * xDiff + this.motionZ * zDiff < 0.0D) {
            xDiff = -xDiff;
            zDiff = -zDiff;
        }

        double speed = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

        if (speed > 2.0D) {
            speed = 2.0D;
        }
        double distDiff = Math.sqrt(xDiff * xDiff + zDiff * zDiff);

        this.motionX = speed * xDiff / distDiff;
        this.motionZ = speed * zDiff / distDiff;

        if (
            this.riddenByEntity instanceof EntityPlayer &&
            (this.riddenByEntity.motionX * this.riddenByEntity.motionX + this.riddenByEntity.motionZ * this.riddenByEntity.motionZ) > 1.0E-4D &&
            (this.motionX * this.motionX + this.motionZ * this.motionZ) < 0.01D
        ) {
            this.motionX += this.riddenByEntity.motionX * 0.1D;
            this.motionZ += this.riddenByEntity.motionZ * 0.1D;
        }
        else if (slowdownRatio < 1.0D) {
            this.motionY = 0.0D;
            if (Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ) < 0.03D) {
                this.motionX = 0.0D;
                this.motionZ = 0.0D;
            }
            else {
                this.motionX *= slowdownRatio;
                this.motionZ *= slowdownRatio;
            }
        }

        double firstX = (double)x + 0.5D + (double)exitPair[0][0] * 0.5D;
        double firstZ = (double)z + 0.5D + (double)exitPair[0][2] * 0.5D;
        double diffX = ((double)x + 0.5D + (double)exitPair[1][0] * 0.5D) - firstX;
        double diffZ = ((double)z + 0.5D + (double)exitPair[1][2] * 0.5D) - firstZ;

        double diffScale;
        if (diffX == 0.0D) {
            //this.posX = (double)x + 0.5D;
            diffScale = this.posZ - (double)z;
        }
        else if (diffZ == 0.0D) {
            //this.posZ = (double)z + 0.5D;
            diffScale = this.posX - (double)x;
        }
        else {
            diffScale = ((this.posX - firstX) * diffX + (this.posZ - firstZ) * diffZ) * 2.0D;
        }

        this.posX = firstX + diffX * diffScale;
        this.posZ = firstZ + diffZ * diffScale;
        this.setPosition(this.posX, this.posY + (double)this.yOffset, this.posZ);
        
        double moveX = this.motionX;
        double moveZ = this.motionZ;

        if (this.riddenByEntity != null) {
            moveX *= 0.75D;
            moveZ *= 0.75D;
        }

        if (moveX < -maxSpeed) {
            moveX = -maxSpeed;
        }

        if (moveX > maxSpeed) {
            moveX = maxSpeed;
        }

        if (moveZ < -maxSpeed) {
            moveZ = -maxSpeed;
        }

        if (moveZ > maxSpeed) {
            moveZ = maxSpeed;
        }

        this.moveEntity(moveX, 0.0D, moveZ);

        if (
            exitPair[0][1] != 0 &&
            MathHelper.floor_double(this.posX) - x == exitPair[0][0] &&
            MathHelper.floor_double(this.posZ) - z == exitPair[0][2]
        ) {
            this.setPosition(this.posX, this.posY + (double)exitPair[0][1], this.posZ);
        }
        else if (
            exitPair[1][1] != 0 &&
            MathHelper.floor_double(this.posX) - x == exitPair[1][0] &&
            MathHelper.floor_double(this.posZ) - z == exitPair[1][2]
        ) {
            this.setPosition(this.posX, this.posY + (double)exitPair[1][1], this.posZ);
        }

        this.applyDrag();
        Vec3 endingRailPos = this.getPos(this.posX, this.posY, this.posZ);

        if (
            endingRailPos != null &&
            startingRailPos != null
        ) {
            double var39 = (startingRailPos.yCoord - endingRailPos.yCoord) * 0.05D;
            speed = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

            if (speed > 0.0D) {
                speed = (speed + var39) / speed;
                this.motionX *= speed;
                this.motionZ *= speed;
            }

            this.setPosition(this.posX, endingRailPos.yCoord, this.posZ);
        }

        int newX = MathHelper.floor_double(this.posX);
        int newZ = MathHelper.floor_double(this.posZ);

        if (newX != x || newZ != z) {
            speed = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.motionX = speed * (double)(newX - x);
            this.motionZ = speed * (double)(newZ - z);
        }

        if (boostRatio > 0.0D) {
            if (this.debounce > 0) {
                this.debounce = -DEBOUNCE_TIME;
            }
            
            speed = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            
#if ENABLE_MODERN_REDSTONE_WIRE
#define RAIL_END_TEST(...) (((IWorldMixins)this.worldObj).isBlockRedstoneConductor(__VA_ARGS__))
#else
#define RAIL_END_TEST(...) (this.worldObj.isBlockNormalCube(__VA_ARGS__))
#endif

            if (speed > 0.01D) {
                this.motionX += this.motionX / speed * boostRatio;
                this.motionZ += this.motionZ / speed * boostRatio;
            }
            else if (meta == RAIL_EAST_WEST) {
                if (RAIL_END_TEST(x - 1, y, z)) {
                    this.motionX = 0.02D;
                }
                else if (RAIL_END_TEST(x + 1, y, z)) {
                    this.motionX = -0.02D;
                }
            }
            else if (meta == RAIL_NORTH_SOUTH) {
                if (RAIL_END_TEST(x, y, z - 1)) {
                    this.motionZ = 0.02D;
                }
                else if (RAIL_END_TEST(x, y, z + 1)) {
                    this.motionZ = -0.02D;
                }
            }
        } else if ((this.motionX * this.motionX + this.motionZ * this.motionZ) <= DEBOUNCE_MOTION_THRESHOLD) {
            meta += meta;
            int x2 = x + exitPair[0][0];
            int y2 = y + exitPair[0][1];
            int z2 = z + exitPair[0][2];
            int buffer_meta;
            if (
                this.worldObj.getBlockId(x2, y2, z2) == BUFFER_STOP_ID &&
                READ_META_FIELD(buffer_meta = this.worldObj.getBlockMetadata(x2, y2, z2), FLAT_DIRECTION) == ZeroUtil.rail_exit_flat_directions[meta]
            ) {
                debounce = !READ_META_FIELD(buffer_meta, POWERED) ? DEBOUNCE_TIME : -DEBOUNCE_TIME;
            } else {
                x2 = x + exitPair[1][0];
                y2 = y + exitPair[1][1];
                z2 = z + exitPair[1][2];
                if (
                    this.worldObj.getBlockId(x2, y2, z2) == BUFFER_STOP_ID &&
                    READ_META_FIELD(buffer_meta = this.worldObj.getBlockMetadata(x2, y2, z2), FLAT_DIRECTION) == ZeroUtil.rail_exit_flat_directions[meta + 1]
                ) {
                    debounce = !READ_META_FIELD(buffer_meta, POWERED) ? DEBOUNCE_TIME : -DEBOUNCE_TIME;
                }
            }
        }
    }
}