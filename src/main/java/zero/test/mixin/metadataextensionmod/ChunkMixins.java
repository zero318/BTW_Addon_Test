package zero.test.mixin.metadataextensionmod;
import net.minecraft.src.*;
import btw.community.arminias.metadata.extension.ChunkExtension;
import btw.community.arminias.metadata.extension.WorldExtension;
import btw.community.arminias.metadata.extension.ExtendedBlockStorageExtension;
import btw.community.arminias.metadata.extension.TileEntityExtension;
import btw.community.arminias.metadata.mixin.ChunkMixin;
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
import zero.test.ZeroUtil;
@Mixin(
    value = Chunk.class,
    priority = 1100
)
public abstract class ChunkMixins
{
    @Redirect(
        method = { "setBlockIDWithMetadataAndExtraMetadata(IIIIII)Z", "setBlockExtraMetadata(IIII)Z" },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/Chunk;getChunkBlockTileEntity(III)Lnet/minecraft/src/TileEntity;",
            ordinal = 0
        )
    )
    public TileEntity make_tile_entity_redirect(Chunk chunk, int x, int y, int z) {
        return chunk.worldObj.getBlockTileEntity((chunk.xPosition << 4) + x, y, (chunk.zPosition << 4) + z);
    }
}
