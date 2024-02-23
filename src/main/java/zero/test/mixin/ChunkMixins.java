package zero.test.mixin;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import zero.test.ZeroUtil;
// Block piston reactions

@Mixin(Chunk.class)
public abstract class ChunkMixins {
/*
    @Redirect(
        method = "setBlockIDWithMetadata(IIIII)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/ITileEntityProvider;createNewTileEntity(Lnet/minecraft/src/World;)Lnet/minecraft/src/TileEntity;"
        )
    )
    public TileEntity make_tile_entity_redirect(ITileEntityProvider provider, World world) {
        if (ZeroUtil.pistonTemp == null) {
            return provider.createNewTileEntity(world);
        }
        TileEntity ret = ZeroUtil.pistonTemp;
        ZeroUtil.pistonTemp = null;
        return ret;
    }
*/
    @Redirect(
        method = { "setBlockIDWithMetadata(IIIII)Z", "setBlockMetadata(IIII)Z" },
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
