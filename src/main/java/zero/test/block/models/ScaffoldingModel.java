package zero.test.block.model;
import net.minecraft.src.*;
import btw.block.model.BlockModel;
import btw.util.PrimitiveAABBWithBenefits;
import btw.util.PrimitiveQuad;
//import zero.test.block.model.TexturedBox;

public class ScaffoldingModel extends BlockModel {
    @Override
    protected void initModel() {
        // Top
        this.addPrimitive(new PrimitiveAABBWithBenefits(0.0D, 0.875D, 0.0D, 1.0D, 1.0D, 1.0D, 1));
        // Post A
        this.addPrimitive(new PrimitiveAABBWithBenefits(0.0D, 0.0D, 0.0D, 0.125D, 0.875D, 0.125D, 0));
        // Post B
        this.addPrimitive(new PrimitiveAABBWithBenefits(0.875D, 0.0D, 0.0D, 1.0D, 0.875D, 0.125D, 0));
        // Post C
        this.addPrimitive(new PrimitiveAABBWithBenefits(0.0D, 0.0D, 0.875D, 0.125D, 0.875D, 1.0D, 0));
        // Post D
        this.addPrimitive(new PrimitiveAABBWithBenefits(0.875D, 0.0D, 0.875D, 1.0D, 0.875D, 1.0D, 0));
    }
}
