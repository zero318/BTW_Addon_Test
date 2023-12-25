package zero.test;

public interface IBlockEntityPistonMixins {
    public long getLastTicked();
    public void setLastTicked(long time);
    
    public boolean hasLargeCenterHardPointToFacing(int X, int Y, int Z, int direction, boolean ignore_transparency);
}