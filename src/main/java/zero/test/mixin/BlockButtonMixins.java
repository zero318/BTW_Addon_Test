package zero.test.mixin;
import net.minecraft.src.*;
import btw.world.util.WorldUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import java.util.List;
@Mixin(BlockButton.class)
public abstract class BlockButtonMixins extends Block {
    public BlockButtonMixins() {
        super(0, null);
    }
    @Override
    public boolean triggersBuddy() {
        return false;
    }
    @Overwrite
    public void func_82536_d(World world, int x, int y, int z, int buttonDir) {
        int direction;
        switch (buttonDir) {
            case 0: case 7:
                direction = 0;
                break;
            case 5: case 6:
                direction = 1;
                break;
            default:
                direction = 6 - buttonDir;
                break;
        }
        world.notifyBlocksOfNeighborChange(
            x - Facing.offsetsXForSide[direction],
            y - Facing.offsetsYForSide[direction],
            z - Facing.offsetsZForSide[direction],
            this.blockID
        );
    }
    @Override
    public AxisAlignedBB getBlockBoundsFromPoolBasedOnState(IBlockAccess blockAccess, int x, int y, int z) {
        int meta = blockAccess.getBlockMetadata(x, y, z);
        float thickness = !((((meta)>7))) ? 0.125F : 0.0625F;
        AABBPool pool = AxisAlignedBB.getAABBPool();
        switch ((((meta)&7))) {
            case 0:
                return pool.getAABB(
                    0.375F, 1.0F - thickness, 0.3125F,
                    0.625F, 1.0F, 0.6875F
                );
            case 1:
                return pool.getAABB(
                    0.0F, 0.375F, 0.3125F,
                    thickness, 0.625F, 0.6875F
                );
            case 2:
                return pool.getAABB(
                    1.0F - thickness, 0.375F, 0.3125F,
                    1.0F, 0.625F, 0.6875F
                );
            case 3:
                return pool.getAABB(
                    0.3125F, 0.375F, 0.0F,
                    0.6875F, 0.625F, thickness
                );
            case 4:
                return pool.getAABB(
                    0.3125F, 0.375F, 1.0F - thickness,
                    0.6875F, 0.625F, 1.0F
                );
            case 5:
                return pool.getAABB(
                    0.3125F, 0.0F, 0.375F,
                    0.6875F, thickness, 0.625F
                );
            case 6:
                return pool.getAABB(
                    0.375F, 0.0F, 0.3125F,
                    0.625F, thickness, 0.6875F
                );
            default:
                return pool.getAABB(
                    0.3125F, 1.0F - thickness, 0.375F,
                    0.6875F, 1.0F, 0.625F
                );
        }
    }
    @Overwrite
    public void func_82535_o(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        boolean isPowered = ((((meta)>7)));
        boolean hasArrow = !world.getEntitiesWithinAABB(
            EntityArrow.class,
            this.getBlockBoundsFromPoolBasedOnState(world, x, y, z).offset(x, y, z)
        ).isEmpty();
        if (isPowered ^ hasArrow) {
            world.setBlockMetadataWithNotify(x, y, z, ((meta)^8), 0x01 | 0x02);
            this.func_82536_d(world, x, y, z, (((meta)&7)));
            world.playSoundEffect((double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, "random.click", 0.3F, isPowered ? 0.5F : 0.6F);
        }
        if (hasArrow) {
            world.scheduleBlockUpdate(x, y, z, this.blockID, this.tickRate(world));
        }
    }
    @Overwrite
    public int isProvidingStrongPower(IBlockAccess blockAccess, int x, int y, int z, int side) {
        int meta = blockAccess.getBlockMetadata(x, y, z);
        if (((((meta)>7)))) {
            int buttonDir = (((meta)&7));
            switch (side) {
                case 0:
                    if (buttonDir == 5 || buttonDir == 6) {
                        return 15;
                    }
                    break;
                case 1:
                    if (buttonDir == 0 || buttonDir == 7) {
                        return 15;
                    }
                    break;
                default:
                    if (6 - buttonDir == side) {
                        return 15;
                    }
                    break;
            }
        }
        return 0;
    }
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving entityLiving, ItemStack itemStack) {
        int meta = world.getBlockMetadata(x, y, z);
        int buttonDir;
        switch ((((meta)&7))) {
            default:
                return;
            case 0:
                if (((int)MathHelper.floor_double((double)(entityLiving.rotationYaw)*0.01111111111111111111111111111111D+0.5D)&1) == 0x0) {
                    buttonDir = 7;
                    break;
                } else {
                    buttonDir = 0;
                    break;
                }
            case 5:
                if (((int)MathHelper.floor_double((double)(entityLiving.rotationYaw)*0.01111111111111111111111111111111D+0.5D)&1) == 0x0) {
                    buttonDir = 5;
                    break;
                } else {
                    buttonDir = 6;
                    break;
                }
        }
        world.setBlockMetadataWithNotify(x, y, z, (((meta)&8|(buttonDir))), 0x02);
    }
    @Overwrite
    public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int direction) {
        return WorldUtils.doesBlockHaveLargeCenterHardpointToFacing(
            world,
            x - Facing.offsetsXForSide[direction],
            y - Facing.offsetsYForSide[direction],
            z - Facing.offsetsZForSide[direction],
            direction,
            true
        );
    }
    @Overwrite
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return
               this.canPlaceBlockOnSide(world, x, y, z, 0) ||
               this.canPlaceBlockOnSide(world, x, y, z, 1) ||
               this.canPlaceBlockOnSide(world, x, y, z, 2) ||
               this.canPlaceBlockOnSide(world, x, y, z, 3) ||
               this.canPlaceBlockOnSide(world, x, y, z, 4) ||
               this.canPlaceBlockOnSide(world, x, y, z, 5);
    }
    @Overwrite
    public int onBlockPlaced(World world, int x, int y, int z, int side, float clickX, float clickY, float clickZ, int meta) {
        int buttonDir = -1;
        if (this.canPlaceBlockOnSide(world, x, y, z, side)) {
            switch (side) {
                case 0:
                    buttonDir = 0;
                    break;
                case 1:
                    buttonDir = 5;
                    break;
                default:
                    buttonDir = 6 - side;
                    break;
/*
                case DIRECTION_NORTH:
                    buttonDir = 4;
                    break;
                case DIRECTION_SOUTH:
                    buttonDir = 3;
                    break;
                case DIRECTION_WEST:
                    buttonDir = 2;
                    break;
                default:
                    buttonDir = 1;
                    break;
*/
            }
        }
        return (((meta)&8|(buttonDir)));
    }
    @Overwrite
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborId) {
        int meta = world.getBlockMetadata(x, y, z);
        int direction = (((meta)&7));
        switch (direction) {
            case 0: case 7:
                direction = 0;
                break;
            case 5: case 6:
                direction = 1;
                break;
            default:
                direction = 6 - direction;
                break;
        }
        if (!this.canPlaceBlockOnSide(world, x, y, z, direction)) {
            this.dropBlockAsItem(world, x, y, z, meta, 0);
            world.setBlockToAir(x, y, z);
        }
    }
}
