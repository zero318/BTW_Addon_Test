package zero.test.mixin;
import net.minecraft.src.Block;
import net.minecraft.src.World;
import net.minecraft.src.BlockPistonBase;
import net.minecraft.src.*;
import btw.block.blocks.PistonBlockBase;
import btw.block.blocks.PistonBlockMoving;
import btw.item.util.ItemUtils;
import btw.AddonHandler;
import btw.BTWAddon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import zero.test.IBlockMixins;
import zero.test.mixin.IPistonBaseAccessMixins;
import zero.test.IWorldMixins;
import zero.test.IBlockEntityPistonMixins;
import java.util.List;
// Block piston reactions
//#define getInputSignal(...) func_94482_f(__VA_ARGS__)
@Mixin(BlockPistonMoving.class)
public class BlockPistonMovingMixins {
    //@Override
    public boolean triggersBuddy() {
        return false;
    }
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB maskBox, List list, Entity entity) {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityPiston) {
            ((IBlockEntityPistonMixins)(Object)tileEntity).getCollisionList(maskBox, list);
        }
    }
    public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int x, int y, int z, int direction, boolean ignoreTransparency) {
        TileEntity tileEntity = blockAccess.getBlockTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityPiston) {
            return ((IBlockEntityPistonMixins)(Object)tileEntity).hasLargeCenterHardPointToFacing(x, y, z, direction, ignoreTransparency);
        }
        return false;
    }
}
