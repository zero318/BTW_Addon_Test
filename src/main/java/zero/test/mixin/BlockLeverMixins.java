package zero.test.mixin;
import net.minecraft.src.*;
import btw.block.blocks.LeverBlock;
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
// Block piston reactions
@Mixin(LeverBlock.class)
public abstract class BlockLeverMixins extends Block {
    public BlockLeverMixins() {
        super(0, null);
    }
    @Override
    public boolean triggersBuddy() {
        return false;
    }
    @Override
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
    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return this.canPlaceBlockOnSide(world, x, y, z, 0) ||
               this.canPlaceBlockOnSide(world, x, y, z, 1) ||
               this.canPlaceBlockOnSide(world, x, y, z, 2) ||
               this.canPlaceBlockOnSide(world, x, y, z, 3) ||
               this.canPlaceBlockOnSide(world, x, y, z, 4) ||
               this.canPlaceBlockOnSide(world, x, y, z, 5);
    }
    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float clickX, float clickY, float clickZ, int meta) {
        int leverDir = -1;
        if (this.canPlaceBlockOnSide(world, x, y, z, side)) {
            switch (side) {
                case 0:
                    leverDir = 0;
                    break;
                case 1:
                    leverDir = 5;
                    break;
                default:
                    leverDir = 6 - side;
                    break;
/*
                case DIRECTION_NORTH:
                    leverDir = 4;
                    break;
                case DIRECTION_SOUTH:
                    leverDir = 3;
                    break;
                case DIRECTION_WEST:
                    leverDir = 2;
                    break;
                default:
                    leverDir = 1;
                    break;
*/
            }
        }
        return (((meta)&8|(leverDir)));
    }
    @Override
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
    @Overwrite
    public boolean onRotatedAroundBlockOnTurntableToFacing(World world, int x, int y, int z, int direction) {
        return true;
    }
    @Override
    public int rotateMetadataAroundJAxis(int meta, boolean reverse) {
        int direction = (((meta)&7));
        switch (direction) {
            case 1: case 2: case 3: case 4:
                direction = 6 - rotateFacingAroundY(6 - direction, reverse);
        }
        return (((meta)&8|(direction)));
    }
}
