package zero.test.mixin;
import net.minecraft.src.*;
import btw.block.blocks.*;
import btw.AddonHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import java.util.Random;
import java.util.List;
import zero.test.IBlockMixins;
@Mixin(CisternBlock.class)
public class CisternBlockMixins {
    @Overwrite(remap=false)
    public void method_413(World world, int X, int Y, int Z, AxisAlignedBB intersect_box, List list, Entity entity) {
     AABBPool pool = AxisAlignedBB.getAABBPool();
        double dX = (double)X;
        double dY = (double)Y;
        double dZ = (double)Z;
        double dX2 = dX + 1.0D;
        double dY2 = dY + 1.0D;
        double dZ2 = dZ + 1.0D;
        pool.getAABB(dX, dY, dZ, dX2, dY + 0.3125D, dZ2).addToListIfIntersects(intersect_box, list);
        pool.getAABB(dX, dY, dZ, dX + 0.125D, dY2, dZ2).addToListIfIntersects(intersect_box, list);
        pool.getAABB(dX, dY, dZ, dX2, dY2, dZ + 0.125D).addToListIfIntersects(intersect_box, list);
        pool.getAABB(dX + 0.875D, dY, dZ, dX2, dY2, dZ2).addToListIfIntersects(intersect_box, list);
        pool.getAABB(dX, dY, dZ + 0.875D, dX2, dY2, dZ2).addToListIfIntersects(intersect_box, list);
    }
}
