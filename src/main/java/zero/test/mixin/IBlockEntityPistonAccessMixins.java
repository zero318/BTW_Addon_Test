package zero.test.mixin;
import net.minecraft.src.*;
import java.util.List;
import btw.block.blocks.PistonBlockBase;
import btw.block.blocks.PistonBlockMoving;
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
@Mixin(TileEntityPiston.class)
public interface IBlockEntityPistonAccessMixins {
    @Accessor
    public List getPushedObjects();
    @Accessor
    public float getProgress();
    @Accessor
    public void setProgress(float value);
    @Accessor
    public float getLastProgress();
    @Accessor
    public void setLastProgress(float value);
    @Invoker("destroyAndDropIfShoveled")
    public abstract boolean callDestroyAndDropIfShoveled();
    @Invoker("preBlockPlaced")
    public abstract void callPreBlockPlaced();
    @Invoker("attemptToPackItems")
    public abstract void callAttemptToPackItems();
    @Invoker("updatePushedObjects")
    public abstract void callUpdatePushedObjects(float progress, float par2);
}
