package zero.test.mixin;

import net.minecraft.src.*;

import btw.AddonHandler;
import btw.item.items.BucketItem;
import btw.item.items.PlaceAsBlockItem;

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
import org.spongepowered.asm.mixin.gen.Accessor;

#include "..\util.h"
#include "..\feature_flags.h"

@Mixin(BucketItem.class)
public abstract class BucketItemMixins extends PlaceAsBlockItem {
    public BucketItemMixins() {
        super(0, 0);
    }
#if ENABLE_MORE_AUTOMATION_RECIPES
    @Override
    public int getTargetFacingPlacedByBlockDispenser(int dispenserFacing) {
        //AddonHandler.logMessage("test");
        return DIRECTION_UP;
    }
#endif
}