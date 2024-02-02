package zero.test.mixin;

import net.minecraft.src.*;

import btw.crafting.manager.PistonPackingCraftingManager;
import btw.crafting.recipe.types.PistonPackingRecipe;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

#include "..\feature_flags.h"
#include "..\util.h"

@Mixin(PistonPackingCraftingManager.class)
public abstract class PistonPackingCraftingManagerMixins {
    
    @Shadow
    private ArrayList<PistonPackingRecipe> recipes;
    
    /*
    @Inject(
        method = "removeRecipe(Lnet/minecraft/src/Block;I[Lnet/minecraft/src/ItemStack;)Z",
        at = @At(
            value = "RETURN",
            ordinal = 0
        )
    )
    */
    
    // This function doesn't actually call recipes.remove in the original. :/
    @Overwrite
    public boolean removeRecipe(Block output, int outputMetadata, ItemStack[] input) {
        PistonPackingRecipe recipeToRemove = new PistonPackingRecipe(output, outputMetadata, input);

        for (PistonPackingRecipe recipe : recipes) {
            if (recipe.matchesRecipe(recipeToRemove)) {
                recipes.remove(recipe);
                return true;
            }
        }

        return false;
    }
}