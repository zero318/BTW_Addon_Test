package zero.test.mixin;

import net.minecraft.src.*;

import java.util.List;

import btw.block.blocks.AnchorBlock;
import btw.block.tileentity.PulleyTileEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import zero.test.IBlockMixins;

#include "..\func_aliases.h"
#include "..\feature_flags.h"
#include "..\util.h"
    
@Mixin(AnchorBlock.class)
public interface IAnchorBlockAccessMixins {
#if ENABLE_PLATFORM_EXTENSIONS
    @Invoker("convertAnchorToEntity")
    public abstract void callConvertAnchorToEntity(World world, int i, int j, int k, PulleyTileEntity attachedTileEntityPulley, int iMovementDirection);
#endif
}