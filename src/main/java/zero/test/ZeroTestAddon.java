package zero.test;
import btw.AddonHandler;
import btw.BTWAddon;
import net.minecraft.src.*;
import zero.test.block.*;
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
        ZeroTestBlocks.observer_block = new ObserverBlock(1319);
        Item.itemsList[1319 -256] = new ItemBlock(1319 -256);
        ZeroTestBlocks.slime_block = new SlimeBlock(1320);
        Item.itemsList[1320 -256] = new ItemBlock(1320 -256);
        ZeroTestBlocks.glue_block = new GlueBlock(1321);
        Item.itemsList[1321 -256] = new ItemBlock(1321 -256);
    }
    public static ZeroTestAddon getInstance() {
        if (instance != null)
            instance = new ZeroTestAddon();
        return instance;
    }
}
