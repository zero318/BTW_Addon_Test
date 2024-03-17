package zero.test.mixin;

import net.minecraft.src.Block;
import net.minecraft.src.World;
import net.minecraft.src.BlockPistonBase;
import net.minecraft.src.*;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import btw.block.BTWBlocks;
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

#include "..\func_aliases.h"
#include "..\feature_flags.h"
#include "..\util.h"

#define DIRECTION_META_OFFSET 0
#define EXTENDED_META_OFFSET 3
#define EXTENDED_META_BITS 1
#define EXTENDED_META_IS_BOOL true

#define PISTON_PRINT_DEBUGGING 0

#if PISTON_PRINT_DEBUGGING
#define PISTON_DEBUG(...) if (!world.isRemote) AddonHandler.logMessage(__VA_ARGS__)
#else
#define PISTON_DEBUG(...)
#endif

#define SHOVEL_PRINT_DEBUGGING 0

#if SHOVEL_PRINT_DEBUGGING
#define SHOVEL_DEBUG(...) if (!world.isRemote) AddonHandler.logMessage(__VA_ARGS__)
#else
#define SHOVEL_DEBUG(...)
#endif

import zero.test.PistonResolver;

@Mixin(
    value = PistonBlockBase.class,
    priority = 1100
)
public abstract class PistonMixins extends BlockPistonBase {

    public PistonMixins() {
        super(0, false);
    }

// Re-enable this once the moving blocks are sorted out
#if ENABLE_BETTER_BUDDY_DETECTION
#if BETTER_BUDDY_PISTON_FIX_TYPE != BETTER_BUDDY_PISTON_FIX_NONE
    @Override
    public boolean triggersBuddy() {
        return false;
    }
#endif
#endif

#if ENABLE_MODERN_REDSTONE_WIRE
    // Don't conduct redstone despite being "normal"
    public boolean isRedstoneConductor(IBlockAccess blockAccess, int x, int y, int z) {
        return false;
    }

    // Suffocate entities inside a retracted piston
    @Override
    public boolean isNormalCube(IBlockAccess blockAccess, int x, int y, int z) {
        return !READ_META_FIELD(blockAccess.getBlockMetadata(x, y, z), EXTENDED);
    }
#endif

    @Override
    public boolean hasCenterHardPointToFacing(IBlockAccess blockAccess, int x, int y, int z, int facing, boolean ignoreTransparency) {
        int meta = blockAccess.getBlockMetadata(x, y, z);
        if (READ_META_FIELD(meta, EXTENDED)) {
            int direction = READ_META_FIELD(meta, DIRECTION);
            if (
                direction == facing ||
                DIRECTION_IS_VERTICAL(direction) && DIRECTION_IS_HORIZONTAL(facing)
            ) {
                return false;
            }
        }
        return true;
    }
    
    // The back of a piston is still a large hardpoint when extended
    @Override
    public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int x, int y, int z, int facing, boolean ignoreTransparency) {
        int meta = blockAccess.getBlockMetadata(x, y, z);
        return !READ_META_FIELD(meta, EXTENDED) || OPPOSITE_DIRECTION(facing) == READ_META_FIELD(meta, DIRECTION);
    }
    
#if ENABLE_MOVING_BLOCK_CHAINING
    
    private static final PistonResolver server_resolver = new PistonResolver();
    private static final PistonResolver client_resolver = new PistonResolver();
    
    public boolean moveBlocks(World world, int x, int y, int z, int direction, boolean isExtending) {
        return (!world.isRemote ? server_resolver : client_resolver).moveBlocks(world, x, y, z, direction, isExtending, this.isSticky);
    }
    
    @Overwrite
    public boolean tryExtend(World world, int x, int y, int z, int direction) {
        return this.moveBlocks(world, x, y, z, direction, true);
    }
    
    @Overwrite
    public boolean canExtend(World world, int x, int y, int z, int direction) {
        return (!world.isRemote ? server_resolver : client_resolver).canExtend(world, x, y, z, direction);
    }
    
    private static final int PISTON_EVENT_EXTENDING = 0;
    private static final int PISTON_EVENT_RETRACTING_NORMAL = 1;
    private static final int PISTON_EVENT_RETRACTING_DROP_BLOCK = 2;
    
    @Override
    public void updatePistonState(World world, int x, int y, int z) {
        if (!world.isRemote) {
            int meta = world.getBlockMetadata(x, y, z);
            int direction = READ_META_FIELD(meta, DIRECTION);
            
            // Why does the 1.5 code check for 7?
            // Apparently directions 6 and 7 have been well known
            // to crash older versions. Extended this check to try
            // and prevent that.
            if (DIRECTION_IS_VALID(direction)) {
                boolean isPowered = ((IPistonBaseAccessMixins)(Object)this).callIsIndirectlyPowered(world, x, y, z, direction);
                if (isPowered != READ_META_FIELD(meta, EXTENDED)) {
                    if (isPowered) {
                        if (this.canExtend(world, x, y, z, direction)) {
                            world.addBlockEvent(x, y, z, this.blockID, PISTON_EVENT_EXTENDING, direction);
                        }
                    }
                    else {
                        int nextX = Facing.offsetsXForSide[direction];
                        int nextY = Facing.offsetsYForSide[direction];
                        int nextZ = Facing.offsetsZForSide[direction];
                        nextX += nextX + x;
                        nextY += nextY + y;
                        nextZ += nextZ + z;
                        int nextBlockId = world.getBlockId(nextX, nextY, nextZ);
                        TileEntity tileEntity;
                        world.addBlockEvent(
                            x, y, z,
                            this.blockID,
                            (
                                nextBlockId == Block.pistonMoving.blockID &&
                                (tileEntity = world.getBlockTileEntity(nextX, nextY, nextZ)) instanceof TileEntityPiston &&
                                ((TileEntityPiston)tileEntity).getPistonOrientation() == direction &&
                                ((TileEntityPiston)tileEntity).isExtending() &&
                                (
                                    ((TileEntityPiston)tileEntity).getProgress(0.0F) < 0.5F ||
                                    world.getTotalWorldTime() == ((IBlockEntityPistonMixins)tileEntity).getLastTicked()
                                    // something about "handling tick"?
                                )
                            ) ? PISTON_EVENT_RETRACTING_DROP_BLOCK : PISTON_EVENT_RETRACTING_NORMAL,
                            direction
                        );
                    }
                }
            }
        }
    }
    
    @Override
    public boolean onBlockEventReceived(World world, int x, int y, int z, int eventType, int direction) {
        if (!world.isRemote) {
            boolean isPowered = ((IPistonBaseAccessMixins)(Object)this).callIsIndirectlyPowered(world, x, y, z, direction);
            if (
                isPowered &&
                (
                    eventType == PISTON_EVENT_RETRACTING_NORMAL ||
                    eventType == PISTON_EVENT_RETRACTING_DROP_BLOCK
                )
            ) {
                world.setBlockMetadataWithNotify(x, y, z, MERGE_META_FIELD(direction, EXTENDED, true), UPDATE_CLIENTS);
                return false;
            }
            if (!isPowered && eventType == PISTON_EVENT_EXTENDING) {
                return false;
            }
        }
        PISTON_DEBUG("===== PistonCoords ("+x+" "+y+" "+z+")");
        switch (eventType) {
            case PISTON_EVENT_EXTENDING: // Extend
                PISTON_DEBUG("Case PISTON_EVENT_EXTENDING");
                if (!this.moveBlocks(world, x, y, z, direction, true)) {
                    return false;
                }
                world.setBlockMetadataWithNotify(x, y, z, MERGE_META_FIELD(direction, EXTENDED, true), UPDATE_NEIGHBORS | UPDATE_CLIENTS | UPDATE_SUPPRESS_DROPS | UPDATE_MOVE_BY_PISTON);
                world.playSoundEffect((double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, "tile.piston.out", 0.5F, world.rand.nextFloat() * 0.25F + 0.6F);
            default:
                return true;
                
            case PISTON_EVENT_RETRACTING_NORMAL: case PISTON_EVENT_RETRACTING_DROP_BLOCK: // Retract
                PISTON_DEBUG("Case PISTON_EVENT_RETRACTING "+eventType);
                int nextX = x + Facing.offsetsXForSide[direction];
                int nextY = y + Facing.offsetsYForSide[direction];
                int nextZ = z + Facing.offsetsZForSide[direction];
                
                TileEntity tileEntity = world.getBlockTileEntity(nextX, nextY, nextZ);
                if (tileEntity instanceof TileEntityPiston) {
                    ((TileEntityPiston)tileEntity).clearPistonTileEntity();
                }
                
                // This is the only time that the metadata of the moving block itself
                // and the metadata of the moving block entity are different.
                // This is done specifically so that a non-extended piston will be placed
                // by the moving block but an extended piston will be used for all
                // property tests while the block is moving.
                world.setBlock(x, y, z, Block.pistonMoving.blockID, MERGE_META_FIELD(direction, EXTENDED, true),  UPDATE_SUPPRESS_DROPS);
                world.setBlockTileEntity(x, y, z, BlockPistonMoving.getTileEntity(this.blockID, direction, direction, false, true));
                //world.notifyBlockChange(x, y, z, Block.pistonMoving.blockID);
                //((IWorldMixins)world).updateNeighbourShapes(x, y, z, UPDATE_CLIENTS);
                
                if (this.isSticky) {
                    int currentX = nextX + Facing.offsetsXForSide[direction];
                    int currentY = nextY + Facing.offsetsYForSide[direction];
                    int currentZ = nextZ + Facing.offsetsZForSide[direction];
                    int nextBlockId = world.getBlockId(currentX, currentY, currentZ);
                    
                    if (nextBlockId == Block.pistonMoving.blockID) {
                        TileEntity nextTileEntity = world.getBlockTileEntity(currentX, currentY, currentZ);
                        if (nextTileEntity instanceof TileEntityPiston) {
                            TileEntityPiston pistonTileEntity = (TileEntityPiston)nextTileEntity;
                            if (
                                pistonTileEntity.getPistonOrientation() == direction &&
                                pistonTileEntity.isExtending()
                            ) {
                                pistonTileEntity.clearPistonTileEntity();
                                break;
                            }
                        }
                    }
                    
                    if (eventType != PISTON_EVENT_RETRACTING_NORMAL) {
                        break;
                    }
                    
                    Block nextBlock = Block.blocksList[nextBlockId];
                    if (
                        !BLOCK_IS_AIR(nextBlock) &&
                        nextBlock.canBlockBePulledByPiston(world, currentX, currentY, currentZ, Block.getOppositeFacing(direction))
                        // What does this block do in modern vanilla?
                        /*&& (
                            nextBlockId == Block.pistonBase.blockID ||
                            nextBlockId == Block.pistonStickyBase.blockID ||
                            block.getMobilityFlag() == PISTON_CAN_PUSH
                        )*/
                    ) {
                        this.moveBlocks(world, x, y, z, direction, false);
                        break;
                    }
                }
                
                world.setBlockToAir(nextX, nextY, nextZ);
                break;
        }
        world.playSoundEffect((double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, "tile.piston.in", 0.5F, world.rand.nextFloat() * 0.15F + 0.6F);
        return true;
    }
#endif
    
    //@Environment(EnvType.CLIENT)
    //public boolean shouldRenderNeighborHalfSlabSide(IBlockAccess blockAccess, int x, int y, int z, int iNeighborSlabSide, boolean bNeighborUpsideDown) {
        //return !isOpaqueCube();
    //}

    @Environment(EnvType.CLIENT)
    @Override
    public boolean shouldRenderNeighborFullFaceSide(IBlockAccess blockAccess, int x, int y, int z, int direction) {
        return !this.hasLargeCenterHardPointToFacing(blockAccess, x, y, z, direction, true);
    }
}