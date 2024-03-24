package zero.test.mixin;
import net.minecraft.src.*;
import btw.AddonHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zero.test.IWorldMixins;
import zero.test.IBlockBaseRailLogicMixins;
import zero.test.mixin.IBlockBaseRailLogicAccessMixins;
import zero.test.IBaseRailBlockMixins;
@Mixin(BlockRailBase.class)
public abstract class BlockRailBaseMixins extends Block implements IBaseRailBlockMixins {
    public BlockRailBaseMixins() {
        super(0, null);
    }
    /*
    @Inject(
        method = "breakBlock(Lnet/minecraft/src/World;IIIII)V",
        at = @At("HEAD"),
        cancellable = true
    )
    public void break_block_new(World world, int x, int y, int z, int par5, int meta, CallbackInfo callbackInfo) {
        if (((IWorldMixins)world).get_is_handling_piston_move()) {
            //AddonHandler.logMessage("Cancel break rail");
            callbackInfo.cancel();
        }
        //AddonHandler.logMessage("Break rail");
    }
    */
    // Make rail placement match the direction players are facing
    @Override
    public int preBlockPlacedBy(World world, int x, int y, int z, int meta, EntityLiving entityLiving) {
        return /*entityLiving instanceof EntityPlayer &&*/
               0x2 != (((Direction.directionToFacing[((int)MathHelper.floor_double((double)(entityLiving.rotationYaw)*0.01111111111111111111111111111111D+0.5D)&3)]))&~1)
                ? 1
                : 0;
    }
    public int getPlatformMobilityFlag(World world, int x, int y, int z) {
        return 2;
    }
    public int adjustMetadataForPlatformMove(int meta) {
        return ((BlockRailBase)(Object)this).isPowered() ? (((meta)&7)) : meta;
    }
    @Redirect(
        method = { "canPlaceBlockAt(Lnet/minecraft/src/World;III)Z", "onNeighborBlockChange(Lnet/minecraft/src/World;IIII)V" },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/World;doesBlockHaveSolidTopSurface(III)Z"
        )
    )
    public boolean doesBlockHaveSolidTopSurface_redirect(World world, int x, int y, int z) {
        return ((IWorldMixins)world).doesBlockSupportRails(x, y, z);
    }
}
