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
// Block piston reactions
@Mixin(LadderBlockBase.class)
public class LadderBlockBaseMixins {
    // Ladders previously used WorldUtils version
    // of the hardpoint check, which still defaults to false
    // to avoid making 5 million mixins
    @Overwrite
    public boolean canAttachToFacing(World world, int X, int Y, int Z, int direction) {
  if (direction >= 2) {
   X += Facing.offsetsXForSide[direction];
   Y += Facing.offsetsYForSide[direction];
   Z += Facing.offsetsZForSide[direction];
            Block block = Block.blocksList[world.getBlockId(X, Y, Z)];
   return !((block)==null) && block.hasLargeCenterHardPointToFacing(world, X, Y, Z, ((direction)^1), true);
  }
  return false;
 }
}
