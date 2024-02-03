package zero.test.block.model;

import net.minecraft.src.*;

import btw.block.model.BlockModel;
import btw.util.PrimitiveAABBWithBenefits;
import btw.util.PrimitiveQuad;

import zero.test.block.model.TexturedBox;

#include "..\..\util.h"
#include "..\..\feature_flags.h"

public class BufferStopModel extends BlockModel {
    
    
#define TEXTURE_INDEX_BASE_PLATE 0
#define TEXTURE_INDEX_SIDE_BOARDS 1
#define TEXTURE_INDEX_BUMPERS 2
    
    @Override
    protected void initModel() {
#if !ENABLE_TEXTURED_BOX
        // Base Plate
        this.addPrimitive(new PrimitiveAABBWithBenefits(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D, TEXTURE_INDEX_BASE_PLATE));
        
        // Side Board A
        this.addPrimitive(new PrimitiveAABBWithBenefits(0.0D, 0.125D, 0.0D, 0.25D, 0.5625D, 1.0D, TEXTURE_INDEX_SIDE_BOARDS));
        
        // Side Board B
        this.addPrimitive(new PrimitiveAABBWithBenefits(0.75D, 0.125D, 0.0D, 1.0D, 0.5625D, 1.0D, TEXTURE_INDEX_SIDE_BOARDS));
        
        // Top Board
        this.addPrimitive(new PrimitiveAABBWithBenefits(0.0D, 0.5625D, 0.0D, 1.0D, 1.0D, 0.5D, TEXTURE_INDEX_SIDE_BOARDS));
        
        // Bumper A
        this.addPrimitive(new PrimitiveAABBWithBenefits(0.125D, 0.625D, -0.0625D, 0.375D, 0.875D, 0.0D, TEXTURE_INDEX_BUMPERS));
        
        // Bumper B
        this.addPrimitive(new PrimitiveAABBWithBenefits(0.625D, 0.625D, -0.0625D, 0.875D, 0.875D, 0.0D, TEXTURE_INDEX_BUMPERS));
#else
        // Base Plate
        this.addPrimitive(
            new TexturedBox(
                0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D, TEXTURE_INDEX_BASE_PLATE,
                0.0F, 0.0F, 16.0F, 16.0F, CULL_DIRECTION_DOWN, TEXTURE_ROTATION_NONE,
                0.0F, 0.0F, 16.0F, 16.0F, CULL_DIRECTION_NONE, TEXTURE_ROTATION_NONE,
                0.0F, 14.0F, 16.0F, 16.0F, CULL_DIRECTION_NORTH, TEXTURE_ROTATION_NONE,
                0.0F, 14.0F, 16.0F, 16.0F, CULL_DIRECTION_SOUTH, TEXTURE_ROTATION_NONE,
                0.0F, 14.0F, 16.0F, 16.0F, CULL_DIRECTION_WEST, TEXTURE_ROTATION_NONE,
                0.0F, 14.0F, 16.0F, 16.0F, CULL_DIRECTION_EAST, TEXTURE_ROTATION_NONE
            )
        );
        
        // Side Board A
        this.addPrimitive(
            new TexturedBox(
                0.0D, 0.125D, 0.0D, 0.25D, 0.5625D, 1.0D, TEXTURE_INDEX_SIDE_BOARDS,
                0.0F, 0.0F, 0.0F, 0.0F, CULL_DIRECTION_ALL, TEXTURE_ROTATION_NONE,
                0.0F, 0.0F, 4.0F, 16.0F, CULL_DIRECTION_NONE, TEXTURE_ROTATION_NONE,
                12.0F, 7.0F, 16.0F, 14.0F, CULL_DIRECTION_NORTH, TEXTURE_ROTATION_NONE,
                0.0F, 7.0F, 4.0F, 14.0F, CULL_DIRECTION_SOUTH, TEXTURE_ROTATION_NONE,
                0.0F, 7.0F, 16.0F, 14.0F, CULL_DIRECTION_WEST, TEXTURE_ROTATION_NONE,
                0.0F, 7.0F, 16.0F, 14.0F, CULL_DIRECTION_NONE, TEXTURE_ROTATION_NONE
            )
        );
        
        // Side Board B
        this.addPrimitive(
            new TexturedBox(
                0.75D, 0.125D, 0.0D, 1.0D, 0.5625D, 1.0D, TEXTURE_INDEX_SIDE_BOARDS,
                0.0F, 0.0F, 0.0F, 0.0F, CULL_DIRECTION_ALL, TEXTURE_ROTATION_NONE,
                12.0F, 0.0F, 16.0F, 16.0F, CULL_DIRECTION_NONE, TEXTURE_ROTATION_NONE,
                0.0F, 7.0F, 16.0F, 14.0F, CULL_DIRECTION_NORTH, TEXTURE_ROTATION_NONE,
                12.0F, 7.0F, 16.0F, 14.0F, CULL_DIRECTION_SOUTH, TEXTURE_ROTATION_NONE,
                0.0F, 7.0F, 16.0F, 14.0F, CULL_DIRECTION_NONE, TEXTURE_ROTATION_NONE,
                0.0F, 7.0F, 16.0F, 14.0F, CULL_DIRECTION_EAST, TEXTURE_ROTATION_NONE
            )
        );
        
        // Top Board
        this.addPrimitive(
            new TexturedBox(
                0.0D, 0.5625D, 0.0D, 1.0D, 1.0D, 0.5D, TEXTURE_INDEX_SIDE_BOARDS,
                0.0F, 8.0F, 16.0F, 16.0F, CULL_DIRECTION_NONE, TEXTURE_ROTATION_NONE,
                0.0F, 0.0F, 16.0F, 8.0F, CULL_DIRECTION_UP, TEXTURE_ROTATION_NONE,
                0.0F, 0.0F, 16.0F, 7.0F, CULL_DIRECTION_NORTH, TEXTURE_ROTATION_NONE,
                0.0F, 0.0F, 16.0F, 7.0F, CULL_DIRECTION_NONE, TEXTURE_ROTATION_NONE,
                0.0F, 0.0F, 8.0F, 7.0F, CULL_DIRECTION_WEST, TEXTURE_ROTATION_NONE,
                8.0F, 0.0F, 16.0F, 7.0F, CULL_DIRECTION_EAST, TEXTURE_ROTATION_NONE
            )
        );
        
        // Bumper A
        this.addPrimitive(
            new TexturedBox(
                0.125D, 0.625D, -0.0625D, 0.375D, 0.875D, 0.0D, TEXTURE_INDEX_BUMPERS,
                0.0F, 0.0F, 4.0F, 1.0F, CULL_DIRECTION_NORTH, TEXTURE_ROTATION_NONE,
                0.0F, 0.0F, 4.0F, 1.0F, CULL_DIRECTION_NORTH, TEXTURE_ROTATION_NONE,
                10.0F, 2.0F, 14.0F, 6.0F, CULL_DIRECTION_NORTH, TEXTURE_ROTATION_NONE,
                0.0F, 0.0F, 0.0F, 0.0F, CULL_DIRECTION_ALL, TEXTURE_ROTATION_NONE,
                0.0F, 0.0F, 1.0F, 4.0F, CULL_DIRECTION_NORTH, TEXTURE_ROTATION_NONE,
                0.0F, 0.0F, 1.0F, 4.0F, CULL_DIRECTION_NORTH, TEXTURE_ROTATION_NONE
            )
        );
        
        // Bumper B
        this.addPrimitive(
            new TexturedBox(
                0.625D, 0.625D, -0.0625D, 0.875D, 0.875D, 0.0D, TEXTURE_INDEX_BUMPERS,
                0.0F, 0.0F, 4.0F, 1.0F, CULL_DIRECTION_NORTH, TEXTURE_ROTATION_NONE,
                0.0F, 0.0F, 4.0F, 1.0F, CULL_DIRECTION_NORTH, TEXTURE_ROTATION_NONE,
                10.0F, 2.0F, 14.0F, 6.0F, CULL_DIRECTION_NORTH, TEXTURE_ROTATION_NONE,
                0.0F, 0.0F, 0.0F, 0.0F, CULL_DIRECTION_ALL, TEXTURE_ROTATION_NONE,
                0.0F, 0.0F, 1.0F, 4.0F, CULL_DIRECTION_NORTH, TEXTURE_ROTATION_NONE,
                0.0F, 0.0F, 1.0F, 4.0F, CULL_DIRECTION_NORTH, TEXTURE_ROTATION_NONE
            )
        );
#endif
    }
}