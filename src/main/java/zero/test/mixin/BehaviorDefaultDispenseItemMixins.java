package zero.test.mixin;
import net.minecraft.src.*;
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
import zero.test.ZeroUtil;
// Block piston reactions

@Mixin(BehaviorDefaultDispenseItem.class)
public abstract class BehaviorDefaultDispenseItemMixins {
    @Overwrite
    public static void doDispense(World world, ItemStack stack, int n, EnumFacing facing, IPosition position) {
        EntityItem entity = (EntityItem)EntityList.createEntityOfType(EntityItem.class, world, position.getX(), position.getY() - (facing.getFrontOffsetY() != 0 ? 0.125D : 0.15625D), position.getZ(), stack);
        double offset = world.rand.nextDouble() * 0.1D + 0.2D;
        double range = 0.0172275D * (double)n;
        entity.motionX = ZeroUtil.triangle_random(world.rand, (double)facing.getFrontOffsetX() * offset, range);
        entity.motionY = ZeroUtil.triangle_random(world.rand, 0.20000000298023224D, range);
        entity.motionZ = ZeroUtil.triangle_random(world.rand, (double)facing.getFrontOffsetZ() * offset, range);
        world.spawnEntityInWorld(entity);
    }
}
