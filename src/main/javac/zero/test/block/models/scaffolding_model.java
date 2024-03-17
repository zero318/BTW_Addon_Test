package zero.test.block.model;

import net.minecraft.src.*;

import btw.block.model.BlockModel;
import btw.util.PrimitiveAABBWithBenefits;
import btw.util.PrimitiveQuad;

//import zero.test.block.model.TexturedBox;

#include "..\..\util.h"
#include "..\..\feature_flags.h"

public class ScaffoldingModel extends BlockModel {
    
#define TEXTURE_INDEX_SIDE 0
#define TEXTURE_INDEX_TOP 1
#define TEXTURE_INDEX_BOTTOM 2
    
    @Override
    protected void initModel() {
        // Top
        this.addPrimitive(new PrimitiveAABBWithBenefits(0.0D, 0.875D, 0.0D, 1.0D, 1.0D, 1.0D, TEXTURE_INDEX_TOP));
        
        // Post A
        this.addPrimitive(new PrimitiveAABBWithBenefits(0.0D, 0.0D, 0.0D, 0.125D, 0.875D, 0.125D, TEXTURE_INDEX_SIDE));
        
        // Post B
        this.addPrimitive(new PrimitiveAABBWithBenefits(0.875D, 0.0D, 0.0D, 1.0D, 0.875D, 0.125D, TEXTURE_INDEX_SIDE));
        
        // Post C
        this.addPrimitive(new PrimitiveAABBWithBenefits(0.0D, 0.0D, 0.875D, 0.125D, 0.875D, 1.0D, TEXTURE_INDEX_SIDE));
        
        // Post D
        this.addPrimitive(new PrimitiveAABBWithBenefits(0.875D, 0.0D, 0.875D, 1.0D, 0.875D, 1.0D, TEXTURE_INDEX_SIDE));
    }
}