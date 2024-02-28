package zero.test;

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
//import zero.test.mixin.IRedstoneWireAccessMixins;
import zero.test.IBlockRedstoneLogicMixins;
//import zero.test.mixin.IRenderBlocksAccessMixins;
import zero.test.IRenderBlocksMixins;

#include "func_aliases.h"
#include "feature_flags.h"
#include "util.h"

public class GenericBlockRenderer {

    private static RenderBlocks renderBlocks = new RenderBlocks();

    public static void render_block(RenderEngine render, Entity entity, double x, double y, double z, int blockId, int meta) {
        
        Block block = Block.blocksList[blockId];
        
        if (!BLOCK_IS_AIR(block)) {
            
            // This is just ripped from falling sand
            
            GL11.glPushMatrix();
            GL11.glTranslatef((float)x, (float)y, (float)z);
            render.bindTexture("/terrain.png");
            GL11.glDisable(GL11.GL_LIGHTING);
            
            renderBlocks.blockAccess = entity.worldObj;
            
            Tessellator tessellator = Tessellator.instance;
            
            tessellator.startDrawingQuads();
            
            tessellator.setTranslation(
                -MathHelper.floor_double(entity.posX) - 0.5D, 
                -MathHelper.floor_double(entity.posY) - 0.5D, 
                -MathHelper.floor_double(entity.posZ) - 0.5D
            );
            
            block.currentBlockRenderer = renderBlocks;
            
            block.renderFallingBlock(
                renderBlocks,
                MathHelper.floor_double(entity.posX),
                MathHelper.floor_double(entity.posY),
                MathHelper.floor_double(entity.posZ),
                meta
            );
            
            tessellator.setTranslation(0.0D, 0.0D, 0.0D);
            
            tessellator.draw();
            
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glPopMatrix();
        }
    }
    
}