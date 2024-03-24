package zero.test.mixin;
import net.minecraft.src.*;
import btw.AddonHandler;
import btw.block.BTWBlocks;
import btw.block.blocks.BucketBlock;
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

@Mixin(EntityCow.class)
public abstract class EntityCowMixins extends EntityMixins {
    @Override
    public void moveEntityByPiston(double x, double y, double z, int direction, boolean isRegularPush) {
        EntityCow self = (EntityCow)(Object)this;
        double pre_movement;
        switch (((direction)&~1)) {
            default:
                pre_movement = self.posX;
                break;
            case 0x0:
                pre_movement = self.posY;
                break;
            case 0x2:
                pre_movement = self.posZ;
                break;
        }
        super.moveEntityByPiston(x, y, z, direction, isRegularPush);
        // Make sure this isn't a push via glue or base ejection
        if (isRegularPush) {
            double post_movement;
            switch (((direction)&~1)) {
                default:
                    post_movement = self.posX;
                    break;
                case 0x0:
                    post_movement = self.posY;
                    break;
                case 0x2:
                    post_movement = self.posZ;
                    break;
            }
            if (pre_movement == post_movement) {
                ((ICowMixins)this).pistonMilk();
            }
        }
    }
}
