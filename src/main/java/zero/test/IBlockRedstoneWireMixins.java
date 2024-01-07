package zero.test;
import net.minecraft.src.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
// Block piston reactions

public interface IBlockRedstoneWireMixins {
    public int getConnectingSides(IBlockAccess block_access, int X, int Y, int Z, boolean for_rendering);
    @Environment(EnvType.CLIENT)
    public int get_texture_index_for_connections(int connections);
    @Environment(EnvType.CLIENT)
    public Icon get_texture_by_index(int icon_index);
}
