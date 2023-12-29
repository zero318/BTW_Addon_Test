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
// Vanilla observers
// Slime blocks
// Push only and dead coral fans
// Allow slime to keep loose blocks
// suspended in midair as if they
// had mortar applied
// Fix how most BTW blocks recieve power
// Allow block dispensers to respond to short pulses
// Block Breaker and Block Placer

public class ZeroTestAddon extends BTWAddon {
    private static ZeroTestAddon instance;
    private ZeroTestAddon() {
        super("Zero Test Addon", "0.0.3", "ZeroTest");
    }
    @Override
    public void initialize() {
        //AddonHandler.logMessage(this.getName() + " Version " + this.getVersionString() + " Initializing...");
        ZeroTestBlocks.cud_block = new CUDBlock(1318);
        Item.itemsList[1318 -256] = new ItemBlock(1318 -256);
        ZeroTestBlocks.observer_block = new ObserverBlock(1319);
        Item.itemsList[1319 -256] = new ItemBlock(1319 -256);
        ZeroTestBlocks.slime_block = new SlimeBlock(1320);
        Item.itemsList[1320 -256] = new ItemBlock(1320 -256);
        ZeroTestBlocks.glue_block = new GlueBlock(1321);
        Item.itemsList[1321 -256] = new ItemBlock(1321 -256);
        ZeroTestBlocks.iron_trapdoor = new IronTrapDoor(1322);
        Item.itemsList[1322 -256] = new ItemBlock(1322 -256);
        ZeroTestBlocks.pull_only_test_block = new PullOnlyTestBlock(1323);
        Item.itemsList[1323 -256] = new ItemBlock(1323 -256);
        ZeroTestBlocks.dead_coral_fan = new DeadCoralFan(1324);
        Item.itemsList[1324 -256] = new ItemBlock(1324 -256);
        ZeroTestBlocks.block_breaker = new BlockBreaker(1325);
        Item.itemsList[1325 -256] = new ItemBlock(1325 -256);
        ZeroTestBlocks.block_placer = new BlockPlacer(1326);
        Item.itemsList[1326 -256] = new ItemBlock(1326 -256);
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
        RecipeManager.addSawRecipe(
            new ItemStack[] {
                new ItemStack(ZeroTestBlocks.block_breaker, 1, 0),
                new ItemStack(ZeroTestBlocks.block_placer, 1, 0)
            },
            BTWBlocks.blockDispenser
        );
    }
    // Is this important?
    public static ZeroTestAddon getInstance() {
        if (instance != null)
            instance = new ZeroTestAddon();
        return instance;
    }
}
