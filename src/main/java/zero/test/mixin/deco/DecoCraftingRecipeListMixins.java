package zero.test.mixin.deco;
import net.minecraft.src.*;
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
// Block piston reactions

@Mixin(deco.crafting.recipes.DecoCraftingRecipeList.class)
public abstract class DecoCraftingRecipeListMixins {
    @Redirect(
        method = "addWoodenSubBlockRecipes",
        at = @At(
            value = "NEW",
            target = "(Lnet/minecraft/src/Block;II)Lnet/minecraft/src/ItemStack;",
            // This seems to target based on any constructor, not just matching signatures
            // If it did signature matching the correct value would be 4
            ordinal = 19
        )
    )
    private static ItemStack fix_slab_crash(
        // Constructor args
        Block block, int count, int damage,
        // Redirect args
        int woodType, Block sidingAndCorner, Block moulding, Block stairs, Block slab, int slabMetadata
    ) {
        return new ItemStack(slab, count, slabMetadata);
    }
}
