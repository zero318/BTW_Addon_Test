package zero.test.mixin;

import net.minecraft.src.*;

import btw.block.blocks.DetectorBlock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

#include "..\util.h"
#include "..\feature_flags.h"

#define POWERED_META_OFFSET 0
#define FACING_META_OFFSET 1

@Mixin(DetectorBlock.class)
public abstract class DetectorBlockMixins extends Block {
    public DetectorBlockMixins() {
        super(0, null);
    }
    
#if ENABLE_NORMAL_DETECTOR_POWER_RANGE && 0
    @Overwrite
    public void setBlockOn(World world, int x, int y, int z, boolean newState) {
        int meta = world.getBlockMetadata(x, y, z);
        
        int new_meta = MERGE_META_FIELD(meta, POWERED, newState)
    }
#endif
}