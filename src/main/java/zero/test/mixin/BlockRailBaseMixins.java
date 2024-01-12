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
// Block piston reactions
@Mixin(BlockRailBase.class)
public class BlockRailBaseMixins extends Block {
    public BlockRailBaseMixins(int par1, Material par2Material) {
        super(par1, par2Material);
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
               0x2 != ((Direction.directionToFacing[(int)Math.floor(entityLiving.rotationYaw / 90.0D + 0.5D) & 3])&~1)
                ? 1
                : 0;
    }
    public int getPlatformMobilityFlag(World world, int x, int y, int z) {
        return 2;
    }
    public int adjustMetadataForPlatformMove(int meta) {
        return ((BlockRailBase)(Object)this).isPowered() ? (((meta)&7)) : meta;
    }
}
