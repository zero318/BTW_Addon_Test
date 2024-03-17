package zero.test.mixin;

import net.minecraft.src.*;

import btw.BTWMod;
import btw.AddonHandler;
import btw.block.BTWBlocks;
import btw.block.blocks.BucketBlock;
import btw.client.fx.BTWEffectManager;
import btw.item.items.BucketItem;
import btw.item.items.PlaceAsBlockItem;
import btw.entity.mob.CowEntity;

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
import org.spongepowered.asm.mixin.gen.Accessor;

import zero.test.IEntityMixins;
import zero.test.mixin.EntityMixins;
import zero.test.ICowMixins;

#include "..\util.h"
#include "..\feature_flags.h"

#define ENABLE_BUCKET_BLOCK 1
#define ENABLE_BUCKET_ITEM 1

@Mixin(CowEntity.class)
public abstract class CowEntityMixins extends EntityCow implements ICowMixins {
    public CowEntityMixins() {
        super(null);
    }
#if ENABLE_AUTOMATIC_COW_MILKING

    private static final int bucket_id = BTWMod.instance.parseID("fcBlockBucketEmptyID");
    
    @Shadow
    public abstract void setGotMilk(boolean value);

    @Override
    public void pistonMilk() {
        
#if DEBUG_BUILD
        // Just for testing
        this.setGotMilk(true);
#endif
        
        if (((CowEntity)(Object)this).gotMilk()) {
            int x = MathHelper.floor_double(this.posX);
            int y = MathHelper.floor_double(this.posY);
            int z = MathHelper.floor_double(this.posZ);
            
#if ENABLE_BUCKET_BLOCK
            int blockId = this.worldObj.getBlockId(x, y, z);
            if (blockId == BTWBlocks.placedBucket.blockID) {
                Block block = Block.blocksList[blockId];
                if (block.getFacing(this.worldObj, x, y, z) == DIRECTION_UP) {
                    
                    attackEntityFrom(DamageSource.generic, 0);
                    if (!this.worldObj.isRemote) {
                        this.setGotMilk(false);
                        
                        this.worldObj.setBlock(
                            x, y, z,
                            BTWBlocks.placedMilkBucket.blockID, DIRECTION_UP,
                            UPDATE_NEIGHBORS | UPDATE_CLIENTS
                        );
                        
                        this.worldObj.playAuxSFX(
                            BTWEffectManager.COW_MILKING_EFFECT_ID,
                            x, y, z,
                            0
                        );
                    }
                    return;
                }
            }
#endif

#if ENABLE_BUCKET_ITEM
            // TODO: Anything
#endif
        }
    }
#endif
}