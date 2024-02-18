package zero.test;
import net.minecraft.src.*;
import btw.AddonHandler;
import btw.BTWAddon;
import btw.block.BTWBlocks;
import btw.block.blocks.*;
import btw.item.BTWItems;
import btw.crafting.recipe.RecipeManager;
import btw.inventory.util.InventoryUtils;
import btw.crafting.manager.*;
import zero.test.block.*;
import zero.test.command.*;
import zero.test.block.ZeroTestBlocks;
import zero.test.item.SlimeBlockItem;
// Block piston reactions

public class ZeroTestAddon extends BTWAddon {
    private static ZeroTestAddon instance;
    private ZeroTestAddon() {
        super("Zero Test Addon", "0.1.11", "ZeroTest");
    }
    @Override
    public void initialize() {
        this.registerAddonCommand(new ServerNoclipCommand());
        ZeroTestBlocks.cud_block = new CUDBlock(1318);
        Item.itemsList[1318] = new ItemBlock(1318 -256);
        ZeroTestBlocks.observer_block = new ObserverBlock(1319);
        Item.itemsList[1319] = new ItemBlock(1319 -256);
        ZeroTestBlocks.slime_block = new SlimeBlock(1320);
        Item.itemsList[1320] = new SlimeBlockItem(1320 -256);
        ZeroTestBlocks.glue_block = new GlueBlock(1321);
        Item.itemsList[1321] = new ItemBlock(1321 -256);
        ZeroTestBlocks.iron_trapdoor = new IronTrapDoor(1322);
        Item.itemsList[1322] = new ItemBlock(1322 -256);
        ZeroTestBlocks.pull_only_test_block = new PullOnlyTestBlock(1323);
        Item.itemsList[1323] = new ItemBlock(1323 -256);
        ZeroTestBlocks.dead_coral_fan = new DeadCoralFan(1324);
        Item.itemsList[1324] = new ItemBlock(1324 -256);
        ZeroTestBlocks.block_breaker = new BlockBreaker(1325);
        Item.itemsList[1325] = new ItemBlock(1325 -256);
        ZeroTestBlocks.block_placer = new BlockPlacer(1326);
        Item.itemsList[1326] = new ItemBlock(1326 -256);
        ZeroTestBlocks.wooden_rail = new WoodenRailBlock(1327);
        Item.itemsList[1327] = new ItemBlock(1327 -256);
        ZeroTestBlocks.steel_rail = new SteelRailBlock(1328);
        Item.itemsList[1328] = new ItemBlock(1328 -256);
        ZeroTestBlocks.buffer_stop = new BufferStopBlock(1329);
        Item.itemsList[1329] = new ItemBlock(1329 -256);
        ZeroTestBlocks.scaffolding = new ScaffoldingBlock(1330);
        Item.itemsList[1330] = new ItemBlock(1330 -256);
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
        RecipeManager.addSawRecipe(
            new ItemStack[] {
                new ItemStack(ZeroTestBlocks.block_breaker, 1, 0),
                new ItemStack(ZeroTestBlocks.block_placer, 1, 0)
            },
            BTWBlocks.blockDispenser
        );
        RecipeManager.addRecipe(
            new ItemStack(Block.railActivator, 6),
            new Object[] {
                "XDX",
                "XSX",
                "XRX",
                'X', BTWItems.ironNugget,
                'S', Item.stick,
                'R', BTWItems.redstoneLatch,
                'D', Item.redstone
            }
        );
        RecipeManager.addStokedCrucibleRecipe(
            new ItemStack(BTWItems.ironNugget, 1),
            new ItemStack[] {
                new ItemStack(Block.railActivator)
            }
        );
        RecipeManager.removeVanillaRecipe(
            new ItemStack(Item.minecartPowered, 1),
            new Object[] {
                "A",
                "B",
                'A', Block.furnaceIdle,
                'B', Item.minecartEmpty
            }
        );
        RecipeManager.addRecipe(
            new ItemStack(Item.minecartPowered, 1),
            new Object[] {
                "A",
                "B",
                'A', BTWBlocks.idleOven,
                'B', Item.minecartEmpty
            }
        );
        RecipeManager.addRecipe(
            new ItemStack(ZeroTestBlocks.wooden_rail, 12),
            new Object[] {
                "XSX",
                "XIX",
                "XSX",
                'X', Block.planks,
                'I', Item.stick,
                'S', Item.silk
            }
        );
        RecipeManager.addRecipe(
            new ItemStack(ZeroTestBlocks.wooden_rail, 12),
            new Object[] {
                "XSX",
                "XIX",
                "XSX",
                'X', Block.planks,
                'I', Item.stick,
                'S',BTWItems.sinew
            }
        );
        RecipeManager.addRecipe(
            new ItemStack(ZeroTestBlocks.wooden_rail, 12),
            new Object[] {
                "XSX",
                "XIX",
                "XSX",
                'X', Block.planks,
                'I', Item.stick,
                'S', BTWItems.hempFibers
            }
        );
        RecipeManager.addRecipe(
            new ItemStack(ZeroTestBlocks.wooden_rail, 12),
            new Object[] {
                "XSX",
                "XIX",
                "XSX",
                'X', new ItemStack(BTWItems.woodMouldingStubID, 1, InventoryUtils.IGNORE_METADATA),
                'I', Item.stick,
                'S', Item.silk
            }
        );
        RecipeManager.addRecipe(
            new ItemStack(ZeroTestBlocks.wooden_rail, 12),
            new Object[] {
                "XSX",
                "XIX",
                "XSX",
                'X', new ItemStack(BTWItems.woodMouldingStubID, 1, InventoryUtils.IGNORE_METADATA),
                'I', Item.stick,
                'S', BTWItems.sinew
            }
        );
        RecipeManager.addRecipe(
            new ItemStack(ZeroTestBlocks.wooden_rail, 12),
            new Object[] {
                "XSX",
                "XIX",
                "XSX",
                'X', new ItemStack(BTWItems.woodMouldingStubID, 1, InventoryUtils.IGNORE_METADATA),
                'I', Item.stick,
                'S', BTWItems.hempFibers
            }
        );
        RecipeManager.addRecipe(
            new ItemStack(ZeroTestBlocks.steel_rail, 12),
            new Object[] {
                "X X",
                "XIX",
                "X X",
                'X', BTWItems.steelNugget,
                'I', Item.stick
            }
        );
        RecipeManager.addStokedCrucibleRecipe(
            new ItemStack(BTWItems.steelNugget, 1),
            new ItemStack[] {
                new ItemStack(ZeroTestBlocks.steel_rail, 2)
            }
        );
        RecipeManager.removeVanillaRecipe(
            new ItemStack(BTWBlocks.aestheticOpaque, 1, AestheticOpaqueBlock.SUBTYPE_SOAP),
            new Object[] {
                "###",
                "###",
                "###",
                '#', BTWItems.soap
            }
        );
        RecipeManager.removeVanillaShapelessRecipe(
            new ItemStack(BTWItems.soap, 9),
            new Object[] {
                new ItemStack(BTWBlocks.aestheticOpaque, 1, AestheticOpaqueBlock.SUBTYPE_SOAP)
            }
        );
        PistonPackingCraftingManager.instance.removeRecipe(
            BTWBlocks.aestheticOpaque, AestheticOpaqueBlock.SUBTYPE_SOAP,
            new ItemStack[] {
                new ItemStack(BTWItems.soap, 9)
            }
        );
        RecipeManager.addRecipe(
            new ItemStack(BTWBlocks.aestheticOpaque, 1, AestheticOpaqueBlock.SUBTYPE_SOAP),
            new Object[] {
                "##",
                "##",
                '#', BTWItems.soap
            }
        );
        RecipeManager.addShapelessRecipe(
            new ItemStack(BTWItems.soap, 4),
            new Object[] {
                new ItemStack(BTWBlocks.aestheticOpaque, 1, AestheticOpaqueBlock.SUBTYPE_SOAP)
            }
        );
        RecipeManager.addPistonPackingRecipe(
            BTWBlocks.aestheticOpaque, AestheticOpaqueBlock.SUBTYPE_SOAP,
            new ItemStack(BTWItems.soap, 4)
        );
        RecipeManager.addRecipe(
            new ItemStack(ZeroTestBlocks.buffer_stop, 1, 0),
            new Object[] {
                "NMN",
                "M M",
                "BBB",
                'N', BTWItems.ironNugget,
                'M', new ItemStack(BTWItems.woodMouldingStubID, 1, InventoryUtils.IGNORE_METADATA),
                'B', new ItemStack(BTWItems.stoneBrick, 1, InventoryUtils.IGNORE_METADATA)
            }
        );
    }
    // Is this important?
    // Also looks kinda backwards tbh (I tried fixing it)
    public static ZeroTestAddon getInstance() {
        if (instance == null) {
            instance = new ZeroTestAddon();
        }
        return instance;
    }
}
