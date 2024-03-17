package zero.test.crafting;

import net.minecraft.src.*;

import btw.crafting.manager.BulkCraftingManager;

#include "..\util.h"
#include "..\feature_flags.h"

public class MixerRecipeManager
#if ENABLE_MIXER_BLOCK
extends BulkCraftingManager
#endif
{
#if ENABLE_MIXER_BLOCK
    private static final MixerRecipeManager instance = new MixerRecipeManager();
    
    private MixerRecipeManager() {
        super();
    }
    
    public static final MixerRecipeManager getInstance() {
        return instance;
    }
#endif
}