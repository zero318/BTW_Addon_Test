package zero.test.mixin;

import net.minecraft.src.*;

import btw.AddonHandler;
import btw.item.items.BucketItem;
import btw.item.items.PlaceAsBlockItem;
import btw.block.blocks.BucketBlock;
import btw.block.blocks.FallingBlock;

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

#include "..\util.h"
#include "..\feature_flags.h"

@Mixin(BucketBlock.class)
public abstract class BucketBlockMixins extends FallingBlock {
    public BucketBlockMixins() {
        super(0, null);
    }
#if ENABLE_AUTOMATIC_COW_MILKING

    // Hands are good enough to avoid bad breaks on buckets, this is fine
    @Override
    public void onBlockDestroyedLandingFromFall(World world, int x, int y, int z, int meta) {
        this.dropBlockAsItem(world, x, y, z, meta, 0);
    }
#endif
}