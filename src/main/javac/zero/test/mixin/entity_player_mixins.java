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

import zero.test.mixin.IEntityPlayerAccessMixins;

#include "..\feature_flags.h"
#include "..\util.h"

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixins extends EntityLiving {
    
    public EntityPlayerMixins(World par1World) {
        super(par1World);
    }
    
#if ENABLE_NOCLIP_COMMAND
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
#endif
#if ENABLE_STABLE_MINECART_CAMERA
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
        ((IEntityPlayerAccessMixins)this).callAddMountedMovementStat(this.posX - var1, this.posY - var3, this.posZ - var5);

        this.rotationPitch = var8;
        this.rotationYaw = var7;
        if (this.ridingEntity instanceof EntityPig) {
            this.renderYawOffset = ((EntityPig)this.ridingEntity).renderYawOffset;
        }
    }
#endif
}