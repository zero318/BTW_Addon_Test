package zero.test.block;

import net.minecraft.src.*;

#include "..\feature_flags.h"
#include "..\util.h"

public class DropperShim
#if ENABLE_NERFED_DROPPER
extends BlockDropper
#endif
{
#if ENABLE_NERFED_DROPPER
    public DropperShim(int blockId) {
        super(blockId);
    }
#endif
}