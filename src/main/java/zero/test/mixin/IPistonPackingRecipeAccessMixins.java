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
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
// Block piston reactions
@Mixin(PistonPackingRecipe.class)
public interface IPistonPackingRecipeAccessMixins {
    @Accessor
    public Block getOutput();
    @Accessor
    public int getOutputMetadata();
    @Accessor
    public ItemStack[] getInput();
}
