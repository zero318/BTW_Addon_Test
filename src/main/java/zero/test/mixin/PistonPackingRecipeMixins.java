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
import zero.test.mixin.IPistonPackingRecipeAccessMixins;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Mixin(PistonPackingRecipe.class)
public abstract class PistonPackingRecipeMixins {
    @Shadow
    private Block output;
    @Shadow
    private int outputMetadata;
    @Shadow
    private ItemStack[] input;
    @Shadow
    public abstract boolean matchesInputs(ItemStack[] inputToMatch);
    // TODO: Why does this one need remap=false?
    @Overwrite(remap=false)
    public boolean matchesRecipe(PistonPackingRecipe recipe) {
        return this.output.blockID == ((IPistonPackingRecipeAccessMixins)recipe).getOutput().blockID &&
               this.outputMetadata == ((IPistonPackingRecipeAccessMixins)recipe).getOutputMetadata() &&
               this.matchesInputs(((IPistonPackingRecipeAccessMixins)recipe).getInput());
    }
}
