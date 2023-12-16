package zero.test;

import btw.AddonHandler;
import btw.BTWAddon;
import net.minecraft.src.*;

import zero.test.block.*;
import zero.test.block.ZeroTestBlocks;

#include "ids.h"
#include "feature_flags.h"

public class ZeroTestAddon extends BTWAddon {
    private static ZeroTestAddon instance;

    private ZeroTestAddon() {
        super("Zero Test Addon", "0.0.1", "ZeroTest");
    }

    @Override
    public void initialize() {
        //AddonHandler.logMessage(this.getName() + " Version " + this.getVersionString() + " Initializing...");
        
        ZeroTestBlocks.cud_block = new CUDBlock(CUD_BLOCK_ID);
        Item.itemsList[CUD_BLOCK_ID-256] = new ItemBlock(CUD_BLOCK_ID-256);
#if ENABLE_DIRECTIONAL_UPDATES
        ZeroTestBlocks.observer_block = new ObserverBlock(OBSERVER_BLOCK_ID);
        Item.itemsList[OBSERVER_BLOCK_ID-256] = new ItemBlock(OBSERVER_BLOCK_ID-256);
#endif
    }

    public static ZeroTestAddon getInstance() {
        if (instance != null)
            instance = new ZeroTestAddon();
        return instance;
    }
}
