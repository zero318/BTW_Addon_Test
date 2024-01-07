package zero.test.mixin;

import net.minecraft.src.*;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

#include "..\func_aliases.h"
#include "..\feature_flags.h"
#include "..\util.h"
    
@Mixin(RenderBlocks.class)
public interface IRenderBlocksAccessMixins {
    @Accessor
    public Icon getOverrideBlockTexture();
}