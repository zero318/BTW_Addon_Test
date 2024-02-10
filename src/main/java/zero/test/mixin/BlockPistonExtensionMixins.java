package zero.test.mixin;
import net.minecraft.src.Block;
import net.minecraft.src.World;
import net.minecraft.src.BlockPistonBase;
import net.minecraft.src.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
// Block piston reactions
//#define getInputSignal(...) func_94482_f(__VA_ARGS__)
@Mixin(BlockPistonExtension.class)
public abstract class BlockPistonExtensionMixins extends Block {
    public BlockPistonExtensionMixins() {
        super(0, null);
    }
    @Override
    public boolean triggersBuddy() {
        return false;
    }
    @Override
    public boolean hasLargeCenterHardPointToFacing(IBlockAccess blockAccess, int x, int y, int z, int direction, boolean ignoreTransparency) {
        return direction == (((blockAccess.getBlockMetadata(x, y, z))&7));
    }
    @Environment(EnvType.CLIENT)
    @Override
    public boolean shouldRenderNeighborFullFaceSide(IBlockAccess blockAccess, int x, int y, int z, int direction) {
        return direction != (((blockAccess.getBlockMetadata(x, y, z))&7));
    }
}
