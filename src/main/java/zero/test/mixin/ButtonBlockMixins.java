package zero.test.mixin;
import net.minecraft.src.*;
import btw.world.util.WorldUtils;
import btw.block.blocks.ButtonBlock;
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
@Mixin(ButtonBlock.class)
public abstract class ButtonBlockMixins extends BlockButton {
    public ButtonBlockMixins() {
        super(0, false);
    }
    @Overwrite
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(IBlockAccess blockAccess, int x, int y, int z) {
        return super.getBlockBoundsFromPoolBasedOnState(blockAccess, x, y, z);
    }
    @Overwrite
    public boolean onRotatedAroundBlockOnTurntableToFacing(World world, int x, int y, int z, int direction) {
        return true;
    }
    @Override
    public int rotateMetadataAroundJAxis(int meta, boolean reverse) {
        int direction = (((meta)&7));
        switch (direction) {
            case 1: case 2: case 3: case 4:
                direction = 6 - rotateFacingAroundY(6 - direction, reverse);
        }
        return (((meta)&8|(direction)));
    }
}
