package zero.test;
import btw.AddonHandler;
import btw.BTWAddon;
import net.minecraft.src.*;
import zero.test.block.CUDBlock;
import zero.test.block.ZeroTestBlocks;

public class ZeroTestAddon extends BTWAddon {
    private static ZeroTestAddon instance;
    private ZeroTestAddon() {
        super("Zero Test Addon", "0.0.1", "ZeroTest");
    }
    @Override
    public void initialize() {
        ZeroTestBlocks.cud_block = new CUDBlock(1318);
        Item.itemsList[1318 -256] = new ItemBlock(1318 -256);
    }
    public static ZeroTestAddon getInstance() {
        if (instance != null)
            instance = new ZeroTestAddon();
        return instance;
    }
}
