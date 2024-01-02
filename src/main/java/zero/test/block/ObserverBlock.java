
package zero.test.block;
import btw.client.fx.BTWEffectManager;
import btw.util.MiscUtils;
import btw.world.util.BlockPos;
import btw.block.blocks.BuddyBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.src.*;
import btw.AddonHandler;
import java.util.Random;
public class ObserverBlock extends BuddyBlock {
    public ObserverBlock(int block_id) {
        super(block_id);
        this.setUnlocalizedName("observer");
        this.setTickRandomly(false);
    }
    @Override
    public int tickRate(World world) {
        return 2;
    }
    @Override
    public boolean triggersBuddy() {
        return true;
    }
    @Override
    public void onBlockAdded(World world, int X, int Y, int Z) {
        // HACK: 
        // Java doesn't have super.super, so there's no way of
        // calling Block.onBlockAdded without going through
        // BuddyBlock.onBlockAdded, which schedules an update.
        // So instead we're just assuming that Block.onBlockAdded
        // is still empty and doing nothing.
    }
    @Override
    public void onNeighborBlockChange(World world, int X, int Y, int Z, int neighbor_id) {
    }
    public int updateShape(World world, int X, int Y, int Z, int direction, int meta) {
        int update_direction = (((meta)>>>1));
        if (update_direction == ((direction)^1)) {
            if (!world.isUpdateScheduledForBlock(X, Y, Z, this.blockID)) {
                world.scheduleBlockUpdate(X, Y, Z, this.blockID, 2);
            }
        } //else {
            //AddonHandler.logMessage("Observer fail "+OPPOSITE_DIRECTION(direction)+" != "+update_direction);
        //}
        return meta;
    }
    @Override
    public void updateTick(World world, int X, int Y, int Z, Random random) {
        int meta = world.getBlockMetadata(X, Y, Z);
        if (((((meta)&1)!=0))) {
            world.setBlockMetadataWithClient(X, Y, Z, (((meta)&14)));
        } else {
            world.setBlockMetadataWithClient(X, Y, Z, (((meta)|1)));
            world.scheduleBlockUpdate(X, Y, Z, this.blockID, 2);
        }
        //notifyNeigborsToFacingOfPowerChange(world, X, Y, Z, READ_META_FIELD(meta, DIRECTION));
        int direction = (((meta)>>>1));
        X += Facing.offsetsXForSide[direction];
        Y += Facing.offsetsYForSide[direction];
        Z += Facing.offsetsZForSide[direction];
        int neighbor_id = world.getBlockId(X, Y, Z);
        Block neighbor_block = Block.blocksList[neighbor_id];
        if (!((neighbor_block)==null)) {
            neighbor_block.onNeighborBlockChange(world, X, Y, Z, this.blockID);
        }
        world.notifyBlocksOfNeighborChange(X, Y, Z, this.blockID, ((direction)^1));
    }
    // This just gets rid of the clicking sound
    @Override
    public void setBlockRedstoneOn(World world, int X, int Y, int Z, boolean is_activated) {
        if (is_activated != isRedstoneOn(world, X, Y, Z)) {
            int meta = world.getBlockMetadata(X, Y, Z);
            if (is_activated) {
                meta |= 1;
            }
            else {
                meta &= ~1;
            }
            world.setBlockMetadataWithClient(X, Y, Z, meta);
            // only notify on the output side to prevent weird shit like doors auto-closing when the block
            // goes off
            int direction = this.getFacing(world, X, Y, Z);
            notifyNeigborsToFacingOfPowerChange(world, X, Y, Z, direction);
            // the following forces a re-render (for texture change)
            world.markBlockRangeForRenderUpdate(X, Y, Z, X, Y, Z);
        }
    }
    /*@Override
    public int onPreBlockPlacedByPiston(World world, int X, int Y, int Z, int meta, int direction) {
        return meta;
    }*/
    // Maybe this will prevent it getting stuck on when moved?
    @Override
    public int adjustMetadataForPistonMove(int meta) {
        return (((meta)&14));
    }
    @Environment(EnvType.CLIENT)
    protected Icon texture_back_off;
    @Environment(EnvType.CLIENT)
    protected Icon texture_back_on;
    @Environment(EnvType.CLIENT)
    protected Icon texture_front;
    @Environment(EnvType.CLIENT)
    protected Icon texture_side;
    @Environment(EnvType.CLIENT)
    protected Icon texture_top;
    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        super.registerIcons(register);
        this.texture_back_off = register.registerIcon("observer_back");
        this.texture_back_on = register.registerIcon("observer_back_on");
        this.texture_front = register.registerIcon("observer_front");
        this.texture_side = register.registerIcon("observer_side");
        this.texture_top = register.registerIcon("observer_top");
    }
    @Override
    @Environment(EnvType.CLIENT)
    public Icon getIcon(int side, int meta) {
        // Why are these different from the regular block texture directions?
        switch (side) {
            case 0:
            case 1:
                return this.texture_top;
            case 2:
                return this.texture_back_off;
            case 3:
                return this.texture_front;
            default:
                return this.texture_side;
        }
    }
    @Override
    @Environment(EnvType.CLIENT)
    public Icon getBlockTexture(IBlockAccess block_access, int X, int Y, int Z, int side) {
        int meta = block_access.getBlockMetadata(X, Y, Z);
        int facing = (((meta)>>>1));
        if (facing == side) {
            return ((((meta)&1)!=0))
                    ? this.texture_back_on
                    : this.texture_back_off;
        }
        if (facing == ((side)^1)) {
            return this.texture_front;
        }
        if (((facing)&~1) != 0x0) {
            return ((side)&~1) == 0x0 ? this.texture_top : this.texture_side;
        }
        // When facing up, the non-arrow side should be on E/W
        return ((side)&~1) == 0x2 ? this.texture_top : this.texture_side;
    }
    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks render, int X, int Y, int Z) {
        // IDK why these rotation values work
        switch ((((render.blockAccess.getBlockMetadata(X, Y, Z))>>>1))) {
            case 0:
                render.setUVRotateEast(3);
                render.setUVRotateWest(3);
                render.setUVRotateNorth(1);
                render.setUVRotateSouth(2);
                render.setUVRotateTop(3);
                render.setUVRotateBottom(3);
                break;
            case 1:
                render.setUVRotateNorth(2);
                render.setUVRotateSouth(1);
                break;
            case 2:
                break;
            case 3:
                render.setUVRotateTop(3);
                render.setUVRotateBottom(3);
                break;
            case 4:
                render.setUVRotateTop(2);
                render.setUVRotateBottom(1);
                break;
            default:
                render.setUVRotateTop(1);
                render.setUVRotateBottom(2);
                break;
        }
        render.setRenderBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        boolean ret = render.renderStandardBlock(this, X, Y, Z);
        render.clearUVRotation();
        return ret;
    }
}
