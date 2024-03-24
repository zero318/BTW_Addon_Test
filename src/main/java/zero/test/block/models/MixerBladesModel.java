package zero.test.block.model;
import net.minecraft.src.*;
import btw.block.model.BlockModel;
import btw.util.PrimitiveAABBWithBenefits;
import btw.util.PrimitiveQuad;
public class MixerBladesModel
extends BlockModel
{
    @Override
    protected void initModel() {
        // Top Axle
        this.addPrimitive(
            new PrimitiveAABBWithBenefits(
                0.375D, 0.9375D, 0.375D,
                0.625D, 0.999D, 0.625D, // Y coord isn't 1 because IDK how to fix the axle UV
                2
            )
        );
        // Center Bar
        this.addPrimitive(
            new PrimitiveAABBWithBenefits(
                0.4375D, 0.1875D, 0.4375D,
                0.5625D, 0.9375D, 0.5625D,
                3
            )
        );
        // Blade 1
        this.addPrimitive(
            new PrimitiveQuad(
                Vec3.createVectorHelper(1.0D, 0.75D, 1.0D),
                Vec3.createVectorHelper(1.0D, 0.75D, 0.0D),
                Vec3.createVectorHelper(0.0D, 0.75D, 0.0D),
                Vec3.createVectorHelper(0.0D, 0.75D, 1.0D)
            ).setIconIndex(0)
        );
        // Blade 2
        this.addPrimitive(
            new PrimitiveQuad(
                Vec3.createVectorHelper(1.0D, 0.5625D, 1.0D),
                Vec3.createVectorHelper(1.0D, 0.5625D, 0.0D),
                Vec3.createVectorHelper(0.0D, 0.5625D, 0.0D),
                Vec3.createVectorHelper(0.0D, 0.5625D, 1.0D)
            ).setIconIndex(1)
        );
        // Blade 3
        this.addPrimitive(
            new PrimitiveQuad(
                Vec3.createVectorHelper(1.0D, 0.375D, 1.0D),
                Vec3.createVectorHelper(1.0D, 0.375D, 0.0D),
                Vec3.createVectorHelper(0.0D, 0.375D, 0.0D),
                Vec3.createVectorHelper(0.0D, 0.375D, 1.0D)
            ).setIconIndex(0)
        );
    }
}
