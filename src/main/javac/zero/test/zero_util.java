package zero.test;

import net.minecraft.src.*;

import btw.block.blocks.PistonBlockMoving;
import btw.block.tileentity.*;
import btw.item.util.ItemUtils;
import btw.entity.mechanical.platform.MovingPlatformEntity;
import btw.entity.mechanical.platform.BlockLiftedByPlatformEntity;
import btw.inventory.util.InventoryUtils;
import btw.AddonHandler;

import net.fabricmc.loader.api.FabricLoader;

//import zero.test.mixin.IBlockAccessMixins;

import java.util.Random;

#include "feature_flags.h"
#include "util.h"

public class ZeroUtil {
    
    public static boolean always_false = false;
    public static boolean always_true = true;
    
    public static final int[] rail_exit_directions = new int[] {
        DIRECTION_SOUTH, DIRECTION_NORTH,
        DIRECTION_EAST, DIRECTION_WEST,
        DIRECTION_EAST, DIRECTION_WEST,
        DIRECTION_EAST, DIRECTION_WEST,
        DIRECTION_SOUTH, DIRECTION_NORTH,
        DIRECTION_SOUTH, DIRECTION_NORTH,
        DIRECTION_NORTH, DIRECTION_WEST,
        DIRECTION_NORTH, DIRECTION_EAST,
        DIRECTION_SOUTH, DIRECTION_EAST,
        DIRECTION_SOUTH, DIRECTION_WEST
    };
    
    public static final int[] rail_exit_flat_directions = new int[] {
        FLAT_DIRECTION_SOUTH, FLAT_DIRECTION_NORTH,
        FLAT_DIRECTION_EAST, FLAT_DIRECTION_WEST,
        FLAT_DIRECTION_EAST, FLAT_DIRECTION_WEST,
        FLAT_DIRECTION_EAST, FLAT_DIRECTION_WEST,
        FLAT_DIRECTION_SOUTH, FLAT_DIRECTION_NORTH,
        FLAT_DIRECTION_SOUTH, FLAT_DIRECTION_NORTH,
        FLAT_DIRECTION_NORTH, FLAT_DIRECTION_WEST,
        FLAT_DIRECTION_NORTH, FLAT_DIRECTION_EAST,
        FLAT_DIRECTION_SOUTH, FLAT_DIRECTION_EAST,
        FLAT_DIRECTION_SOUTH, FLAT_DIRECTION_WEST
    };
    
    
#if ENABLE_MODERN_PUBLISH_COMMAND
    public static int lan_port = 0;
#endif

    // Range: (-180.0,180.0]
    public static float angle_diff(float angle, float value) {
        if ((angle -= value) > 180.0f) {
            return angle - 360.0f;
        }
        if (angle <= -180.0f) {
            return angle + 360.0f;
        }
        return angle;
    }
    
    // Range: [0.0,180.0]
    public static float angle_diff_abs(float angle, float value) {
        if ((angle -= value) < 0.0f) {
            angle = -angle;
        }
        if (angle > 180.0f) {
            return 360.0f - angle;
        }
        return angle;
    }
    
    // Range: [0.0,179.0]
    public static float angle_plane(float angle) {
        if (angle < 0.0f) {
            return angle + 180.0f;
        }
        return angle;
    }
    
    // Range: [0.0,90.0]
    public static float angle_plane_diff(float angle, float value) {
        if (angle < 0.0f) {
            angle += 180.0f;
        }
        if (value < 0.0f) {
            value += 180.0f;
        }
        if ((angle -= value) < 0.0f) {
            angle = -angle;
        }
        if (angle >= 90.0f) {
            return 180.0f - angle;
        }
        return angle;
    }
    
    // Range: [-180.0,180.0)
    public static float angle_rotate_180(float angle) {
        if ((angle += -180.0f) < -180.0f) {
            return angle + 360.0f;
        }
        return angle;
    }
    
    
    
#if ENABLE_DEBUG_STRING_JANK

#define PRINT_INTERVAL 10

    public static int print_index = 0;
    public static String print_text = "";
    public static void debug_print(String text) {
        print_text += text;
        if (++print_index == PRINT_INTERVAL) {
            print_index = 0;
            AddonHandler.logMessage(print_text);
            print_text = "";
        } else {
            print_text += "\n";
        }
    }
#endif

    public static double triangle_random(Random random, double offset, double range) {
        return offset + range * (random.nextDouble() - random.nextDouble());
    }
    
    public static void break_tile_entity(World world, int x, int y, int z, TileEntity tileEntity) {
        if (tileEntity instanceof IInventory) {
            InventoryUtils.ejectInventoryContents(world, x, y, z, (IInventory)tileEntity);
        }
        else if (tileEntity instanceof TileEntityRecordPlayer) {
            ItemStack record;
            if ((record = ((TileEntityRecordPlayer)tileEntity).func_96097_a()) != null) {
                ItemUtils.ejectStackWithRandomOffset(world, x, y, z, record);
            }
        }
        else if (tileEntity instanceof ArcaneVesselTileEntity) {
            ((ArcaneVesselTileEntity)tileEntity).ejectContentsOnBlockBreak();
        }
        else if (tileEntity instanceof BasketTileEntity) {
            ((BasketTileEntity)tileEntity).ejectContents();
        }
        else if (tileEntity instanceof PlacedToolTileEntity) {
            ((PlacedToolTileEntity)tileEntity).ejectContents();
        }
        else if (tileEntity instanceof CampfireTileEntity) {
            ((CampfireTileEntity)tileEntity).ejectContents();
        }
    }
    
    
    private static boolean deco_is_loaded;
    private static boolean deco_lookup = false;
    
    public static boolean isDecoLoaded() {
        if (deco_lookup) {
            return deco_is_loaded;
        }
        deco_lookup = true;
        return deco_is_loaded = FabricLoader.getInstance().isModLoaded("decoaddon");
    }
    
    private static boolean craftguide_is_loaded;
    private static boolean craftguide_lookup = false;
    
    public static boolean isCraftguideLoaded() {
        if (craftguide_lookup) {
            return craftguide_is_loaded;
        }
        craftguide_lookup = true;
        return craftguide_is_loaded = FabricLoader.getInstance().isModLoaded("craftguide");
    }
    
    private static boolean metadata_extension_is_loaded;
    private static boolean metadata_extension_lookup = false;
    
    public static boolean isMetadataExtensionLoaded() {
        if (metadata_extension_lookup) {
            return metadata_extension_is_loaded;
        }
        metadata_extension_lookup = true;
        return metadata_extension_is_loaded = FabricLoader.getInstance().isModLoaded("metadataextensionmod");
    }
    
    // For some reason the deco mixins refused to compile
    // if I specified the correct parent classes because
    // the way I setup the dependency is stupid? It can't
    // seem to locate net.minecraft.src.Block specifically
    // in that context.
    //
    // So just pass the object over here where it's valid
    // to read the block ID property and then return it.
    public static int getBlockId(Object block) {
        return ((Block)block).blockID;
    }
    
    /*
    public static void onStartFalling(Object block, EntityFallingSand entity) {
        ((IBlockAccessMixins)block).callOnStartFalling(entity);
    }
    */
}