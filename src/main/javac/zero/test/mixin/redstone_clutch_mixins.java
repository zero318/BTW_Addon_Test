package zero.test.mixin;

import net.minecraft.src.*;

import btw.block.blocks.*;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Random;

#include "..\feature_flags.h"
#include "..\util.h"

@Mixin(RedstoneClutchBlock.class)
public class RedstoneClutchBlockMixins extends GearBoxBlock {
    
    public RedstoneClutchBlockMixins(int block_id) {
        super(block_id);
    }
    
#if ENABLE_LESS_CRAP_BTW_BLOCK_POWERING
    
    // Prevent clutches getting quasi powered

    @Overwrite
    public void updateTick(World world, int X, int Y, int Z, Random random) {
        this.updateMechPoweredState(world, X, Y, Z, 
            this.isInputtingMechanicalPower(world, X, Y, Z) &&
            !world.isBlockGettingPowered(X, Y, Z) // a Redstone powered gearbox outputs no mechanical power
        );
    }
    
    @Overwrite
    protected boolean isCurrentStateValid(World world, int X, int Y, int Z) {
        //RedstoneClutchBlock self = (RedstoneClutchBlock)(Object)this;
    	return this.isGearBoxOn(world, X, Y, Z) == (
            this.isInputtingMechanicalPower(world, X, Y, Z) &&
            !world.isBlockGettingPowered(X, Y, Z) // a Redstone powered gearbox outputs no mechanical power
        );
	}
#endif
}