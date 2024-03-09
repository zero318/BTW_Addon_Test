package zero.test;
import net.minecraft.src.*;
import btw.block.tileentity.*;
import btw.item.util.ItemUtils;
import btw.inventory.util.InventoryUtils;
import btw.AddonHandler;
import java.util.Random;
// Block piston reactions
public class ZeroUtil {
    public static final int[] rail_exit_directions = new int[] {
        3, 2,
        5, 4,
        5, 4,
        5, 4,
        3, 2,
        3, 2,
        2, 4,
        2, 5,
        3, 5,
        3, 4
    };
    public static final int[] rail_exit_flat_directions = new int[] {
        2, 0,
        1, 3,
        1, 3,
        1, 3,
        2, 0,
        2, 0,
        0, 3,
        0, 1,
        2, 1,
        2, 3
    };
    public static int lan_port = 0;
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
}
