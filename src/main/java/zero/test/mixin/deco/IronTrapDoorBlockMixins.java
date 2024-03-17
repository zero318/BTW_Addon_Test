package zero.test.mixin.deco;
import net.minecraft.src.*;
import btw.block.blocks.*;
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
import deco.block.blocks.IronTrapDoorBlock;
// Block piston reactions

@Mixin(IronTrapDoorBlock.class)
public abstract class IronTrapDoorBlockMixins extends TrapDoorBlock {
    public IronTrapDoorBlockMixins() {
        super(0);
    }
    @Inject(
        method = "<init>(I)V",
        at = @At("TAIL")
    )
    public void constructor_inject(int blockId, CallbackInfo info) {
        this.setAxesEffectiveOn(false);
        this.setPicksEffectiveOn(true);
    }
    @Override
 public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int side) {
  return true;
 }
    @Override
 public boolean isBreakableBarricade(IBlockAccess blockAccess, int x, int y, int z) {
  return false;
 }
    @Override
    public boolean isBreakableBarricadeOpen(IBlockAccess blockAccess, int x, int y, int z) {
  return false;
 }
}
