package zero.test.crafting;
import net.minecraft.src.*;
import btw.block.BTWBlocks;
import btw.item.BTWItems;
import btw.inventory.util.InventoryUtils;
import zero.test.crafting.MixerRecipeManager;
// Block piston reactions
// Recipes get prioritized from top to bottom
public class MixerRecipeList {
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
        // Wicker
        manager.addRecipe(
            new ItemStack(BTWBlocks.wickerPane),
            new ItemStack(Item.reed, 4)
        );
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
        /// Dyes
        // Red Dye
        manager.addRecipe(
            new ItemStack(Item.dyePowder, 2, 1),
            new ItemStack(Block.plantRed)
        );
        // Yellow Dye
        manager.addRecipe(
            new ItemStack(Item.dyePowder, 2, 11),
            new ItemStack(Block.plantYellow)
        );
        // Purple Dye
        manager.addRecipe(
            new ItemStack(Item.dyePowder, 2, 5),
            new ItemStack[] {
                new ItemStack(Item.dyePowder, 1, 4),
                new ItemStack(Item.dyePowder, 1, 1)
            }
        );
        // Cyan Dye
        manager.addRecipe(
            new ItemStack(Item.dyePowder, 2, 6),
            new ItemStack[] {
                new ItemStack(Item.dyePowder, 1, 4),
                new ItemStack(Item.dyePowder, 1, 2)
            }
        );
        // Light Gray Dye
        manager.addRecipe(
            new ItemStack(Item.dyePowder, 2, 7),
            new ItemStack[] {
                new ItemStack(Item.dyePowder, 1, 8),
                new ItemStack(Item.dyePowder, 1, 15)
            }
        );
        // Gray Dye
        manager.addRecipe(
            new ItemStack(Item.dyePowder, 2, 8),
            new ItemStack[] {
                new ItemStack(Item.dyePowder, 1, 0),
                new ItemStack(Item.dyePowder, 1, 15)
            }
        );
        // Pink Dye
        manager.addRecipe(
            new ItemStack(Item.dyePowder, 2, 9),
            new ItemStack[] {
                new ItemStack(Item.dyePowder, 1, 1),
                new ItemStack(Item.dyePowder, 1, 15)
            }
        );
        // Lime Dye
        manager.addRecipe(
            new ItemStack(Item.dyePowder, 2, 10),
            new ItemStack[] {
                new ItemStack(Item.dyePowder, 1, 2),
                new ItemStack(Item.dyePowder, 1, 15)
            }
        );
        // Light Blue Dye
        manager.addRecipe(
            new ItemStack(Item.dyePowder, 2, 12),
            new ItemStack[] {
                new ItemStack(Item.dyePowder, 1, 4),
                new ItemStack(Item.dyePowder, 1, 15)
            }
        );
        // Orange Dye
        manager.addRecipe(
            new ItemStack(Item.dyePowder, 2, 14),
            new ItemStack[] {
                new ItemStack(Item.dyePowder, 1, 1),
                new ItemStack(Item.dyePowder, 1, 11)
            }
        );
        // Magenta Dye
        manager.addRecipe(
            new ItemStack(Item.dyePowder, 2, 13),
            new ItemStack[] {
                new ItemStack(Item.dyePowder, 1, 5),
                new ItemStack(Item.dyePowder, 1, 9)
            }
        );
    }
}
