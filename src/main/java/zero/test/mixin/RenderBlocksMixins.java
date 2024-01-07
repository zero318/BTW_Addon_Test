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
// Block piston reactions
//#define getInputSignal(...) func_94482_f(__VA_ARGS__)
@Mixin(RenderBlocks.class)
public class RenderBlocksMixins implements IRenderBlocksMixins {
/*
    0 East Side
    1 East Up
    2 East Down
    3 West Side
    4 West Up
    5 West Down
    6 South Side
    7 South Up
    8 South Down
    9 North Side
    10 North Up
    11 North Down
*/

    @Overwrite
    public boolean renderBlockRedstoneWire(Block block, int X, int Y, int Z) {
        RenderBlocks self = (RenderBlocks)(Object)this;
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(block.getMixedBrightnessForBlock(self.blockAccess, X, Y, Z));
        Block below_block = Block.blocksList[self.blockAccess.getBlockId(X, Y - 1, Z)];
        // Assuming the redstone isn't floating in midair seems
        // a bit *too* dangerous, even for my taste
        boolean render_bottom = ((below_block)==null) || below_block.shouldRenderNeighborFullFaceSide(self.blockAccess, X, Y - 1, Z, 1);
        int power = (((self.blockAccess.getBlockMetadata(X, Y, Z))));
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
        if (texture_index != 0) {
            // Rendering the cross texture
            double minU = 0.0D;
            double minV = 0.0D;
            double maxU = 16.0D;
            double maxV = 16.0D;
            tempMinX = minX;
            tempMaxX = maxX;
            tempMinZ = minZ;
            tempMaxZ = maxZ;
            if (!(((connections)&0x001)!=0)) {
                tempMaxX -= 0.3125D;
                maxU = 11.0D;
            }
            if (!(((connections)&0x008)!=0)) {
                tempMinX += 0.3125D;
                minU = 5.0D;
            }
            if (!(((connections)&0x040)!=0)) {
                tempMaxZ -= 0.3125D;
                maxV = 11.0D;
            }
            if (!(((connections)&0x200)!=0)) {
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
            if (render_bottom) {
                tessellator.addVertexWithUV(tempMinX, minY, tempMaxZ, texMinU, texMaxV);
                tessellator.addVertexWithUV(tempMinX, minY, tempMinZ, texMinU, texMinV);
                tessellator.addVertexWithUV(tempMaxX, minY, tempMinZ, texMaxU, texMinV);
                tessellator.addVertexWithUV(tempMaxX, minY, tempMaxZ, texMaxU, texMaxV);
            }
            tessellator.setColorRGBA(255, 255, 255, 255);
            texMinU = (double)overlay_texture.getInterpolatedU(minU);
            texMaxU = (double)overlay_texture.getInterpolatedU(maxU);
            texMinV = (double)overlay_texture.getInterpolatedV(minV);
            texMaxV = (double)overlay_texture.getInterpolatedV(maxV);
            tessellator.addVertexWithUV(tempMaxX, minY, tempMaxZ, texMaxU, texMaxV);
            tessellator.addVertexWithUV(tempMaxX, minY, tempMinZ, texMaxU, texMinV);
            tessellator.addVertexWithUV(tempMinX, minY, tempMinZ, texMinU, texMinV);
            tessellator.addVertexWithUV(tempMinX, minY, tempMaxZ, texMinU, texMaxV);
            if (render_bottom) {
                tessellator.addVertexWithUV(tempMinX, minY, tempMaxZ, texMinU, texMaxV);
                tessellator.addVertexWithUV(tempMinX, minY, tempMinZ, texMinU, texMinV);
                tessellator.addVertexWithUV(tempMaxX, minY, tempMinZ, texMaxU, texMinV);
                tessellator.addVertexWithUV(tempMaxX, minY, tempMaxZ, texMaxU, texMaxV);
            }
        }
        else {
            // Rendering the line texture
            if (!((connections)>=0x040)) {
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
            if (render_bottom) {
                tessellator.addVertexWithUV(minX, minY, maxZ, texMinU, texMaxV);
                tessellator.addVertexWithUV(minX, minY, minZ, base_texture.getMinU(), base_texture.getMinV());
                tessellator.addVertexWithUV(maxX, minY, minZ, texMaxU, texMinV);
                tessellator.addVertexWithUV(maxX, minY, maxZ, base_texture.getMaxU(), base_texture.getMaxV());
            }
            tessellator.setColorRGBA(255, 255, 255, 255);
            if (!((connections)>=0x040)) {
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
            if (render_bottom) {
                tessellator.addVertexWithUV(minX, minY, maxZ, texMinU, texMaxV);
                tessellator.addVertexWithUV(minX, minY, minZ, overlay_texture.getMinU(), overlay_texture.getMinV());
                tessellator.addVertexWithUV(maxX, minY, minZ, texMaxU, texMinV);
                tessellator.addVertexWithUV(maxX, minY, maxZ, overlay_texture.getMaxU(), overlay_texture.getMaxV());
            }
        }
        // Vertical dust rendering
        if ((((connections)&(0x002|0x010|0x080|0x400))!=0)) {
            tessellator.setColorOpaque_F(red, green, blue);
            base_texture = ((IBlockRedstoneWireMixins)block).get_texture_by_index(0);
            double maxY;
            maxY = (minY = (double)Y) + 1.021875D;
            tempMinX = minX + 0.015625D;
            tempMaxX = maxX - 0.015625D;
            tempMinZ = minZ + 0.015625D;
            tempMaxZ = maxZ - 0.015625D;
            if ((((connections)&0x002)!=0)) {
                tessellator.addVertexWithUV(tempMaxX, minY, maxZ, base_texture.getMinU(), base_texture.getMaxV());
                tessellator.addVertexWithUV(tempMaxX, maxY, maxZ, base_texture.getMaxU(), base_texture.getMaxV());
                tessellator.addVertexWithUV(tempMaxX, maxY, minZ, base_texture.getMaxU(), base_texture.getMinV());
                tessellator.addVertexWithUV(tempMaxX, minY, minZ, base_texture.getMinU(), base_texture.getMinV());
                if ((((connections)&0x004)!=0)) {
                    tessellator.addVertexWithUV(tempMaxX, minY, minZ, base_texture.getMinU(), base_texture.getMinV());
                    tessellator.addVertexWithUV(tempMaxX, maxY, minZ, base_texture.getMaxU(), base_texture.getMinV());
                    tessellator.addVertexWithUV(tempMaxX, maxY, maxZ, base_texture.getMaxU(), base_texture.getMaxV());
                    tessellator.addVertexWithUV(tempMaxX, minY, maxZ, base_texture.getMinU(), base_texture.getMaxV());
                }
            }
            if ((((connections)&0x010)!=0)) {
                tessellator.addVertexWithUV(tempMinX, maxY, maxZ, base_texture.getMaxU(), base_texture.getMinV());
                tessellator.addVertexWithUV(tempMinX, minY, maxZ, base_texture.getMinU(), base_texture.getMinV());
                tessellator.addVertexWithUV(tempMinX, minY, minZ, base_texture.getMinU(), base_texture.getMaxV());
                tessellator.addVertexWithUV(tempMinX, maxY, minZ, base_texture.getMaxU(), base_texture.getMaxV());
                if ((((connections)&0x020)!=0)) {
                    tessellator.addVertexWithUV(tempMinX, maxY, minZ, base_texture.getMaxU(), base_texture.getMaxV());
                    tessellator.addVertexWithUV(tempMinX, minY, minZ, base_texture.getMinU(), base_texture.getMaxV());
                    tessellator.addVertexWithUV(tempMinX, minY, maxZ, base_texture.getMinU(), base_texture.getMinV());
                    tessellator.addVertexWithUV(tempMinX, maxY, maxZ, base_texture.getMaxU(), base_texture.getMinV());
                }
            }
            if ((((connections)&0x080)!=0)) {
                tessellator.addVertexWithUV(maxX, maxY, tempMaxZ, base_texture.getMaxU(), base_texture.getMinV());
                tessellator.addVertexWithUV(maxX, minY, tempMaxZ, base_texture.getMinU(), base_texture.getMinV());
                tessellator.addVertexWithUV(minX, minY, tempMaxZ, base_texture.getMinU(), base_texture.getMaxV());
                tessellator.addVertexWithUV(minX, maxY, tempMaxZ, base_texture.getMaxU(), base_texture.getMaxV());
                if ((((connections)&0x100)!=0)) {
                    tessellator.addVertexWithUV(minX, maxY, tempMaxZ, base_texture.getMaxU(), base_texture.getMaxV());
                    tessellator.addVertexWithUV(minX, minY, tempMaxZ, base_texture.getMinU(), base_texture.getMaxV());
                    tessellator.addVertexWithUV(maxX, minY, tempMaxZ, base_texture.getMinU(), base_texture.getMinV());
                    tessellator.addVertexWithUV(maxX, maxY, tempMaxZ, base_texture.getMaxU(), base_texture.getMinV());
                }
            }
            if ((((connections)&0x400)!=0)) {
                tessellator.addVertexWithUV(maxX, minY, tempMinZ, base_texture.getMinU(), base_texture.getMaxV());
                tessellator.addVertexWithUV(maxX, maxY, tempMinZ, base_texture.getMaxU(), base_texture.getMaxV());
                tessellator.addVertexWithUV(minX, maxY, tempMinZ, base_texture.getMaxU(), base_texture.getMinV());
                tessellator.addVertexWithUV(minX, minY, tempMinZ, base_texture.getMinU(), base_texture.getMinV());
                if (((connections)>=0x800)) {
                    tessellator.addVertexWithUV(minX, minY, tempMinZ, base_texture.getMinU(), base_texture.getMinV());
                    tessellator.addVertexWithUV(minX, maxY, tempMinZ, base_texture.getMaxU(), base_texture.getMinV());
                    tessellator.addVertexWithUV(maxX, maxY, tempMinZ, base_texture.getMaxU(), base_texture.getMaxV());
                    tessellator.addVertexWithUV(maxX, minY, tempMinZ, base_texture.getMinU(), base_texture.getMaxV());
                }
            }
            tessellator.setColorRGBA(255, 255, 255, 255);
            overlay_texture = ((IBlockRedstoneWireMixins)block).get_texture_by_index(1);
            if ((((connections)&0x002)!=0)) {
                tessellator.addVertexWithUV(tempMaxX, minY, maxZ, overlay_texture.getMinU(), overlay_texture.getMaxV());
                tessellator.addVertexWithUV(tempMaxX, maxY, maxZ, overlay_texture.getMaxU(), overlay_texture.getMaxV());
                tessellator.addVertexWithUV(tempMaxX, maxY, minZ, overlay_texture.getMaxU(), overlay_texture.getMinV());
                tessellator.addVertexWithUV(tempMaxX, minY, minZ, overlay_texture.getMinU(), overlay_texture.getMinV());
                if ((((connections)&0x004)!=0)) {
                    tessellator.addVertexWithUV(tempMaxX, minY, minZ, overlay_texture.getMinU(), overlay_texture.getMinV());
                    tessellator.addVertexWithUV(tempMaxX, maxY, minZ, overlay_texture.getMaxU(), overlay_texture.getMinV());
                    tessellator.addVertexWithUV(tempMaxX, maxY, maxZ, overlay_texture.getMaxU(), overlay_texture.getMaxV());
                    tessellator.addVertexWithUV(tempMaxX, minY, maxZ, overlay_texture.getMinU(), overlay_texture.getMaxV());
                }
            }
            if ((((connections)&0x010)!=0)) {
                tessellator.addVertexWithUV(tempMinX, maxY, maxZ, overlay_texture.getMaxU(), overlay_texture.getMinV());
                tessellator.addVertexWithUV(tempMinX, minY, maxZ, overlay_texture.getMinU(), overlay_texture.getMinV());
                tessellator.addVertexWithUV(tempMinX, minY, minZ, overlay_texture.getMinU(), overlay_texture.getMaxV());
                tessellator.addVertexWithUV(tempMinX, maxY, minZ, overlay_texture.getMaxU(), overlay_texture.getMaxV());
                if ((((connections)&0x020)!=0)) {
                    tessellator.addVertexWithUV(tempMinX, maxY, minZ, overlay_texture.getMaxU(), overlay_texture.getMaxV());
                    tessellator.addVertexWithUV(tempMinX, minY, minZ, overlay_texture.getMinU(), overlay_texture.getMaxV());
                    tessellator.addVertexWithUV(tempMinX, minY, maxZ, overlay_texture.getMinU(), overlay_texture.getMinV());
                    tessellator.addVertexWithUV(tempMinX, maxY, maxZ, overlay_texture.getMaxU(), overlay_texture.getMinV());
                }
            }
            if ((((connections)&0x080)!=0)) {
                tessellator.addVertexWithUV(maxX, maxY, tempMaxZ, overlay_texture.getMaxU(), overlay_texture.getMinV());
                tessellator.addVertexWithUV(maxX, minY, tempMaxZ, overlay_texture.getMinU(), overlay_texture.getMinV());
                tessellator.addVertexWithUV(minX, minY, tempMaxZ, overlay_texture.getMinU(), overlay_texture.getMaxV());
                tessellator.addVertexWithUV(minX, maxY, tempMaxZ, overlay_texture.getMaxU(), overlay_texture.getMaxV());
                if ((((connections)&0x100)!=0)) {
                    tessellator.addVertexWithUV(minX, maxY, tempMaxZ, overlay_texture.getMaxU(), overlay_texture.getMaxV());
                    tessellator.addVertexWithUV(minX, minY, tempMaxZ, overlay_texture.getMinU(), overlay_texture.getMaxV());
                    tessellator.addVertexWithUV(maxX, minY, tempMaxZ, overlay_texture.getMinU(), overlay_texture.getMinV());
                    tessellator.addVertexWithUV(maxX, maxY, tempMaxZ, overlay_texture.getMaxU(), overlay_texture.getMinV());
                }
            }
            if ((((connections)&0x400)!=0)) {
                tessellator.addVertexWithUV(maxX, minY, tempMinZ, overlay_texture.getMinU(), overlay_texture.getMaxV());
                tessellator.addVertexWithUV(maxX, maxY, tempMinZ, overlay_texture.getMaxU(), overlay_texture.getMaxV());
                tessellator.addVertexWithUV(minX, maxY, tempMinZ, overlay_texture.getMaxU(), overlay_texture.getMinV());
                tessellator.addVertexWithUV(minX, minY, tempMinZ, overlay_texture.getMinU(), overlay_texture.getMinV());
                if (((connections)>=0x800)) {
                    tessellator.addVertexWithUV(minX, minY, tempMinZ, overlay_texture.getMinU(), overlay_texture.getMinV());
                    tessellator.addVertexWithUV(minX, maxY, tempMinZ, overlay_texture.getMaxU(), overlay_texture.getMinV());
                    tessellator.addVertexWithUV(maxX, maxY, tempMinZ, overlay_texture.getMaxU(), overlay_texture.getMaxV());
                    tessellator.addVertexWithUV(maxX, minY, tempMinZ, overlay_texture.getMinU(), overlay_texture.getMaxV());
                }
            }
        }
        return true;
    }
    @Inject(
        method = "renderBlockRepeater(Lnet/minecraft/src/BlockRedstoneRepeater;III)Z",
        at = @At("HEAD")
    )
    public void renderBlockRepeater_inject(BlockRedstoneRepeater block, int X, int Y, int Z, CallbackInfoReturnable info) {
        ((IBlockRedstoneLogicMixins)block).setRenderingBaseTextures(false);
    }
    // Apparently this is unused?
    // Some sort of BTW override class prevents the call.
    @Inject(
        method = "renderBlockComparator(Lnet/minecraft/src/BlockComparator;III)Z",
        at = @At("HEAD")
    )
    public void renderBlockComparator_inject(BlockComparator block, int X, int Y, int Z, CallbackInfoReturnable info) {
        ((IBlockRedstoneLogicMixins)block).setRenderingBaseTextures(false);
    }
    // Replacement torch rendering that skips the unnecessary
    // XZ offset calculations and prevents torches clipping
    // into the base of the block.
    public void renderTorchForRedstoneLogic(Block block, double X, double Y, double Z, int meta) {
        RenderBlocks self = (RenderBlocks)(Object)this;
        Tessellator tessellator = Tessellator.instance;
        Icon texture = self.getBlockIconFromSideAndMetadata(block, 0, meta);
        if (self.hasOverrideBlockTexture()) {
            // Comparator front torch hack
            if ((meta & 4) != 0) {
                Y -= 0.125D;
            }
            texture = ((IRenderBlocksAccessMixins)self).getOverrideBlockTexture();
        }
        double minU = (double)texture.getInterpolatedU(7.0D);
        double minV = (double)texture.getInterpolatedV(6.0D);
        double maxU = (double)texture.getInterpolatedU(9.0D);
        double maxV = (double)texture.getInterpolatedV(8.0D);
        double minX = X + 0.4375D;
        double maxX = X + 0.5625D;
        double minZ = Z + 0.4375D;
        double maxZ = Z + 0.5625D;
        double maxY = Y + 0.625D;
        tessellator.addVertexWithUV(minX, maxY, minZ, minU, minV);
        tessellator.addVertexWithUV(minX, maxY, maxZ, minU, maxV);
        tessellator.addVertexWithUV(maxX, maxY, maxZ, maxU, maxV);
        tessellator.addVertexWithUV(maxX, maxY, minZ, maxU, minV);
        maxY = Y + 1.0D;
        Y = Math.ceil((double)Y) + 0.125D;
        minU = texture.getMinU();
        minV = texture.getMinV();
        maxU = texture.getMaxU();
        maxV = (double)texture.getInterpolatedV((maxY - Y) * 16.0D);
        double temp = Z + 1.0D;
        tessellator.addVertexWithUV(minX, maxY, Z, minU, minV);
        tessellator.addVertexWithUV(minX, Y, Z, minU, maxV);
        tessellator.addVertexWithUV(minX, Y, temp, maxU, maxV);
        tessellator.addVertexWithUV(minX, maxY, temp, maxU, minV);
        tessellator.addVertexWithUV(maxX, maxY, temp, minU, minV);
        tessellator.addVertexWithUV(maxX, Y, temp, minU, maxV);
        tessellator.addVertexWithUV(maxX, Y, Z, maxU, maxV);
        tessellator.addVertexWithUV(maxX, maxY, Z, maxU, minV);
        temp = X + 1.0D;
        tessellator.addVertexWithUV(X, maxY, maxZ, minU, minV);
        tessellator.addVertexWithUV(X, Y, maxZ, minU, maxV);
        tessellator.addVertexWithUV(temp, Y, maxZ, maxU, maxV);
        tessellator.addVertexWithUV(temp, maxY, maxZ, maxU, minV);
        tessellator.addVertexWithUV(temp, maxY, minZ, minU, minV);
        tessellator.addVertexWithUV(temp, Y, minZ, minU, maxV);
        tessellator.addVertexWithUV(X, Y, minZ, maxU, maxV);
        tessellator.addVertexWithUV(X, maxY, minZ, maxU, minV);
    }
    @Redirect(
        method = { "renderBlockRepeater", "renderBlockComparator" },
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/src/RenderBlocks;renderTorchAtAngle(Lnet/minecraft/src/Block;DDDDDI)V"
        )
    )
    public void renderTorchAtAngle_redirect(RenderBlocks self, Block block, double X, double Y, double Z, double angleA, double angleB, int meta) {
        ((IRenderBlocksMixins)self).renderTorchForRedstoneLogic(block, X, Y, Z, meta);
    }
    @Inject(
        method = "renderBlockRedstoneLogicMetadata(Lnet/minecraft/src/BlockRedstoneLogic;IIII)V",
        at = @At("HEAD")
    )
    public void renderBlockRedstoneLogicMetadata_inject(BlockRedstoneLogic block, int X, int Y, int Z, int meta, CallbackInfo info) {
        ((IBlockRedstoneLogicMixins)block).setRenderingBaseTextures(true);
    }
}
