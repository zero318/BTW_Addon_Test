package zero.test.mixin;
import net.minecraft.src.*;
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
//import zero.test.mixin.IEntityPlayerAccessMixins;
// Block piston reactions
@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixins extends EntityLiving {
    public EntityPlayerMixins(World par1World) {
        super(par1World);
    }
    @Inject(
        method = "isEntityInsideOpaqueBlock()Z",
        at = @At("HEAD"),
        cancellable = true
    )
    protected void isEntityInsideOpaqueBlock_cancel_if_noclip(CallbackInfoReturnable callbackInfo) {
        if (((EntityPlayer)(Object)this).noClip) {
            callbackInfo.setReturnValue(false);
        }
    }
    @Shadow
    public abstract void addMountedMovementStat(double par1, double par3, double par5);
    @Overwrite
    public void updateRidden() {
        EntityPlayer self = (EntityPlayer)(Object)this;
        double var1 = this.posX;
        double var3 = this.posY;
        double var5 = this.posZ;
        float var7 = this.rotationYaw;
        float var8 = this.rotationPitch;
        super.updateRidden();
        self.prevCameraYaw = self.cameraYaw;
        self.cameraYaw = 0.0F;
        this.addMountedMovementStat(this.posX - var1, this.posY - var3, this.posZ - var5);
        this.rotationPitch = var8;
        this.rotationYaw = var7;
        if (this.ridingEntity instanceof EntityPig) {
            this.renderYawOffset = ((EntityPig)this.ridingEntity).renderYawOffset;
        }
    }
// Somehow this one fails to apply?
/*
    @Redirect(
        method = "onDeath(Lnet/minecraft/src/DamageSource;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/Entity;setPosition(DDD)V"
        )
    )
    public void setPosition_fix_illegal_stance(Entity entity, double x, double y, double z) {
        entity.ySize = 0.0F;
        entity.setPosition(x, y, z);
    }
*/
    @Overwrite
    public void onDeath(DamageSource par1DamageSource)
    {
        super.onDeath(par1DamageSource);
        EntityPlayer self = (EntityPlayer)(Object)this;
        this.setSize(0.2F, 0.2F);
        this.ySize = 0.0F;
        this.setPosition(this.posX, this.posY, this.posZ);
        this.motionY = 0.10000000149011612D;
        if (self.username.equals("Notch"))
        {
            self.dropPlayerItemWithRandomChoice(new ItemStack(Item.appleRed, 1), true);
        }
        if (!this.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory"))
        {
            self.inventory.dropAllItems();
        }
        if (par1DamageSource != null)
        {
            this.motionX = (double)(-MathHelper.cos((this.attackedAtYaw + this.rotationYaw) * (float)Math.PI / 180.0F) * 0.1F);
            this.motionZ = (double)(-MathHelper.sin((this.attackedAtYaw + this.rotationYaw) * (float)Math.PI / 180.0F) * 0.1F);
        }
        else
        {
            this.motionX = this.motionZ = 0.0D;
        }
        this.yOffset = 0.1F;
        self.addStat(StatList.deathsStat, 1);
    }
}
