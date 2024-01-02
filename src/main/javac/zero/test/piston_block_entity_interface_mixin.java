package zero.test;

public interface IBlockEntityPistonMixins {
    public long getLastTicked();
    public void setLastTicked(long time);
    
    public boolean hasLargeCenterHardPointToFacing(int x, int y, int z, int direction, boolean ignoreTransparency);
}