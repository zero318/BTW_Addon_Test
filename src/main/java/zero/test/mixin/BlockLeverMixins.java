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
// Block piston reactions
@Mixin(BlockLever.class)
public abstract class BlockLeverMixins extends Block {
    public BlockLeverMixins(int blockId, Material material) {
        super(blockId, material);
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
        return this.canPlaceBlockOnSide(world, x, y, z, 0) ||
               this.canPlaceBlockOnSide(world, x, y, z, 1) ||
               this.canPlaceBlockOnSide(world, x, y, z, 2) ||
               this.canPlaceBlockOnSide(world, x, y, z, 3) ||
               this.canPlaceBlockOnSide(world, x, y, z, 4) ||
               this.canPlaceBlockOnSide(world, x, y, z, 5);
    }
    @Overwrite
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
                case 2:
                    leverDir = 4;
                    break;
                case 3:
                    leverDir = 3;
                    break;
                case 4:
                    leverDir = 2;
                    break;
                default:
                    leverDir = 1;
                    break;
            }
        }
        return (((meta)&8|(leverDir)));
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
