package zero.test.mixin;

import net.minecraft.src.Block;
import net.minecraft.src.World;
import net.minecraft.src.BlockPistonBase;
import net.minecraft.src.*;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

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

#include "..\func_aliases.h"
#include "..\feature_flags.h"
#include "..\util.h"

#define DIRECTION_META_OFFSET 0
#define STICKY_META_OFFSET 3
#define STICKY_META_BITS 1
#define STICKY_IS_BOOL true

@Mixin(BlockPistonExtension.class)
public class BlockPistonExtensionMixins {
#if ENABLE_BETTER_BUDDY_DETECTION
    //@Override
    public boolean triggersBuddy() {
        return false;
    }
#endif
    
    public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int x, int y, int z, int direction, boolean ignoreTransparency) {
        return direction == READ_META_FIELD(blockAccess.getBlockMetadata(x, y, z), DIRECTION);
    }
    
    @Environment(EnvType.CLIENT)
    public boolean shouldRenderNeighborFullFaceSide(IBlockAccess blockAccess, int x, int y, int z, int direction) {
        return direction != READ_META_FIELD(blockAccess.getBlockMetadata(x, y, z), DIRECTION);
    }
}