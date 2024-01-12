package zero.test;
import net.minecraft.src.Block;
public interface IRenderBlocksMixins {
    public void renderTorchForRedstoneLogic(Block block, double x, double y, double z, int meta);
}
