package zero.test.mixin;

import net.minecraft.src.*;

import btw.AddonHandler;
import btw.block.BTWBlocks;
import btw.block.blocks.PistonBlockBase;
import btw.block.blocks.TorchBlockBase;
import btw.world.util.WorldUtils;
import btw.client.render.entity.BlockLiftedByPlatformRenderer;
import btw.entity.mechanical.platform.BlockLiftedByPlatformEntity;

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

import zero.test.IBlockMixins;
import zero.test.IWorldMixins;
import zero.test.IBlockRedstoneWireMixins;
import zero.test.mixin.IRedstoneWireAccessMixins;
import zero.test.IBlockRedstoneLogicMixins;
import zero.test.mixin.IRenderBlocksAccessMixins;
import zero.test.IRenderBlocksMixins;

#include "..\func_aliases.h"
#include "..\feature_flags.h"
#include "..\util.h"

@Mixin(BlockLiftedByPlatformRenderer.class)
public abstract class BlockLiftedByPlatformRendererMixins extends Render {
#if ENABLE_PLATFORM_EXTENSIONS
    private RenderBlocks platformRenderBlocks = new RenderBlocks();
    
    //@Overwrite
    @Inject(
        method = "doRender(Lnet/minecraft/src/Entity;DDDFF)V",
        at = @At("TAIL")
    )
    public void doRender_inject(Entity entity, double x, double y, double z, float yaw, float renderPartialTicks, CallbackInfo info) {
        BlockLiftedByPlatformEntity liftedBlockEntity = (BlockLiftedByPlatformEntity)entity;
        
        int blockID = liftedBlockEntity.getBlockID();
    	
    	Block block = Block.blocksList[blockID];
        
        if (
            !BLOCK_IS_AIR(block) &&
            !(block instanceof BlockRailBase) &&
            !(block instanceof BlockRedstoneWire)
        ) {
            
            // This is just ripped from falling sand
            
            GL11.glPushMatrix();
            GL11.glTranslatef((float)x, (float)y, (float)z);
            this.loadTexture("/terrain.png");
            GL11.glDisable(GL11.GL_LIGHTING);
            
            platformRenderBlocks.blockAccess = entity.worldObj;
            
            Tessellator tessellator = Tessellator.instance;
            
            tessellator.startDrawingQuads();
            
            tessellator.setTranslation(
                -MathHelper.floor_double(liftedBlockEntity.posX) - 0.5D, 
                -MathHelper.floor_double(liftedBlockEntity.posY) - 0.5D, 
                -MathHelper.floor_double(liftedBlockEntity.posZ) - 0.5D
            );
            
            block.currentBlockRenderer = platformRenderBlocks;
            
            block.renderFallingBlock(
                platformRenderBlocks,
                MathHelper.floor_double(liftedBlockEntity.posX),
                MathHelper.floor_double(liftedBlockEntity.posY),
                MathHelper.floor_double(liftedBlockEntity.posZ),
                liftedBlockEntity.getBlockMetadata()
            );
            
            tessellator.setTranslation(0.0D, 0.0D, 0.0D);
            
            tessellator.draw();
            
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glPopMatrix();
        }
    }
#endif
}