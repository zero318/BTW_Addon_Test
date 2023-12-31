package zero.test.mixin;

import net.minecraft.src.Block;
import net.minecraft.src.World;
import net.minecraft.src.BlockPistonBase;
import net.minecraft.src.*;

import btw.block.blocks.PistonBlockBase;
import btw.block.blocks.PistonBlockMoving;
import btw.item.util.ItemUtils;
import btw.AddonHandler;
import btw.BTWAddon;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

import zero.test.IBlockMixins;
import zero.test.mixin.IPistonBaseAccessMixins;
import zero.test.IWorldMixins;
import zero.test.IBlockEntityPistonMixins;

import java.util.List;

#include "..\func_aliases.h"
#include "..\feature_flags.h"
#include "..\util.h"

#define DIRECTION_META_OFFSET 0
#define STICKY_META_OFFSET 3
#define STICKY_META_BITS 1
#define STICKY_IS_BOOL true

@Mixin(BlockPistonMoving.class)
public class BlockPistonMovingMixins {
#if ENABLE_BETTER_BUDDY_DETECTION
    //@Override
    public boolean triggersBuddy() {
        return false;
    }
#endif
    
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB maskBox, List list, Entity entity) {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityPiston) {
            ((IBlockEntityPistonMixins)(Object)tileEntity).getCollisionList(maskBox, list);
        }
    }
    
    public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int x, int y, int z, int direction, boolean ignoreTransparency) {
        TileEntity tileEntity = blockAccess.getBlockTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityPiston) {
            return ((IBlockEntityPistonMixins)(Object)tileEntity).hasLargeCenterHardPointToFacing(x, y, z, direction, ignoreTransparency);
        }
        return false;
    }
}