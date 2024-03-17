package zero.test.gui;

import net.minecraft.src.*;

import btw.client.gui.CookingVesselGui;
import btw.inventory.container.CookingVesselContainer;
import btw.block.tileentity.CookingVesselTileEntity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.lwjgl.opengl.GL11;

#include "..\util.h"
#include "..\feature_flags.h"
#include "..\ids.h"

@Environment(EnvType.CLIENT)
public class MixerGui
#if ENABLE_MIXER_BLOCK
extends CookingVesselGui 
#endif
{
#if ENABLE_MIXER_BLOCK

    protected CookingVesselTileEntity vesselEntity;

    public MixerGui(InventoryPlayer inventoryPlayer, CookingVesselTileEntity tileEntity) {
        super(inventoryPlayer, tileEntity, MIXER_CONTAINER_ID);
        this.vesselEntity = tileEntity;
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int IDK, int WTF) {
        // I have no idea how the other parameters control
        // text position, but adding a single space centers
        // everything *perfectly*
        fontRenderer.drawString(" Blender", 66, 6, 0x404040);
    }
    
#define SCREW_ICON_HEIGHT 12
    
    @Override
    protected void drawGuiContainerBackgroundLayer(float I, int D, int K) {
        // draw the background image
        
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        
        mc.renderEngine.bindTexture("/textures/gui/mixer.png");
        
        int xPos = (width - xSize) / 2;
        int yPos = (height - ySize) / 2;
        
        drawTexturedModalRect(xPos, yPos, 0, 0, xSize, ySize);
        
        // draw the cooking indicator
        
        if (this.vesselEntity.isCooking()) {
            int scaledIconHeight = this.vesselEntity.getCookProgressScaled(SCREW_ICON_HEIGHT);
            
            drawTexturedModalRect(
                xPos + 81, // screen x pos
                yPos + 19 + SCREW_ICON_HEIGHT - scaledIconHeight, // screen y pos
                176, // bitmap source x
                SCREW_ICON_HEIGHT - scaledIconHeight, // bitmap source y
                14, // width
                scaledIconHeight + 2
            );
        }
    }
#endif
}