package zero.test.block.block_entity;
import net.minecraft.src.*;
import btw.block.BTWBlocks;
import btw.block.blocks.CookingVesselBlock;
import btw.block.tileentity.CookingVesselTileEntity;
import btw.block.util.MechPowerUtils;
import btw.inventory.util.InventoryUtils;
import btw.crafting.manager.BulkCraftingManager;
import btw.crafting.manager.CauldronCraftingManager;
import btw.crafting.manager.CauldronStokedCraftingManager;
import btw.item.BTWItems;
import btw.item.util.ItemUtils;
import zero.test.block.MixerBlock;
import zero.test.crafting.MixerRecipeManager;
import zero.test.mixin.CookingVesselTileEntityAccessMixins;
public class MixerBlockEntity
extends CookingVesselTileEntity
{
/*
Regular fire: 5 + 3 * surrounding fires
*/
// 150 * (5 + 3 * 8)
    public int overpowerTimer = 0;
    public void calcScaledCookTime() {
        this.scaledCookCounter = (this.cookCounter * 1000) / 4350;
    }
    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.overpowerTimer = nbttagcompound.getInteger("turbo");
        this.cookCounter = nbttagcompound.getInteger("cook_counter");
        this.calcScaledCookTime();
        this.stokedCooldownCounter = nbttagcompound.getInteger("inertia");
        //this.containsValidIngredientsForState = nbttagcompound.getBoolean("has_recipe");
    }
    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setInteger("turbo", this.overpowerTimer);
        nbttagcompound.setInteger("cook_counter", this.cookCounter);
        nbttagcompound.setInteger("inertia", this.stokedCooldownCounter);
    }
    @Override
    protected boolean doesContainExplosives() {
        // Don't care about fire here
        return false;
    }
    // This only gets called from updateTick
    @Override
    public void validateFireUnderType() {
        if (
            // Not sure what the worldObj check is for here
            this.worldObj != null
        ) {
            int new_speed =
                ((((this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord))>7)))
                    ? (this.overpowerTimer > 0 ? 1 : 0) + (
                        // The spinning state can only be set if at least one
                        // axle is already powering the block, so it's only
                        // necessary to check for both at once.
                        MechPowerUtils.isBlockPoweredByAxleToSide(this.worldObj, this.xCoord, this.yCoord, this.zCoord, 0) &&
                        MechPowerUtils.isBlockPoweredByAxleToSide(this.worldObj, this.xCoord, this.yCoord, this.zCoord, 1)
                            ? 2
                            : 1
                    )
                    : 0;
            if (new_speed != this.fireUnderType) {
                this.fireUnderType = new_speed;
                this.validateContentsForState();
            }
        }
    }
    private static final int[] cook_speeds = new int[] {
        0,
        17,
        29,
        31
    };
    public void overpower() {
        this.overpowerTimer = 5;
    }
    @Override
    public int getCurrentFireFactor() {
        return cook_speeds[this.fireUnderType];
    }
    @Override
    public void updateEntity() {
        if (!this.worldObj.isRemote) {
            if (this.overpowerTimer > 0) {
                --this.overpowerTimer;
            }
            int meta = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
            if (!((((meta)&4)!=0))) {
                // Why is this field private?!
                if (((CookingVesselTileEntityAccessMixins)this).getForceValidateOnUpdate()) {
                    this.validateContentsForState();
                    ((CookingVesselTileEntityAccessMixins)this).setForceValidateOnUpdate(false);
                }
                if (((((meta)>7)))) {
                    this.stokedCooldownCounter = 20;
                }
                else if (this.stokedCooldownCounter > 0) {
                    --this.stokedCooldownCounter;
                }
                int cookSpeed = this.getCurrentFireFactor();
                if (
                    cookSpeed > 0 &&
                    this.containsValidIngredientsForState
                ) {
                    if ((this.cookCounter += cookSpeed) >= 4350) {
                        this.attemptToCookNormal();
                    } else {
                        // All other paths eventually set
                        // this cook counter to 0
                        this.calcScaledCookTime();
                        return;
                    }
                }
            }
            else {
                // Again, WHY IS THIS PRIVATE
                ((CookingVesselTileEntityAccessMixins)this).callAttemptToEjectStackFromInv((((meta)&3)) + 2);
            }
            this.scaledCookCounter = this.cookCounter = 0;
        }
    }
    // No override for attemptToCookNormal()
    // because attemptToCookWithManager is a private
    // function and I don't want to reimplement it
    @Override
    protected boolean attemptToCookStoked() {
        // Just redirect this if it somehow gets called
        return this.attemptToCookNormal();
    }
    @Override
    protected BulkCraftingManager getCraftingManager(int fireType) {
        return MixerRecipeManager.getInstance();
    }
    @Override
    public void validateContentsForState() {
        if (this.fireUnderType != 0) {
            this.containsValidIngredientsForState = MixerRecipeManager.getInstance().getCraftingResult(this) != null;
        }
        else {
            this.containsValidIngredientsForState = false;
        }
    }
    @Override
    public String getInvName() {
        return "Blender";
    }
    @Override
    public boolean isStackValidForSlot(int slot, ItemStack stack) {
        return true;
    }
    @Override
    public boolean isInvNameLocalized() {
        return true;
    }
}
