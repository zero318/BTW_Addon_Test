package zero.test.crafting;
import net.minecraft.src.*;
import btw.crafting.manager.BulkCraftingManager;

public class MixerRecipeManager
extends BulkCraftingManager
{
    private static final MixerRecipeManager instance = new MixerRecipeManager();
    private MixerRecipeManager() {
        super();
    }
    public static final MixerRecipeManager getInstance() {
        return instance;
    }
}
