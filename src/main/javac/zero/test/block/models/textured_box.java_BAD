package zero.test.block.model;

import net.minecraft.src.*;

import com.prupe.mcpatcher.renderpass.RenderPass;

import btw.block.model.BlockModel;
import btw.util.PrimitiveAABBWithBenefits;
import btw.util.PrimitiveGeometric;
import btw.util.PrimitiveQuad;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import zero.test.IBlockMixins;

#include "..\..\util.h"
#include "..\..\feature_flags.h"

public class TexturedBox
#if ENABLE_TEXTURED_BOX
extends AxisAlignedBB 
#endif
{
    
#if ENABLE_TEXTURED_BOX
    private int textureIndex = -1;
    private int renderData;



#define GET_CULL_DIRECTION(data) ((data)&7)
#define GET_TEXTURE_ROTATION(data) ((data)>>>3&3)

#define MIN_U(uv_index) (uv_index)
#define MIN_V(uv_index) ((uv_index)+1)
#define MAX_U(uv_index) ((uv_index)+2)
#define MAX_V(uv_index) ((uv_index)+3)
    
    /*
    union {
        struct {
            downCullDir : 3;
            downRotation : 2;
            upCullDir : 3;
            upRotation : 2;
            northCullDir : 3;
            northRotation : 3;
            southCullDir : 3;
            southRotation : 2;
            westCullDir : 3;
            westRotation : 2;
            eastCullDir : 3;
            eastRotation : 2;
        }
    };
    */
    private float[] uv = new float[24];
    
    private static int[] uv_lookup = new int[] {
        
    };
    
    public TexturedBox(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax, int textureIndex,
        float downUMin, float downVMin, float downUMax, float downVMax, int downCull, int downRotation,
        float upUMin, float upVMin, float upUMax, float upVMax, int upCull, int upRotation,
        float northUMin, float northVMin, float northUMax, float northVMax, int northCull, int northRotation,
        float southUMin, float southVMin, float southUMax, float southVMax, int southCull, int southRotation,
        float westUMin, float westVMin, float westUMax, float westVMax, int westCull, int westRotation,
        float eastUMin, float eastVMin, float eastUMax, float eastVMax, int eastCull, int eastRotation
    ) {
        this(
            xMin, yMin, zMin, xMax, yMax, zMax, textureIndex,
            downCull | downRotation << 3 | upCull << 5 | upRotation << 8 | northCull << 10 | northRotation << 13 | southCull << 15 | southRotation << 18 | westCull << 20 | westRotation << 23 | eastCull << 25 | eastRotation << 28,
            downUMin, downVMin, downUMax, downVMax,
            upUMin, upVMin, upUMax, upVMin,
            northUMin, northVMin, northUMax, northVMax,
            southUMin, southVMin, southUMax, southVMax,
            westUMin, westVMin, westUMax, westVMax,
            eastUMin, eastVMin, eastUMax, eastUMin
        );
    }
    
    public TexturedBox(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax, int textureIndex, int renderBits,
        float downUMin, float downVMin, float downUMax, float downVMax,
        float upUMin, float upVMin, float upUMax, float upVMax,
        float northUMin, float northVMin, float northUMax, float northVMax,
        float southUMin, float southVMin, float southUMax, float southVMax,
        float westUMin, float westVMin, float westUMax, float westVMax,
        float eastUMin, float eastVMin, float eastUMax, float eastVMax
    ) {
        super(xMin, yMin, zMin, xMax, yMax, zMax);
        this.textureIndex = textureIndex;
        this.renderData = renderBits;
        this.uv[0] = downUMin;
        this.uv[1] = downVMin;
        this.uv[2] = downUMax;
        this.uv[3] = downVMax;
        this.uv[4] = upUMin;
        this.uv[5] = upVMin;
        this.uv[6] = upUMax;
        this.uv[7] = upVMax;
        this.uv[8] = northUMin;
        this.uv[9] = northVMin;
        this.uv[10] = northUMax;
        this.uv[11] = northVMax;
        this.uv[12] = southUMin;
        this.uv[13] = southVMin;
        this.uv[14] = southUMax;
        this.uv[15] = southVMax;
        this.uv[16] = westUMin;
        this.uv[17] = westVMin;
        this.uv[18] = westUMax;
        this.uv[19] = westVMax;
        this.uv[20] = eastUMin;
        this.uv[21] = eastVMin;
        this.uv[22] = eastUMax;
        this.uv[23] = eastVMax;
    }
    
    @Environment(EnvType.CLIENT)
    public void addFaces(RenderBlocks renderBlocks, Block block, int x, int y, int z) {
        int direction = 0;
        int uvIndex = 0;
        int renderBits = this.renderData;
        
        double dX = (double)x;
        double dY = (double)y;
        double dZ = (double)z;
        
        Tessellator tessellator = Tessellator.instance;
        
        do {
            int cullDirection;
            switch (cullDirection = GET_CULL_DIRECTION(renderBits)) {
                default:
                    if (!RenderPass.shouldSideBeRendered(block, renderBlocks.blockAccess, x + Facing.offsetsXForSide[cullDirection], y + Facing.offsetsYForSide[cullDirection], z + Facing.offsetsZForSide[cullDirection], cullDirection)) {
                        break;
                    }
                case CULL_DIRECTION_NONE:
                    Icon texture = ((IBlockMixins)block).getIconBySidedIndex(direction, textureIndex);
                    double minU, minU2;
                    double minV, minV2;
                    double maxU, maxU2;
                    double maxV, maxV2;
                    
                    double minX, maxX;
                    double minY, maxY;
                    double minZ, maxZ;
                    switch (direction) {
                        case DIRECTION_DOWN:
                            minX = dX + this.minX;
                            maxX = dX + this.maxX;
                            maxY = minY = dY + this.minY;
                            minZ = dZ + this.minZ;
                            maxZ = dZ + this.maxZ;
                            switch (GET_TEXTURE_ROTATION(renderBits)) {
                                case TEXTURE_ROTATION_NONE:
                                    minU2 = minU = (double)texture.getInterpolatedU((double)this.uv[MIN_U(uvIndex)]);
                                    minV2 = minV = (double)texture.getInterpolatedV((double)this.uv[MIN_V(uvIndex)]);
                                    maxU2 = maxU = (double)texture.getInterpolatedU((double)this.uv[MAX_U(uvIndex)]);
                                    maxV2 = maxV = (double)texture.getInterpolatedV((double)this.uv[MAX_V(uvIndex)]);
                                    break;
                                case TEXTURE_ROTATION_90:
                                    maxU2 = minU = (double)texture.getInterpolatedU(16.0D - (double)this.uv[MIN_V(uvIndex)]);
                                    minU2 = maxU = (double)texture.getInterpolatedU(16.0D - (double)this.uv[MAX_V(uvIndex)]);
                                    minV2 = maxV = (double)texture.getInterpolatedV((double)this.uv[MAX_U(uvIndex)]);
                                    maxV2 = minV = (double)texture.getInterpolatedV((double)this.uv[MIN_U(uvIndex)]);
                                    break;
                                default: //case TEXTURE_ROTATION_180:
                                    minU2 = minU = (double)texture.getInterpolatedU(16.0D - (double)this.uv[MIN_U(uvIndex)]);
                                    minV2 = minV = (double)texture.getInterpolatedV(16.0D - (double)this.uv[MIN_V(uvIndex)]);
                                    maxU2 = maxU = (double)texture.getInterpolatedU(16.0D - (double)this.uv[MAX_U(uvIndex)]);
                                    maxV2 = maxV = (double)texture.getInterpolatedV(16.0D - (double)this.uv[MAX_V(uvIndex)]);
                                    break;
                                case TEXTURE_ROTATION_270:
                                    maxU2 = minU = (double)texture.getInterpolatedU((double)this.uv[MIN_V(uvIndex)]);
                                    minU2 = maxU = (double)texture.getInterpolatedU((double)this.uv[MAX_V(uvIndex)]);
                                    minV2 = maxV = (double)texture.getInterpolatedV(16.0D - (double)this.uv[MAX_U(uvIndex)]);
                                    maxV2 = minV = (double)texture.getInterpolatedV(16.0D - (double)this.uv[MIN_U(uvIndex)]);
                                    break;
                            }
                            break;
                        case DIRECTION_UP:
                            minX = dX + this.maxX;
                            maxX = dX + this.minX;
                            maxY = minY = dY + this.maxY;
                            minZ = dZ + this.minZ;
                            maxZ = dZ + this.maxZ;
                            break;
                        case DIRECTION_NORTH:
                        case DIRECTION_SOUTH:
                        case DIRECTION_WEST:
                        default: // DIRECTION_EAST:
                    }
                    tessellator.addVertexWithUV(minX, var30, maxZ, minU2, maxV2);
                    tessellator.addVertexWithUV(minX, var30, minZ, minU, minV);
                    tessellator.addVertexWithUV(maxX, var30, minZ, maxU2, minV2);
                    tessellator.addVertexWithUV(maxX, var30, maxZ, maxU, maxV);
                case CULL_DIRECTION_ALL:
                    break;
            }
            uvIndex += 4;
            renderBits >>>= 5;
        } while (DIRECTION_IS_VALID(++direction));
    }
    
    
    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderAsBlock(RenderBlocks renderBlocks, Block block, int x, int y, int z) {
        
        this.addFaces(renderBlocks, block, x, y, z);
        return true;
    }
/*
    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderAsBlockWithColorMultiplier(RenderBlocks renderBlocks, Block block, int x, int y, int z, float red, float green, float blue) {
        renderBlocks.setRenderBounds(this);
        
        return renderBlocks.renderStandardBlockWithColorMultiplier(block, x, y, z, red, green, blue);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderAsBlockWithTexture(RenderBlocks renderBlocks, Block block, int x, int y, int z, Icon texture) {
        renderBlocks.setRenderBounds(this);
        
        RenderUtils.renderStandardBlockWithTexture(renderBlocks, block, x, y, z, texture);
        
        return true;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean renderAsBlockFullBrightWithTexture(RenderBlocks renderBlocks, Block block, int x, int y, int z, Icon texture) {
        renderBlocks.setRenderBounds(this);
        
        RenderUtils.renderBlockFullBrightWithTexture(renderBlocks, renderBlocks.blockAccess, x, y, z, texture);
        
        return true;
    }
*/

    @Override
    @Environment(EnvType.CLIENT)
    public void renderAsItemBlock(RenderBlocks renderBlocks, Block block, int damage) {
        renderBlocks.setRenderBounds(this);
        
        RenderUtils.renderInvBlockWithMetadata(renderBlocks, block, -0.5F, -0.5F, -0.5F, damage);
    }

/*
    @Override
    @Environment(EnvType.CLIENT)
    public void renderAsFallingBlock(RenderBlocks renderBlocks, Block block, int x, int y, int z, int meta) {
        renderBlocks.setRenderBounds(this);
        
        renderBlocks.renderStandardFallingBlock(block, x, y, z, meta);
    }
*/
#endif
}