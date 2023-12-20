package zero.test.block;

import net.minecraft.src.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
public class SlimeBlock extends Block {
    public SlimeBlock(int block_id) {
        super(block_id, Material.grass);
        this.setUnlocalizedName("slime_block");
    }
    public int getMobilityFlag() {
        return 0;
    }
    public boolean isSticky(int X, int Y, int Z, int direction) {
        return true;
    }
    @Override
    public boolean isNormalCube(IBlockAccess blockAccess, int X, int Y, int Z) {
  return true;
 }
    @Environment(EnvType.CLIENT)
 @Override
 public int getRenderBlockPass() {
  return 1;
 }
    @Environment(EnvType.CLIENT)
    public boolean shouldRenderNeighborFullFaceSide(IBlockAccess blockAccess, int X, int Y, int Z, int iNeighborSide) {
  return true;
 }
}
