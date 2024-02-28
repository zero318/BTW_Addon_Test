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
    public ButtonBlockMixins() {
        super(0, false);
    }
#if ENABLE_VERTICAL_BUTTONS
    @Overwrite
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(IBlockAccess blockAccess, int x, int y, int z) {
        return super.getBlockBoundsFromPoolBasedOnState(blockAccess, x, y, z);
    }
#endif

#if ENABLE_MORE_TURNABLE_BLOCKS
    @Overwrite
    public boolean onRotatedAroundBlockOnTurntableToFacing(World world, int x, int y, int z, int direction) {
        return true;
    }
    
    @Override
    public int rotateMetadataAroundJAxis(int meta, boolean reverse) {
        int direction = READ_META_FIELD(meta, BUTTON_DIRECTION);
        switch (direction) {
            case 1: case 2: case 3: case 4:
                direction = 6 - rotateFacingAroundY(6 - direction, reverse);
        }
        return MERGE_META_FIELD(meta, BUTTON_DIRECTION, direction);
    }
#endif
}