package zero.test;
import net.minecraft.src.*;

public interface IMovingPlatformEntityMixins {
    public void setBlockId(int blockId);
    public int getBlockId();
    public void setBlockMeta(int blockMeta);
    public int getBlockMeta();
    //public void setStickySides(int sides);
    //public int getStickySides();
    public void storeTileEntity(TileEntity tileEntity);
}
