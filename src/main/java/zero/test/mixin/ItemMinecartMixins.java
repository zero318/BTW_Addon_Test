package zero.test.mixin;
import net.minecraft.src.*;
import btw.item.items.MinecartItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import zero.test.IWorldMixins;
import java.util.List;
// Block piston reactions
@Mixin(MinecartItem.class)
public abstract class ItemMinecartMixins extends ItemMinecart {
    public ItemMinecartMixins() {
        super(0, 0);
    }
    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int par7, float par8, float par9, float par10) {
        int blockId = world.getBlockId(x, y, z);
        if (BlockRailBase.isRailBlock(blockId)) {
            if (!world.isRemote) {
                int railShape = world.getBlockMetadata(x, y, z);
                if (((BlockRailBase)Block.blocksList[blockId]).isPowered()) {
                    railShape &= 7;
                }
                ItemMinecart self = (ItemMinecart)(Object)this;
                EntityMinecart minecart = EntityMinecart.createMinecart(world, (double)x + 0.5D, (double)y + (!(((railShape)>=2)&((railShape)<=5)) ? 0.0625D : 0.5625D), (double)z + 0.5D, self.minecartType);
                if (itemStack.hasDisplayName()) {
                    minecart.func_96094_a(itemStack.getDisplayName());
                }
                //minecart.rotationYaw = (float)(((MathHelper.floor_double((double)(entityPlayer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3) - 1) * 90);
                world.spawnEntityInWorld(minecart);
            }
            --itemStack.stackSize;
            return true;
        }
        return false;
    }
    @Overwrite
    public boolean onItemUsedByBlockDispenser(ItemStack itemStack, World world, int x, int y, int z, int direction) {
        int offsetX = Facing.offsetsXForSide[direction];
        int offsetZ = Facing.offsetsZForSide[direction];
        int facingX = x + offsetX;
        int facingY = y + Facing.offsetsYForSide[direction];
        int facingZ = z + offsetZ;
        double xPos = (double)(facingX) + 0.5D;
        double yPos = (double)(facingY) + 0.0625D;
        double zPos = (double)(facingZ) + 0.5D;
        List list = world.getEntitiesWithinAABB(
            EntityMinecart.class,
            AxisAlignedBB.getAABBPool().getAABB(
                xPos - 0.49D, yPos, zPos - 0.49D,
                xPos + 0.49D, yPos + 0.7D, zPos + 0.49D
            )
        );
        if (list != null && list.size() > 0) {
            // minecart was found, don't eject new minecart
            return false;
        }
        Entity entity = EntityMinecart.createMinecart(world, xPos, yPos, zPos, ((ItemMinecart)itemStack.getItem()).minecartType);
        // speed of minecart getting shot out: just 1 or -1 in the offset direction atm
        world.spawnEntityInWorld(entity);
        if (
            ((direction)&~1) != 0x0 &&
            ((IWorldMixins)world).isRailBlockWithExitTowards(facingX, facingY, facingZ, ((direction)^1))
        ) {
            entity.setVelocity((double)offsetX, 0.0D, (double)offsetZ);
        }
        //entity.setVelocity((double)Facing.offsetsXForSide[direction], (double)Facing.offsetsYForSide[direction], (double)Facing.offsetsZForSide[direction]);
        world.playAuxSFX(1000, x, y, z, 0); // normal pitch click
        return true;
    }
}
