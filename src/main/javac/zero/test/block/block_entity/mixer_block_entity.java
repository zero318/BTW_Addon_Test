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

#include "..\..\util.h"
#include "..\..\feature_flags.h"

#define TILT_DIRECTION_META_BITS 2
#define TILT_DIRECTION_META_OFFSET 0

#define TILT_DIRECTION_META_NORTH 0
#define TILT_DIRECTION_META_SOUTH 1
#define TILT_DIRECTION_META_WEST 2
#define TILT_DIRECTION_META_EAST 3

#define POWERED_META_OFFSET 2

#define SPINNING_META_BITS 1
#define SPINNING_META_IS_BOOL true
#define SPINNING_META_OFFSET 3

public class MixerBlockEntity
#if ENABLE_MIXER_BLOCK
extends CookingVesselTileEntity
#endif
{
#if ENABLE_MIXER_BLOCK
    
#define COOK_SPEED_OFF 0
#define COOK_SPEED_SLOW 1
#define COOK_SPEED_FAST 2

#define INERTIA_COOLDOWN 20
#define OVERPOWER_COOLDOWN 5

#define BASE_SPEED_FACTOR 5
#define AXLE_SPEED_FACTOR 12

#define SLOW_SPEED 17
#define FAST_SPEED 29
#define TURBO_SPEED 31

/*
Regular fire: 5 + 3 * surrounding fires
*/

// 150 * (5 + 3 * 8)
#define RECIPE_TIME 4350

    public int overpowerTimer = 0;

    public void calcScaledCookTime() {
        this.scaledCookCounter = (this.cookCounter * 1000) / RECIPE_TIME;
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
                READ_META_FIELD(this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord), SPINNING)
                    ? (this.overpowerTimer > 0 ? 1 : 0) + (
                        // The spinning state can only be set if at least one
                        // axle is already powering the block, so it's only
                        // necessary to check for both at once.
                        MechPowerUtils.isBlockPoweredByAxleToSide(this.worldObj, this.xCoord, this.yCoord, this.zCoord, DIRECTION_DOWN) &&
                        MechPowerUtils.isBlockPoweredByAxleToSide(this.worldObj, this.xCoord, this.yCoord, this.zCoord, DIRECTION_UP)
                            ? COOK_SPEED_FAST
                            : COOK_SPEED_SLOW
                    )
                    : COOK_SPEED_OFF;
            if (new_speed != this.fireUnderType) {
                this.fireUnderType = new_speed;
                this.validateContentsForState();
            }
        }
    }
    
    private static final int[] cook_speeds = new int[] {
        0,
        SLOW_SPEED,
        FAST_SPEED,
        TURBO_SPEED
    };
    
    public void overpower() {
        this.overpowerTimer = OVERPOWER_COOLDOWN;
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
            if (!READ_META_FIELD(meta, POWERED)) {
                
                // Why is this field private?!
                if (((CookingVesselTileEntityAccessMixins)this).getForceValidateOnUpdate()) {
                    this.validateContentsForState();
                    ((CookingVesselTileEntityAccessMixins)this).setForceValidateOnUpdate(false);
                }
                
                if (READ_META_FIELD(meta, SPINNING)) {
                    this.stokedCooldownCounter = INERTIA_COOLDOWN;
                }
                else if (this.stokedCooldownCounter > 0) {
                    --this.stokedCooldownCounter;
                }
                
                int cookSpeed = this.getCurrentFireFactor();
                if (
                    cookSpeed > 0 &&
                    this.containsValidIngredientsForState
                ) {
                    if ((this.cookCounter += cookSpeed) >= RECIPE_TIME) {
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
                ((CookingVesselTileEntityAccessMixins)this).callAttemptToEjectStackFromInv(READ_META_FIELD(meta, TILT_DIRECTION) + 2);
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
        if (this.fireUnderType != COOK_SPEED_OFF) {
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
#endif
}