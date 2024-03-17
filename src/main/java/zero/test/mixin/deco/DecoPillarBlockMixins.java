package zero.test.mixin.deco;
import net.minecraft.src.*;
import btw.block.blocks.PillarBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import deco.block.blocks.DecoPillarBlock;
import zero.test.ZeroUtil;
// Block piston reactions

@Mixin(DecoPillarBlock.class)
public abstract class DecoPillarBlockMixins {
    public boolean canBlockBePulledByPiston(World world, int x, int y, int z, int direction) {
        return (((((Integer.compareUnsigned((ZeroUtil.getBlockId(this))-(3639),(3654)-(3639)))<=0)))^true);
    }
    public boolean canBeStuckTo(World world, int x, int y, int z, int direction, int neighborId) {
        return (((((Integer.compareUnsigned((ZeroUtil.getBlockId(this))-(3639),(3654)-(3639)))<=0)))^true);
    }
}
