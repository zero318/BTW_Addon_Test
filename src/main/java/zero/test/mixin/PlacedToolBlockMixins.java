package zero.test.mixin;
import net.minecraft.src.*;
import btw.block.blocks.PlacedToolBlock;
import btw.block.tileentity.PlacedToolTileEntity;
import btw.item.items.ToolItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PlacedToolBlock.class)
public abstract class PlacedToolBlockMixins extends BlockContainer {
    public PlacedToolBlockMixins() {
        super(0, null);
    }
    @Override
    public void breakBlock(World world, int x, int y, int z, int blockId, int meta) {
        TileEntity tileEntity;
        if ((tileEntity = world.getBlockTileEntity(x, y, z)) instanceof PlacedToolTileEntity) {
            ((PlacedToolTileEntity)tileEntity).ejectContents();
        }
        world.func_96440_m(x, y, z, blockId);
        super.breakBlock(world, x, y, z, blockId, meta);
    }
    @Overwrite
    public boolean onRotatedAroundBlockOnTurntableToFacing(World world, int x, int y, int z, int direction) {
        return true;
    }
    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }
    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int flatDirection) {
        TileEntity tileEntity;
        if ((tileEntity = world.getBlockTileEntity(x, y, z)) instanceof PlacedToolTileEntity) {
            ItemStack stack;
            if ((stack = ((PlacedToolTileEntity)tileEntity).getToolStack()) != null) {
                int maxDamage = stack.getMaxDamage();
                int currentDamage = stack.getItemDamage();
                if (currentDamage != maxDamage - 1) {
                    return MathHelper.floor_float(((float)(maxDamage - currentDamage) / (float)maxDamage) * 14.0F) + 1;
                }
            }
        }
        return 0;
    }
}
