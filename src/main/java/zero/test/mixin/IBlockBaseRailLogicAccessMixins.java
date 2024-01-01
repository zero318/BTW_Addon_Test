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
@Mixin(BlockBaseRailLogic.class)
public interface IBlockBaseRailLogicAccessMixins {
    @Accessor
    public World getLogicWorld();
    @Accessor
    public int getRailX();
    @Accessor
    public int getRailY();
    @Accessor
    public int getRailZ();
    @Accessor
    public boolean getIsStraightRail();
    @Accessor
    public List getRailChunkPosition();
    @Invoker("canConnectFrom")
    public abstract boolean hasNeighborRail(int X, int Y, int Z);
    @Invoker("setBasicRail")
    public abstract void updateConnections(int par1);
    @Invoker("getRailLogic")
    public abstract BlockBaseRailLogic getRail(ChunkPosition par1ChunkPosition);
    @Invoker("refreshConnectedTracks")
    public abstract void removeSoftConnections();
    @Invoker("canConnectTo")
    public abstract boolean callCanConnectTo(BlockBaseRailLogic par1BlockBaseRailLogic);
    @Invoker("connectToNeighbor")
    public abstract void connectTo(BlockBaseRailLogic par1BlockBaseRailLogic);
}
