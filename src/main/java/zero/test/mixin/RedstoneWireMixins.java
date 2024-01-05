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
//#define getInputSignal(...) func_94482_f(__VA_ARGS__)
@Mixin(BlockRedstoneWire.class)
public class RedstoneWireMixins implements IBlockRedstoneWireMixins {
    //@Override
    //public void updateIndirectNeighbourShapes(World world, int X, int Y, int Z) {
        //((IRedstoneWireAccessMixins)(Object)this).callUpdateAndPropagateCurrentStrength(world, X, Y, Z);
    //}
    public boolean canRedstoneConnectToSide(IBlockAccess block_access, int X, int Y, int Z, int flat_direction) {
        return true;
    }
    @Overwrite
    public static boolean isPowerProviderOrWire(IBlockAccess block_access, int X, int Y, int Z, int flat_direction) {
        int block_id = block_access.getBlockId(X, Y, Z);
        if (block_id != 0) {
            if (
                block_id == Block.redstoneWire.blockID
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
/*
    0 East Side
    1 East Up
    2 East Down
    3 West Side
    4 West Up
    5 West Down
    6 South Side
    7 South Up
    8 South Down
    9 North Side
    10 North Up
    11 North Down
*/

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
        boolean above_is_solid = !((block)==null) && ((IBlockMixins)block).isRedstoneConductor(block_access, X, Y, Z);
        Y -= 2;
        block = Block.blocksList[block_access.getBlockId(X, Y, Z)];
        boolean below_is_solid = !((block)==null) && ((IBlockMixins)block).isRedstoneConductor(block_access, X, Y, Z);
        ++Y;
        int direction = 2;
        do {
            connections <<= 3;
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
                    block.hasLargeCenterHardPointToFacing(block_access, nextX, Y, nextZ, ((direction)^1), true)
                ) {
                    connections += 2;
                }
                connections += 1;
            }
            else if (isPowerProviderOrWire(block_access, nextX, Y, nextZ, Direction.facingToDirection[direction])) {
                connections += 1;
            }
            if (
                (
                    ((block)==null) ||
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
        if (!for_rendering) {
            if (!(((connections)&~0x1C0)!=0)) {
                connections_ret += 0x200;
            }
            if (!(((connections)&~0xE00)!=0)) {
                connections_ret += 0x040;
            }
            if (!((connections)>0x007)) {
                connections_ret += 0x008;
            }
            if (!(((connections)&~0x038)!=0)) {
                connections_ret += 0x001;
            }
        }
        return connections_ret;
    }
    @Overwrite
    public int isProvidingWeakPower(IBlockAccess block_access, int X, int Y, int Z, int direction) {
        BlockRedstoneWire self = (BlockRedstoneWire)(Object)this;
        if (
            direction != 0 &&
            self.canProvidePower()
        ) {
            int power = (((block_access.getBlockMetadata(X, Y, Z))));
            if (power != 0) {
                int connections = this.getConnectingSides(block_access, X, Y, Z, false);
                                                                                                                           ;
                switch (direction) {
                    case 2:
                        if (!(((connections)&0x040)!=0)) {
                            break;
                        }
                    // Apparently this isn't called in cases where the block
                    // the wire is sitting on isn't conductive?
                    default:
                        return power;
                    case 3:
                        if (!(((connections)&0x200)!=0)) {
                            break;
                        }
                        return power;
                    case 4:
                        if (!(((connections)&0x001)!=0)) {
                            break;
                        }
                        return power;
                    case 5:
                        if (!(((connections)&0x008)!=0)) {
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
            ((connections & (0x001 | 0x008)) != 0) ^
            ((connections & (0x040 | 0x200)) == 0)
        ) {
            return 2;
        }
        return 0;
    }
    @Environment(EnvType.CLIENT)
    public Icon get_texture_by_index(int icon_index) {
        IRedstoneWireAccessMixins self = (IRedstoneWireAccessMixins)this;
        switch (icon_index) {
            case 0:
                // Line Texture
                return self.getField_94410_cO();
            case 1:
                // Line Overlay
                return self.getField_94412_cQ();
            case 2:
                // Cross Texture
                return self.getField_94413_c();
            default: // CROSS_OVERLAY_TEXTURE_INDEX
                // Cross Overlay
                return self.getField_94411_cP();
        }
    }
}
