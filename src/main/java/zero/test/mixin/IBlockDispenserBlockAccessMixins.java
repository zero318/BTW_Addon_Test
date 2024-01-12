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
// Block piston reactions
//#define getInputSignal(...) func_94482_f(__VA_ARGS__)
@Mixin(BlockDispenserBlock.class)
public interface IBlockDispenserBlockAccessMixins {
    @Environment(EnvType.CLIENT)
    @Accessor
    public Icon[] getIconBySideArray();
    @Invoker("consumeFacingBlock")
    public abstract void callConsumeFacingBlock(World world, int x, int y, int z);
    @Invoker("dispenseBlockOrItem")
    public abstract boolean callDispenseBlockOrItem(World world, int x, int y, int z);
}
