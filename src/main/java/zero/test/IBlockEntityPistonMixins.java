package zero.test;
public interface IBlockEntityPistonMixins {
    public long getLastTicked();
    public boolean hasLargeCenterHardPointToFacing(int X, int Y, int Z, int direction, boolean ignore_transparency);
}
