package zero.test.mixin.metadataextensionmod;

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

import zero.test.ZeroUtil;
import zero.test.metadata_compat.IMovingPlatformEntityMixins;

import java.util.Random;

#include "..\..\feature_flags.h"
#include "..\..\util.h"

@Mixin(
    value = MovingPlatformEntity.class,
    priority = 1100
)
public abstract class MovingPlatformEntityMixins extends Entity implements IMovingPlatformEntityMixins {
    public MovingPlatformEntityMixins() {
        super(null);
    }
    
    public int extMeta;
    
    @Override
    public int getBlockExtMetadata() {
        return this.extMeta;
    }
    
    @Override
    public void setBlockExtMetadata(int value) {
        this.extMeta = value;
    }
    
#if ENABLE_PLATFORM_EXTENSIONS
    @Inject(
        method = "writeEntityToNBT(Lnet/minecraft/src/NBTTagCompound;)V",
        at = @At("TAIL")
    )
    public void writeEntityToNBT_inject(NBTTagCompound nbttagcompound, CallbackInfo info) {
        nbttagcompound.setInteger("BlockExtMeta", this.extMeta);
    }
    
    @Inject(
        method = "readEntityFromNBT(Lnet/minecraft/src/NBTTagCompound;)V",
        at = @At("TAIL")
    )
    public void readEntityFromNBT_inject(NBTTagCompound nbttagcompound, CallbackInfo info) {
        if (nbttagcompound.hasKey("BlockExtMeta")) {
            this.extMeta = nbttagcompound.getInteger("BlockExtMeta");
        }
    }
    
    // For now just don't bother rendering the extended meta I guess?
#endif
}