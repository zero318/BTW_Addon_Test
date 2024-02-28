package zero.test.mixin;
import net.minecraft.src.*;
import btw.AddonHandler;
import btw.block.BTWBlocks;
import btw.block.blocks.PlatformBlock;
import btw.block.blocks.AnchorBlock;
import btw.entity.mechanical.platform.BlockLiftedByPlatformEntity;
import btw.entity.mechanical.platform.MovingAnchorEntity;
import btw.entity.mechanical.platform.MovingPlatformEntity;
import btw.block.tileentity.PulleyTileEntity;
import btw.item.util.ItemUtils;
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
import zero.test.PlatformResolver;
import zero.test.IWorldMixins;
import zero.test.IBlockMixins;
import zero.test.IMovingPlatformEntityMixins;
//import zero.test.mixin.IAnchorBlockAccessMixins;
import java.util.Random;
// Block piston reactions
@Mixin(AnchorBlock.class)
public abstract class AnchorBlockMixins extends Block {
    public AnchorBlockMixins() {
        super(0, null);
    }
    @Shadow
    public abstract void convertAnchorToEntity(World world, int i, int j, int k, PulleyTileEntity attachedTileEntityPulley, int iMovementDirection);
    private static final PlatformResolver server_resolver = new PlatformResolver();
    private static final PlatformResolver client_resolver = new PlatformResolver();
    @Overwrite(remap=false)
    public void convertConnectedPlatformsToEntities(World world, int x, int y, int z, MovingAnchorEntity associatedAnchorEntity) {
        (!world.isRemote ? server_resolver : client_resolver).liftBlocks(world, x, y, z, associatedAnchorEntity);
    }
    @Overwrite(remap=false)
    public boolean notifyAnchorBlockOfAttachedPulleyStateChange(PulleyTileEntity tileEntityPulley, World world, int x, int y, int z){
  int iMovementDirection = 0;
  if (tileEntityPulley.isRaising()) {
   if (world.getBlockId(x, y + 1, z) == BTWBlocks.ropeBlock.blockID) {
    iMovementDirection = 1;
   }
  }
  else if (tileEntityPulley.isLowering()) {
            Block block = Block.blocksList[world.getBlockId(x, y - 1, z)];
            if (
                ((block)==null) ||
                ((IBlockMixins)block).getPlatformMobilityFlag(world, x, y - 1, z) == 1
            ) {
                iMovementDirection = -1;
            }
  }
  if (iMovementDirection != 0) {
   this.convertAnchorToEntity(world, x, y, z, tileEntityPulley, iMovementDirection);
   return true;
  }
  return false;
 }
}
