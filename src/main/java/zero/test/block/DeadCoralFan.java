package zero.test.block;

import net.minecraft.src.*;
import btw.AddonHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import java.util.Random;
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
    @Override
    protected boolean canSilkHarvest() {
        return true;
    }
    @Override
    public int idDropped(int meta, Random rand, int fortuneModifier) {
        return -1;
    }
    public int updateShape(World world, int x, int y, int z, int direction, int meta) {
        int attachedFace = (((meta)&7));
        if (((attachedFace)<=5)) {
            x -= Facing.offsetsXForSide[attachedFace];
            y -= Facing.offsetsYForSide[attachedFace];
            z -= Facing.offsetsZForSide[attachedFace];
            Block attachedBlock = Block.blocksList[world.getBlockId(x, y, z)];
            if (
                ((attachedBlock)==null) ||
                !attachedBlock.hasLargeCenterHardPointToFacing(world, x, y, z, ((attachedFace)^1))
            ) {
                return -1;
            }
        }
        return meta;
    }
    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        int direction = 0;
        do {
            int testX = x - Facing.offsetsXForSide[direction];
            int testY = y - Facing.offsetsYForSide[direction];
            int testZ = z - Facing.offsetsZForSide[direction];
            Block neighborBlock = Block.blocksList[world.getBlockId(testX, testY, testZ)];
            if (
                !((neighborBlock)==null) &&
                neighborBlock.hasLargeCenterHardPointToFacing(world, testX, testY, testZ, ((direction)^1))
            ) {
                return true;
            }
        } while (((++direction)<=5));
        return false;
    }
    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta) {
        x -= Facing.offsetsXForSide[side];
        y -= Facing.offsetsYForSide[side];
        z -= Facing.offsetsZForSide[side];
        Block neighborBlock = Block.blocksList[world.getBlockId(x, y, z)];
        if (
            !((neighborBlock)==null) &&
            neighborBlock.hasLargeCenterHardPointToFacing(world, x, y, z, ((side)^1))
        ) {
            return side;
        }
        return -1;
    }
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
    }
    @Override
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(IBlockAccess blockAccess, int x, int y, int z) {
        switch ((((blockAccess.getBlockMetadata(x, y, z))&7))) {
            case 0:
                return AxisAlignedBB.getAABBPool().getAABB(0.125D, 0.75D, 0.125D, 0.875D, 1.0D, 0.875D);
            case 1:
                return AxisAlignedBB.getAABBPool().getAABB(0.125D, 0.0D, 0.125D, 0.875D, 0.25D, 0.875D);
            case 2:
                return AxisAlignedBB.getAABBPool().getAABB(0.0D, 0.25D, 0.3125D, 1.0D, 0.75D, 1.0D);
            case 3:
                return AxisAlignedBB.getAABBPool().getAABB(0.0D, 0.25D, 0.0D, 1.0D, 0.75D, 0.6875D);
            case 4:
                return AxisAlignedBB.getAABBPool().getAABB(0.3125D, 0.25D, 0.0D, 1.0D, 0.75D, 1.0D);
            default:
                return AxisAlignedBB.getAABBPool().getAABB(0.0D, 0.25D, 0.0D, 0.6875D, 0.75D, 1.0D);
        }
    }
    @Override
    @Environment(EnvType.CLIENT)
    public void registerIcons(IconRegister register) {
    }
}
