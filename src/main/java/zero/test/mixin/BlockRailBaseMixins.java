package zero.test.mixin;
import net.minecraft.src.*;
import btw.AddonHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zero.test.IWorldMixins;
import zero.test.IBlockBaseRailLogicMixins;
import zero.test.mixin.IBlockBaseRailLogicAccessMixins;
@Mixin(BlockRailBase.class)
public class BlockRailBaseMixins {
    /*
    @Inject(
        method = "breakBlock(Lnet/minecraft/src/World;IIIII)V",
        at = @At("HEAD"),
        cancellable = true
    )
    public void break_block_new(World world, int X, int Y, int Z, int par5, int meta, CallbackInfo callback_info) {
        if (((IWorldMixins)world).get_is_handling_piston_move()) {
            //AddonHandler.logMessage("Cancel break rail");
            callback_info.cancel();
        }
        //AddonHandler.logMessage("Break rail");
    }
    */
    //@Override
    public int preBlockPlacedBy(World world, int X, int Y, int Z, int meta, EntityLiving entity_living) {
        return entity_living instanceof EntityPlayer &&
               0x2 != ((Direction.directionToFacing[(int)Math.floor(entity_living.rotationYaw / 90.0 + 0.5) & 3])&~1)
                ? 1
                : 0;
    }
}
