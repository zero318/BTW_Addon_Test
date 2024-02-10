package zero.test.mixin;
import net.minecraft.src.*;
import btw.world.util.WorldUtils;
import btw.block.blocks.DoorBlock;
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
// Block piston reactions
// This doesn't work, but consistency is cool
@Mixin(DoorBlock.class)
public abstract class DoorBlockMixins extends BlockDoor {
    public DoorBlockMixins() {
        super(0, null);
    }
    @Override
    public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int x, int y, int z, int facing, boolean ignoreTransparency) {
        if (ignoreTransparency) {
            int meta = this.getFullMetadata(blockAccess, x, y, z);
            int openDiff = 0;
            if (((((meta)&4)!=0))) {
                openDiff = meta <= 15 ? 1 : -1;
            }
            switch ((((meta + openDiff)&3))) {
                case 0:
                    return facing == 4;
                case 1:
                    return facing == 2;
                case 2:
                    return facing == 5;
                default:
                    return facing == 3;
            }
        }
        return false;
    }
}
