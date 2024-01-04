package zero.test.mixin;

import net.minecraft.src.*;

import btw.block.blocks.MetalSpikeBlock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

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
    
    @Inject(
        method = "updateTick",
        at = @At("TAIL")
    )
    public void updateTick_inject(World world, int X, int Y, int Z, Random random) {
        // The block can already be set to air
        // when updated normally, so make sure
        // that hasn't happened before doing
        // anything with power states.
        if (world.getBlockId(X, Y, Z) != 0) {
            MetalSpikeBlock self = (MetalSpikeBlock)(Object)this;
            
            int meta = world.getBlockMetadata(X, Y, Z);
        
            int direction = READ_META_FIELD(meta, DIRECTION);
            
            int input_power = 0;
            
            int i = 0;
            do {
                if (i != direction) {
                    int block_power = world.isBlockProvidingPowerTo(
                        X + Facing.offsetsXForSide[i],
                        Y + Facing.offsetsYForSide[i],
                        Z + Facing.offsetsZForSide[i],
                        OPPOSITE_DIRECTION(i)
                    );
                }
            } while (++i < 6);
            
            X += Facing.offsetsXForSide[direction];
            Y += Facing.offsetsYForSide[direction];
            Z += Facing.offsetsZForSide[direction];
            
            Block neighbor_block = Block.blocksList[world.getBlockId(X, Y, Z)];
            if (!BLOCK_IS_AIR(neighbor_block)) {
                neighbor_block.onNeighborBlockChange(world, X, Y, Z, self.blockID);
            }
            world.notifyBlocksOfNeighborChange(X, Y, Z, self.blockID, OPPOSITE_DIRECTION(direction));
        }
    }
    
#if ENABLE_BETTER_REDSTONE_WIRE_CONNECTIONS
    public boolean canRedstoneConnectToSide(IBlockAccess block_access, int X, int Y, int Z, int flat_direction) {
        return true;
    }
#endif
#endif
}