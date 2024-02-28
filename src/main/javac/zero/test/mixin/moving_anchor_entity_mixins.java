package zero.test.mixin;

import net.minecraft.src.*;

import btw.entity.mechanical.platform.MovingAnchorEntity;
import btw.item.util.ItemUtils;

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

#include "..\feature_flags.h"
#include "..\util.h"

@Mixin(MovingAnchorEntity.class)
public abstract class MovingAnchorEntityMixins {
#if ENABLE_PLATFORM_FIXES
    @Redirect(
        method = "convertToBlock",
        at = @At(
            value = "INVOKE",
            target = "Lbtw/item/util/ItemUtils;ejectSingleItemWithRandomOffset(Lnet/minecraft/src/World;IIIII)V"
        )
    )
    public void dont_drop_ghost_itemsA(World world, int x, int y, int z, int itemId, int damage) {
        if (!world.isRemote) {
            ItemUtils.ejectSingleItemWithRandomOffset(world, x, y, z, itemId, damage);
        }
    }
    
    @Redirect(
        method = "destroyAnchorWithDrop",
        at = @At(
            value = "INVOKE",
            target = "Lbtw/item/util/ItemUtils;ejectStackWithRandomOffset(Lnet/minecraft/src/World;IIILnet/minecraft/src/ItemStack;)V"
        )
    )
    public void dont_drop_ghost_itemsB(World world, int x, int y, int z, ItemStack stack) {
        if (!world.isRemote) {
            ItemUtils.ejectStackWithRandomOffset(world, x, y, z, stack);
        }
    }
#endif
}