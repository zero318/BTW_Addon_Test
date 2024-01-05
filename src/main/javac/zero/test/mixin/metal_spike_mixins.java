package zero.test.mixin;

import net.minecraft.src.*;

import btw.AddonHandler;
import btw.block.blocks.MetalSpikeBlock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

import zero.test.IWorldMixins;

import java.util.Random;

#include "..\feature_flags.h"
#include "..\util.h"

#define DIRECTION_META_OFFSET 0
#define POWERED_META_OFFSET 3

@Mixin(MetalSpikeBlock.class)
public class MetalSpikeBlockMixins {
#if ENABLE_CONDUCTIVE_METAL_SPIKES
    public boolean canProvidePower() {
        return true;
    }
    
    public void updateNeighborsInDirection(World world, int X, int Y, int Z, int direction) {
        X += Facing.offsetsXForSide[direction];
        Y += Facing.offsetsYForSide[direction];
        Z += Facing.offsetsZForSide[direction];
        
        MetalSpikeBlock self = (MetalSpikeBlock)(Object)this;
        
        Block neighbor_block = Block.blocksList[world.getBlockId(X, Y, Z)];
        if (!BLOCK_IS_AIR(neighbor_block)) {
            neighbor_block.onNeighborBlockChange(world, X, Y, Z, self.blockID);
        }
        world.notifyBlocksOfNeighborChange(X, Y, Z, self.blockID, OPPOSITE_DIRECTION(direction));
    }
    
    public void refreshOutputState(World world, int X, int Y, int Z) {
        int meta = world.getBlockMetadata(X, Y, Z);
            
        int direction = READ_META_FIELD(meta, DIRECTION);
        
        boolean is_receiving_power = ((IWorldMixins)world).getBlockStrongPowerInputExceptFacing(X, Y, Z, direction) > 0;
        
        //AddonHandler.logMessage("SpikeUpdate: "+meta+" "+is_receiving_power);
        
        if (is_receiving_power != READ_META_FIELD(meta, POWERED)) {
            
            // 
            world.setBlockMetadataWithNotify(X, Y, Z, meta ^ 8, UPDATE_CLIENTS | UPDATE_SUPPRESS_LIGHT);
            
            this.updateNeighborsInDirection(world, X, Y, Z, direction);
        }
    }
    
    /*
    @Inject(
        method = "onBlockAdded",
        at = @At("TAIL")
    )
    */
    public void onBlockAdded(World world, int X, int Y, int Z) {
        this.refreshOutputState(world, X, Y, Z);
    }
    
    /*
    @Inject(
        method = "breakBlock",
        at = @At("TAIL")
    )
    */
    public void breakBlock(World world, int X, int Y, int Z, int idk, int prev_meta) {
        this.updateNeighborsInDirection(world, X, Y, Z, READ_META_FIELD(prev_meta, DIRECTION));
    }
    
    @Inject(
        method = "onNeighborBlockChange",
        at = @At("TAIL")
    )
    public void onNeighborBlockChange_inject(World world, int X, int Y, int Z, int neighbor_id, CallbackInfo info) {
        // The block can already be set to air
        // when updated normally since this is
        // only added to the tail of the function
        // and is run after support checks, so
        // make sure that hasn't happened before
        // doing anything with power states.
        //
        // TODO: Do indirect neighbors still need
        // to be notified, or would removing the
        // block already take care of that?
        if (world.getBlockId(X, Y, Z) != 0) {
            this.refreshOutputState(world, X, Y, Z);
        }
    }
    
    public int isProvidingStrongPower(IBlockAccess block_access, int X, int Y, int Z, int side) {
        // This prevents supplying power to redstone wire
        // via the output block
        if (Block.redstoneWire.canProvidePower()) {
            return isProvidingWeakPower(block_access, X, Y, Z, side);
        }
        return 0;
    }
    
    public int isProvidingWeakPower(IBlockAccess block_access, int X, int Y, int Z, int side) {
        int meta = block_access.getBlockMetadata(X, Y, Z);
        //AddonHandler.logMessage("SpikeUpdate: "+meta+" "+side);
        int direction;
        if (
            READ_META_FIELD(meta, POWERED) &&
            OPPOSITE_DIRECTION(side) == (direction = READ_META_FIELD(meta, DIRECTION))
        ) {
            // Only check for strong power to prevent getting
            // powered by a weakly powered block
            return ((IWorldMixins)block_access).getBlockStrongPowerInputExceptFacing(X, Y, Z, direction);
        }
        return 0;
    }
    
#if ENABLE_BETTER_REDSTONE_WIRE_CONNECTIONS
    public boolean canRedstoneConnectToSide(IBlockAccess block_access, int X, int Y, int Z, int flat_direction) {
        return true;
    }
#endif
#endif
}