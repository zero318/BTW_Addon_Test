package zero.test.block.model;

import net.minecraft.src.*;

import btw.block.model.BlockModel;
import btw.util.PrimitiveAABBWithBenefits;
import btw.util.PrimitiveQuad;

#include "..\..\util.h"
#include "..\..\feature_flags.h"

#define USE_OVERSIZED_BLADE_POLYGONS 1

public class MixerBladesModel
#if ENABLE_MIXER_BLOCK
extends BlockModel
#endif
{
#if ENABLE_MIXER_BLOCK

#if USE_OVERSIZED_BLADE_POLYGONS
#define BLADE_MIN_COORD 0.0D
#define BLADE_MAX_COORD 1.0D
#else
#define BLADE_MIN_COORD 0.25D
#define BLADE_MAX_COORD 0.75D
#endif

#define TEXTURE_INDEX_BLADE_1 0
#define TEXTURE_INDEX_BLADE_2 1
#define TEXTURE_INDEX_AXLE 2
#define TEXTURE_INDEX_BAR 3
    
    @Override
    protected void initModel() {
        // Top Axle
        this.addPrimitive(
            new PrimitiveAABBWithBenefits(
                0.375D, 0.9375D, 0.375D,
                0.625D, 0.999D, 0.625D, // Y coord isn't 1 because IDK how to fix the axle UV
                TEXTURE_INDEX_AXLE
            )
        );
        // Center Bar
        this.addPrimitive(
            new PrimitiveAABBWithBenefits(
                0.4375D, 0.1875D, 0.4375D,
                0.5625D, 0.9375D, 0.5625D, 
                TEXTURE_INDEX_BAR
            )
        );
        // Blade 1
        this.addPrimitive(
            new PrimitiveQuad(
                Vec3.createVectorHelper(BLADE_MAX_COORD, 0.75D, BLADE_MAX_COORD),
                Vec3.createVectorHelper(BLADE_MAX_COORD, 0.75D, BLADE_MIN_COORD),
                Vec3.createVectorHelper(BLADE_MIN_COORD, 0.75D, BLADE_MIN_COORD),
                Vec3.createVectorHelper(BLADE_MIN_COORD, 0.75D, BLADE_MAX_COORD)
            ).setIconIndex(TEXTURE_INDEX_BLADE_1)
        );
        // Blade 2
        this.addPrimitive(
            new PrimitiveQuad(
                Vec3.createVectorHelper(BLADE_MAX_COORD, 0.5625D, BLADE_MAX_COORD),
                Vec3.createVectorHelper(BLADE_MAX_COORD, 0.5625D, BLADE_MIN_COORD),
                Vec3.createVectorHelper(BLADE_MIN_COORD, 0.5625D, BLADE_MIN_COORD),
                Vec3.createVectorHelper(BLADE_MIN_COORD, 0.5625D, BLADE_MAX_COORD)
            ).setIconIndex(TEXTURE_INDEX_BLADE_2)
        );
        // Blade 3
        this.addPrimitive(
            new PrimitiveQuad(
                Vec3.createVectorHelper(BLADE_MAX_COORD, 0.375D, BLADE_MAX_COORD),
                Vec3.createVectorHelper(BLADE_MAX_COORD, 0.375D, BLADE_MIN_COORD),
                Vec3.createVectorHelper(BLADE_MIN_COORD, 0.375D, BLADE_MIN_COORD),
                Vec3.createVectorHelper(BLADE_MIN_COORD, 0.375D, BLADE_MAX_COORD)
            ).setIconIndex(TEXTURE_INDEX_BLADE_1)
        );
    }
#endif
}