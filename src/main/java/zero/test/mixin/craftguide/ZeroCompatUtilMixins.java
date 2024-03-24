package zero.test.mixin.craftguide;
import net.minecraft.src.*;
import btw.crafting.manager.BulkCraftingManager;
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
import zero.test.ZeroCompatUtil;
import uristqwerty.CraftGuide.recipes.BulkRecipes;
import uristqwerty.CraftGuide.recipes.BTWRecipes;
import java.lang.reflect.InvocationTargetException;
@Mixin(ZeroCompatUtil.class)
public abstract class ZeroCompatUtilMixins {
    private static ItemStack mixer = new ItemStack(ZeroTestBlocks.mixer_block);
    private static ItemStack axle = new ItemStack(BTWBlocks.axlePowerSource);
    @Overwrite(remap=false)
    public static void initCraftguide()
    {
        //new BulkRecipes(MixerRecipeManager.getInstance(), 0, mixer, axle);
        // This was complaining about being unable to locate the
        // ItemStack class, so maybe reflection will work?
        Object test = null;
        try { test = (Object)BulkRecipes.class.getConstructor( BulkCraftingManager.class, int.class, ItemStack[].class ).newInstance( MixerRecipeManager.getInstance(), 0, (Object)new ItemStack[] { mixer, axle } ); } catch (Exception e) {};
        if (test != null) {
            AddonHandler.logMessage("Mixer Recipes Initialized");
        } else {
            AddonHandler.logMessage("Mixer Recipes Failed");
        }
    }
}
