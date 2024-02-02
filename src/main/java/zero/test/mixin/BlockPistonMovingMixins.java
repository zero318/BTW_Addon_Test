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
import java.util.ArrayList;
// Block piston reactions
//#define getInputSignal(...) func_94482_f(__VA_ARGS__)
@Mixin(PistonBlockMoving.class)
public class BlockPistonMovingMixins extends BlockPistonMoving {
    public BlockPistonMovingMixins(int block_id) {
        super(block_id);
    }
    @Override
    public boolean triggersBuddy() {
        return false;
    }
    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB maskBox, List list, Entity entity) {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityPiston) {
            ((IBlockEntityPistonMixins)(Object)tileEntity).getCollisionList(maskBox, list, entity);
        }
    }
    @Override
    public boolean hasSmallCenterHardPointToFacing(IBlockAccess blockAccess, int x, int y, int z, int direction, boolean ignoreTransparency) {
        TileEntity tileEntity = blockAccess.getBlockTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityPiston) {
            return ((IBlockEntityPistonMixins)(Object)tileEntity).hasSmallCenterHardPointToFacing(x, y, z, direction, ignoreTransparency);
        }
        return false;
    }
    @Override
    public boolean hasCenterHardPointToFacing(IBlockAccess blockAccess, int x, int y, int z, int direction, boolean ignoreTransparency) {
        TileEntity tileEntity = blockAccess.getBlockTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityPiston) {
            return ((IBlockEntityPistonMixins)(Object)tileEntity).hasCenterHardPointToFacing(x, y, z, direction, ignoreTransparency);
        }
        return false;
    }
    @Override
    public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int x, int y, int z, int direction, boolean ignoreTransparency) {
        TileEntity tileEntity = blockAccess.getBlockTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityPiston) {
            return ((IBlockEntityPistonMixins)(Object)tileEntity).hasLargeCenterHardPointToFacing(x, y, z, direction, ignoreTransparency);
        }
        return false;
    }
    // If the whole point of this is to test the block bounds,
    // maybe overriding to use raw collision isn't a good idea?
    /*
    @Override
    public MovingObjectPosition collisionRayTraceVsBlockBounds(World world, int x, int y, int z, Vec3 startRay, Vec3 endRay) {
        TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityPiston) {
            List<AxisAlignedBB> collisionList = new ArrayList();
            
            AxisAlignedBB fakeMask = AxisAlignedBB.getAABBPool().getAABB(x - 1.0D, y - 1.0D, z - 1.0D, x + 2.0D, y + 2.0D, z + 2.0D);
            ((IBlockEntityPistonMixins)(Object)tileEntity).getCollisionList(fakeMask, collisionList);
            
            for (AxisAlignedBB collisionBox : collisionList) {
                MovingObjectPosition collisionPoint = collisionBox.calculateIntercept(startRay, endRay);
                if (collisionPoint != null) {
                    collisionPoint.blockX = x;
                    collisionPoint.blockY = y;
                    collisionPoint.blockZ = z;
                    return collisionPoint;
                }
            }
        }
        return null;
    }
    */
    // This fixes the selection box of retracting pistons
    // to not derp out and extend behind the base.
    @Overwrite
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(IBlockAccess blockAccess, int x, int y, int z) {
        TileEntity tileEntity = blockAccess.getBlockTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityPiston) {
            AxisAlignedBB boundingBox = ((IBlockEntityPistonMixins)(Object)tileEntity).getBlockBoundsFromPoolBasedOnState();
            if (boundingBox != null) {
                return boundingBox;
            }
        }
        // If the tile entity isn't loaded somehow on the client
        // then returning null from this can cause a crash
        // since some code directly uses the output of getBlockBoundsFromPoolBasedOnState
        // without a null check. This situation shouldn't be possible,
        // but sock had a crash that is *likely* this.
        //
        // TODO: Remove the warning message once it's confirmed
        // whether or not this was a freak accident
        AddonHandler.logMessage("ZASSERT: Moving piston entity not loaded at "+x+" "+y+" "+z+", block bounds incorrect");
        return super.getBlockBoundsFromPoolBasedOnState(blockAccess, x, y, z);
    }
}
