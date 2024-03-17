package zero.test.mixin.craftguide;

import net.minecraft.src.*;

import btw.block.BTWBlocks;
import btw.AddonHandler;

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

import zero.test.block.ZeroTestBlocks;
import zero.test.crafting.MixerRecipeManager;

import uristqwerty.CraftGuide.recipes.BulkRecipes;
import uristqwerty.CraftGuide.recipes.BTWRecipes;

#include "..\..\util.h"
#include "..\..\feature_flags.h"

@Mixin(BTWRecipes.class)
public abstract class BTWRecipesMixins {
#if ENABLE_MIXER_BLOCK
    ItemStack mixer = new ItemStack(ZeroTestBlocks.mixer_block);
    ItemStack axle = new ItemStack(BTWBlocks.axlePowerSource);
    
    @Inject(
        method = "<init>()V",
        at = @At("TAIL")
    )
    public void constructor_inject(CallbackInfo info) {
        //new BulkRecipes(MixerRecipeManager.getInstance(), 0, mixer, axle);
        // This was complaining about being unable to locate the
        // ItemStack class, so maybe reflection will work?
        Object test = null;
        try_ignore(
            test = (Object)BulkRecipes.class.getConstructor(
                MixerRecipeManager.class,
                int.class,
                ItemStack[].class
            ).newInstance(
                MixerRecipeManager.getInstance(),
                0,
                (Object)new ItemStack[] {
                    mixer//,
                    //axle
                }
            );
        );
        if (test != null) {
            AddonHandler.logMessage("Mixer Recipes Initialized");
        } else {
            AddonHandler.logMessage("Mixer Recipes Failed");
        }
    }
#endif
}