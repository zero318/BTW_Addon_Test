package zero.test.mixin;

import net.minecraft.src.*;

import btw.AddonHandler;
import btw.block.BTWBlocks;
import btw.block.blocks.PistonBlockBase;
import btw.block.blocks.TorchBlockBase;
import btw.world.util.WorldUtils;

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
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;

import zero.test.IBlockMixins;
import zero.test.IWorldMixins;
import zero.test.IBlockRedstoneWireMixins;
import zero.test.mixin.IRedstoneWireAccessMixins;

#include "..\func_aliases.h"
#include "..\feature_flags.h"
#include "..\util.h"

#define POWER_META_OFFSET 0

@Mixin(RenderBlocks.class)
public class RenderBlocksMixins {
    
#if ENABLE_MODERN_REDSTONE_WIRE

#include "redstone_wire_defines.h"
    
    @Overwrite
    public boolean renderBlockRedstoneWire(Block block, int X, int Y, int Z) {
        RenderBlocks self = (RenderBlocks)(Object)this;
        
        Tessellator tessellator = Tessellator.instance;
        
        tessellator.setBrightness(block.getMixedBrightnessForBlock(self.blockAccess, X, Y, Z));
        
        int power = READ_META_FIELD(self.blockAccess.getBlockMetadata(X, Y, Z), POWER);
        
        float red;
        float green;
        float blue;
        
        if (ColorizeBlock.computeRedstoneWireColor(power)) {
            red = Colorizer.setColor[0];
            green = Colorizer.setColor[1];
            blue = Colorizer.setColor[2];
        }
        else {
            float temp;
            red = (temp = (float)power / 15.0F) * 0.6F + (power != 0 ? 0.4F : 0.3F);
            green = (temp *= temp) * 0.7F - 0.5F;
            blue = temp * 0.6F - 0.7F;
        }
        if (green < 0.0F) {
            green = 0.0F;
        }
        if (blue < 0.0F) {
            blue = 0.0F;
        }
        
        tessellator.setColorOpaque_F(red, green, blue);
        
        int connections = ((IBlockRedstoneWireMixins)block).getConnectingSides(self.blockAccess, X, Y, Z, true);
        
        int texture_index = ((IBlockRedstoneWireMixins)block).get_texture_index_for_connections(connections);
        
        Icon base_texture = ((IBlockRedstoneWireMixins)block).get_texture_by_index(texture_index);
        Icon overlay_texture = ((IBlockRedstoneWireMixins)block).get_texture_by_index(texture_index + 1);
        
        double minX, maxX;
        double minY;
        double minZ, maxZ;
        
        maxX = (minX = (double)X) + 1.0D;
        //maxY = (minY = (double)Y) + 0.015625D;
        maxZ = (minZ = (double)Z) + 1.0D;
        
        minY = (double)Y + 0.015625D;
        
        double tempMinX;
        double tempMaxX;
        double tempMinZ;
        double tempMaxZ;
        
        double texMinU;
        double texMaxU;
        double texMinV;
        double texMaxV;
        
        /*
        AddonHandler.logMessage(""
            +X+" "+Y+" "+Z+"\n"
            +connections+" "+texture_index
        );
        */
        
        // Flat dust rendering
        if (texture_index != LINE_TEXTURE_INDEX) {
            // Rendering the cross texture
            double minU = 0.0D;
            double minV = 0.0D;
            double maxU = 16.0D;
            double maxV = 16.0D;
            tempMinX = minX;
            tempMaxX = maxX;
            tempMinZ = minZ;
            tempMaxZ = maxZ;
            
            if (!HAS_EAST_SIDE_CONNECTION(connections)) {
                tempMaxX -= 0.3125D;
                maxU = 11.0D;
            }
            if (!HAS_WEST_SIDE_CONNECTION(connections)) {
                tempMinX += 0.3125D;
                minU = 5.0D;
            }
            if (!HAS_SOUTH_SIDE_CONNECTION(connections)) {
                tempMaxZ -= 0.3125D;
                maxV = 11.0D;
            }
            if (!HAS_NORTH_SIDE_CONNECTION(connections)) {
                tempMinZ += 0.3125D;
                minV = 5.0D;
            }
            texMinU = (double)base_texture.getInterpolatedU(minU);
            texMaxU = (double)base_texture.getInterpolatedU(maxU);
            texMinV = (double)base_texture.getInterpolatedV(minV);
            texMaxV = (double)base_texture.getInterpolatedV(maxV);
            
            tessellator.addVertexWithUV(tempMaxX, minY, tempMaxZ, texMaxU, texMaxV);
            tessellator.addVertexWithUV(tempMaxX, minY, tempMinZ, texMaxU, texMinV);
            tessellator.addVertexWithUV(tempMinX, minY, tempMinZ, texMinU, texMinV);
            tessellator.addVertexWithUV(tempMinX, minY, tempMaxZ, texMinU, texMaxV);
            
            tessellator.setColorRGBA(255, 255, 255, 255);
            
            texMinU = (double)overlay_texture.getInterpolatedU(minU);
            texMaxU = (double)overlay_texture.getInterpolatedU(maxU);
            texMinV = (double)overlay_texture.getInterpolatedV(minV);
            texMaxV = (double)overlay_texture.getInterpolatedV(maxV);
            tessellator.addVertexWithUV(tempMaxX, minY, tempMaxZ, texMaxU, texMaxV);
            tessellator.addVertexWithUV(tempMaxX, minY, tempMinZ, texMaxU, texMinV);
            tessellator.addVertexWithUV(tempMinX, minY, tempMinZ, texMinU, texMinV);
            tessellator.addVertexWithUV(tempMinX, minY, tempMaxZ, texMinU, texMaxV);
        }
        else {
            // Rendering the line texture
            if (!HAS_ANY_NS_CONNECTION(connections)) {
                texMinU = base_texture.getMinU();
                texMaxU = base_texture.getMaxU();
                texMinV = base_texture.getMinV();
                texMaxV = base_texture.getMaxV();
            } else {
                texMinU = base_texture.getMaxU();
                texMaxU = base_texture.getMinU();
                texMinV = base_texture.getMaxV();
                texMaxV = base_texture.getMinV();
            }
            
            tessellator.addVertexWithUV(maxX, minY, maxZ, base_texture.getMaxU(), base_texture.getMaxV());
            tessellator.addVertexWithUV(maxX, minY, minZ, texMaxU, texMinV);
            tessellator.addVertexWithUV(minX, minY, minZ, base_texture.getMinU(), base_texture.getMinV());
            tessellator.addVertexWithUV(minX, minY, maxZ, texMinU, texMaxV);
            
            tessellator.setColorRGBA(255, 255, 255, 255);
            
            if (!HAS_ANY_NS_CONNECTION(connections)) {
                texMinU = overlay_texture.getMinU();
                texMaxU = overlay_texture.getMaxU();
                texMinV = overlay_texture.getMinV();
                texMaxV = overlay_texture.getMaxV();
            } else {
                texMinU = overlay_texture.getMaxU();
                texMaxU = overlay_texture.getMinU();
                texMinV = overlay_texture.getMaxV();
                texMaxV = overlay_texture.getMinV();
            }
            
            tessellator.addVertexWithUV(maxX, minY, maxZ, overlay_texture.getMaxU(), overlay_texture.getMaxV());
            tessellator.addVertexWithUV(maxX, minY, minZ, texMaxU, texMinV);
            tessellator.addVertexWithUV(minX, minY, minZ, overlay_texture.getMinU(), overlay_texture.getMinV());
            tessellator.addVertexWithUV(minX, minY, maxZ, texMinU, texMaxV);
        }
        
        // Vertical dust rendering
        if (HAS_UP_CONNECTION(connections)) {
            
            tessellator.setColorOpaque_F(red, green, blue);
            
            base_texture = ((IBlockRedstoneWireMixins)block).get_texture_by_index(LINE_TEXTURE_INDEX);
            
            double maxY;
            maxY = (minY = (double)Y) + 1.021875D;
            
            tempMinX = minX + 0.015625D;
            tempMaxX = maxX - 0.015625D;
            tempMinZ = minZ + 0.015625D;
            tempMaxZ = maxZ - 0.015625D;
            
            if (HAS_EAST_UP_CONNECTION(connections)) {
                tessellator.addVertexWithUV(tempMaxX, minY, maxZ, base_texture.getMinU(), base_texture.getMaxV());
                tessellator.addVertexWithUV(tempMaxX, maxY, maxZ, base_texture.getMaxU(), base_texture.getMaxV());
                tessellator.addVertexWithUV(tempMaxX, maxY, minZ, base_texture.getMaxU(), base_texture.getMinV());
                tessellator.addVertexWithUV(tempMaxX, minY, minZ, base_texture.getMinU(), base_texture.getMinV());
            }
            if (HAS_WEST_UP_CONNECTION(connections)) {
                tessellator.addVertexWithUV(tempMinX, maxY, maxZ, base_texture.getMaxU(), base_texture.getMinV());
                tessellator.addVertexWithUV(tempMinX, minY, maxZ, base_texture.getMinU(), base_texture.getMinV());
                tessellator.addVertexWithUV(tempMinX, minY, minZ, base_texture.getMinU(), base_texture.getMaxV());
                tessellator.addVertexWithUV(tempMinX, maxY, minZ, base_texture.getMaxU(), base_texture.getMaxV());
            }
            if (HAS_SOUTH_UP_CONNECTION(connections)) {
                tessellator.addVertexWithUV(maxX, maxY, tempMaxZ, base_texture.getMaxU(), base_texture.getMinV());
                tessellator.addVertexWithUV(maxX, minY, tempMaxZ, base_texture.getMinU(), base_texture.getMinV());
                tessellator.addVertexWithUV(minX, minY, tempMaxZ, base_texture.getMinU(), base_texture.getMaxV());
                tessellator.addVertexWithUV(minX, maxY, tempMaxZ, base_texture.getMaxU(), base_texture.getMaxV());
            }
            if (HAS_NORTH_UP_CONNECTION(connections)) {
                tessellator.addVertexWithUV(maxX, minY, tempMinZ, base_texture.getMinU(), base_texture.getMaxV());
                tessellator.addVertexWithUV(maxX, maxY, tempMinZ, base_texture.getMaxU(), base_texture.getMaxV());
                tessellator.addVertexWithUV(minX, maxY, tempMinZ, base_texture.getMaxU(), base_texture.getMinV());
                tessellator.addVertexWithUV(minX, minY, tempMinZ, base_texture.getMinU(), base_texture.getMinV());
            }
            
            tessellator.setColorRGBA(255, 255, 255, 255);
            
            overlay_texture = ((IBlockRedstoneWireMixins)block).get_texture_by_index(LINE_OVERLAY_TEXTURE_INDEX);
            
            if (HAS_EAST_UP_CONNECTION(connections)) {
                tessellator.addVertexWithUV(tempMaxX, minY, maxZ, overlay_texture.getMinU(), overlay_texture.getMaxV());
                tessellator.addVertexWithUV(tempMaxX, maxY, maxZ, overlay_texture.getMaxU(), overlay_texture.getMaxV());
                tessellator.addVertexWithUV(tempMaxX, maxY, minZ, overlay_texture.getMaxU(), overlay_texture.getMinV());
                tessellator.addVertexWithUV(tempMaxX, minY, minZ, overlay_texture.getMinU(), overlay_texture.getMinV());
            }
            if (HAS_WEST_UP_CONNECTION(connections)) {
                tessellator.addVertexWithUV(tempMinX, maxY, maxZ, overlay_texture.getMaxU(), overlay_texture.getMinV());
                tessellator.addVertexWithUV(tempMinX, minY, maxZ, overlay_texture.getMinU(), overlay_texture.getMinV());
                tessellator.addVertexWithUV(tempMinX, minY, minZ, overlay_texture.getMinU(), overlay_texture.getMaxV());
                tessellator.addVertexWithUV(tempMinX, maxY, minZ, overlay_texture.getMaxU(), overlay_texture.getMaxV());
            }
            if (HAS_SOUTH_UP_CONNECTION(connections)) {
                tessellator.addVertexWithUV(maxX, maxY, tempMaxZ, overlay_texture.getMaxU(), overlay_texture.getMinV());
                tessellator.addVertexWithUV(maxX, minY, tempMaxZ, overlay_texture.getMinU(), overlay_texture.getMinV());
                tessellator.addVertexWithUV(minX, minY, tempMaxZ, overlay_texture.getMinU(), overlay_texture.getMaxV());
                tessellator.addVertexWithUV(minX, maxY, tempMaxZ, overlay_texture.getMaxU(), overlay_texture.getMaxV());
            }
            if (HAS_NORTH_UP_CONNECTION(connections)) {
                tessellator.addVertexWithUV(maxX, minY, tempMinZ, overlay_texture.getMinU(), overlay_texture.getMaxV());
                tessellator.addVertexWithUV(maxX, maxY, tempMinZ, overlay_texture.getMaxU(), overlay_texture.getMaxV());
                tessellator.addVertexWithUV(minX, maxY, tempMinZ, overlay_texture.getMaxU(), overlay_texture.getMinV());
                tessellator.addVertexWithUV(minX, minY, tempMinZ, overlay_texture.getMinU(), overlay_texture.getMinV());
            }
        }
        
        return true;
    }
#endif
}