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

#include "..\util.h"
#include "..\feature_flags.h"

#define BUTTON_DIRECTION_META_OFFSET 0
#define BUTTON_DIRECTION_META_BITS 3

#define POWERED_META_OFFSET 3

@Mixin(ButtonBlock.class)
public abstract class ButtonBlockMixins extends BlockButton {
    public ButtonBlockMixins(int par1, boolean par2) {
        super(par1, par2);
    }
#if ENABLE_VERTICAL_BUTTONS
    @Overwrite
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(IBlockAccess blockAccess, int x, int y, int z) {
        return super.getBlockBoundsFromPoolBasedOnState(blockAccess, x, y, z);
    }
#endif
}