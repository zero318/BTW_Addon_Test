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
import zero.test.mixin.IRedstoneWireAccessMixins;
import zero.test.IBlockRedstoneLogicMixins;
import zero.test.mixin.IRenderBlocksAccessMixins;
import zero.test.IRenderBlocksMixins;
import zero.test.IMovingPlatformEntityMixins;

#include "..\func_aliases.h"
#include "..\feature_flags.h"
#include "..\util.h"

@Mixin(MovingPlatformRenderer.class)
public abstract class MovingPlatformRendererMixins {
#if ENABLE_PLATFORM_EXTENSIONS
    
    @Redirect(
        method = "doRender",
        at = @At(
            value = "FIELD",
            target = "Lbtw/block/BTWBlocks;platform:Lnet/minecraft/src/Block;",
            opcode = Opcodes.GETSTATIC
        )
    )
    private Block redirect_block_get(Entity entity, double x, double y, double z, float fYaw, float renderPartialTicks) {
        Block block = Block.blocksList[((IMovingPlatformEntityMixins)(MovingPlatformEntity)entity).getBlockId()];
        return !BLOCK_IS_AIR(block) ? block : BTWBlocks.platform;
    }
#endif
}