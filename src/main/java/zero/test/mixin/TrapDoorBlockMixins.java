package zero.test.mixin;
import net.minecraft.src.*;
import btw.world.util.WorldUtils;
import btw.block.blocks.TrapDoorBlock;
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
@Mixin(TrapDoorBlock.class)
public abstract class TrapDoorBlockMixins extends BlockTrapDoor {
    public TrapDoorBlockMixins() {
        super(0, null);
    }
    @Override
    public boolean canRotateOnTurntable(IBlockAccess blockAccess, int x, int y, int z) {
        return true;
    }
    @Override
    public int rotateMetadataAroundJAxis(int meta, boolean reverse) {
        ;return (((meta)&12|(rotateFacingAroundY((((meta)&3)) + 2, ((reverse)^true)) - 2)));
    }
}
