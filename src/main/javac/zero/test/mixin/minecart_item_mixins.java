

package zero.test.mixin;

import net.minecraft.src.*;

import btw.item.items.MinecartItem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

#include "..\feature_flags.h"
#include "..\util.h"

@Mixin(ItemMinecart.class)
public abstract class ItemMinecartMixins extends Item {
    public ItemMinecartMixins(int iItemID) {
        super(iItemID);
    }
    
#if ENABLE_MINECART_HITBOX_FIXES
    @Overwrite
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int par7, float par8, float par9, float par10) {
        
        int blockId = world.getBlockId(x, y, z);

        if (BlockRailBase.isRailBlock(blockId)) {
            if (!world.isRemote) {
                
                int railShape = world.getBlockMetadata(x, y, z);
                
                if (((BlockRailBase)Block.blocksList[blockId]).isPowered()) {
                    railShape &= 7;
                }
                
                ItemMinecart self = (ItemMinecart)(Object)this;
                EntityMinecart minecart = EntityMinecart.createMinecart(world, (double)x + 0.5D, (double)y + (!RAIL_IS_ASCENDING(railShape) ? 0.0625D : 0.5625D), (double)z + 0.5D, self.minecartType);

                if (itemStack.hasDisplayName()) {
                    minecart.func_96094_a(itemStack.getDisplayName());
                }
                //minecart.rotationYaw = (float)(((MathHelper.floor_double((double)(entityPlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3) - 1) * 90);

                world.spawnEntityInWorld(minecart);
            }

            --itemStack.stackSize;
            return true;
        }
        return false;
    }
#endif
}