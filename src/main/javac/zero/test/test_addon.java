package zero.test;

import net.minecraft.src.*;

import btw.AddonHandler;
import btw.BTWAddon;
import btw.block.BTWBlocks;
import btw.item.BTWItems;
import btw.crafting.recipe.RecipeManager;
import btw.inventory.util.InventoryUtils;

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
#if ENABLE_MOVING_BLOCK_CHAINING
        ZeroTestBlocks.slime_block = new SlimeBlock(SLIME_BLOCK_ID);
        Item.itemsList[SLIME_BLOCK_ID-256] = new ItemBlock(SLIME_BLOCK_ID-256);
        ZeroTestBlocks.glue_block = new GlueBlock(GLUE_BLOCK_ID);
        Item.itemsList[GLUE_BLOCK_ID-256] = new ItemBlock(GLUE_BLOCK_ID-256);
#endif
        ZeroTestBlocks.iron_trapdoor = new IronTrapDoor(IRON_TRAPDOOR_ID);
        Item.itemsList[IRON_TRAPDOOR_ID-256] = new ItemBlock(IRON_TRAPDOOR_ID-256);
#if ENABLE_PISTON_TEST_BLOCKS
        ZeroTestBlocks.pull_only_test_block = new PullOnlyTestBlock(PULL_ONLY_TEST_BLOCK_ID);
        Item.itemsList[PULL_ONLY_TEST_BLOCK_ID-256] = new ItemBlock(PULL_ONLY_TEST_BLOCK_ID-256);
        ZeroTestBlocks.dead_coral_fan = new DeadCoralFan(DEAD_CORAL_FAN_ID);
        Item.itemsList[DEAD_CORAL_FAN_ID-256] = new ItemBlock(DEAD_CORAL_FAN_ID-256);
#endif
    }
    
    @Override
    public void postInitialize() {
        
        // TODO: Make this less lazy so it's not expensive
        // CUD Block
        RecipeManager.addShapelessRecipe(
            new ItemStack(ZeroTestBlocks.cud_block, 1, 0),
            new ItemStack[] {
                new ItemStack(Item.comparator),
                new ItemStack(BTWBlocks.buddyBlock, 1, 0)
            }
        );
        
#if ENABLE_DIRECTIONAL_UPDATES
        // Observer recipe
		RecipeManager.addSoulforgeRecipe(
            new ItemStack(ZeroTestBlocks.observer_block, 1),
            new Object[] {
				"##X#",
				"XYY#",
				"#YYX",
				"#X##",
				'#', new ItemStack(BTWItems.stoneBrick, 1, InventoryUtils.IGNORE_METADATA),
				'X', BTWItems.redstoneEye,
				'Y', Item.netherQuartz
            }
        );
#endif
        
#if ENABLE_MOVING_BLOCK_CHAINING
        // Slime block
        RecipeManager.addPistonPackingRecipe(
            ZeroTestBlocks.slime_block,
            new ItemStack(Item.slimeBall, 9)
        );
        // Glue block
        RecipeManager.addPistonPackingRecipe(
            ZeroTestBlocks.glue_block,
            new ItemStack(BTWItems.glue, 4)
        );
#endif
    }

    // Is this important?
    public static ZeroTestAddon getInstance() {
        if (instance != null)
            instance = new ZeroTestAddon();
        return instance;
    }
}
