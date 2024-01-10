package zero.test;

import net.minecraft.src.*;

import btw.AddonHandler;
import btw.BTWAddon;
import btw.block.BTWBlocks;
import btw.item.BTWItems;
import btw.crafting.recipe.RecipeManager;
import btw.inventory.util.InventoryUtils;

import zero.test.block.*;
import zero.test.command.*;
import zero.test.block.ZeroTestBlocks;
import zero.test.item.SlimeBlockItem;

#include "ids.h"
#include "feature_flags.h"

public class ZeroTestAddon extends BTWAddon {
    private static ZeroTestAddon instance;

    private ZeroTestAddon() {
        super("Zero Test Addon", "0.0.9", "ZeroTest");
    }

    @Override
    public void initialize() {
        //AddonHandler.logMessage(this.getName() + " Version " + this.getVersionString() + " Initializing...");
        
#if ENABLE_NOCLIP_COMMAND
        this.registerAddonCommand(new ServerNoclipCommand());
#endif
        
        ZeroTestBlocks.cud_block = new CUDBlock(CUD_BLOCK_ID);
        Item.itemsList[CUD_BLOCK_ID] = new ItemBlock(CUD_BLOCK_ID-256);
#if ENABLE_DIRECTIONAL_UPDATES
        ZeroTestBlocks.observer_block = new ObserverBlock(OBSERVER_BLOCK_ID);
        Item.itemsList[OBSERVER_BLOCK_ID] = new ItemBlock(OBSERVER_BLOCK_ID-256);
#endif
#if ENABLE_MOVING_BLOCK_CHAINING
        ZeroTestBlocks.slime_block = new SlimeBlock(SLIME_BLOCK_ID);
        Item.itemsList[SLIME_BLOCK_ID] = new SlimeBlockItem(SLIME_BLOCK_ID-256);
        ZeroTestBlocks.glue_block = new GlueBlock(GLUE_BLOCK_ID);
        Item.itemsList[GLUE_BLOCK_ID] = new ItemBlock(GLUE_BLOCK_ID-256);
#endif
        ZeroTestBlocks.iron_trapdoor = new IronTrapDoor(IRON_TRAPDOOR_ID);
        Item.itemsList[IRON_TRAPDOOR_ID] = new ItemBlock(IRON_TRAPDOOR_ID-256);
#if ENABLE_PISTON_TEST_BLOCKS
        ZeroTestBlocks.pull_only_test_block = new PullOnlyTestBlock(PULL_ONLY_TEST_BLOCK_ID);
        Item.itemsList[PULL_ONLY_TEST_BLOCK_ID] = new ItemBlock(PULL_ONLY_TEST_BLOCK_ID-256);
        ZeroTestBlocks.dead_coral_fan = new DeadCoralFan(DEAD_CORAL_FAN_ID);
        Item.itemsList[DEAD_CORAL_FAN_ID] = new ItemBlock(DEAD_CORAL_FAN_ID-256);
#endif
#if ENABLE_BLOCK_DISPENSER_VARIANTS
        ZeroTestBlocks.block_breaker = new BlockBreaker(BLOCK_BREAKER_ID);
        Item.itemsList[BLOCK_BREAKER_ID] = new ItemBlock(BLOCK_BREAKER_ID-256);
        ZeroTestBlocks.block_placer = new BlockPlacer(BLOCK_PLACER_ID);
        Item.itemsList[BLOCK_PLACER_ID] = new ItemBlock(BLOCK_PLACER_ID-256);
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
            new ItemStack(Item.slimeBall, 9, 0)
        );
        RecipeManager.addStokedCauldronRecipe(
            new ItemStack(Item.slimeBall, 9, 0),
            new ItemStack[] {
                new ItemStack(ZeroTestBlocks.slime_block, 1, 0),
            }
        );
        // Glue block
        RecipeManager.addPistonPackingRecipe(
            ZeroTestBlocks.glue_block,
            new ItemStack(BTWItems.glue, 4, 0)
        );
        RecipeManager.addStokedCauldronRecipe(
            new ItemStack(BTWItems.glue, 4, 0),
            new ItemStack[] {
                new ItemStack(ZeroTestBlocks.glue_block, 1, 0)
            }
        );
#endif

#if ENABLE_BLOCK_DISPENSER_VARIANTS
        RecipeManager.addSawRecipe(
            new ItemStack[] {
                new ItemStack(ZeroTestBlocks.block_breaker, 1, 0),
                new ItemStack(ZeroTestBlocks.block_placer, 1, 0)
            },
            BTWBlocks.blockDispenser
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
