package zero.test.mixin;
import net.minecraft.src.*;
import btw.AddonHandler;
import btw.client.fx.BTWEffectManager;
import btw.block.blocks.PlatformBlock;
import btw.block.BTWBlocks;
import btw.entity.mechanical.platform.MovingPlatformEntity;
import btw.entity.mechanical.platform.MovingAnchorEntity;
import btw.item.util.ItemUtils;
import btw.world.util.WorldUtils;
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
import zero.test.IWorldMixins;
import zero.test.IBlockMixins;
import zero.test.IMovingPlatformEntityMixins;
import java.util.Random;
// Block piston reactions
@Mixin(MovingPlatformEntity.class)
public abstract class MovingPlatformEntityMixins implements IMovingPlatformEntityMixins {
}
