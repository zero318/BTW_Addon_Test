package zero.test.mixin;
import net.minecraft.src.*;
import java.util.List;
import btw.block.blocks.BlockDispenserBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import zero.test.IBlockMixins;
// func_96440_m = updateNeighbourForOutputSignal
// func_94487_f = blockIdIsActiveOrInactive
// func_94485_e = getActiveBlockID
// func_94484_i = getInactiveBlockID
// func_96470_c(metadata) = getRepeaterPoweredState(metadata)
// func_94478_d = shouldTurnOn
// func_94488_g = getAlternateSignal
// func_94490_c = isSubtractMode
// func_94491_m = calculateOutputSignal
// func_94483_i_ = __notifyOpposite
// func_94481_j_ = getComparatorDelay
/// func_94482_f = getInputSignal
// func_96476_c = refreshOutputState
//#define getInputSignal(...) func_94482_f(__VA_ARGS__)
@Mixin(BlockDispenserBlock.class)
public interface IBlockDispenserBlockAccessMixins {
    @Environment(EnvType.CLIENT)
    @Accessor
    public Icon[] getIconBySideArray();
    @Invoker("consumeFacingBlock")
    public abstract void callConsumeFacingBlock(World world, int X, int Y, int Z);
    @Invoker("dispenseBlockOrItem")
    public abstract boolean callDispenseBlockOrItem(World world, int X, int Y, int Z);
}
