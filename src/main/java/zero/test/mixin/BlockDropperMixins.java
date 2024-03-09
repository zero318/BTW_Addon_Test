package zero.test.mixin;
import net.minecraft.src.*;
import btw.util.MiscUtils;
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
// Block piston reactions
@Mixin(BlockDropper.class)
public abstract class BlockDropperMixins extends BlockDispenser {
    public BlockDropperMixins() {
        super(0);
    }
    @Override
    public int getFacing(int meta) {
        return (((meta)&7));
    }
    @Override
    public int setFacing(int meta, int facing) {
        return (((meta)&8|(facing)));
    }
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving entityLiving, ItemStack stack) {
        this.setFacing(world, x, y, z, MiscUtils.convertPlacingEntityOrientationToBlockFacingReversed(entityLiving));
        if (stack.hasDisplayName()) {
            ((TileEntityDispenser)world.getBlockTileEntity(x, y, z)).setCustomName(stack.getDisplayName());
        }
    }
    @Shadow
    @Final
    public IBehaviorDispenseItem dropperDefaultBehaviour;
    @Overwrite
    public void dispense(World world, int x, int y, int z) {
        BlockSourceImpl blockSource = new BlockSourceImpl(world, x, y, z);
        TileEntityDispenser tileEntity = (TileEntityDispenser)blockSource.getBlockTileEntity();
        if (tileEntity != null) {
            int slot = tileEntity.getRandomStackFromInventory();
            if (slot < 0) {
                world.playAuxSFX(1001, x, y, z, 0);
            }
            else {
                ItemStack newStack = this.dropperDefaultBehaviour.dispense(blockSource, tileEntity.getStackInSlot(slot));
                if (newStack != null && newStack.stackSize == 0) {
                    newStack = null;
                }
                tileEntity.setInventorySlotContents(slot, newStack);
            }
        }
    }
}
