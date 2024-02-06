package zero.test.mixin;


import net.minecraft.src.*;

import btw.AddonHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Overwrite;

import zero.test.IBlockBaseRailLogicMixins;
import zero.test.mixin.IBlockBaseRailLogicAccessMixins;

import java.util.List;

#include "..\feature_flags.h"
#include "..\util.h"

#define RAIL_INVALID -1
#define RAIL_NORTH_SOUTH 0
#define RAIL_EAST_WEST 1
#define RAIL_ASCENDING_EAST 2
#define RAIL_ASCENDING_WEST 3
#define RAIL_ASCENDING_NORTH 4
#define RAIL_ASCENDING_SOUTH 5
#define RAIL_SOUTH_EAST 6
#define RAIL_SOUTH_WEST 7
#define RAIL_NORTH_WEST 8
#define RAIL_NORTH_EAST 9

#define POWERED_META_OFFSET 3

@Mixin(BlockBaseRailLogic.class)
public abstract class BlockBaseRailLogicMixins {
    
    @Shadow
    public World logicWorld;
    @Shadow
    public int railX;
    @Shadow
    public int railY;
    @Shadow
    public int railZ;
    @Shadow
    public boolean isStraightRail;
    @Shadow
    public List<ChunkPosition> railChunkPosition;
    
    @Shadow
    public abstract boolean canConnectFrom(int x, int y, int z);
#define hasNeighborRail(...) canConnectFrom(__VA_ARGS__)
    
    @Shadow
    public abstract void setBasicRail(int par1);
#define updateConnections(...) setBasicRail(__VA_ARGS__)

    @Shadow
    public abstract BlockBaseRailLogic getRailLogic(ChunkPosition chunkPosition);
#define getRail(...) getRailLogic(__VA_ARGS__)
    
    @Overwrite
    public void func_94511_a(boolean is_powered, boolean par2) {
        World world = this.logicWorld;
        int selfX = this.railX;
        int selfY = this.railY;
        int selfZ = this.railZ;
        boolean is_straight_rail = this.isStraightRail;
        
        int meta = world.getBlockMetadata(selfX, selfY, selfZ);
        int previous_shape = meta;
        if (is_straight_rail) {
            previous_shape &= 7;
        }
        
        boolean has_north_connect = this.hasNeighborRail(selfX, selfY, selfZ - 1);
        boolean has_south_connect = this.hasNeighborRail(selfX, selfY, selfZ + 1);
        boolean has_west_connect = this.hasNeighborRail(selfX - 1, selfY, selfZ);
        boolean has_east_connect = this.hasNeighborRail(selfX + 1, selfY, selfZ);
        
        int rail_shape = RAIL_INVALID;

        if (
            (has_north_connect | has_south_connect) &
            BOOL_INVERT(has_west_connect | has_east_connect)
        ) {
            rail_shape = RAIL_NORTH_SOUTH;
        }

        if (
            (has_west_connect | has_east_connect) &
            BOOL_INVERT(has_north_connect | has_south_connect)
        ) {
            rail_shape = RAIL_EAST_WEST;
        }

        if (!is_straight_rail) {
            if (
                has_south_connect &
                has_east_connect &
                BOOL_INVERT(has_north_connect | has_west_connect)
            ) {
                rail_shape = RAIL_SOUTH_EAST;
            }

            if (
                has_south_connect &
                has_west_connect &
                BOOL_INVERT(has_north_connect | has_east_connect)
            ) {
                rail_shape = RAIL_SOUTH_WEST;
            }

            if (
                has_north_connect &
                has_west_connect &
                BOOL_INVERT(has_south_connect | has_east_connect)
            ) {
                rail_shape = RAIL_NORTH_WEST;
            }

            if (
                has_north_connect &
                has_east_connect &
                BOOL_INVERT(has_south_connect | has_west_connect)
            ) {
                rail_shape = RAIL_NORTH_EAST;
            }
        }

        if (rail_shape == RAIL_INVALID) {
            if (
                (has_north_connect | has_south_connect) &
                (has_west_connect | has_east_connect)
            ) {
                rail_shape = previous_shape;
            }
            else if (has_north_connect | has_south_connect) {
                rail_shape = RAIL_NORTH_SOUTH;
            }
            else if (has_west_connect | has_east_connect) {
                rail_shape = RAIL_EAST_WEST;
            }

            if (!is_straight_rail) {
                if (is_powered) {
                    if (has_south_connect & has_east_connect) {
                        rail_shape = RAIL_SOUTH_EAST;
                    }

                    if (has_west_connect & has_south_connect) {
                        rail_shape = RAIL_SOUTH_WEST;
                    }

                    if (has_east_connect & has_north_connect) {
                        rail_shape = RAIL_NORTH_EAST;
                    }

                    if (has_north_connect & has_west_connect) {
                        rail_shape = RAIL_NORTH_WEST;
                    }
                }
                else {
                    if (has_north_connect & has_west_connect) {
                        rail_shape = RAIL_NORTH_WEST;
                    }

                    if (has_east_connect & has_north_connect) {
                        rail_shape = RAIL_NORTH_EAST;
                    }

                    if (has_west_connect & has_south_connect) {
                        rail_shape = RAIL_SOUTH_WEST;
                    }

                    if (has_south_connect & has_east_connect) {
                        rail_shape = RAIL_SOUTH_EAST;
                    }
                }
            }
        }

        if (rail_shape == RAIL_NORTH_SOUTH) {
            if (BlockRailBase.isRailBlockAt(world, selfX, selfY + 1, selfZ - 1)) {
                rail_shape = RAIL_ASCENDING_NORTH;
            }

            if (BlockRailBase.isRailBlockAt(world, selfX, selfY + 1, selfZ + 1)) {
                rail_shape = RAIL_ASCENDING_SOUTH;
            }
        }

        if (rail_shape == RAIL_EAST_WEST) {
            if (BlockRailBase.isRailBlockAt(world, selfX + 1, selfY + 1, selfZ)) {
                rail_shape = RAIL_ASCENDING_EAST;
            }

            if (BlockRailBase.isRailBlockAt(world, selfX - 1, selfY + 1, selfZ)) {
                rail_shape = RAIL_ASCENDING_WEST;
            }
        }

        if (rail_shape == RAIL_INVALID) {
            rail_shape = previous_shape;
        }

        this.updateConnections(rail_shape);
        int new_rail_shape = rail_shape;

        if (is_straight_rail) {
            new_rail_shape = meta & 8 | rail_shape;
        }
        //if (!world.isRemote) AddonHandler.logMessage("Rail metas "+meta+" "+previous_shape+" "+rail_shape+" "+new_rail_shape);

        if (par2 || meta != new_rail_shape) {
            world.setBlockMetadataWithNotify(selfX, selfY, selfZ, new_rail_shape, UPDATE_NEIGHBORS | UPDATE_CLIENTS);

            List<ChunkPosition> position_list = this.railChunkPosition;
            for (ChunkPosition position : position_list) {
                IBlockBaseRailLogicAccessMixins rail_logic = (IBlockBaseRailLogicAccessMixins)this.getRail(position);

                if (rail_logic != null) {
                    rail_logic.removeSoftConnections();

                    if (rail_logic.callCanConnectTo((BlockBaseRailLogic)(Object)this)) {
                        rail_logic.connectTo((BlockBaseRailLogic)(Object)this);
                    }
                }
            }
        }
    }
}