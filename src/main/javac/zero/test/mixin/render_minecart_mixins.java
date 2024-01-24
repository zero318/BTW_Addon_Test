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

import org.lwjgl.opengl.GL11;

import zero.test.mixin.IEntityMinecartAccessMixins;
import zero.test.ZeroUtil;

#include "..\feature_flags.h"
#include "..\util.h"

@Mixin(RenderMinecart.class)
public abstract class RenderMinecartMixins extends Render {
    
#if ENABLE_MINECART_FULL_ROTATION
    
    @Shadow
    protected ModelBase modelMinecart;
    
    @Shadow
    protected RenderBlocks field_94145_f;
    
    @Shadow
    protected abstract void renderBlockInMinecart(EntityMinecart par1EntityMinecart, float par2, Block par3Block, int par4);

    public void renderTheMinecart(EntityMinecart par1EntityMinecart, double x, double y, double z, float yaw, float par9) {
        GL11.glPushMatrix();
        long var10 = (long)par1EntityMinecart.entityId * 493286711L;
        var10 = var10 * var10 * 4392167121L + var10 * 98761L;
        float var12 = (((float)(var10 >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        float var13 = (((float)(var10 >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        float var14 = (((float)(var10 >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
        GL11.glTranslatef(var12, var13, var14);
        double var15 = par1EntityMinecart.lastTickPosX + (par1EntityMinecart.posX - par1EntityMinecart.lastTickPosX) * (double)par9;
        double var17 = par1EntityMinecart.lastTickPosY + (par1EntityMinecart.posY - par1EntityMinecart.lastTickPosY) * (double)par9;
        double var19 = par1EntityMinecart.lastTickPosZ + (par1EntityMinecart.posZ - par1EntityMinecart.lastTickPosZ) * (double)par9;
        double var21 = 0.30000001192092896D;
        Vec3 var23 = par1EntityMinecart.func_70489_a(var15, var17, var19);
        float var24 = par1EntityMinecart.prevRotationPitch + (par1EntityMinecart.rotationPitch - par1EntityMinecart.prevRotationPitch) * par9;

        if (var23 != null) {
            Vec3 var25 = par1EntityMinecart.func_70495_a(var15, var17, var19, var21);
            Vec3 var26 = par1EntityMinecart.func_70495_a(var15, var17, var19, -var21);

            if (var25 == null)
            {
                var25 = var23;
            }

            if (var26 == null)
            {
                var26 = var23;
            }

            x += var23.xCoord - var15;
            y += (var25.yCoord + var26.yCoord) / 2.0D - var17;
            z += var23.zCoord - var19;
            Vec3 var27 = var26.addVector(-var25.xCoord, -var25.yCoord, -var25.zCoord);

            if (var27.lengthVector() != 0.0D)
            {
                var27 = var27.normalize();
                if (((IEntityMinecartAccessMixins)par1EntityMinecart).getIsInReverse()) {
                    yaw = ZeroUtil.angle_rotate_180(yaw);
                }
                //yaw = (float)(Math.atan2(var27.zCoord, var27.xCoord) * 180.0D / Math.PI);
                float newYaw = (float)(Math.atan2(var27.zCoord, var27.xCoord) * 180.0D / Math.PI);
                //float newYaw = (float)(Math.atan(var27.zCoord / var27.xCoord) * 180.0D / Math.PI);
                //AddonHandler.logMessage(
                    //yaw+" "+newYaw
                //);
                yaw = ZeroUtil.angle_diff_abs(yaw, newYaw) <= 90.0F ? newYaw : ZeroUtil.angle_rotate_180(newYaw);
                //yaw = newYaw;
                var24 = (float)(Math.atan(var27.yCoord) * 73.0D);
            }
        }
#if ENABLE_MINECART_HITBOX_FIXES
        GL11.glTranslatef((float)x, (float)y + 0.375F, (float)z);
#else
        GL11.glTranslatef((float)x, (float)y, (float)z);
#endif
        GL11.glRotatef(-var24, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(180.0F - yaw, 0.0F, 1.0F, 0.0F);
        float var31 = (float)par1EntityMinecart.getRollingAmplitude() - par9;
        float var32 = (float)par1EntityMinecart.getDamage() - par9;

        if (var32 < 0.0F) {
            var32 = 0.0F;
        }

        if (var31 > 0.0F) {
            GL11.glRotatef(MathHelper.sin(var31) * var31 * var32 / 10.0F * (float)par1EntityMinecart.getRollingDirection(), 1.0F, 0.0F, 0.0F);
        }

        int var33 = par1EntityMinecart.getDisplayTileOffset();
        Block var28 = par1EntityMinecart.getDisplayTile();
        int var29 = par1EntityMinecart.getDisplayTileData();

        if (var28 != null) {
            GL11.glPushMatrix();
            this.loadTexture("/terrain.png");
            float var30 = 0.75F;
            GL11.glScalef(var30, var30, var30);
            GL11.glTranslatef(0.0F, (float)var33 / 16.0F, 0.0F);
            this.renderBlockInMinecart(par1EntityMinecart, par9, var28, var29);
            GL11.glPopMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }

        this.loadTexture("/item/cart.png");
        GL11.glScalef(-1.0F, -1.0F, 1.0F);
        this.modelMinecart.render(par1EntityMinecart, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        GL11.glPopMatrix();
    }
#elif ENABLE_MINECART_HITBOX_FIXES
    @Redirect(
        method = "renderTheMinecart(Lnet/minecraft/src/EntityMinecart;DDDFF)V",
        at = @At(
            value = "INVOKE",
            target = "Lorg/lwjgl/opengl/GL11;glTranslatef(FFF)V",
            ordinal = 1
        )
    )
    private void glTranslatef_redirectA(float x, float y, float z) {
        GL11.glTranslatef(x, y + 0.375F, z);
    }
#endif
}