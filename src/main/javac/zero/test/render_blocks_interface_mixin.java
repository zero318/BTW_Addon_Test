package zero.test;

import net.minecraft.src.Block;

public interface IRenderBlocksMixins {
    public void renderTorchForRedstoneLogic(Block block, double X, double Y, double Z, int meta);
}