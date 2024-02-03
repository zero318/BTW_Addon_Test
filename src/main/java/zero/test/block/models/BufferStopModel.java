package zero.test.block.model;
import net.minecraft.src.*;
import btw.block.model.BlockModel;
import btw.util.PrimitiveAABBWithBenefits;
import btw.util.PrimitiveQuad;
import zero.test.block.model.TexturedBox;
// Block piston reactions

public class BufferStopModel extends BlockModel {
    @Override
    protected void initModel() {
        // Base Plate
        this.addPrimitive(new PrimitiveAABBWithBenefits(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D, 0));
        // Side Board A
        this.addPrimitive(new PrimitiveAABBWithBenefits(0.0D, 0.125D, 0.0D, 0.25D, 0.5625D, 1.0D, 1));
        // Side Board B
        this.addPrimitive(new PrimitiveAABBWithBenefits(0.75D, 0.125D, 0.0D, 1.0D, 0.5625D, 1.0D, 1));
        // Top Board
        this.addPrimitive(new PrimitiveAABBWithBenefits(0.0D, 0.5625D, 0.0D, 1.0D, 1.0D, 0.5D, 1));
        // Bumper A
        this.addPrimitive(new PrimitiveAABBWithBenefits(0.125D, 0.625D, -0.0625D, 0.375D, 0.875D, 0.0D, 2));
        // Bumper B
        this.addPrimitive(new PrimitiveAABBWithBenefits(0.625D, 0.625D, -0.0625D, 0.875D, 0.875D, 0.0D, 2));
    }
}
