package zero.test.mixin;

import net.minecraft.src.*;

import btw.AddonHandler;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

import zero.test.IBlockMixins;
import zero.test.IWorldMixins;
import zero.test.IBlockRedstoneWireMixins;
import zero.test.mixin.IRedstoneWireAccessMixins;

#include "..\func_aliases.h"
#include "..\feature_flags.h"
#include "..\util.h"

#define POWER_META_OFFSET 0

#define WIRE_POWER_PRINT_DEBUGGING 0

#if WIRE_POWER_PRINT_DEBUGGING
#define WIRE_POWER_DEBUG(...) if (block_access instanceof World && !((World)block_access).isRemote) AddonHandler.logMessage(__VA_ARGS__)
#else
#define WIRE_POWER_DEBUG(...)
#endif

@Mixin(BlockRedstoneWire.class)
public class RedstoneWireMixins implements IBlockRedstoneWireMixins {
    
    //@Override
    //public void updateIndirectNeighbourShapes(World world, int X, int Y, int Z) {
        //((IRedstoneWireAccessMixins)(Object)this).callUpdateAndPropagateCurrentStrength(world, X, Y, Z);
    //}
    
#if ENABLE_BETTER_REDSTONE_WIRE_CONNECTIONS
    public boolean canRedstoneConnectToSide(IBlockAccess block_access, int X, int Y, int Z, int flat_direction) {
        return true;
    }

    @Overwrite
    public static boolean isPowerProviderOrWire(IBlockAccess block_access, int X, int Y, int Z, int flat_direction) {
        int block_id = block_access.getBlockId(X, Y, Z);
        if (block_id != 0) {
            if (
                block_id == Block.redstoneWire.blockID
#if ENABLE_REDSTONE_WIRE_DOT_SHAPE
                || block_id == DUST_DOT_ID
#endif
            ) {
                return true;
            }
            if (flat_direction >= 0) {
                Block block = Block.blocksList[block_id];
                return ((IBlockMixins)block).canRedstoneConnectToSide(block_access, X, Y, Z, flat_direction);
            }
        }
        return false;
    }
#endif

#if ENABLE_MODERN_REDSTONE_WIRE

#if ENABLE_REDSTONE_WIRE_DOT_SHAPE
    boolean is_dot;
    
    public boolean getIsDot() {
        return this.is_dot;
    }
    
    public void setIsDot(boolean value) {
        this.is_dot = value;
    }
#endif

#include "redstone_wire_defines.h"

    @Redirect(
        method = { "calculateCurrentChanges", "onBlockAdded", "breakBlock" },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/World;isBlockNormalCube(III)Z"
        )
    )
    public boolean redirect_isBlockNormalCube(World world, int X, int Y, int Z) {
        return ((IWorldMixins)world).isBlockRedstoneConductor(X, Y, Z);
    }
    
    public int getConnectingSides(IBlockAccess block_access, int X, int Y, int Z, boolean for_rendering) {
        BlockRedstoneWire self = (BlockRedstoneWire)(Object)this;
        
        int connections = 0;
        
        ++Y;
        Block block = Block.blocksList[block_access.getBlockId(X, Y, Z)];
        boolean above_is_solid = !BLOCK_IS_AIR(block) && ((IBlockMixins)block).isRedstoneConductor(block_access, X, Y, Z);
        Y -= 2;
        block = Block.blocksList[block_access.getBlockId(X, Y, Z)];
        boolean below_is_solid = !BLOCK_IS_AIR(block) && ((IBlockMixins)block).isRedstoneConductor(block_access, X, Y, Z);
        ++Y;
        
        int direction = 2;
        do {
            connections <<= BITS_PER_DIRECTION;
            int nextX = X + Facing.offsetsXForSide[direction];
            int nextZ = Z + Facing.offsetsZForSide[direction];
            int neighbor_id = block_access.getBlockId(nextX, Y, nextZ);
            block = Block.blocksList[neighbor_id];
            if (
                !above_is_solid &&
                /*
                self.canPlaceBlockAt(nextX, Y, nextZ) &&
                isPowerProviderOrWire(block_access, nextX, Y + 1, nextZ, Direction.facingToDirection[direction])
                */
                block_access.getBlockId(nextX, Y + 1, nextZ) == Block.redstoneWire.blockID
            ) {
                if (
                    !for_rendering ||
                    neighbor_id == Block.glowStone.blockID || // TODO: Block property?
                    block.hasLargeCenterHardPointToFacing(block_access, nextX, Y, nextZ, OPPOSITE_DIRECTION(direction))
                ) {
                    connections += 2;
                }
                ++connections;
            }
            else if (isPowerProviderOrWire(block_access, nextX, Y, nextZ, Direction.facingToDirection[direction])) {
                ++connections;
            }
            if (
                (
                    BLOCK_IS_AIR(block) ||
                    !((IBlockMixins)block).isRedstoneConductor(block_access, nextX, Y, nextZ)
                ) &&
                block_access.getBlockId(nextX, Y - 1, nextZ) == Block.redstoneWire.blockID
            ) {
                connections |= 1;
                if (below_is_solid) {
                    connections += 4;
                }
            }
        } while (++direction < 6);
        
        int connections_ret = connections;
#if !ENABLE_REDSTONE_WIRE_DOT_SHAPE
        if (!for_rendering) {
#endif
            if (!HAS_ANY_NWE_CONNECTION(connections)) {
                connections_ret += NORTH_SIDE;
            }
            if (!HAS_ANY_SWE_CONNECTION(connections)) {
                connections_ret += SOUTH_SIDE;
            }
            if (!HAS_ANY_NSW_CONNECTION(connections)) {
                connections_ret += WEST_SIDE;
            }
            if (!HAS_ANY_NSE_CONNECTION(connections)) {
                connections_ret += EAST_SIDE;
            }
#if !ENABLE_REDSTONE_WIRE_DOT_SHAPE
        }
#endif
        return connections_ret;
    }
    
#if WIRE_POWER_PRINT_DEBUGGING
    private static final String[] connection_labels = new String[] {
        "East Side, ",
        "East Up, ",
        "East Down, ",
        "West Side, ",
        "West Up, ",
        "West Down, ",
        "South Side, ",
        "South Up, ",
        "South Down, ",
        "North Side, ",
        "North Up, ",
        "North Down, "
    };
    
    private String make_connections_string(int connections) {
        String ret = "";
        for (int i = 0; i < 12; ++i) {
            if ((connections & 1 << i) != 0) {
                ret += connection_labels[i];
            }
        }
        return ret;
    }
#endif
    
    @Overwrite
    public int isProvidingWeakPower(IBlockAccess block_access, int X, int Y, int Z, int direction) {
        BlockRedstoneWire self = (BlockRedstoneWire)(Object)this;
        if (
            direction != DIRECTION_DOWN &&
            self.canProvidePower()
        ) {
            int power = READ_META_FIELD(block_access.getBlockMetadata(X, Y, Z), POWER);
            if (power != 0) {
                int connections = this.getConnectingSides(block_access, X, Y, Z, false);
                WIRE_POWER_DEBUG(""+X+" "+Y+" "+Z+" ("+Facing.facings[direction]+") "+make_connections_string(connections));
                
                switch (direction) {
                    case DIRECTION_NORTH:
                        if (!HAS_SOUTH_SIDE_CONNECTION(connections)) {
                            break;
                        }
                    // Apparently this isn't called in cases where the block
                    // the wire is sitting on isn't conductive?
                    default:
                        return power;
                    case DIRECTION_SOUTH:
                        if (!HAS_NORTH_SIDE_CONNECTION(connections)) {
                            break;
                        }
                        return power;
                    case DIRECTION_WEST:
                        if (!HAS_EAST_SIDE_CONNECTION(connections)) {
                            break;
                        }
                        return power;
                    case DIRECTION_EAST:
                        if (!HAS_WEST_SIDE_CONNECTION(connections)) {
                            break;
                        }
                        return power;
                }
            }
        }
        return 0;
    }
    
    @Environment(EnvType.CLIENT)
    public int get_texture_index_for_connections(int connections) {
        if (
            ((connections & (EAST_SIDE | WEST_SIDE)) != 0) ^
            ((connections & (SOUTH_SIDE | NORTH_SIDE)) == 0)
        ) {
            return CROSS_TEXTURE_INDEX;
        }
        return LINE_TEXTURE_INDEX;
    }

    @Environment(EnvType.CLIENT)
    public Icon get_texture_by_index(int icon_index) {
        IRedstoneWireAccessMixins self = (IRedstoneWireAccessMixins)this;
        switch (icon_index) {
            case LINE_TEXTURE_INDEX:
                // Line Texture
                return self.getField_94410_cO();
            case LINE_OVERLAY_TEXTURE_INDEX:
                // Line Overlay
                return self.getField_94412_cQ();
            case CROSS_TEXTURE_INDEX:
                // Cross Texture
                return self.getField_94413_c();
            default: // CROSS_OVERLAY_TEXTURE_INDEX
                // Cross Overlay
                return self.getField_94411_cP();
        }
    }
#endif
}