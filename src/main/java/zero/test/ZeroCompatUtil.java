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
public class ZeroCompatUtil {
    public static int getBlockExtMetadata(World world, int x, int y, int z) {
        return 0;
    }
    public static long getBlockFullMetadata(World world, int x, int y, int z) {
        return ((long)(world.getBlockMetadata(x, y, z))) | ((long)(getBlockExtMetadata(world, x, y, z))&0xFFFFFFFFL) << 4;
    }
    public static boolean setBlockExtMetadataWithNotify(World world, int x, int y, int z, int extMeta, int flags) {
        return false;
    }
    public static boolean setBlockBothMetadataWithNotify(World world, int x, int y, int z, int meta, int extMeta, int flags) {
        return world.setBlockMetadataWithNotify(x, y, z, meta, flags);
    }
    public static boolean setBlockWithExtra(World world, int x, int y, int z, int blockId, int meta, int extMeta, int flags) {
        return world.setBlock(x, y, z, blockId, meta, flags);
    }
    public static TileEntity getPistonTileEntity(int blockId, int meta, int extMeta, int direction, boolean isExtending, boolean isBase) {
        return BlockPistonMoving.getTileEntity(blockId, meta, direction, isExtending, isBase);
    }
    public static TileEntity getShoveledTileEntity(int blockId, int meta, int extMeta, int direction) {
        return PistonBlockMoving.getShoveledTileEntity(blockId, meta, direction);
    }
    public static int getPistonTileEntityExtMeta(TileEntityPiston tileEntity) {
        return 0;
    }
    public static void addExtMetaToMovingPlatformEntity(MovingPlatformEntity entity, int extMeta) {
    }
    public static int getMovingPlatformEntityExtMeta(MovingPlatformEntity entity) {
        return 0;
    }
    public static void addExtMetaToLiftedBlockEntity(BlockLiftedByPlatformEntity entity, int extMeta) {
    }
    public static int getBlockLiftedByPlatformEntityExtMeta(BlockLiftedByPlatformEntity entity) {
        return 0;
    }
    public static void initCraftguide() {
    }
}
