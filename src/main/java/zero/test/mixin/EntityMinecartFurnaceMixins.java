package zero.test.mixin;
import net.minecraft.src.*;
import net.minecraft.server.MinecraftServer;
import btw.AddonHandler;
import btw.block.BTWBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import zero.test.IEntityMixins;
//import zero.test.mixin.IEntityMinecartFurnaceAccessMixins;
import zero.test.IEntityMinecartFurnaceMixins;
import java.util.List;
// Block piston reactions
@Mixin(EntityMinecartFurnace.class)
public abstract class EntityMinecartFurnaceMixins extends EntityMinecart implements IEntityMinecartFurnaceMixins {
    public EntityMinecartFurnaceMixins(World world) {
        super(world);
    }
    public EntityMinecartFurnaceMixins(World world, double x, double y, double z) {
        super(world, x, y, z);
    }
    // Fix MC-51053
    @Overwrite
    public void updateOnTrack(int par1, int par2, int par3, double par4, double par6, int par8, int par9) {
        super.updateOnTrack(par1, par2, par3, par4, par6, par8, par9);
        EntityMinecartFurnace self = (EntityMinecartFurnace)(Object)this;
        double push;
        double motion;
        if (
            (push = self.pushX * self.pushX + self.pushZ * self.pushZ) > 1.0E-4D &&
            (motion = self.motionX * self.motionX + self.motionZ * self.motionZ) > 0.001D
        ) {
            double temp = Math.sqrt(push) / Math.sqrt(motion);
            self.pushX = self.motionX * temp;
            self.pushZ = self.motionZ * temp;
        }
    }
    // Fix MC-10186
    @Overwrite
    public void applyDrag() {
        EntityMinecartFurnace self = (EntityMinecartFurnace)(Object)this;
        double push = self.pushX * self.pushX + self.pushZ * self.pushZ;
        if (push > 1.0E-4D) {
            push = Math.sqrt(push);
            self.pushX /= push;
            self.pushZ /= push;
            self.motionX *= 0.8D;
            self.motionZ *= 0.8D;
            self.motionX += self.pushX;
            self.motionZ += self.pushZ;
        }
        else
        {
            self.motionX *= 0.98D;
            self.motionZ *= 0.98D;
        }
        self.motionY = 0.0D;
    }
    public long prevActivationCoord = 137438953472L;
    @Override
    public void onActivatorRailPass(int x, int y, int z, boolean powered) {
        if (powered) {
            long posHash = ((long)(z)<<12 +26^(long)(x)<<12^(y));
            if (posHash != this.prevActivationCoord) {
                this.prevActivationCoord = posHash;
                EntityMinecartFurnace self = (EntityMinecartFurnace)(Object)this;
                self.pushX = -self.pushX;
                self.pushZ = -self.pushZ;
            }
        } else {
            prevActivationCoord = 137438953472L;
        }
    }
    @Overwrite
    public void killMinecart(DamageSource damageSource) {
        super.killMinecart(damageSource);
        if (!damageSource.isExplosion()) {
            this.entityDropItem(new ItemStack(BTWBlocks.idleOven, 1), 0.0F);
        }
    }
    @Shadow
    public int fuel;
    // Copied from OvenTileEntity
    private static int maxFuelBurnTime = 14200;
    public int attemptToAddFuel(ItemStack stack) {
        int burnCount = 0;
        if (stack != null) {
            Item item = stack.getItem();
            int itemDamage = stack.getItemDamage();
            if (item.getCanBeFedDirectlyIntoBrickOven(itemDamage)) {
                int currentFuel = this.fuel;
                int possibleBurnTime = maxFuelBurnTime - currentFuel;
                if (possibleBurnTime > 0) {
                    // Multiplier calculated from OvenTileEntity and TileEntityFurnace
                    int itemBurnTime = item.getFurnaceBurnTime(itemDamage) * 8;
                    burnCount = possibleBurnTime / itemBurnTime;
                    if (burnCount == 0 && currentFuel <= 1600) {
                        burnCount = 1;
                    }
                    if (burnCount > 0) {
                        if (burnCount > stack.stackSize) {
                            burnCount = stack.stackSize;
                        }
                        this.fuel = currentFuel + burnCount * itemBurnTime;
                    }
                }
            }
        }
        return burnCount;
    }
    @Overwrite
    public boolean interact(EntityPlayer entityPlayer) {
        ItemStack itemStack = entityPlayer.inventory.getCurrentItem();
        if (
            itemStack != null &&
            (itemStack.stackSize -= this.attemptToAddFuel(itemStack)) == 0
        ) {
            entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, (ItemStack)null);
        }
        // Fix MC-163375
        if (this.fuel > 0) {
            double newPushX;
            double newPushZ;
            if (entityPlayer.isSneaking()) {
                newPushX = 0.0D;
                newPushZ = 0.0D;
            }
            else {
                newPushX = this.posX - entityPlayer.posX;
                newPushZ = this.posZ - entityPlayer.posZ;
                if (entityPlayer.isUsingSpecialKey()) {
                    newPushX = -newPushX;
                    newPushZ = -newPushZ;
                }
            }
            EntityMinecartFurnace self = (EntityMinecartFurnace)(Object)this;
            self.pushX = newPushX;
            self.pushZ = newPushZ;
            return true;
        }
        // Fix MC-200544
        return false;
    }
    @Shadow
    public abstract boolean isMinecartPowered();
    @Overwrite
    public Block getDefaultDisplayTile() {
        return this.isMinecartPowered() ? BTWBlocks.burningOven : BTWBlocks.idleOven;
    }
    @Overwrite
    public int getDefaultDisplayTileData() {
        return 0;
    }
}
