package zero.test.mixin;

import net.minecraft.src.*;

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
    
@Mixin(BlockRedstoneLogic.class)
public interface IBlockRedstoneLogicAccessMixins {
#if ENABLE_REDSTONE_BUGFIXES
    @Accessor
    public boolean getIsRepeaterPowered();
    
    @Invoker
    public abstract boolean callFunc_96470_c(int meta);
#endif
}
