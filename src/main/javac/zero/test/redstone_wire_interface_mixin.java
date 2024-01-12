package zero.test;

import net.minecraft.src.*;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

#include "feature_flags.h"
#include "ids.h"

public interface IBlockRedstoneWireMixins {
    
#if ENABLE_MODERN_REDSTONE_WIRE
    
    public int getConnectingSides(IBlockAccess blockAccess, int x, int y, int z, boolean forRendering);

#if ENABLE_REDSTONE_WIRE_DOT_SHAPE
    public boolean getIsDot();
    public void setIsDot(boolean value);
    
    public static boolean isRedstoneWireBlockID(int blockId) {
        return blockId == Block.redstoneWire.blockID || blockId == DUST_DOT_ID;
    }
    default public boolean isRedstoneWireBlockID(int blockId) {
        return IBlockRedstoneWireMixins.isRedstoneWireBlockID(blockId);
    }
#endif

    @Environment(EnvType.CLIENT)
    public int get_texture_index_for_connections(int connections);
    
    @Environment(EnvType.CLIENT)
    public Icon get_texture_by_index(int icon_index);
#endif
}