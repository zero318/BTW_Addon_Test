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
import zero.test.block.block_entity.*;
import zero.test.item.*;
import zero.test.command.*;
import zero.test.crafting.*;
import zero.test.ZeroUtil;
import zero.test.ZeroCompatUtil;

#include "ids.h"
#include "feature_flags.h"
#include "_VERSION.h"

public class ZeroTestAddon extends BTWAddon {
    private static ZeroTestAddon instance;

    private ZeroTestAddon() {
        super("Zero Test Addon", MACRO_STR(MOD_VERSION), "ZeroTest");
    }

    @Override
    public void initialize() {
        
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
        if (!ZeroUtil.isDecoLoaded()) {
            ZeroTestBlocks.iron_trapdoor = new IronTrapDoor(IRON_TRAPDOOR_ID);
            Item.itemsList[IRON_TRAPDOOR_ID] = new ItemBlock(IRON_TRAPDOOR_ID-256);
        }
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
#if ENABLE_WOODEN_RAILS
        ZeroTestBlocks.wooden_rail = new WoodenRailBlock(WOODEN_RAIL_ID);
        Item.itemsList[WOODEN_RAIL_ID] = new ItemBlock(WOODEN_RAIL_ID-256);
#endif
#if ENABLE_STEEL_RAILS
        ZeroTestBlocks.steel_rail = new SteelRailBlock(STEEL_RAIL_ID);
        Item.itemsList[STEEL_RAIL_ID] = new ItemBlock(STEEL_RAIL_ID-256);
#endif
#if ENABLE_RAIL_BUFFER_STOP
        ZeroTestBlocks.buffer_stop = new BufferStopBlock(BUFFER_STOP_ID);
        Item.itemsList[BUFFER_STOP_ID] = new ItemBlock(BUFFER_STOP_ID-256);
#endif
#if ENABLE_SCAFFOLDING
        ZeroTestBlocks.scaffolding = new ScaffoldingBlock(SCAFFOLDING_ID);
        Item.itemsList[SCAFFOLDING_ID] = new ItemBlock(SCAFFOLDING_ID-256);
#endif
#if ENABLE_MIXER_BLOCK
        ZeroTestBlocks.mixer_block = new MixerBlock(MIXER_BLOCK_ID);
        Item.itemsList[MIXER_BLOCK_ID] = new ItemBlock(MIXER_BLOCK_ID-256);
        TileEntity.addMapping(MixerBlockEntity.class, "mixer");
#endif

#if ENABLE_DEBUG_STICKS
        ZeroTestItems.debug_stick = new DebugStick(DEBUG_STICK_ID-256);
#endif
    }
    
    @Override
    public void postInitialize() {
        
        // TODO: Make this less lazy so it's not expensive
        // CUD Block
        RecipeManager.addShapelessRecipe(
            new ItemStack(ZeroTestBlocks.cud_block),
            new ItemStack[] {
                new ItemStack(Item.comparator),
                new ItemStack(BTWBlocks.buddyBlock)
            }
        );
        
#if ENABLE_DIRECTIONAL_UPDATES
        // Observer recipe
        RecipeManager.addSoulforgeRecipe(
            new ItemStack(ZeroTestBlocks.observer_block),
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
        RecipeManager.addStokedCauldronRecipe(
            new ItemStack(Item.slimeBall, 9),
            new ItemStack[] {
                new ItemStack(ZeroTestBlocks.slime_block),
            }
        );
        // Glue block
        RecipeManager.addPistonPackingRecipe(
            ZeroTestBlocks.glue_block,
            new ItemStack(BTWItems.glue, 4)
        );
        RecipeManager.addStokedCauldronRecipe(
            new ItemStack(BTWItems.glue, 4),
            new ItemStack[] {
                new ItemStack(ZeroTestBlocks.glue_block)
            }
        );
#endif

#if ENABLE_BLOCK_DISPENSER_VARIANTS
        RecipeManager.addSawRecipe(
            new ItemStack[] {
                new ItemStack(ZeroTestBlocks.block_breaker),
                new ItemStack(ZeroTestBlocks.block_placer)
            },
            BTWBlocks.blockDispenser
        );
#endif

#if ENABLE_ACTIVATOR_RAILS
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
            new ItemStack(BTWItems.ironNugget),
            new ItemStack[] {
                new ItemStack(Block.railActivator)
            }
        );
#endif

#if ENABLE_MINECART_OVEN
        RecipeManager.removeVanillaRecipe(
            new ItemStack(Item.minecartPowered),
            new Object[] {
                "A",
                "B",
                'A', Block.furnaceIdle, 
                'B', Item.minecartEmpty
            }
        );
        RecipeManager.addRecipe(
            new ItemStack(Item.minecartPowered),
            new Object[] {
                "A",
                "B",
                'A', BTWBlocks.idleOven, 
                'B', Item.minecartEmpty
            }
        );
#endif

#if ENABLE_WOODEN_RAILS
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
#endif

#if ENABLE_STEEL_RAILS
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
            new ItemStack(BTWItems.steelNugget),
            new ItemStack[] {
                new ItemStack(ZeroTestBlocks.steel_rail, 2)
            }
        );
#endif

#if ENABLE_CHEAPER_SOAP_BLOCK
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
#endif


#if ENABLE_RAIL_BUFFER_STOP
        RecipeManager.addRecipe(
            new ItemStack(ZeroTestBlocks.buffer_stop),
            new Object[] {
                "NMN",
                "M M",
                "BBB",
                'N', BTWItems.ironNugget,
                'M', new ItemStack(BTWItems.woodMouldingStubID, 1, InventoryUtils.IGNORE_METADATA),
                'B', new ItemStack(BTWItems.stoneBrick, 1, InventoryUtils.IGNORE_METADATA)
            }
        );
#endif

#if ENABLE_NERFED_DROPPER
        RecipeManager.addRecipe(
            new ItemStack(Block.dropper),
            new Object[] {
                "###", 
                "# #", 
                "#R#", 
                '#', new ItemStack(BTWItems.stoneBrick, 1, InventoryUtils.IGNORE_METADATA),
                'R', BTWItems.redstoneLatch
            }
        );
#endif

#if ENABLE_MIXER_BLOCK
        RecipeManager.addRecipe(
            new ItemStack(ZeroTestBlocks.mixer_block),
            new Object[] {
                "#Y#",
                "#X#",
                "#Y#",
                '#', Item.ingotIron,
                'X', BTWItems.screw,
                'Y', new ItemStack(BTWItems.woodCornerStubID, 1, InventoryUtils.IGNORE_METADATA)
            }
        );
        MixerRecipeList.addRecipes();
#endif

#if ENABLE_CRAFTGUIDE_COMPAT
        ZeroCompatUtil.initCraftguide();
#endif
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
