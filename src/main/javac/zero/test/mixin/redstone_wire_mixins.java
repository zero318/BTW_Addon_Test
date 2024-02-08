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
//import zero.test.mixin.IRedstoneWireAccessMixins;

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
    //public void updateIndirectNeighbourShapes(World world, int x, int y, int z) {
        //((IRedstoneWireAccessMixins)(Object)this).callUpdateAndPropagateCurrentStrength(world, x, y, z);
    //}
    
#if ENABLE_BETTER_REDSTONE_WIRE_CONNECTIONS
    public boolean canRedstoneConnectToSide(IBlockAccess blockAccess, int x, int y, int z, int flatDirection) {
        return true;
    }

    @Overwrite
    public static boolean isPowerProviderOrWire(IBlockAccess blockAccess, int x, int y, int z, int flatDirection) {
        int blockId = blockAccess.getBlockId(x, y, z);
        if (blockId != 0) {
            if (
                blockId == Block.redstoneWire.blockID
#if ENABLE_REDSTONE_WIRE_DOT_SHAPE
                || blockId == DUST_DOT_ID
#endif
            ) {
                return true;
            }
            if (flatDirection >= 0) {
                Block block = Block.blocksList[blockId];
                return ((IBlockMixins)block).canRedstoneConnectToSide(blockAccess, x, y, z, flatDirection);
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

#if ENABLE_MODERN_SUPPORT_LOGIC == MODERN_SUPPORT_LOGIC_PER_BLOCK
    @Overwrite
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        Block block = Block.blocksList[world.getBlockId(x, --y, z)];
        return !BLOCK_IS_AIR(block) && block.hasLargeCenterHardPointToFacing(world, x, y, z, DIRECTION_UP, true);
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
    public boolean redirect_isBlockNormalCube(World world, int x, int y, int z) {
        return ((IWorldMixins)world).isBlockRedstoneConductor(x, y, z);
    }
    
    public int getConnectingSides(IBlockAccess blockAccess, int x, int y, int z, boolean forRendering) {
        BlockRedstoneWire self = (BlockRedstoneWire)(Object)this;
        
        int connections = 0;
        
        ++y;
        Block block = Block.blocksList[blockAccess.getBlockId(x, y, z)];
        boolean aboveIsConductive = !BLOCK_IS_AIR(block) && ((IBlockMixins)block).isRedstoneConductor(blockAccess, x, y, z);
        y -= 2;
        block = Block.blocksList[blockAccess.getBlockId(x, y, z)];
        boolean belowIsConductive =
#if ENABLE_MODERN_SUPPORT_LOGIC != MODERN_SUPPORT_LOGIC_DISABLED
            !forRendering &&
#endif
            !BLOCK_IS_AIR(block) && ((IBlockMixins)block).isRedstoneConductor(blockAccess, x, y, z);
        ++y;
        
        int direction = 2;
        do {
            connections <<= BITS_PER_DIRECTION;
            int nextX = x + Facing.offsetsXForSide[direction];
            int nextZ = z + Facing.offsetsZForSide[direction];
            block = Block.blocksList[blockAccess.getBlockId(nextX, y, nextZ)];
            if (
                !aboveIsConductive &&
                /*
                self.canPlaceBlockAt(nextX, y, nextZ) &&
                isPowerProviderOrWire(blockAccess, nextX, y + 1, nextZ, Direction.facingToDirection[direction])
                */
                blockAccess.getBlockId(nextX, y + 1, nextZ) == Block.redstoneWire.blockID
            ) {
                if (
                    !forRendering ||
                    // These conditions only affect rendering of vertical
                    // connections, primarily to prevent floating dust.
                    (!BLOCK_IS_AIR(block) && (
                        block.hasCenterHardPointToFacing(blockAccess, nextX, y, nextZ, OPPOSITE_DIRECTION(direction), true) ||
                        block.isNormalCube(blockAccess, nextX, y, nextZ) // dangerous stinky hack to make more things show up
                    ))
                ) {
                    connections += UP_CONNECTION;
#if ENABLE_MODERN_SUPPORT_LOGIC != MODERN_SUPPORT_LOGIC_DISABLED
                    if (
                        forRendering &&
                        !BLOCK_IS_AIR(block) && block.shouldRenderNeighborFullFaceSide(blockAccess, nextX, y, nextZ, OPPOSITE_DIRECTION(direction))
                    ) {
                        connections += UP_CONNECTION_RENDER_BACK;
                    }
#endif
                }
                connections += SIDE_CONNECTION;
            }
            else if (isPowerProviderOrWire(blockAccess, nextX, y, nextZ, Direction.facingToDirection[direction])) {
                connections += SIDE_CONNECTION;
            }
            if (
                (
                    BLOCK_IS_AIR(block) ||
                    !((IBlockMixins)block).isRedstoneConductor(blockAccess, nextX, y, nextZ)
                ) &&
                blockAccess.getBlockId(nextX, y - 1, nextZ) == Block.redstoneWire.blockID
            ) {
                connections |= SIDE_CONNECTION;
                if (belowIsConductive) {
                    connections += DOWN_CONNECTION;
                }
            }
        } while (DIRECTION_IS_VALID(++direction));
        
        int connectionsRet = connections;
#if !ENABLE_REDSTONE_WIRE_DOT_SHAPE
        if (!forRendering) {
#endif
            if (!HAS_ANY_NWE_CONNECTION(connections)) {
                connectionsRet += NORTH_SIDE;
            }
            if (!HAS_ANY_SWE_CONNECTION(connections)) {
                connectionsRet += SOUTH_SIDE;
            }
            if (!HAS_ANY_NSW_CONNECTION(connections)) {
                connectionsRet += WEST_SIDE;
            }
            if (!HAS_ANY_NSE_CONNECTION(connections)) {
                connectionsRet += EAST_SIDE;
            }
#if !ENABLE_REDSTONE_WIRE_DOT_SHAPE
        }
#endif
        return connectionsRet;
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
    public int isProvidingWeakPower(IBlockAccess block_access, int x, int y, int z, int direction) {
        BlockRedstoneWire self = (BlockRedstoneWire)(Object)this;
        if (
            direction != DIRECTION_DOWN &&
            self.canProvidePower()
        ) {
            int power = READ_META_FIELD(block_access.getBlockMetadata(x, y, z), POWER);
            if (power != 0) {
                int connections = this.getConnectingSides(block_access, x, y, z, false);
                WIRE_POWER_DEBUG(""+x+" "+y+" "+z+" ("+Facing.facings[direction]+") "+make_connections_string(connections));
                
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
    @Shadow
    public Icon field_94413_c;
    @Environment(EnvType.CLIENT)
    @Shadow
    public Icon field_94410_cO;
    @Environment(EnvType.CLIENT)
    @Shadow
    public Icon field_94411_cP;
    @Environment(EnvType.CLIENT)
    @Shadow
    public Icon field_94412_cQ;

    @Environment(EnvType.CLIENT)
    public Icon get_texture_by_index(int icon_index) {
        switch (icon_index) {
            case LINE_TEXTURE_INDEX:
                // Line Texture
                return this.field_94410_cO;
            case LINE_OVERLAY_TEXTURE_INDEX:
                // Line Overlay
                return this.field_94412_cQ;
            case CROSS_TEXTURE_INDEX:
                // Cross Texture
                return this.field_94413_c;
            default: // CROSS_OVERLAY_TEXTURE_INDEX
                // Cross Overlay
                return this.field_94411_cP;
        }
    }
#endif

#if ENABLE_PLATFORM_FIXES
    public int getPlatformMobilityFlag(World world, int x, int y, int z) {
        return PLATFORM_CAN_LIFT;
    }
    
    public int adjustMetadataForPlatformMove(int meta) {
        return 0;
    }
#endif
}