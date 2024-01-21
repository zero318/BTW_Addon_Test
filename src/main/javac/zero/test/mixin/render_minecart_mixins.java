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

#include "..\feature_flags.h"
#include "..\util.h"

@Mixin(RenderMinecart.class)
public abstract class RenderMinecartMixins extends Render {
#if ENABLE_MINECART_FIXES
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