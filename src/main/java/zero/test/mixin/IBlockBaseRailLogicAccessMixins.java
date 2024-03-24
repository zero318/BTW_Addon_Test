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
import java.util.List;
import zero.test.IBlockMixins;
//#define getInputSignal(...) func_94482_f(__VA_ARGS__)
@Mixin(BlockBaseRailLogic.class)
public interface IBlockBaseRailLogicAccessMixins {
    @Invoker("refreshConnectedTracks")
    public abstract void removeSoftConnections();
    @Invoker("canConnectTo")
    public abstract boolean callCanConnectTo(BlockBaseRailLogic par1BlockBaseRailLogic);
    @Invoker("connectToNeighbor")
    public abstract void connectTo(BlockBaseRailLogic par1BlockBaseRailLogic);
}
