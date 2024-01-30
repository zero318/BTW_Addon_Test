package zero.test.block;

import net.minecraft.src.*;

#include "..\util.h"
#include "..\feature_flags.h"

public class BufferStopBlock extends Block {
    public BufferStopBlock(int blockId) {
        super(blockId, Material.circuits);
        this.setAxesEffectiveOn();
        this.setStepSound(Block.soundWoodFootstep);
        this.setUnlocalizedName("buffer_stop");
    }
    
#if ENABLE_RAIL_BUFFER_STOP

#endif
}