package zero.test.block;

import net.minecraft.src.*;
import btw.AddonHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
public class DeadCoralFan extends Block {
    public DeadCoralFan(int block_id) {
        super(block_id, Material.rock);
        this.setUnlocalizedName("dead_coral_fan");
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }
    public boolean isOpaqueCube() {
        return false;
    }
    public boolean renderAsNormalBlock() {
        return false;
    }
    public int updateShape(World world, int X, int Y, int Z, int direction, int meta) {
        AddonHandler.logMessage("CORAL META "+meta);
        int attached_face = (((meta)&7));
        if (attached_face != 7) {
            X -= Facing.offsetsXForSide[attached_face];
            Y -= Facing.offsetsYForSide[attached_face];
            Z -= Facing.offsetsZForSide[attached_face];
            int attached_block_id = world.getBlockId(X, Y, Z);
            Block attached_block = Block.blocksList[attached_block_id];
            if (
                !((attached_block)==null) &&
                !attached_block.hasLargeCenterHardPointToFacing(world, X, Y, Z, ((attached_face)^1))
            ) {
                return -1;
            }
        }
        return meta;
    }
    @Override
    public int onBlockPlaced(World world, int X, int Y, int Z, int side, float hitX, float hitY, float hitZ, int meta) {
        int ret = -1;
        int neighbor_id;
        X -= Facing.offsetsXForSide[side];
        Y -= Facing.offsetsYForSide[side];
        Z -= Facing.offsetsZForSide[side];
        Block neighbor_block = Block.blocksList[world.getBlockId(X, Y, Z)];
        if (
            !((neighbor_block)==null) &&
            neighbor_block.hasLargeCenterHardPointToFacing(world, X, Y, Z, ((side)^1))
        ) {
            return side;
        }
        return -1;
    }
    @Environment(EnvType.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int X, int Y, int Z) {
        this.setBlockBoundsBasedOnState(world, X, Y, Z);
        return super.getSelectedBoundingBoxFromPool(world, X, Y, Z);
    }
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int X, int Y, int Z) {
        this.setBlockBoundsBasedOnState(world, X, Y, Z);
        return super.getCollisionBoundingBoxFromPool(world, X, Y, Z);
    }
    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess block_access, int X, int Y, int Z) {
        this.setBlockBoundsForBlockRender(block_access.getBlockMetadata(X, Y, Z));
    }
    public void setBlockBoundsForBlockRender(int meta) {
        switch ((((meta)&7))) {
            case 0:
                this.setBlockBounds(2.0f, 0.0f, 2.0f, 14.0f, 4.0f, 14.0f);
                return;
            case 1:
                this.setBlockBounds(2.0f, 12.0f, 2.0f, 14.0f, 16.0f, 14.0f);
                return;
            case 2:
                this.setBlockBounds(0.0f, 4.0f, 5.0f, 16.0f, 12.0f, 16.0f);
                return;
            case 3:
                this.setBlockBounds(0.0f, 4.0f, 0.0f, 16.0f, 12.0f, 11.0f);
                return;
            case 4:
                this.setBlockBounds(5.0f, 4.0f, 0.0f, 16.0f, 12.0f, 16.0f);
                return;
            default:
                this.setBlockBounds(0.0f, 4.0f, 0.0f, 11.0f, 12.0f, 16.0f);
                return;
        }
    }
}
