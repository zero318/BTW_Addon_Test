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

#include "..\util.h"
#include "..\feature_flags.h"

#define BUTTON_DIRECTION_META_OFFSET 0
#define BUTTON_DIRECTION_META_BITS 3

#define POWERED_META_OFFSET 3

@Mixin(BlockButton.class)
public abstract class BlockButtonMixins extends Block {
    public BlockButtonMixins(int blockId, Material material) {
        super(blockId, material);
    }
    
#if ENABLE_BUDDY_BLOCK_IGNORES_POWER_UPDATES
    @Override
    public boolean triggersBuddy() {
        return false;
    }
#endif
    
    
#if ENABLE_VERTICAL_BUTTONS
    @Overwrite
    public void func_82536_d(World world, int x, int y, int z, int buttonDir) {
        int direction;
        switch (buttonDir) {
            case 0: case 7:
                direction = DIRECTION_DOWN;
                break;
            case 5: case 6:
                direction = DIRECTION_UP;
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
        
        float thickness = !READ_META_FIELD(meta, POWERED) ? 0.125F : 0.0625F;

#define HEIGHT_MIN 0.375F
#define HEIGHT_MAX 0.625F
#define WIDTH_MIN 0.3125F
#define WIDTH_MAX 0.6875F

        AABBPool pool = AxisAlignedBB.getAABBPool();
        switch (READ_META_FIELD(meta, BUTTON_DIRECTION)) {
            case 0:
                return pool.getAABB(
                    HEIGHT_MIN, 1.0F - thickness, WIDTH_MIN,
                    HEIGHT_MAX, 1.0F, WIDTH_MAX
                );
            case 1:
                return pool.getAABB(
                    0.0F, HEIGHT_MIN, WIDTH_MIN,
                    thickness, HEIGHT_MAX, WIDTH_MAX
                );
            case 2:
                return pool.getAABB(
                    1.0F - thickness, HEIGHT_MIN, WIDTH_MIN,
                    1.0F, HEIGHT_MAX, WIDTH_MAX
                );
            case 3:
                return pool.getAABB(
                    WIDTH_MIN, HEIGHT_MIN, 0.0F,
                    WIDTH_MAX, HEIGHT_MAX, thickness
                );
            case 4:
                return pool.getAABB(
                    WIDTH_MIN, HEIGHT_MIN, 1.0F - thickness,
                    WIDTH_MAX, HEIGHT_MAX, 1.0F
                );
            case 5:
                return pool.getAABB(
                    WIDTH_MIN, 0.0F, HEIGHT_MIN,
                    WIDTH_MAX, thickness, HEIGHT_MAX
                );
            case 6:
                return pool.getAABB(
                    HEIGHT_MIN, 0.0F, WIDTH_MIN,
                    HEIGHT_MAX, thickness, WIDTH_MAX
                );
            default:
                return pool.getAABB(
                    WIDTH_MIN, 1.0F - thickness, HEIGHT_MIN,
                    WIDTH_MAX, 1.0F, HEIGHT_MAX
                );
        }
    }
    
    @Overwrite
    public void func_82535_o(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        
        boolean isPowered = READ_META_FIELD(meta, POWERED);
        boolean hasArrow = !world.getEntitiesWithinAABB(
            EntityArrow.class,
            this.getBlockBoundsFromPoolBasedOnState(world, x, y, z).offset(x, y, z)
        ).isEmpty();
        if (isPowered ^ hasArrow) {
            world.setBlockMetadataWithNotify(x, y, z, TOGGLE_META_FIELD(meta, POWERED), UPDATE_NEIGHBORS | UPDATE_CLIENTS);
            this.func_82536_d(world, x, y, z, READ_META_FIELD(meta, BUTTON_DIRECTION));
            world.playSoundEffect((double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, "random.click", 0.3F, isPowered ? 0.5F : 0.6F);
        }
        if (hasArrow) {
            world.scheduleBlockUpdate(x, y, z, this.blockID, this.tickRate(world));
        }
    }
    
    @Overwrite
    public int isProvidingStrongPower(IBlockAccess blockAccess, int x, int y, int z, int side) {
        int meta = blockAccess.getBlockMetadata(x, y, z);
        if (READ_META_FIELD(meta, POWERED)) {
            int buttonDir = READ_META_FIELD(meta, BUTTON_DIRECTION);
            switch (side) {
                case DIRECTION_DOWN:
                    if (buttonDir == 5 || buttonDir == 6) {
                        return 15;
                    }
                    break;
                case DIRECTION_UP:
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
        switch (READ_META_FIELD(meta, BUTTON_DIRECTION)) {
            default:
                return;
            case 0:
                if (YAW_FLAT_AXIS(entityLiving.rotationYaw) == FLAT_AXIS_Z) {
                    buttonDir = 7;
                    break;
                } else {
                    buttonDir = 0;
                    break;
                }
            case 5:
                if (YAW_FLAT_AXIS(entityLiving.rotationYaw) == FLAT_AXIS_Z) {
                    buttonDir = 5;
                    break;
                } else {
                    buttonDir = 6;
                    break;
                }
        }
        world.setBlockMetadataWithNotify(x, y, z, MERGE_META_FIELD(meta, BUTTON_DIRECTION, buttonDir), UPDATE_CLIENTS);
    }
#endif
    
#if ENABLE_MODERN_SUPPORT_LOGIC == MODERN_SUPPORT_LOGIC_GLOBAL_ALL

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
#if ENABLE_VERTICAL_BUTTONS
               this.canPlaceBlockOnSide(world, x, y, z, DIRECTION_DOWN) ||
               this.canPlaceBlockOnSide(world, x, y, z, DIRECTION_UP) ||
#endif
               this.canPlaceBlockOnSide(world, x, y, z, DIRECTION_NORTH) ||
               this.canPlaceBlockOnSide(world, x, y, z, DIRECTION_SOUTH) ||
               this.canPlaceBlockOnSide(world, x, y, z, DIRECTION_WEST) ||
               this.canPlaceBlockOnSide(world, x, y, z, DIRECTION_EAST);
    }
    
    @Overwrite
    public int onBlockPlaced(World world, int x, int y, int z, int side, float clickX, float clickY, float clickZ, int meta) {
        int buttonDir = -1;
        if (this.canPlaceBlockOnSide(world, x, y, z, side)) {
            switch (side) {
#if ENABLE_VERTICAL_BUTTONS
                case DIRECTION_DOWN:
                    buttonDir = 0;
                    break;
                case DIRECTION_UP:
                    buttonDir = 5;
                    break;
#endif
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
        return MERGE_META_FIELD(meta, BUTTON_DIRECTION, buttonDir);
    }
    
    @Overwrite
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborId) {
        int meta = world.getBlockMetadata(x, y, z);
        int direction = READ_META_FIELD(meta, BUTTON_DIRECTION);
        switch (direction) {
#if ENABLE_VERTICAL_BUTTONS
            case 0: case 7:
                direction = DIRECTION_DOWN;
                break;
            case 5: case 6:
                direction = DIRECTION_UP;
                break;
#endif
            default:
                direction = 6 - direction;
                break;
        }
        if (!this.canPlaceBlockOnSide(world, x, y, z, direction)) {
            this.dropBlockAsItem(world, x, y, z, meta, 0);
            world.setBlockToAir(x, y, z);
        }
    }
#endif
}