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

#define POWER_META_OFFSET 0

@Mixin(RenderBlocks.class)
public class RenderBlocksMixins implements IRenderBlocksMixins {
    
#if ENABLE_MODERN_REDSTONE_WIRE

#include "redstone_wire_defines.h"
    
    @Overwrite
    public boolean renderBlockRedstoneWire(Block block, int x, int y, int z) {
        RenderBlocks self = (RenderBlocks)(Object)this;
        
        Tessellator tessellator = Tessellator.instance;
        
        tessellator.setBrightness(block.getMixedBrightnessForBlock(self.blockAccess, x, y, z));
        
#if ENABLE_MODERN_SUPPORT_LOGIC != MODERN_SUPPORT_LOGIC_DISABLED
        Block belowBlock = Block.blocksList[self.blockAccess.getBlockId(x, y - 1, z)];
        // Assuming the redstone isn't floating in midair seems
        // a bit *too* dangerous, even for my taste
        boolean renderBottom = BLOCK_IS_AIR(belowBlock) || belowBlock.shouldRenderNeighborFullFaceSide(self.blockAccess, x, y - 1, z, DIRECTION_UP);
#endif
        
        int power = READ_META_FIELD(self.blockAccess.getBlockMetadata(x, y, z), POWER);
        
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
        
        int connections = ((IBlockRedstoneWireMixins)block).getConnectingSides(self.blockAccess, x, y, z, true);
        
        int texture_index = ((IBlockRedstoneWireMixins)block).get_texture_index_for_connections(connections);
        
        Icon base_texture = ((IBlockRedstoneWireMixins)block).get_texture_by_index(texture_index);
        Icon overlay_texture = ((IBlockRedstoneWireMixins)block).get_texture_by_index(texture_index + 1);
        
        double minX, maxX;
        double minY;
        double minZ, maxZ;
        
        maxX = (minX = (double)x) + 1.0D;
        //maxY = (minY = (double)y) + 0.015625D;
        maxZ = (minZ = (double)z) + 1.0D;
        
        minY = (double)y + 0.015625D;
        
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
#if ENABLE_MODERN_SUPPORT_LOGIC != MODERN_SUPPORT_LOGIC_DISABLED
            if (renderBottom) {
                tessellator.addVertexWithUV(tempMinX, minY, tempMaxZ, texMinU, texMaxV);
                tessellator.addVertexWithUV(tempMinX, minY, tempMinZ, texMinU, texMinV);
                tessellator.addVertexWithUV(tempMaxX, minY, tempMinZ, texMaxU, texMinV);
                tessellator.addVertexWithUV(tempMaxX, minY, tempMaxZ, texMaxU, texMaxV);
            }
#endif
            
            tessellator.setColorRGBA(255, 255, 255, 255);
            
            texMinU = (double)overlay_texture.getInterpolatedU(minU);
            texMaxU = (double)overlay_texture.getInterpolatedU(maxU);
            texMinV = (double)overlay_texture.getInterpolatedV(minV);
            texMaxV = (double)overlay_texture.getInterpolatedV(maxV);
            tessellator.addVertexWithUV(tempMaxX, minY, tempMaxZ, texMaxU, texMaxV);
            tessellator.addVertexWithUV(tempMaxX, minY, tempMinZ, texMaxU, texMinV);
            tessellator.addVertexWithUV(tempMinX, minY, tempMinZ, texMinU, texMinV);
            tessellator.addVertexWithUV(tempMinX, minY, tempMaxZ, texMinU, texMaxV);
#if ENABLE_MODERN_SUPPORT_LOGIC != MODERN_SUPPORT_LOGIC_DISABLED
            if (renderBottom) {
                tessellator.addVertexWithUV(tempMinX, minY, tempMaxZ, texMinU, texMaxV);
                tessellator.addVertexWithUV(tempMinX, minY, tempMinZ, texMinU, texMinV);
                tessellator.addVertexWithUV(tempMaxX, minY, tempMinZ, texMaxU, texMinV);
                tessellator.addVertexWithUV(tempMaxX, minY, tempMaxZ, texMaxU, texMaxV);
            }
#endif
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
#if ENABLE_MODERN_SUPPORT_LOGIC != MODERN_SUPPORT_LOGIC_DISABLED
            if (renderBottom) {
                tessellator.addVertexWithUV(minX, minY, maxZ, texMinU, texMaxV);
                tessellator.addVertexWithUV(minX, minY, minZ, base_texture.getMinU(), base_texture.getMinV());
                tessellator.addVertexWithUV(maxX, minY, minZ, texMaxU, texMinV);
                tessellator.addVertexWithUV(maxX, minY, maxZ, base_texture.getMaxU(), base_texture.getMaxV());
            }
#endif
            
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
#if ENABLE_MODERN_SUPPORT_LOGIC != MODERN_SUPPORT_LOGIC_DISABLED
            if (renderBottom) {
                tessellator.addVertexWithUV(minX, minY, maxZ, texMinU, texMaxV);
                tessellator.addVertexWithUV(minX, minY, minZ, overlay_texture.getMinU(), overlay_texture.getMinV());
                tessellator.addVertexWithUV(maxX, minY, minZ, texMaxU, texMinV);
                tessellator.addVertexWithUV(maxX, minY, maxZ, overlay_texture.getMaxU(), overlay_texture.getMaxV());
            }
#endif
        }
        
        // Vertical dust rendering
        if (HAS_UP_CONNECTION(connections)) {
            
            tessellator.setColorOpaque_F(red, green, blue);
            
            base_texture = ((IBlockRedstoneWireMixins)block).get_texture_by_index(LINE_TEXTURE_INDEX);
            
            double maxY;
            maxY = (minY = (double)y) + 1.021875D;
            
            tempMinX = minX + 0.015625D;
            tempMaxX = maxX - 0.015625D;
            tempMinZ = minZ + 0.015625D;
            tempMaxZ = maxZ - 0.015625D;
            
            if (HAS_EAST_UP_CONNECTION(connections)) {
                tessellator.addVertexWithUV(tempMaxX, minY, maxZ, base_texture.getMinU(), base_texture.getMaxV());
                tessellator.addVertexWithUV(tempMaxX, maxY, maxZ, base_texture.getMaxU(), base_texture.getMaxV());
                tessellator.addVertexWithUV(tempMaxX, maxY, minZ, base_texture.getMaxU(), base_texture.getMinV());
                tessellator.addVertexWithUV(tempMaxX, minY, minZ, base_texture.getMinU(), base_texture.getMinV());
#if ENABLE_MODERN_SUPPORT_LOGIC != MODERN_SUPPORT_LOGIC_DISABLED
                if (HAS_EAST_UP_RENDER_BACK(connections)) {
                    tessellator.addVertexWithUV(tempMaxX, minY, minZ, base_texture.getMinU(), base_texture.getMinV());
                    tessellator.addVertexWithUV(tempMaxX, maxY, minZ, base_texture.getMaxU(), base_texture.getMinV());
                    tessellator.addVertexWithUV(tempMaxX, maxY, maxZ, base_texture.getMaxU(), base_texture.getMaxV());
                    tessellator.addVertexWithUV(tempMaxX, minY, maxZ, base_texture.getMinU(), base_texture.getMaxV());
                }
#endif
            }
            if (HAS_WEST_UP_CONNECTION(connections)) {
                tessellator.addVertexWithUV(tempMinX, maxY, maxZ, base_texture.getMaxU(), base_texture.getMinV());
                tessellator.addVertexWithUV(tempMinX, minY, maxZ, base_texture.getMinU(), base_texture.getMinV());
                tessellator.addVertexWithUV(tempMinX, minY, minZ, base_texture.getMinU(), base_texture.getMaxV());
                tessellator.addVertexWithUV(tempMinX, maxY, minZ, base_texture.getMaxU(), base_texture.getMaxV());
#if ENABLE_MODERN_SUPPORT_LOGIC != MODERN_SUPPORT_LOGIC_DISABLED
                if (HAS_WEST_UP_RENDER_BACK(connections)) {
                    tessellator.addVertexWithUV(tempMinX, maxY, minZ, base_texture.getMaxU(), base_texture.getMaxV());
                    tessellator.addVertexWithUV(tempMinX, minY, minZ, base_texture.getMinU(), base_texture.getMaxV());
                    tessellator.addVertexWithUV(tempMinX, minY, maxZ, base_texture.getMinU(), base_texture.getMinV());
                    tessellator.addVertexWithUV(tempMinX, maxY, maxZ, base_texture.getMaxU(), base_texture.getMinV());
                }
#endif
            }
            if (HAS_SOUTH_UP_CONNECTION(connections)) {
                tessellator.addVertexWithUV(maxX, maxY, tempMaxZ, base_texture.getMaxU(), base_texture.getMinV());
                tessellator.addVertexWithUV(maxX, minY, tempMaxZ, base_texture.getMinU(), base_texture.getMinV());
                tessellator.addVertexWithUV(minX, minY, tempMaxZ, base_texture.getMinU(), base_texture.getMaxV());
                tessellator.addVertexWithUV(minX, maxY, tempMaxZ, base_texture.getMaxU(), base_texture.getMaxV());
#if ENABLE_MODERN_SUPPORT_LOGIC != MODERN_SUPPORT_LOGIC_DISABLED
                if (HAS_SOUTH_UP_RENDER_BACK(connections)) {
                    tessellator.addVertexWithUV(minX, maxY, tempMaxZ, base_texture.getMaxU(), base_texture.getMaxV());
                    tessellator.addVertexWithUV(minX, minY, tempMaxZ, base_texture.getMinU(), base_texture.getMaxV());
                    tessellator.addVertexWithUV(maxX, minY, tempMaxZ, base_texture.getMinU(), base_texture.getMinV());
                    tessellator.addVertexWithUV(maxX, maxY, tempMaxZ, base_texture.getMaxU(), base_texture.getMinV());
                }
#endif
            }
            if (HAS_NORTH_UP_CONNECTION(connections)) {
                tessellator.addVertexWithUV(maxX, minY, tempMinZ, base_texture.getMinU(), base_texture.getMaxV());
                tessellator.addVertexWithUV(maxX, maxY, tempMinZ, base_texture.getMaxU(), base_texture.getMaxV());
                tessellator.addVertexWithUV(minX, maxY, tempMinZ, base_texture.getMaxU(), base_texture.getMinV());
                tessellator.addVertexWithUV(minX, minY, tempMinZ, base_texture.getMinU(), base_texture.getMinV());
#if ENABLE_MODERN_SUPPORT_LOGIC != MODERN_SUPPORT_LOGIC_DISABLED
                if (HAS_NORTH_UP_RENDER_BACK(connections)) {
                    tessellator.addVertexWithUV(minX, minY, tempMinZ, base_texture.getMinU(), base_texture.getMinV());
                    tessellator.addVertexWithUV(minX, maxY, tempMinZ, base_texture.getMaxU(), base_texture.getMinV());
                    tessellator.addVertexWithUV(maxX, maxY, tempMinZ, base_texture.getMaxU(), base_texture.getMaxV());
                    tessellator.addVertexWithUV(maxX, minY, tempMinZ, base_texture.getMinU(), base_texture.getMaxV());
                }
#endif
            }
            
            tessellator.setColorRGBA(255, 255, 255, 255);
            
            overlay_texture = ((IBlockRedstoneWireMixins)block).get_texture_by_index(LINE_OVERLAY_TEXTURE_INDEX);
            
            if (HAS_EAST_UP_CONNECTION(connections)) {
                tessellator.addVertexWithUV(tempMaxX, minY, maxZ, overlay_texture.getMinU(), overlay_texture.getMaxV());
                tessellator.addVertexWithUV(tempMaxX, maxY, maxZ, overlay_texture.getMaxU(), overlay_texture.getMaxV());
                tessellator.addVertexWithUV(tempMaxX, maxY, minZ, overlay_texture.getMaxU(), overlay_texture.getMinV());
                tessellator.addVertexWithUV(tempMaxX, minY, minZ, overlay_texture.getMinU(), overlay_texture.getMinV());
#if ENABLE_MODERN_SUPPORT_LOGIC != MODERN_SUPPORT_LOGIC_DISABLED
                if (HAS_EAST_UP_RENDER_BACK(connections)) {
                    tessellator.addVertexWithUV(tempMaxX, minY, minZ, overlay_texture.getMinU(), overlay_texture.getMinV());
                    tessellator.addVertexWithUV(tempMaxX, maxY, minZ, overlay_texture.getMaxU(), overlay_texture.getMinV());
                    tessellator.addVertexWithUV(tempMaxX, maxY, maxZ, overlay_texture.getMaxU(), overlay_texture.getMaxV());
                    tessellator.addVertexWithUV(tempMaxX, minY, maxZ, overlay_texture.getMinU(), overlay_texture.getMaxV());
                }
#endif
            }
            if (HAS_WEST_UP_CONNECTION(connections)) {
                tessellator.addVertexWithUV(tempMinX, maxY, maxZ, overlay_texture.getMaxU(), overlay_texture.getMinV());
                tessellator.addVertexWithUV(tempMinX, minY, maxZ, overlay_texture.getMinU(), overlay_texture.getMinV());
                tessellator.addVertexWithUV(tempMinX, minY, minZ, overlay_texture.getMinU(), overlay_texture.getMaxV());
                tessellator.addVertexWithUV(tempMinX, maxY, minZ, overlay_texture.getMaxU(), overlay_texture.getMaxV());
#if ENABLE_MODERN_SUPPORT_LOGIC != MODERN_SUPPORT_LOGIC_DISABLED
                if (HAS_WEST_UP_RENDER_BACK(connections)) {
                    tessellator.addVertexWithUV(tempMinX, maxY, minZ, overlay_texture.getMaxU(), overlay_texture.getMaxV());
                    tessellator.addVertexWithUV(tempMinX, minY, minZ, overlay_texture.getMinU(), overlay_texture.getMaxV());
                    tessellator.addVertexWithUV(tempMinX, minY, maxZ, overlay_texture.getMinU(), overlay_texture.getMinV());
                    tessellator.addVertexWithUV(tempMinX, maxY, maxZ, overlay_texture.getMaxU(), overlay_texture.getMinV());
                }
#endif
            }
            if (HAS_SOUTH_UP_CONNECTION(connections)) {
                tessellator.addVertexWithUV(maxX, maxY, tempMaxZ, overlay_texture.getMaxU(), overlay_texture.getMinV());
                tessellator.addVertexWithUV(maxX, minY, tempMaxZ, overlay_texture.getMinU(), overlay_texture.getMinV());
                tessellator.addVertexWithUV(minX, minY, tempMaxZ, overlay_texture.getMinU(), overlay_texture.getMaxV());
                tessellator.addVertexWithUV(minX, maxY, tempMaxZ, overlay_texture.getMaxU(), overlay_texture.getMaxV());
#if ENABLE_MODERN_SUPPORT_LOGIC != MODERN_SUPPORT_LOGIC_DISABLED
                if (HAS_SOUTH_UP_RENDER_BACK(connections)) {
                    tessellator.addVertexWithUV(minX, maxY, tempMaxZ, overlay_texture.getMaxU(), overlay_texture.getMaxV());
                    tessellator.addVertexWithUV(minX, minY, tempMaxZ, overlay_texture.getMinU(), overlay_texture.getMaxV());
                    tessellator.addVertexWithUV(maxX, minY, tempMaxZ, overlay_texture.getMinU(), overlay_texture.getMinV());
                    tessellator.addVertexWithUV(maxX, maxY, tempMaxZ, overlay_texture.getMaxU(), overlay_texture.getMinV());
                }
#endif
            }
            if (HAS_NORTH_UP_CONNECTION(connections)) {
                tessellator.addVertexWithUV(maxX, minY, tempMinZ, overlay_texture.getMinU(), overlay_texture.getMaxV());
                tessellator.addVertexWithUV(maxX, maxY, tempMinZ, overlay_texture.getMaxU(), overlay_texture.getMaxV());
                tessellator.addVertexWithUV(minX, maxY, tempMinZ, overlay_texture.getMaxU(), overlay_texture.getMinV());
                tessellator.addVertexWithUV(minX, minY, tempMinZ, overlay_texture.getMinU(), overlay_texture.getMinV());
#if ENABLE_MODERN_SUPPORT_LOGIC != MODERN_SUPPORT_LOGIC_DISABLED
                if (HAS_NORTH_UP_RENDER_BACK(connections)) {
                    tessellator.addVertexWithUV(minX, minY, tempMinZ, overlay_texture.getMinU(), overlay_texture.getMinV());
                    tessellator.addVertexWithUV(minX, maxY, tempMinZ, overlay_texture.getMaxU(), overlay_texture.getMinV());
                    tessellator.addVertexWithUV(maxX, maxY, tempMinZ, overlay_texture.getMaxU(), overlay_texture.getMaxV());
                    tessellator.addVertexWithUV(maxX, minY, tempMinZ, overlay_texture.getMinU(), overlay_texture.getMaxV());
                }
#endif
            }
        }
        
        return true;
    }
#endif

    @Inject(
        method = "renderBlockRepeater(Lnet/minecraft/src/BlockRedstoneRepeater;III)Z",
        at = @At("HEAD")
    )
    public void renderBlockRepeater_inject(BlockRedstoneRepeater block, int x, int y, int z, CallbackInfoReturnable info) {
        ((IBlockRedstoneLogicMixins)block).setRenderingBaseTextures(false);
    }
    
    // Apparently this is unused?
    // Some sort of BTW override class prevents the call.
    @Inject(
        method = "renderBlockComparator(Lnet/minecraft/src/BlockComparator;III)Z",
        at = @At("HEAD")
    )
    public void renderBlockComparator_inject(BlockComparator block, int x, int y, int z, CallbackInfoReturnable info) {
        ((IBlockRedstoneLogicMixins)block).setRenderingBaseTextures(false);
    }
    
    @Shadow
    public Icon overrideBlockTexture;
    
    // Replacement torch rendering that skips the unnecessary
    // XZ offset calculations and prevents torches clipping
    // into the base of the block.
    public void renderTorchForRedstoneLogic(Block block, double x, double y, double z, int meta) {
        RenderBlocks self = (RenderBlocks)(Object)this;
        
        Tessellator tessellator = Tessellator.instance;
        Icon texture = self.getBlockIconFromSideAndMetadata(block, 0, meta);
        
        if (self.hasOverrideBlockTexture()) {
            // Comparator front torch hack
            if ((meta & 4) != 0) {
                y -= 0.125D;
            }
            texture = this.overrideBlockTexture;
        }
        
        double minU = (double)texture.getInterpolatedU(7.0D);
        double minV = (double)texture.getInterpolatedV(6.0D);
        double maxU = (double)texture.getInterpolatedU(9.0D);
        double maxV = (double)texture.getInterpolatedV(8.0D);
        
        double minX = x + 0.4375D;
        double maxX = x + 0.5625D;
        double minZ = z + 0.4375D;
        double maxZ = z + 0.5625D;
        
        double maxY = y + 0.625D;
        
        tessellator.addVertexWithUV(minX, maxY, minZ, minU, minV);
        tessellator.addVertexWithUV(minX, maxY, maxZ, minU, maxV);
        tessellator.addVertexWithUV(maxX, maxY, maxZ, maxU, maxV);
        tessellator.addVertexWithUV(maxX, maxY, minZ, maxU, minV);
        
        maxY = y + 1.0D;
        y = Math.ceil((double)y) + 0.125D;
        
        minU = texture.getMinU();
        minV = texture.getMinV();
        maxU = texture.getMaxU();
        maxV = (double)texture.getInterpolatedV((maxY - y) * 16.0D);
        
        double temp = z + 1.0D;
        
        tessellator.addVertexWithUV(minX, maxY, z, minU, minV);
        tessellator.addVertexWithUV(minX, y, z, minU, maxV);
        tessellator.addVertexWithUV(minX, y, temp, maxU, maxV);
        tessellator.addVertexWithUV(minX, maxY, temp, maxU, minV);
        
        tessellator.addVertexWithUV(maxX, maxY, temp, minU, minV);
        tessellator.addVertexWithUV(maxX, y, temp, minU, maxV);
        tessellator.addVertexWithUV(maxX, y, z, maxU, maxV);
        tessellator.addVertexWithUV(maxX, maxY, z, maxU, minV);
        
        temp = x + 1.0D;
        
        tessellator.addVertexWithUV(x, maxY, maxZ, minU, minV);
        tessellator.addVertexWithUV(x, y, maxZ, minU, maxV);
        tessellator.addVertexWithUV(temp, y, maxZ, maxU, maxV);
        tessellator.addVertexWithUV(temp, maxY, maxZ, maxU, minV);
        
        tessellator.addVertexWithUV(temp, maxY, minZ, minU, minV);
        tessellator.addVertexWithUV(temp, y, minZ, minU, maxV);
        tessellator.addVertexWithUV(x, y, minZ, maxU, maxV);
        tessellator.addVertexWithUV(x, maxY, minZ, maxU, minV);
    }
    
    @Redirect(
        method = { "renderBlockRepeater", "renderBlockComparator" },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/RenderBlocks;renderTorchAtAngle(Lnet/minecraft/src/Block;DDDDDI)V"
        )
    )
    public void renderTorchAtAngle_redirect(RenderBlocks self, Block block, double x, double y, double z, double angleA, double angleB, int meta) {
        ((IRenderBlocksMixins)self).renderTorchForRedstoneLogic(block, x, y, z, meta);
    }
    
    @Inject(
        method = "renderBlockRedstoneLogicMetadata(Lnet/minecraft/src/BlockRedstoneLogic;IIII)V",
        at = @At("HEAD")
    )
    public void renderBlockRedstoneLogicMetadata_inject(BlockRedstoneLogic block, int x, int y, int z, int meta, CallbackInfo info) {
        ((IBlockRedstoneLogicMixins)block).setRenderingBaseTextures(true);
    }
}