package zero.test.mixin;
import net.minecraft.src.*;
import btw.AddonHandler;
import btw.block.BTWBlocks;
import btw.block.blocks.PistonBlockBase;
import btw.block.blocks.TorchBlockBase;
import btw.world.util.WorldUtils;
import btw.client.render.entity.MovingPlatformRenderer;
import btw.entity.mechanical.platform.MovingPlatformEntity;
import com.prupe.mcpatcher.cc.ColorizeBlock;
import com.prupe.mcpatcher.cc.Colorizer;
import com.prupe.mcpatcher.ctm.CTMUtils;
import com.prupe.mcpatcher.ctm.GlassPaneRenderer;
import com.prupe.mcpatcher.mal.block.RenderBlocksUtils;
import com.prupe.mcpatcher.renderpass.RenderPass;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
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
import org.objectweb.asm.Opcodes;
import zero.test.IBlockMixins;
import zero.test.IWorldMixins;
import zero.test.IBlockRedstoneWireMixins;
//import zero.test.mixin.IRedstoneWireAccessMixins;
import zero.test.IBlockRedstoneLogicMixins;
//import zero.test.mixin.IRenderBlocksAccessMixins;
import zero.test.IRenderBlocksMixins;
import zero.test.IMovingPlatformEntityMixins;
import zero.test.GenericBlockRenderer;
//#define getInputSignal(...) func_94482_f(__VA_ARGS__)
@Mixin(MovingPlatformRenderer.class)
public abstract class MovingPlatformRendererMixins extends Render {
    @Inject(
        method = "doRender",
        at = @At("HEAD"),
        cancellable = true
    )
    public void doRender_inject(Entity entity, double x, double y, double z, float yaw, float renderPartialTicks, CallbackInfo callbackInfo) {
        int blockId = ((IMovingPlatformEntityMixins)entity).getBlockId();
        if (
            blockId != BTWBlocks.platform.blockID
        ) {
            GenericBlockRenderer.render_block(this.renderManager.renderEngine, entity, x, y, z, blockId, ((IMovingPlatformEntityMixins)entity).getBlockMeta());
            callbackInfo.cancel();
        }
    }
}
