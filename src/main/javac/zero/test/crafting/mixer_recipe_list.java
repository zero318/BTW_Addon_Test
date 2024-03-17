package zero.test.crafting;

import net.minecraft.src.*;

import btw.block.BTWBlocks;
import btw.item.BTWItems;
import btw.inventory.util.InventoryUtils;

import zero.test.crafting.MixerRecipeManager;

#include "..\util.h"
#include "..\feature_flags.h"

#define EXCLUDE_LOOM_RECIPES 0

#define ENABLE_PROGRESSIVE_ITEM_RECIPES 0

#define ENABLE_MULTI_DYE_RECIPES 0

#define ENABLE_PAPER_RECIPE 0

// Recipes get prioritized from top to bottom

public class MixerRecipeList {
#if ENABLE_MIXER_BLOCK
    public static void addRecipes() {
        MixerRecipeManager manager = MixerRecipeManager.getInstance();
        
        /// Foods
        
        // Cake Batter
        manager.addRecipe(
            new ItemStack[] {
                new ItemStack(BTWItems.unbakedCake),
                new ItemStack(Item.bucketEmpty, 2)
            },
            new ItemStack[] {
                new ItemStack(Item.sugar, 3),
                new ItemStack(Item.bucketMilk, 2),
                new ItemStack(BTWItems.flour, 3),
                new ItemStack(BTWItems.rawEgg)
            }
        );
        
        // Oven Ready Pumpkin Pie
        manager.addRecipe(
            new ItemStack(BTWItems.unbakedPumpkinPie),
            new ItemStack[] {
                new ItemStack(Item.sugar),
                new ItemStack(BTWBlocks.freshPumpkin),
                new ItemStack(BTWItems.rawEgg),
                new ItemStack(BTWItems.flour, 3)
            }
        );
        
        // Cookie Dough
        manager.addRecipe(
            new ItemStack(BTWItems.unbakedCookies),
            new ItemStack[] {
                new ItemStack(BTWItems.chocolate),
                new ItemStack(BTWItems.flour, 4)
            }
        );
        
        // Bread Dough
        manager.addRecipe(
            new ItemStack(BTWItems.breadDough),
            new ItemStack(BTWItems.flour, 3)
        );
        
        // Raw Mushroom Omelet
        manager.addRecipe(
            new ItemStack(BTWItems.rawMushroomOmelet, 2),
            new ItemStack[] {
                new ItemStack(BTWItems.brownMushroom, 3),
                new ItemStack(BTWItems.rawEgg)
            }
        );
        
        // Raw Scrambled Egg
        manager.addRecipe(
            new ItemStack[] {
                new ItemStack(BTWItems.rawScrambledEggs, 2),
                new ItemStack(Item.bucketEmpty)
            },
            new ItemStack[] {
                new ItemStack(BTWItems.rawEgg),
                new ItemStack(Item.bucketMilk)
            }
        );
        manager.addRecipe(
            new ItemStack[] {
                new ItemStack(BTWItems.rawScrambledEggs, 2),
                new ItemStack(Item.bucketEmpty)
            },
            new ItemStack[] {
                new ItemStack(BTWItems.hardBoiledEgg),
                new ItemStack(Item.bucketMilk)
            }
        );
        manager.addRecipe(
            new ItemStack[] {
                new ItemStack(BTWItems.rawScrambledEggs, 2),
                new ItemStack(Item.bucketEmpty)
            },
            new ItemStack[] {
                new ItemStack(BTWItems.friedEgg),
                new ItemStack(Item.bucketMilk)
            }
        );
        
        // Chocolate Milk
        manager.addRecipe(
            new ItemStack(BTWItems.milkChocolateBucket),
            new ItemStack[] {
                new ItemStack(Item.bucketMilk),
                new ItemStack(Item.dyePowder, 1, 3) // Cocoa powder
            }
        );
        
        // Mashed Melon
        manager.addRecipe(
            new ItemStack(BTWItems.mashedMelon, 2),
            new ItemStack(Block.melon)
        );
        
        // Cured Meat
        manager.addRecipe(
            new ItemStack(BTWItems.curedMeat),
            new ItemStack[] {
                new ItemStack(BTWItems.rawMutton),
                new ItemStack(BTWItems.nitre)
            }
        );
        manager.addRecipe(
            new ItemStack(BTWItems.curedMeat),
            new ItemStack[] {
                new ItemStack(Item.chickenRaw),
                new ItemStack(BTWItems.nitre)
            }
        );
        manager.addRecipe(
            new ItemStack(BTWItems.curedMeat),
            new ItemStack[] {
                new ItemStack(Item.beefRaw),
                new ItemStack(BTWItems.nitre)
            }
        );
        manager.addRecipe(
            new ItemStack(BTWItems.curedMeat),
            new ItemStack[] {
                new ItemStack(Item.fishRaw),
                new ItemStack(BTWItems.nitre)
            }
        );
        manager.addRecipe(
            new ItemStack(BTWItems.curedMeat),
            new ItemStack[] {
                new ItemStack(Item.porkRaw),
                new ItemStack(BTWItems.nitre)
            }
        );
        manager.addRecipe(
            new ItemStack(BTWItems.curedMeat),
            new ItemStack[] {
                new ItemStack(BTWItems.rawWolfChop),
                new ItemStack(BTWItems.nitre)
            }
        );
        manager.addRecipe(
            new ItemStack(BTWItems.curedMeat),
            new ItemStack[] {
                new ItemStack(BTWItems.rawMysteryMeat),
                new ItemStack(BTWItems.nitre)
            }
        );
        manager.addRecipe(
            new ItemStack(BTWItems.curedMeat),
            new ItemStack[] {
                new ItemStack(BTWItems.rawLiver),
                new ItemStack(BTWItems.nitre)
            }
        );
        
        /// Non-foods

#if !EXCLUDE_LOOM_RECIPES
        // Wicker
#if ENABLE_PROGRESSIVE_ITEM_RECIPES
        manager.addRecipe(
            new ItemStack(BTWBlocks.wickerPane),
            new ItemStack(BTWItems.wickerWeaving, 1, InventoryUtils.IGNORE_METADATA)
        );
#endif
        manager.addRecipe(
            new ItemStack(BTWBlocks.wickerPane),
            new ItemStack(Item.reed, 4)
        );
#endif
        
#if ENABLE_PAPER_RECIPE
        // Paper
        manager.addRecipe(
            new ItemStack(Item.paper),
            new ItemStack(Item.reed)
        );
#endif

        // Candle
        manager.addRecipe(
            new ItemStack(BTWItems.candle, 4, 16),
            new ItemStack[] {
                new ItemStack(BTWItems.hempFibers),
                new ItemStack(BTWItems.tallow)
            }
        );
        
        // Magma Cream
        manager.addRecipe(
            new ItemStack(Item.magmaCream),
            new ItemStack[] {
                new ItemStack(Item.blazePowder),
                new ItemStack(Item.slimeBall)
            }
        );
        
        // Stump Remover
        manager.addRecipe(
            new ItemStack(BTWItems.stumpRemover, 2),
            new ItemStack[] {
                new ItemStack(BTWItems.creeperOysters),
                new ItemStack(BTWItems.redMushroom),
                new ItemStack(Item.rottenFlesh)
            }
        );
        
        // Sinew
#if ENABLE_PROGRESSIVE_ITEM_RECIPES
        manager.addRecipe(
            new ItemStack(BTWItems.sinew),
            new ItemStack(BTWItems.sinewExtractingBeef, 1, InventoryUtils.IGNORE_METADATA)
        );
        manager.addRecipe(
            new ItemStack(BTWItems.sinew),
            new ItemStack(BTWItems.sinewExtractingWolf, 1, InventoryUtils.IGNORE_METADATA)
        );
#endif
        manager.addRecipe(
            new ItemStack(BTWItems.sinew),
            new ItemStack(Item.beefRaw, 2)
        );
        manager.addRecipe(
            new ItemStack(BTWItems.sinew),
            new ItemStack(BTWItems.rawWolfChop, 2)
        );
        manager.addRecipe(
            new ItemStack(BTWItems.sinew),
            new ItemStack(Item.beefCooked, 2)
        );
        manager.addRecipe(
            new ItemStack(BTWItems.sinew),
            new ItemStack(BTWItems.cookedWolfChop, 2)
        );

#define DYE_INK_SAC 0
#define DYE_RED 1
#define DYE_GREEN 2
#define DYE_COCOA_BEANS 3
#define DYE_LAPIS_LAZULI 4
#define DYE_PURPLE 5
#define DYE_CYAN 6
#define DYE_LIGHT_GRAY 7
#define DYE_GRAY 8
#define DYE_PINK 9
#define DYE_LIME 10
#define DYE_YELLOW 11
#define DYE_LIGHT_BLUE 12
#define DYE_MAGENTA 13
#define DYE_ORANGE 14
#define DYE_BONE_MEAL 15

        /// Dyes
        
        // Red Dye
        manager.addRecipe(
            new ItemStack(Item.dyePowder, 2, DYE_RED),
            new ItemStack(Block.plantRed)
        );
        
        // Yellow Dye
        manager.addRecipe(
            new ItemStack(Item.dyePowder, 2, DYE_YELLOW),
            new ItemStack(Block.plantYellow)
        );
        
        // Purple Dye
        manager.addRecipe(
            new ItemStack(Item.dyePowder, 2, DYE_PURPLE),
            new ItemStack[] {
                new ItemStack(Item.dyePowder, 1, DYE_LAPIS_LAZULI),
                new ItemStack(Item.dyePowder, 1, DYE_RED)
            }
        );
        
        // Cyan Dye
        manager.addRecipe(
            new ItemStack(Item.dyePowder, 2, DYE_CYAN),
            new ItemStack[] {
                new ItemStack(Item.dyePowder, 1, DYE_LAPIS_LAZULI),
                new ItemStack(Item.dyePowder, 1, DYE_GREEN)
            }
        );
        
        // Light Gray Dye
        manager.addRecipe(
            new ItemStack(Item.dyePowder, 2, DYE_LIGHT_GRAY),
            new ItemStack[] {
                new ItemStack(Item.dyePowder, 1, DYE_GRAY),
                new ItemStack(Item.dyePowder, 1, DYE_BONE_MEAL)
            }
        );
#if ENABLE_MULTI_DYE_RECIPES
        manager.addRecipe(
            new ItemStack(Item.dyePowder, 3, DYE_LIGHT_GRAY),
            new ItemStack[] {
                new ItemStack(Item.dyePowder, 1, DYE_INK_SAC),
                new ItemStack(Item.dyePowder, 2, DYE_BONE_MEAL)
            }
        );
#endif
        
        // Gray Dye
        manager.addRecipe(
            new ItemStack(Item.dyePowder, 2, DYE_GRAY),
            new ItemStack[] {
                new ItemStack(Item.dyePowder, 1, DYE_INK_SAC),
                new ItemStack(Item.dyePowder, 1, DYE_BONE_MEAL)
            }
        );
        
        // Pink Dye
        manager.addRecipe(
            new ItemStack(Item.dyePowder, 2, DYE_PINK),
            new ItemStack[] {
                new ItemStack(Item.dyePowder, 1, DYE_RED),
                new ItemStack(Item.dyePowder, 1, DYE_BONE_MEAL)
            }
        );
        
        // Lime Dye
        manager.addRecipe(
            new ItemStack(Item.dyePowder, 2, DYE_LIME),
            new ItemStack[] {
                new ItemStack(Item.dyePowder, 1, DYE_GREEN),
                new ItemStack(Item.dyePowder, 1, DYE_BONE_MEAL)
            }
        );
        
        // Light Blue Dye
        manager.addRecipe(
            new ItemStack(Item.dyePowder, 2, DYE_LIGHT_BLUE),
            new ItemStack[] {
                new ItemStack(Item.dyePowder, 1, DYE_LAPIS_LAZULI),
                new ItemStack(Item.dyePowder, 1, DYE_BONE_MEAL)
            }
        );
        
        // Orange Dye
        manager.addRecipe(
            new ItemStack(Item.dyePowder, 2, DYE_ORANGE),
            new ItemStack[] {
                new ItemStack(Item.dyePowder, 1, DYE_RED),
                new ItemStack(Item.dyePowder, 1, DYE_YELLOW)
            }
        );
        
        // Magenta Dye
        manager.addRecipe(
            new ItemStack(Item.dyePowder, 2, DYE_MAGENTA),
            new ItemStack[] {
                new ItemStack(Item.dyePowder, 1, DYE_PURPLE),
                new ItemStack(Item.dyePowder, 1, DYE_PINK)
            }
        );
#if ENABLE_MULTI_DYE_RECIPES
        manager.addRecipe(
            new ItemStack(Item.dyePowder, 3, DYE_MAGENTA),
            new ItemStack[] {
                new ItemStack(Item.dyePowder, 1, DYE_LAPIS_LAZULI),
                new ItemStack(Item.dyePowder, 1, DYE_RED),
                new ItemStack(Item.dyePowder, 1, DYE_PINK)
            }
        );
        manager.addRecipe(
            new ItemStack(Item.dyePowder, 4, DYE_MAGENTA),
            new ItemStack[] {
                new ItemStack(Item.dyePowder, 1, DYE_LAPIS_LAZULI),
                new ItemStack(Item.dyePowder, 2, DYE_RED),
                new ItemStack(Item.dyePowder, 1, DYE_BONE_MEAL)
            }
        );
#endif
    }
#endif
}