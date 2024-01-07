package zero.test.mixin;
import net.minecraft.src.*;
import btw.block.blocks.NoteBlock;
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
import zero.test.mixin.IBlockComparatorAccessMixins;
import java.util.Random;
import zero.test.IWorldMixins;
// Block piston reactions
//updateNeighbourShapes
@Mixin(NoteBlock.class)
public class NoteBlockMixins {
    //@Overwrite
    public void onNeighborBlockChange(World world, int X, int Y, int Z, int neighbor_id) {
        boolean is_receiving_power = world.isBlockIndirectlyGettingPowered(X, Y, Z);
        int meta = world.getBlockMetadata(X, Y, Z);
        if (is_receiving_power != ((((meta)&1)!=0))) {
            TileEntityNote tile_entity = (TileEntityNote)world.getBlockTileEntity(X, Y, Z);
            if (tile_entity != null) {
                tile_entity.previousRedstoneState = is_receiving_power;
                tile_entity.triggerNote(world, X, Y, Z);
            }
            world.setBlockMetadataWithNotify(X, Y, Z, meta ^ 1, 0x01 | 0x02 | 0x80);
        }
    }
    @Inject(
        method = "onBlockActivated",
        at = @At("HEAD")
    )
    public void onBlockActivated_inject(World world, int X, int Y, int Z, EntityPlayer player, int iFacing, float fXClick, float fYClick, float fZClick, CallbackInfoReturnable info) {
        if (!world.isRemote) {
            // Just do *something* to the metadata so that stuff updates
            world.setBlockMetadataWithNotify(X, Y, Z, world.getBlockMetadata(X, Y, Z) ^ 2, 0x01 | 0x02 | 0x80);
        }
    }
}
