package zero.test.mixin.metadataextensionmod;
import net.minecraft.src.*;
import btw.block.blocks.PistonBlockMoving;
import btw.entity.mechanical.platform.BlockLiftedByPlatformEntity;
import btw.entity.mechanical.platform.MovingPlatformEntity;
import btw.community.arminias.metadata.PistonHelper;
import btw.community.arminias.metadata.extension.WorldExtension;
import btw.community.arminias.metadata.extension.TileEntityPistonExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.gen.Accessor;
import zero.test.ZeroUtil;
import zero.test.ZeroCompatUtil;
import zero.test.metadata_compat.IBlockLiftedByPlatformEntityMixins;
import zero.test.metadata_compat.IMovingPlatformEntityMixins;
import zero.test.metadata_compat.ITileEntityPistonMixins;

@Mixin(ZeroCompatUtil.class)
public abstract class ZeroCompatUtilMixins {
    @Overwrite(remap=false)
    public static int getBlockExtMetadata(World world, int x, int y, int z) {
        return ((WorldExtension)world).getBlockExtraMetadata(x, y, z);
    }
    @Overwrite(remap=false)
    public static boolean setBlockExtMetadataWithNotify(World world, int x, int y, int z, int extMeta, int flags) {
        return ((WorldExtension)world).setBlockExtraMetadataWithNotify(x, y, z, extMeta, flags);
    }
    @Overwrite(remap=false)
    public static boolean setBlockBothMetadataWithNotify(World world, int x, int y, int z, int meta, int extMeta, int flags) {
        return ((WorldExtension)world).setBlockMetadataAndExtraWithNotify(x, y, z, meta, flags, extMeta);
    }
    @Overwrite(remap=false)
    public static boolean setBlockWithExtra(World world, int x, int y, int z, int blockId, int meta, int extMeta, int flags) {
        return ((WorldExtension)world).setBlockWithExtra(x, y, z, blockId, meta, flags, extMeta);
    }
    @Overwrite(remap=false)
    public static TileEntity getPistonTileEntity(int blockId, int meta, int extMeta, int direction, boolean isExtending, boolean isBase) {
        TileEntity tileEntity = BlockPistonMoving.getTileEntity(blockId, meta, direction, isExtending, isBase);
        ((ITileEntityPistonMixins)tileEntity).setBlockExtMetadata(extMeta);
        return tileEntity;
    }
    @Overwrite(remap=false)
    public static TileEntity getShoveledTileEntity(int blockId, int meta, int extMeta, int direction) {
        TileEntity tileEntity = PistonBlockMoving.getShoveledTileEntity(blockId, meta, direction);
        ((ITileEntityPistonMixins)tileEntity).setBlockExtMetadata(extMeta);
        return tileEntity;
    }
    @Overwrite(remap=false)
    public static int getPistonTileEntityExtMeta(TileEntityPiston tileEntity) {
        return ((ITileEntityPistonMixins)tileEntity).getBlockExtMetadata();
    }
    @Overwrite(remap=false)
    public static void addExtMetaToMovingPlatformEntity(MovingPlatformEntity entity, int extMeta) {
        ((IMovingPlatformEntityMixins)entity).setBlockExtMetadata(extMeta);
    }
    @Overwrite(remap=false)
    public static int getMovingPlatformEntityExtMeta(MovingPlatformEntity entity) {
        return ((IMovingPlatformEntityMixins)entity).getBlockExtMetadata();
    }
    @Overwrite(remap=false)
    public static void addExtMetaToLiftedBlockEntity(BlockLiftedByPlatformEntity entity, int extMeta) {
        ((IBlockLiftedByPlatformEntityMixins)entity).setBlockExtMetadata(extMeta);
    }
    @Overwrite(remap=false)
    public static int getBlockLiftedByPlatformEntityExtMeta(BlockLiftedByPlatformEntity entity) {
        return ((IBlockLiftedByPlatformEntityMixins)entity).getBlockExtMetadata();
    }
}
