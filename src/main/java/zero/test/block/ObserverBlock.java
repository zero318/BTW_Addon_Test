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
// Block piston reactions
public class ObserverBlock extends BuddyBlock {
    public ObserverBlock(int blockId) {
        super(blockId);
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
    public void onBlockAdded(World world, int x, int y, int z) {
        // HACK: 
        // Java doesn't have super.super, so there's no way of
        // calling Block.onBlockAdded without going through
        // BuddyBlock.onBlockAdded, which schedules an update.
        // So instead we're just assuming that Block.onBlockAdded
        // is still empty and doing nothing.
    }
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborId) {
    }
    public int updateShape(World world, int x, int y, int z, int direction, int meta) {
        if (
            (((meta)>>>1)) == ((direction)^1) &&
            !world.isUpdateScheduledForBlock(x, y, z, this.blockID)
        ) {
            world.scheduleBlockUpdate(x, y, z, this.blockID, 2);
        }
        return meta;
    }
    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        int meta = world.getBlockMetadata(x, y, z);
        if (((((meta)&1)!=0))) {
            world.setBlockMetadataWithClient(x, y, z, (((meta)&14)));
        } else {
            world.setBlockMetadataWithClient(x, y, z, (((meta)|1)));
            world.scheduleBlockUpdate(x, y, z, this.blockID, 2);
        }
        int direction = (((meta)>>>1));
        x += Facing.offsetsXForSide[direction];
        y += Facing.offsetsYForSide[direction];
        z += Facing.offsetsZForSide[direction];
        Block neighborBlock = Block.blocksList[world.getBlockId(x, y, z)];
        if (!((neighborBlock)==null)) {
            neighborBlock.onNeighborBlockChange(world, x, y, z, this.blockID);
        }
        world.notifyBlocksOfNeighborChange(x, y, z, this.blockID, ((direction)^1));
    }
    public boolean isRedstoneConductor(IBlockAccess blockAccess, int x, int y, int z) {
        return false;
    }
    public boolean canRedstoneConnectToSide(IBlockAccess blockAccess, int x, int y, int z, int flatDirection) {
        return (((((blockAccess.getBlockMetadata(x, y, z))>>>1)))^1) == Direction.directionToFacing[flatDirection];
    }
    // This just gets rid of the clicking sound
    // TODO: Remove override now that the code
    // is being used directly in updateTick
    @Override
    public void setBlockRedstoneOn(World world, int x, int y, int z, boolean isActivated) {
        if (isActivated != isRedstoneOn(world, x, y, z)) {
            int meta = world.getBlockMetadata(x, y, z);
            world.setBlockMetadataWithClient(x, y, z, (((meta)&14|((isActivated)?1:0))));
            // only notify on the output side to prevent weird shit like doors auto-closing when the block
            // goes off
            int direction = this.getFacing(world, x, y, z);
            notifyNeigborsToFacingOfPowerChange(world, x, y, z, direction);
            // the following forces a re-render (for texture change)
            world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
        }
    }
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
    //@Environment(EnvType.CLIENT)
    //protected Icon texture_side;
    @Environment(EnvType.CLIENT)
    protected Icon texture_top;
    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
        super.registerIcons(register);
        this.texture_back_off = register.registerIcon("observer_back");
        this.texture_back_on = register.registerIcon("observer_back_on");
        this.texture_front = register.registerIcon("observer_front");
        //this.texture_side = register.registerIcon("observer_side");
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
                return this.blockIcon;
                //return this.texture_side;
        }
    }
    @Override
    @Environment(EnvType.CLIENT)
    public Icon getBlockTexture(IBlockAccess block_access, int x, int y, int z, int side) {
        int meta = block_access.getBlockMetadata(x, y, z);
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
            return ((side)&~1) == 0x0 ? this.texture_top : this.blockIcon;
        }
        // When facing up, the non-arrow side should be on E/W
        return ((side)&~1) == 0x2 ? this.texture_top : this.blockIcon;
    }
    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderBlock(RenderBlocks render, int x, int y, int z) {
        // IDK why these rotation values work
        switch ((((render.blockAccess.getBlockMetadata(x, y, z))>>>1))) {
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
        render.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
        boolean ret = render.renderStandardBlock(this, x, y, z);
        render.clearUVRotation();
        return ret;
    }
}
