package zero.test.mixin;
import net.minecraft.src.*;
import btw.block.BTWBlocks;
import btw.block.tileentity.HopperTileEntity;
import btw.inventory.util.InventoryUtils;
import btw.world.util.WorldUtils;
import btw.client.fx.BTWEffectManager;
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
import zero.test.IEntityMinecartFurnaceMixins;
import java.util.List;
// Block piston reactions
@Mixin(HopperTileEntity.class)
public abstract class HopperTileEntityMixins extends TileEntity /*implements IInventory, TileEntityDataPacketHandler*/ {
    @Shadow
    public int filterItemID;
    @Shadow
    public abstract void ejectStack(ItemStack stack);
    @Overwrite(remap=false)
    public void attemptToEjectStackFromInv() {
        HopperTileEntity self = (HopperTileEntity)(Object)this;
        int iStackIndex = InventoryUtils.getRandomOccupiedStackInRange(self, self.worldObj.rand, 0, 17);
        if (iStackIndex >= 0 && iStackIndex <= 17) {
            ItemStack invStack = self.getStackInSlot(iStackIndex);
            int iEjectStackSize;
            if (8 > invStack.stackSize) {
                iEjectStackSize = invStack.stackSize;
            }
            else {
                iEjectStackSize = 8;
            }
            ItemStack ejectStack = new ItemStack(invStack.itemID, iEjectStackSize, invStack.getItemDamage());
            InventoryUtils.copyEnchantments(ejectStack, invStack);
            int iTargetI = self.xCoord;
            int iTargetJ = self.yCoord - 1;
            int iTargetK = self.zCoord;
            boolean bEjectIntoWorld = false;
            if (self.worldObj.isAirBlock(iTargetI, iTargetJ, iTargetK)) {
                bEjectIntoWorld = true;
            }
            else {
                if (WorldUtils.isReplaceableBlock(self.worldObj, iTargetI, iTargetJ, iTargetK)) {
                    bEjectIntoWorld = true;
                }
                else {
                    int iTargetBlockID = self.worldObj.getBlockId(iTargetI, iTargetJ, iTargetK);
                    Block targetBlock = Block.blocksList[iTargetBlockID];
                    if (targetBlock == null || !targetBlock.doesBlockHopperEject(self.worldObj, iTargetI, iTargetJ, iTargetK)) {
                        bEjectIntoWorld = true;
                    }
                    else if (targetBlock.doesBlockHopperInsert(self.worldObj, iTargetI, iTargetJ, iTargetK)) {
                        self.outputBlocked = true;
                    }
                    else {
                        TileEntity targetTileEntity = self.worldObj.getBlockTileEntity(iTargetI, iTargetJ, iTargetK);
                        int iNumItemsStored = 0;
                        if (targetTileEntity != null && targetTileEntity instanceof IInventory) {
                            int iMinSlotToAddTo = 0;
                            int iMaxSlotToAddTo = ((IInventory)targetTileEntity).getSizeInventory() - 1;
                            boolean canProcessStack = true;
                            if (iTargetBlockID == Block.furnaceIdle.blockID || iTargetBlockID == Block.furnaceBurning.blockID) {
                                iMaxSlotToAddTo = 0;
                            }
                            else if (iTargetBlockID == BTWBlocks.hopper.blockID) {
                                iMaxSlotToAddTo = 17;
                                int iTargetFilterID = ((HopperTileEntityMixins) targetTileEntity).filterItemID;
                                if (iTargetFilterID > 0) {
                                    // filters in the hopper below block ejection
                                    canProcessStack = false;
                                }
                            }
                            if (canProcessStack) {
                                boolean bFullStackDeposited;
                                if (iTargetBlockID != Block.chest.blockID && iTargetBlockID != BTWBlocks.chest.blockID) {
                                    bFullStackDeposited = InventoryUtils
                                            .addItemStackToInventoryInSlotRange((IInventory) targetTileEntity, ejectStack, iMinSlotToAddTo, iMaxSlotToAddTo);
                                }
                                else {
                                    bFullStackDeposited = InventoryUtils.addItemStackToChest((TileEntityChest) targetTileEntity, ejectStack);
                                }
                                if (!bFullStackDeposited) {
                                    iNumItemsStored = iEjectStackSize - ejectStack.stackSize;
                                }
                                else {
                                    iNumItemsStored = iEjectStackSize;
                                }
                                if (iNumItemsStored > 0) {
                                    self.decrStackSize(iStackIndex, iNumItemsStored);
                                    worldObj.playAuxSFX(BTWEffectManager.ITEM_COLLECTION_POP_EFFECT_ID, xCoord, yCoord, zCoord, 0);
                                }
                            }
                            else {
                                self.outputBlocked = true;
                            }
                        }
                        else {
                            self.outputBlocked = true;
                        }
                    }
                }
            }
            if (bEjectIntoWorld) {
                // test for a storage cart below the hopper
                List list = worldObj.getEntitiesWithinAABB(EntityMinecart.class, AxisAlignedBB.getAABBPool()
                        .getAABB((float) xCoord + 0.4f, (float) yCoord - 0.5f, (float) zCoord + 0.4f, (float) xCoord + 0.6f, yCoord, (float) zCoord + 0.6f));
                if (list != null && list.size() > 0) {
                    for (int listIndex = 0; listIndex < list.size(); listIndex++) {
                        EntityMinecart minecartEntity = (EntityMinecart) list.get(listIndex);
                        int minecartType = minecartEntity.getMinecartType();
                        if (
                            minecartType == 1 ||
                            minecartType == 5 ||
                            minecartType == 2
                        ) {
                            // check if the cart is properly aligned with the nozzle
                            if (minecartEntity.boundingBox.intersectsWith(AxisAlignedBB.getAABBPool()
                                    .getAABB((float) xCoord, (float) yCoord - 0.5f, (float) zCoord, (float) xCoord + 0.25f, yCoord, (float) zCoord + 1.0f)) &&
                                    minecartEntity.boundingBox.intersectsWith(AxisAlignedBB.getAABBPool()
                                            .getAABB((float) xCoord + 0.75f, (float) yCoord - 0.5f, (float) zCoord, (float) xCoord + 1.0f, yCoord,
                                                    (float) zCoord + 1.0f)) && minecartEntity.boundingBox.intersectsWith(AxisAlignedBB.getAABBPool()
                                    .getAABB((float) xCoord, (float) yCoord - 0.5f, (float) zCoord, (float) xCoord + 1.0f, yCoord, (float) zCoord + 0.25f)) &&
                                    minecartEntity.boundingBox.intersectsWith(AxisAlignedBB.getAABBPool()
                                            .getAABB((float) xCoord, (float) yCoord - 0.5f, (float) zCoord + 0.75f, (float) xCoord + 1.0f, yCoord,
                                                    (float) zCoord + 1.0f))) {
                                int iNumItemsStored = 0;
                                // Stupid mixins and their BS "can't alter control flow".
                                // This shouldn't need an overwrite
                                if (minecartType != 2) {
                                    if (InventoryUtils.addItemStackToInventory((IInventory) minecartEntity, ejectStack)) {
                                        iNumItemsStored = iEjectStackSize;
                                    }
                                    else {
                                        iNumItemsStored = iEjectStackSize - ejectStack.stackSize;
                                    }
                                }
                                else {
                                    iNumItemsStored = ((IEntityMinecartFurnaceMixins)minecartEntity).attemptToAddFuel(ejectStack);
                                }
                                if (iNumItemsStored > 0) {
                                    self.decrStackSize(iStackIndex, iNumItemsStored);
                                    worldObj.playAuxSFX(BTWEffectManager.ITEM_COLLECTION_POP_EFFECT_ID, xCoord, yCoord, zCoord, 0);
                                }
                                bEjectIntoWorld = false;
                                break;
                            }
                        }
                    }
                }
            }
            if (bEjectIntoWorld) {
                ejectStack(ejectStack);
                self.decrStackSize(iStackIndex, iEjectStackSize);
            }
        }
    }
}
