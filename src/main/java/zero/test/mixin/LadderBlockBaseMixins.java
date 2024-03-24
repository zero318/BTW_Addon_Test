package zero.test.mixin;
import net.minecraft.src.*;
import btw.AddonHandler;
import btw.block.blocks.LadderBlockBase;
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
import zero.test.IWorldMixins;
import java.util.Random;
@Mixin(LadderBlockBase.class)
public abstract class LadderBlockBaseMixins {
    // Ladders previously used WorldUtils version
    // of the hardpoint check, which still defaults to false
    // to avoid making 5 million mixins
    @Overwrite
    public boolean canAttachToFacing(World world, int x, int y, int z, int direction) {
  if (((direction)>=2)) {
   x += Facing.offsetsXForSide[direction];
   z += Facing.offsetsZForSide[direction];
            Block block = Block.blocksList[world.getBlockId(x, y, z)];
   return !((block)==null) && block.hasLargeCenterHardPointToFacing(world, x, y, z, ((direction)^1), true);
  }
  return false;
 }
}
