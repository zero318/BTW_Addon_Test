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
//#define getInputSignal(...) func_94482_f(__VA_ARGS__)
@Mixin(BlockRedstoneWire.class)
public abstract class RedstoneWireMixins implements IBlockRedstoneWireMixins {
    //@Override
    //public void updateIndirectNeighbourShapes(World world, int x, int y, int z) {
        //((IRedstoneWireAccessMixins)(Object)this).callUpdateAndPropagateCurrentStrength(world, x, y, z);
    //}
    public boolean canRedstoneConnectToSide(IBlockAccess blockAccess, int x, int y, int z, int flatDirection) {
        return true;
    }
    @Overwrite
    public static boolean isPowerProviderOrWire(IBlockAccess blockAccess, int x, int y, int z, int flatDirection) {
        int blockId = blockAccess.getBlockId(x, y, z);
        if (blockId != 0) {
            if (
                blockId == Block.redstoneWire.blockID
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
    public boolean redirect_isBlockNormalCube(World world, int x, int y, int z) {
        return ((IWorldMixins)world).isBlockRedstoneConductor(x, y, z);
    }
    public int getConnectingSides(IBlockAccess blockAccess, int x, int y, int z, boolean forRendering) {
        BlockRedstoneWire self = (BlockRedstoneWire)(Object)this;
        int connections = 0;
        ++y;
        Block block = Block.blocksList[blockAccess.getBlockId(x, y, z)];
        boolean aboveIsConductive = !((block)==null) && ((IBlockMixins)block).isRedstoneConductor(blockAccess, x, y, z);
        y -= 2;
        block = Block.blocksList[blockAccess.getBlockId(x, y, z)];
        boolean belowIsConductive =
            !forRendering &&
            !((block)==null) && ((IBlockMixins)block).isRedstoneConductor(blockAccess, x, y, z);
        ++y;
        int direction = 2;
        do {
            connections <<= 3;
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
                    (!((block)==null) && (
                        block.hasCenterHardPointToFacing(blockAccess, nextX, y, nextZ, ((direction)^1), true) ||
                        block.isNormalCube(blockAccess, nextX, y, nextZ) // dangerous stinky hack to make more things show up
                    ))
                ) {
                    connections += 02;
                    if (
                        forRendering &&
                        !((block)==null) && block.shouldRenderNeighborFullFaceSide(blockAccess, nextX, y, nextZ, ((direction)^1))
                    ) {
                        connections += 04;
                    }
                }
                connections += 01;
            }
            else if (isPowerProviderOrWire(blockAccess, nextX, y, nextZ, Direction.facingToDirection[direction])) {
                connections += 01;
            }
            if (
                (
                    ((block)==null) ||
                    !((IBlockMixins)block).isRedstoneConductor(blockAccess, nextX, y, nextZ)
                ) &&
                blockAccess.getBlockId(nextX, y - 1, nextZ) == Block.redstoneWire.blockID
            ) {
                connections |= 01;
                if (belowIsConductive) {
                    connections += 04;
                }
            }
        } while (((++direction)<=5));
        int connectionsRet = connections;
        if (!forRendering) {
            if (!(((connections)&~00700)!=0)) {
                connectionsRet += 01000;
            }
            if (!(((connections)&~07000)!=0)) {
                connectionsRet += 00100;
            }
            if (!((connections)>00007)) {
                connectionsRet += 00010;
            }
            if (!(((connections)&~00070)!=0)) {
                connectionsRet += 00001;
            }
        }
        return connectionsRet;
    }
    @Overwrite
    public int isProvidingWeakPower(IBlockAccess block_access, int x, int y, int z, int direction) {
        BlockRedstoneWire self = (BlockRedstoneWire)(Object)this;
        if (
            direction != 0 &&
            self.canProvidePower()
        ) {
            int power = (((block_access.getBlockMetadata(x, y, z))));
            if (power != 0) {
                int connections = this.getConnectingSides(block_access, x, y, z, false);
                                                                                                                           ;
                switch (direction) {
                    case 2:
                        if (!(((connections)&00100)!=0)) {
                            break;
                        }
                    // Apparently this isn't called in cases where the block
                    // the wire is sitting on isn't conductive?
                    default:
                        return power;
                    case 3:
                        if (!(((connections)&01000)!=0)) {
                            break;
                        }
                        return power;
                    case 4:
                        if (!(((connections)&00001)!=0)) {
                            break;
                        }
                        return power;
                    case 5:
                        if (!(((connections)&00010)!=0)) {
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
            ((connections & (00001 | 00010)) != 0) ^
            ((connections & (00100 | 01000)) == 0)
        ) {
            return 2;
        }
        return 0;
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
            case 0:
                // Line Texture
                return this.field_94410_cO;
            case 1:
                // Line Overlay
                return this.field_94412_cQ;
            case 2:
                // Cross Texture
                return this.field_94413_c;
            default: // CROSS_OVERLAY_TEXTURE_INDEX
                // Cross Overlay
                return this.field_94411_cP;
        }
    }
    public int getPlatformMobilityFlag(World world, int x, int y, int z) {
        return 2;
    }
    public int adjustMetadataForPlatformMove(int meta) {
        return 0;
    }
}
