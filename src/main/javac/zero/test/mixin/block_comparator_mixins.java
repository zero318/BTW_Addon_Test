package zero.test.mixin;

import net.minecraft.src.*;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Implements;

//import zero.test.mixin.IBlockComparatorAccessMixins;
import zero.test.mixin.IBlockRedstoneLogicAccessMixins;
import zero.test.IBlockRedstoneLogicMixins;
import zero.test.IBlockMixins;

import java.util.Random;

#include "..\feature_flags.h"
#include "..\util.h"

#define SUBTRACT_META_BITS 1
#define SUBTRACT_META_IS_BOOL true

#define FLAT_DIRECTION_META_OFFSET 0
#define SUBTRACT_META_OFFSET 2
#define POWERED_META_OFFSET 3

@Mixin(BlockComparator.class)
public abstract class BlockComparatorMixins extends BlockRedstoneLogic {
    
    public BlockComparatorMixins() {
        super(0, false);
    }
   
#if ENABLE_BETTER_BUDDY_DETECTION
    @Override
    public boolean triggersBuddy() {
        return false;
    }
#endif

#if ENABLE_REDSTONE_BUGFIXES
    // Fixes: MC-12211, MC-63669
    // shouldTurnOn
    @Overwrite
    public boolean func_94478_d(World world, int x, int y, int z, int meta) {
        int inputPower = this.getInputStrength(world, x, y, z, meta);
        if (inputPower != 0) {
            int sidePower = this.func_94482_f(world, x, y, z, meta);
            if (inputPower >= sidePower) {
                return inputPower > sidePower || !READ_META_FIELD(meta, SUBTRACT);
            }
        }
        return false;
    }
    
    // Fixes: MC-195351
    // calculateOutputSignal
    @Overwrite
    public int func_94491_m(World world, int x, int y, int z, int meta) {
        int inputPower = this.getInputStrength(world, x, y, z, meta);
        if (inputPower != 0) {
            int sidePower = this.func_94482_f(world, x, y, z, meta);
            if (sidePower <= inputPower) {
                return !READ_META_FIELD(meta, SUBTRACT) ? inputPower : inputPower - sidePower;
            }
        }
        return 0;
    }
    
    // Fixes: MC-8911, MC-10653
    @Overwrite
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int par6, float par7, float par8, float par9) {
        int meta = world.getBlockMetadata(x, y, z);
        boolean mode = !READ_META_FIELD(meta, SUBTRACT); //!this.isSubtractMode(var10);
        world.playSoundEffect((double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, "random.click", 0.3F, mode ? 0.55F : 0.5F);
        world.setBlockMetadataWithNotify(x, y, z, MERGE_META_FIELD_VAR(meta, SUBTRACT, mode), UPDATE_CLIENTS);
        if (
            !world.isRemote && // MC-10653
            !world.isUpdateScheduledForBlock(x, y, z, this.blockID) // MC-8911
        ) {
            this.func_96476_c(world, x, y, z, world.rand);
        }
        return true;
    }
    
    // Fixes: More of MC-195351?
    // refreshOutputState
    @Overwrite
    public void func_96476_c(World world, int x, int y, int z, Random random) {
        int meta = world.getBlockMetadata(x, y, z);
        int newPower = this.func_94491_m(world, x, y, z, meta);
        TileEntityComparator tileEntity = ((BlockComparator)(Object)this).getTileEntityComparator(world, x, y, z);
        int prevPower = tileEntity.func_96100_a();

        if (newPower != prevPower) {
            tileEntity.func_96099_a(newPower);
            
            //boolean should_turn_on = this.func_94478_d(world, x, y, z, meta);
            //boolean is_currently_on = READ_META_FIELD(meta, POWERED);

            //if (is_currently_on && !should_turn_on) {
                //world.setBlockMetadataWithNotify(x, y, z, MERGE_META_FIELD(meta, POWERED, false), UPDATE_CLIENTS);
            //}
            //else if (!is_currently_on && should_turn_on) {
                //world.setBlockMetadataWithNotify(x, y, z, MERGE_META_FIELD(meta, POWERED, true), UPDATE_CLIENTS);
            //}
            
            if (prevPower == 0 || newPower == 0) {
                world.setBlockMetadataWithNotify(x, y, z, TOGGLE_META_FIELD(meta, POWERED), UPDATE_CLIENTS);
            }
            this.func_94483_i_(world, x, y, z);
        }
    }

    @Override
    public boolean canRotateOnTurntable(IBlockAccess blockAccess, int x, int y, int z) {
        return true;
    }
    
    @Override
    public boolean rotateAroundJAxis(World world, int x, int y, int z, boolean reverse) {
        int prevMeta = world.getBlockMetadata(x, y, z);
        int newMeta = this.rotateMetadataAroundJAxis(prevMeta, reverse);
        if (prevMeta != newMeta) {
            world.setBlockMetadataWithNotify(x, y, z, newMeta, UPDATE_NEIGHBORS | UPDATE_CLIENTS);
            
            this.onNeighborBlockChange(world, x, y, z, 0);
            
            switch (READ_META_FIELD(prevMeta, FLAT_DIRECTION)) {
                case FLAT_DIRECTION_META_NORTH:
                    world.notifyBlockOfNeighborChange(x, y, z - 1, this.blockID);
                    world.notifyBlocksOfNeighborChange(x, y, z - 1, this.blockID, DIRECTION_SOUTH);
                    break;
                case FLAT_DIRECTION_META_EAST:
                    world.notifyBlockOfNeighborChange(x + 1, y, z, this.blockID);
                    world.notifyBlocksOfNeighborChange(x + 1, y, z, this.blockID, DIRECTION_WEST);
                    break;
                case FLAT_DIRECTION_META_SOUTH:
                    world.notifyBlockOfNeighborChange(x, y, z + 1, this.blockID);
                    world.notifyBlocksOfNeighborChange(x, y, z + 1, this.blockID, DIRECTION_NORTH);
                    break;
                default:
                    world.notifyBlockOfNeighborChange(x - 1, y, z, this.blockID);
                    world.notifyBlocksOfNeighborChange(x - 1, y, z, this.blockID, DIRECTION_EAST);
                    break;
            }
            this.func_94483_i_(world, x, y, z);
            return true;
        }
        
        return false;
    }
    
    @Override
    public int rotateMetadataAroundJAxis(int meta, boolean reverse) {
        return MERGE_META_FIELD(meta, FLAT_DIRECTION, meta + (reverse ? 1 : -1) & 3);
    }

    // handles BD placement, player handled in parent onBlockPlaceBy()
    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int direction, float clickX, float clickY, float clickZ, int meta) {
        switch (meta) {
            case DIRECTION_WEST:
                return FLAT_DIRECTION_META_EAST;
            case DIRECTION_EAST:
                return FLAT_DIRECTION_META_WEST;
            case DIRECTION_NORTH:
                return FLAT_DIRECTION_META_SOUTH;
            default:
                return FLAT_DIRECTION_META_NORTH;
        }
    }

#endif

    @Override
    public int func_94488_g(IBlockAccess blockAccess, int x, int y, int z, int side)
    {
        int blockId = blockAccess.getBlockId(x, y, z);
        if (blockId == Block.redstoneWire.blockID) {
            return blockAccess.getBlockMetadata(x, y, z);
        }
        if (blockId == Block.blockRedstone.blockID) {
            return MAX_REDSTONE_POWER;
        }
        if (this.func_94477_d(blockId)) {
            return blockAccess.isBlockProvidingPowerTo(x, y, z, side);
        }
        return 0;
    }
    
    //@Override
    public boolean getWeakChanges(World world, int x, int y, int z, int meta) {
        return true;
    }
    
#if ENABLE_MODERN_REDSTONE_WIRE
    // Deal with the conductivity change
    @Overwrite
    public int getInputStrength(World world, int x, int y, int z, int meta) {
        int power = super.getInputStrength(world, x, y, z, meta);
        int direction = READ_META_FIELD(meta, FLAT_DIRECTION);
        x += Direction.offsetX[direction];
        z += Direction.offsetZ[direction];
        Block block = Block.blocksList[world.getBlockId(x, y, z)];

        if (!BLOCK_IS_AIR(block)) {
            if (block.hasComparatorInputOverride()) {
                power = block.getComparatorInputOverride(world, x, y, z, OPPOSITE_FLAT_DIRECTION(direction));
            }
            else if (
                power < MAX_REDSTONE_POWER &&
                ((IBlockMixins)block).isRedstoneConductor(world, x, y, z)
            ) {
                x += Direction.offsetX[direction];
                z += Direction.offsetZ[direction];
                block = Block.blocksList[world.getBlockId(x, y, z)];

                if (
                    !BLOCK_IS_AIR(block) &&
                    block.hasComparatorInputOverride()
                ) {
                    power = block.getComparatorInputOverride(world, x, y, z, OPPOSITE_FLAT_DIRECTION(direction));
                }
            }
        }

        return power;
    }
#endif

    //@Shadow
    //public abstract boolean getRenderingBaseTextures();

    // Hacky fix for rendering a bottom texture
    @Environment(EnvType.CLIENT)
    @Overwrite
    public Icon getIcon(int side, int meta) {
        BlockComparator self = (BlockComparator)(Object)this;
        boolean isPowered = READ_META_FIELD(meta, POWERED) || ((IBlockRedstoneLogicAccessMixins)self).getIsRepeaterPowered();
        if (side == DIRECTION_DOWN && !((IBlockRedstoneLogicMixins)self).getRenderingBaseTextures()) {
            return (isPowered ? Block.torchRedstoneActive : Block.torchRedstoneIdle).getBlockTextureFromSide(side);
        }
        return side == DIRECTION_UP ? (isPowered ? Block.redstoneComparatorActive : self).blockIcon : Block.stoneDoubleSlab.getBlockTextureFromSide(1);
        
    }
}