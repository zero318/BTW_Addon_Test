package zero.test.block;

import net.minecraft.src.*;

#include "..\util.h"
#include "..\feature_flags.h"

#define POWERED_META_OFFSET 3

public class PoweredRailBlock extends BlockRailPowered {
    
    public PoweredRailBlock(int blockId) {
        super(blockId);
        this.setPicksEffectiveOn();
        this.setHardness(0.7F);
        this.setStepSound(soundMetalFootstep);
        this.setUnlocalizedName("goldenRail");
    }
    
    public double cartBoostRatio(int meta) {
        return READ_META_FIELD(meta, POWERED) ? 0.06D : 0.0D;
    }
    public double cartSlowdownRatio(int meta) {
        return READ_META_FIELD(meta, POWERED) ? 1.0D : 0.5D;
    }
}