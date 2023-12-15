package zero.test;

import btw.AddonHandler;
import btw.BTWAddon;
import net.minecraft.src.*;

import zero.test.block.CUDBlock;
import zero.test.block.ZeroTestBlocks;

#include "ids.h"

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
    }

    public static ZeroTestAddon getInstance() {
        if (instance != null)
            instance = new ZeroTestAddon();
        return instance;
    }
}
