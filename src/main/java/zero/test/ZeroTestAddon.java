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

public class ZeroTestAddon extends BTWAddon {
    private static ZeroTestAddon instance;
    private ZeroTestAddon() {
        super("Zero Test Addon", "0.0.3", "ZeroTest");
    }
    @Override
    public void initialize() {
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
    }
    @Override
    public void postInitialize() {
        RecipeManager.addShapelessRecipe(
            new ItemStack(ZeroTestBlocks.cud_block, 1, 0),
            new ItemStack[] {
                new ItemStack(Item.comparator),
                new ItemStack(BTWBlocks.buddyBlock, 1, 0)
            }
        );
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
        RecipeManager.addPistonPackingRecipe(
            ZeroTestBlocks.slime_block,
            new ItemStack(Item.slimeBall, 9)
        );
        RecipeManager.addPistonPackingRecipe(
            ZeroTestBlocks.glue_block,
            new ItemStack(BTWItems.glue, 4)
        );
    }
    public static ZeroTestAddon getInstance() {
        if (instance != null)
            instance = new ZeroTestAddon();
        return instance;
    }
}
