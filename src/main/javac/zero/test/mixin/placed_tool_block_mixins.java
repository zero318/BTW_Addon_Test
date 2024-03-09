package zero.test.mixin;

import net.minecraft.src.*;

import btw.block.blocks.PlacedToolBlock;
import btw.block.tileentity.PlacedToolTileEntity;
import btw.item.items.ToolItem;

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

@Mixin(PlacedToolBlock.class)
public abstract class PlacedToolBlockMixins extends BlockContainer {
    public PlacedToolBlockMixins() {
        super(0, null);
    }
    
#if ENABLE_TURNTABLE_SLIME_SUPPORT

    @Override
    public void breakBlock(World world, int x, int y, int z, int blockId, int meta) {
        TileEntity tileEntity;
        if ((tileEntity = world.getBlockTileEntity(x, y, z)) instanceof PlacedToolTileEntity) {
            ((PlacedToolTileEntity)tileEntity).ejectContents();
        }
        
#if ENABLE_MORE_COMPARATOR_OUTPUTS
        world.func_96440_m(x, y, z, blockId);
#endif
        
        super.breakBlock(world, x, y, z, blockId, meta);	        
    }

#if ENABLE_MORE_TURNABLE_BLOCKS
    @Overwrite
    public boolean onRotatedAroundBlockOnTurntableToFacing(World world, int x, int y, int z, int direction) {
        return true;
    }
#endif

#endif

#if ENABLE_MORE_COMPARATOR_OUTPUTS
    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }
    
#define OUTPUT_HARVEST_LEVEL 0
#define OUTPUT_DURABILITY 1

#define COMPARATOR_OUTPUT_TYPE OUTPUT_DURABILITY
    
    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int flatDirection) {
        TileEntity tileEntity;
        if ((tileEntity = world.getBlockTileEntity(x, y, z)) instanceof PlacedToolTileEntity) {
            ItemStack stack;
            if ((stack = ((PlacedToolTileEntity)tileEntity).getToolStack()) != null) {
#if COMPARATOR_OUTPUT_TYPE == OUTPUT_HARVEST_LEVEL
                Item item;
                if ((item = stack.getItem()) instanceof ToolItem) {
                    return ((ToolItem)item).toolMaterial.getHarvestLevel() + 1;
                }
#elif COMPARATOR_OUTPUT_TYPE == OUTPUT_DURABILITY
                int maxDamage = stack.getMaxDamage();
                int currentDamage = stack.getItemDamage();
                if (currentDamage != maxDamage - 1) {
                    return MathHelper.floor_float(((float)(maxDamage - currentDamage) / (float)maxDamage) * 14.0F) + 1;
                }
#endif
            }
        }
        return 0;
    }
#endif
}