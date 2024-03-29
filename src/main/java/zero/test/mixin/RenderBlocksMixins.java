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
//import zero.test.mixin.IRedstoneWireAccessMixins;
import zero.test.IBlockRedstoneLogicMixins;
//import zero.test.mixin.IRenderBlocksAccessMixins;
import zero.test.IRenderBlocksMixins;
//#define getInputSignal(...) func_94482_f(__VA_ARGS__)
@Mixin(RenderBlocks.class)
public abstract class RenderBlocksMixins implements IRenderBlocksMixins {
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
    public boolean renderBlockRedstoneWire(Block block, int x, int y, int z) {
        RenderBlocks self = (RenderBlocks)(Object)this;
        Tessellator tessellator = Tessellator.instance;
        tessellator.setBrightness(block.getMixedBrightnessForBlock(self.blockAccess, x, y, z));
        Block belowBlock = Block.blocksList[self.blockAccess.getBlockId(x, y - 1, z)];
        // Assuming the redstone isn't floating in midair seems
        // a bit *too* dangerous, even for my taste
        boolean renderBottom = ((belowBlock)==null) || belowBlock.shouldRenderNeighborFullFaceSide(self.blockAccess, x, y - 1, z, 1);
        int power = (((self.blockAccess.getBlockMetadata(x, y, z))));
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
            red = (temp = (float)power * 0.06666667F) * 0.6F + (power != 0 ? 0.4F : 0.3F);
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
        double minU;
        double maxU;
        double minV;
        double maxV;
        /*
        AddonHandler.logMessage(""
            +X+" "+Y+" "+Z+"\n"
            +connections+" "+texture_index
        );
        */
        // Flat dust rendering
        if (texture_index != 0) {
            // Rendering the cross texture
            minU = 0.0D;
            minV = 0.0D;
            maxU = 16.0D;
            maxV = 16.0D;
            tempMinX = minX;
            tempMaxX = maxX;
            tempMinZ = minZ;
            tempMaxZ = maxZ;
            if (!(((connections)&00001)!=0)) {
                tempMaxX -= 0.3125D;
                maxU = 11.0D;
            }
            if (!(((connections)&00010)!=0)) {
                tempMinX += 0.3125D;
                minU = 5.0D;
            }
            if (!(((connections)&00100)!=0)) {
                tempMaxZ -= 0.3125D;
                maxV = 11.0D;
            }
            if (!(((connections)&01000)!=0)) {
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
            if (renderBottom) {
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
            if (renderBottom) {
                tessellator.addVertexWithUV(tempMinX, minY, tempMaxZ, texMinU, texMaxV);
                tessellator.addVertexWithUV(tempMinX, minY, tempMinZ, texMinU, texMinV);
                tessellator.addVertexWithUV(tempMaxX, minY, tempMinZ, texMaxU, texMinV);
                tessellator.addVertexWithUV(tempMaxX, minY, tempMaxZ, texMaxU, texMaxV);
            }
        }
        else {
            // Rendering the line texture
            minU = base_texture.getMinU();
            maxU = base_texture.getMaxU();
            minV = base_texture.getMinV();
            maxV = base_texture.getMaxV();
            if (!((connections)>=00100)) {
                texMinU = minU;
                texMaxU = maxU;
                texMinV = minV;
                texMaxV = maxV;
            } else {
                texMinU = maxU;
                texMaxU = minU;
                texMinV = maxV;
                texMaxV = minV;
            }
            tessellator.addVertexWithUV(maxX, minY, maxZ, maxU, maxV);
            tessellator.addVertexWithUV(maxX, minY, minZ, texMaxU, texMinV);
            tessellator.addVertexWithUV(minX, minY, minZ, minU, minV);
            tessellator.addVertexWithUV(minX, minY, maxZ, texMinU, texMaxV);
            if (renderBottom) {
                tessellator.addVertexWithUV(minX, minY, maxZ, texMinU, texMaxV);
                tessellator.addVertexWithUV(minX, minY, minZ, minU, minV);
                tessellator.addVertexWithUV(maxX, minY, minZ, texMaxU, texMinV);
                tessellator.addVertexWithUV(maxX, minY, maxZ, maxU, maxV);
            }
            tessellator.setColorRGBA(255, 255, 255, 255);
            minU = overlay_texture.getMinU();
            maxU = overlay_texture.getMaxU();
            minV = overlay_texture.getMinV();
            maxV = overlay_texture.getMaxV();
            if (!((connections)>=00100)) {
                texMinU = minU;
                texMaxU = maxU;
                texMinV = minV;
                texMaxV = maxV;
            } else {
                texMinU = maxU;
                texMaxU = minU;
                texMinV = maxV;
                texMaxV = minV;
            }
            tessellator.addVertexWithUV(maxX, minY, maxZ, maxU, maxV);
            tessellator.addVertexWithUV(maxX, minY, minZ, texMaxU, texMinV);
            tessellator.addVertexWithUV(minX, minY, minZ, minU, minV);
            tessellator.addVertexWithUV(minX, minY, maxZ, texMinU, texMaxV);
            if (renderBottom) {
                tessellator.addVertexWithUV(minX, minY, maxZ, texMinU, texMaxV);
                tessellator.addVertexWithUV(minX, minY, minZ, minU, minV);
                tessellator.addVertexWithUV(maxX, minY, minZ, texMaxU, texMinV);
                tessellator.addVertexWithUV(maxX, minY, maxZ, maxU, maxV);
            }
        }
        // Vertical dust rendering
        if ((((connections)&(00002|00020|00200|02000))!=0)) {
            tessellator.setColorOpaque_F(red, green, blue);
            base_texture = ((IBlockRedstoneWireMixins)block).get_texture_by_index(0);
            double maxY;
            maxY = (minY = (double)y) + 1.021875D;
            tempMinX = minX + 0.015625D;
            tempMaxX = maxX - 0.015625D;
            tempMinZ = minZ + 0.015625D;
            tempMaxZ = maxZ - 0.015625D;
            minU = base_texture.getMinU();
            maxU = base_texture.getMaxU();
            minV = base_texture.getMinV();
            maxV = base_texture.getMaxV();
            if ((((connections)&00002)!=0)) {
                tessellator.addVertexWithUV(tempMaxX, minY, maxZ, minU, maxV);
                tessellator.addVertexWithUV(tempMaxX, maxY, maxZ, maxU, maxV);
                tessellator.addVertexWithUV(tempMaxX, maxY, minZ, maxU, minV);
                tessellator.addVertexWithUV(tempMaxX, minY, minZ, minU, minV);
                if ((((connections)&00004)!=0)) {
                    tessellator.addVertexWithUV(tempMaxX, minY, minZ, minU, minV);
                    tessellator.addVertexWithUV(tempMaxX, maxY, minZ, maxU, minV);
                    tessellator.addVertexWithUV(tempMaxX, maxY, maxZ, maxU, maxV);
                    tessellator.addVertexWithUV(tempMaxX, minY, maxZ, minU, maxV);
                }
            }
            if ((((connections)&00020)!=0)) {
                tessellator.addVertexWithUV(tempMinX, maxY, maxZ, maxU, minV);
                tessellator.addVertexWithUV(tempMinX, minY, maxZ, minU, minV);
                tessellator.addVertexWithUV(tempMinX, minY, minZ, minU, maxV);
                tessellator.addVertexWithUV(tempMinX, maxY, minZ, maxU, maxV);
                if ((((connections)&00040)!=0)) {
                    tessellator.addVertexWithUV(tempMinX, maxY, minZ, maxU, maxV);
                    tessellator.addVertexWithUV(tempMinX, minY, minZ, minU, maxV);
                    tessellator.addVertexWithUV(tempMinX, minY, maxZ, minU, minV);
                    tessellator.addVertexWithUV(tempMinX, maxY, maxZ, maxU, minV);
                }
            }
            if ((((connections)&00200)!=0)) {
                tessellator.addVertexWithUV(maxX, maxY, tempMaxZ, maxU, minV);
                tessellator.addVertexWithUV(maxX, minY, tempMaxZ, minU, minV);
                tessellator.addVertexWithUV(minX, minY, tempMaxZ, minU, maxV);
                tessellator.addVertexWithUV(minX, maxY, tempMaxZ, maxU, maxV);
                if ((((connections)&00400)!=0)) {
                    tessellator.addVertexWithUV(minX, maxY, tempMaxZ, maxU, maxV);
                    tessellator.addVertexWithUV(minX, minY, tempMaxZ, minU, maxV);
                    tessellator.addVertexWithUV(maxX, minY, tempMaxZ, minU, minV);
                    tessellator.addVertexWithUV(maxX, maxY, tempMaxZ, maxU, minV);
                }
            }
            if ((((connections)&02000)!=0)) {
                tessellator.addVertexWithUV(maxX, minY, tempMinZ, minU, maxV);
                tessellator.addVertexWithUV(maxX, maxY, tempMinZ, maxU, maxV);
                tessellator.addVertexWithUV(minX, maxY, tempMinZ, maxU, minV);
                tessellator.addVertexWithUV(minX, minY, tempMinZ, minU, minV);
                if (((connections)>=04000)) {
                    tessellator.addVertexWithUV(minX, minY, tempMinZ, minU, minV);
                    tessellator.addVertexWithUV(minX, maxY, tempMinZ, maxU, minV);
                    tessellator.addVertexWithUV(maxX, maxY, tempMinZ, maxU, maxV);
                    tessellator.addVertexWithUV(maxX, minY, tempMinZ, minU, maxV);
                }
            }
            tessellator.setColorRGBA(255, 255, 255, 255);
            overlay_texture = ((IBlockRedstoneWireMixins)block).get_texture_by_index(1);
            if ((((connections)&00002)!=0)) {
                tessellator.addVertexWithUV(tempMaxX, minY, maxZ, overlay_texture.getMinU(), overlay_texture.getMaxV());
                tessellator.addVertexWithUV(tempMaxX, maxY, maxZ, overlay_texture.getMaxU(), overlay_texture.getMaxV());
                tessellator.addVertexWithUV(tempMaxX, maxY, minZ, overlay_texture.getMaxU(), overlay_texture.getMinV());
                tessellator.addVertexWithUV(tempMaxX, minY, minZ, overlay_texture.getMinU(), overlay_texture.getMinV());
                if ((((connections)&00004)!=0)) {
                    tessellator.addVertexWithUV(tempMaxX, minY, minZ, overlay_texture.getMinU(), overlay_texture.getMinV());
                    tessellator.addVertexWithUV(tempMaxX, maxY, minZ, overlay_texture.getMaxU(), overlay_texture.getMinV());
                    tessellator.addVertexWithUV(tempMaxX, maxY, maxZ, overlay_texture.getMaxU(), overlay_texture.getMaxV());
                    tessellator.addVertexWithUV(tempMaxX, minY, maxZ, overlay_texture.getMinU(), overlay_texture.getMaxV());
                }
            }
            if ((((connections)&00020)!=0)) {
                tessellator.addVertexWithUV(tempMinX, maxY, maxZ, overlay_texture.getMaxU(), overlay_texture.getMinV());
                tessellator.addVertexWithUV(tempMinX, minY, maxZ, overlay_texture.getMinU(), overlay_texture.getMinV());
                tessellator.addVertexWithUV(tempMinX, minY, minZ, overlay_texture.getMinU(), overlay_texture.getMaxV());
                tessellator.addVertexWithUV(tempMinX, maxY, minZ, overlay_texture.getMaxU(), overlay_texture.getMaxV());
                if ((((connections)&00040)!=0)) {
                    tessellator.addVertexWithUV(tempMinX, maxY, minZ, overlay_texture.getMaxU(), overlay_texture.getMaxV());
                    tessellator.addVertexWithUV(tempMinX, minY, minZ, overlay_texture.getMinU(), overlay_texture.getMaxV());
                    tessellator.addVertexWithUV(tempMinX, minY, maxZ, overlay_texture.getMinU(), overlay_texture.getMinV());
                    tessellator.addVertexWithUV(tempMinX, maxY, maxZ, overlay_texture.getMaxU(), overlay_texture.getMinV());
                }
            }
            if ((((connections)&00200)!=0)) {
                tessellator.addVertexWithUV(maxX, maxY, tempMaxZ, overlay_texture.getMaxU(), overlay_texture.getMinV());
                tessellator.addVertexWithUV(maxX, minY, tempMaxZ, overlay_texture.getMinU(), overlay_texture.getMinV());
                tessellator.addVertexWithUV(minX, minY, tempMaxZ, overlay_texture.getMinU(), overlay_texture.getMaxV());
                tessellator.addVertexWithUV(minX, maxY, tempMaxZ, overlay_texture.getMaxU(), overlay_texture.getMaxV());
                if ((((connections)&00400)!=0)) {
                    tessellator.addVertexWithUV(minX, maxY, tempMaxZ, overlay_texture.getMaxU(), overlay_texture.getMaxV());
                    tessellator.addVertexWithUV(minX, minY, tempMaxZ, overlay_texture.getMinU(), overlay_texture.getMaxV());
                    tessellator.addVertexWithUV(maxX, minY, tempMaxZ, overlay_texture.getMinU(), overlay_texture.getMinV());
                    tessellator.addVertexWithUV(maxX, maxY, tempMaxZ, overlay_texture.getMaxU(), overlay_texture.getMinV());
                }
            }
            if ((((connections)&02000)!=0)) {
                tessellator.addVertexWithUV(maxX, minY, tempMinZ, overlay_texture.getMinU(), overlay_texture.getMaxV());
                tessellator.addVertexWithUV(maxX, maxY, tempMinZ, overlay_texture.getMaxU(), overlay_texture.getMaxV());
                tessellator.addVertexWithUV(minX, maxY, tempMinZ, overlay_texture.getMaxU(), overlay_texture.getMinV());
                tessellator.addVertexWithUV(minX, minY, tempMinZ, overlay_texture.getMinU(), overlay_texture.getMinV());
                if (((connections)>=04000)) {
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
